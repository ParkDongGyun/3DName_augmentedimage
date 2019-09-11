/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.FixedHeightViewSizer;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.samples.DB.ARCoreTestDB;
import com.google.ar.sceneform.samples.DB.CourseDao;
import com.google.ar.sceneform.samples.DB.CourseInfo;
import com.google.ar.sceneform.samples.DB.TrackedImgDao;
import com.google.ar.sceneform.samples.DB.TrackedImgInfo;
import com.google.ar.sceneform.samples.DBActivity;
import com.google.ar.sceneform.samples.common.helpers.DemoUtils;
import com.google.ar.sceneform.samples.common.helpers.PermissionHelper;
import com.google.ar.sceneform.samples.common.helpers.SnackbarHelper;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 *
 * <p>In this example, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * ArAugmentedImage_getTrackingMethod() and render only when the tracking method equals to
 * AR_AUGMENTED_IMAGE_TRACKING_METHOD_FULL_TRACKING. See details in <a
 * href="https://developers.google.com/ar/develop/c/augmented-images/">Recognize and Augment
 * Images</a>.
 */
public class AugmentedImageActivity extends AppCompatActivity {

	private ArFragment arFragment;
	private ImageView fitToScanView;

	// Augmented image and its associated center pose anchor, keyed by the augmented image in
	// the database.
	private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

	private ViewRenderable viewRenderable_auginfo;

	private TrackedImgDao trackedImgDao;
	private List<TrackedImgInfo> trackedImgList;
	private CourseDao courseDao;
	private LiveData<List<CourseInfo>> courseInfoList;

	private Button button;
	private Button statisticBtn;


	private int totalImgCount;
	private int c_Id = 0;
	private boolean isSaving = false;

	private Boolean backKeyPressed = false;
	private final int btFinishSec = 3;

	private final Set<String> augmentedImgName = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
		fitToScanView = findViewById(R.id.image_view_fit_to_scan);
		//
		arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);


		////////////////////코딩함//////////////////////////////////
		CompletableFuture<ViewRenderable> viewRenderableCompletableFuture = ViewRenderable.builder()
				.setSizer(new FixedHeightViewSizer(0.12f))
				.setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
				.setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
				.setView(this, R.layout.renderable_text)
				.build();

		CompletableFuture.allOf(viewRenderableCompletableFuture).handle(
				(notUsed, throwable) -> {
					if (throwable != null) {
						DemoUtils.displayError(this, "Unable to load renderable", throwable);
						return null;
					}

					try {
						viewRenderable_auginfo = viewRenderableCompletableFuture.get();
					} catch (InterruptedException | ExecutionException ex) {
						DemoUtils.displayError(this, "Unable to load renderable", ex);
					}

					return null;
				});


		getCourseInfoList();
		getTrackedImgList();

		totalImgCount = 0;

		button = findViewById(R.id.loaddata);
		button.setText(R.string.startcheck);
		button.setOnClickListener(v -> {
			if (!isSaving) {
				CheckStartCourse();
			} else {
				CheckStopCourse();
			}
		});

		statisticBtn = findViewById(R.id.statisticsbtn);
		statisticBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckCourseTrackCount();
				if(isSaving)
					CheckStopCourse();

				Intent intent = new Intent(getApplicationContext(), DBActivity.class);
				startActivity(intent);
			}
		});

		backKeyPressed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!PermissionHelper.hasPermission(this)) {
			PermissionHelper.requestPermission(this);
			return;
		}

		if (augmentedImageMap.isEmpty()) {
			fitToScanView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(!PermissionHelper.hasPermission(this)) {
			Toast.makeText(this, "Camera permissions or Storage are needed to run this application", Toast.LENGTH_LONG).show();
			if(!PermissionHelper.shouldShowRequestPermissionRationale(this)) {
				PermissionHelper.launchPermissionSettings(this);
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onBackPressed() {
		if (backKeyPressed)
			finish();
		else {
			Toast.makeText(this, getString(R.string.finishNotice), Toast.LENGTH_SHORT).show();
			backKeyPressed = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					backKeyPressed = false;
				}
			}, btFinishSec * 1000);
		}
	}

	/**
	 * Registered with the Sceneform Scene object, this method is called at the start of each frame.
	 *
	 * @param frameTime - time since last frame.
	 */
	private void onUpdateFrame(FrameTime frameTime) {
		Frame frame = arFragment.getArSceneView().getArFrame();

		// If there is no frame, just return.
		if (frame == null) {
			return;
		}

		Collection<AugmentedImage> updatedAugmentedImages =	frame.getUpdatedTrackables(AugmentedImage.class);

		for (AugmentedImage augmentedImage : updatedAugmentedImages) {

			switch (augmentedImage.getTrackingState()) {
				case PAUSED:
					// When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
					// but not yet tracked.
					//String text = "Detected Image " + augmentedImage.getIndex();
					String text = augmentedImage.getName();
					SnackbarHelper.getInstance().showMessageWithDismiss(this, text);

					if (isSaving && c_Id != 0) {
						try (Image image = frame.acquireCameraImage()) {
							if (image.getFormat() != ImageFormat.YUV_420_888) {
								throw new IllegalArgumentException("" + image.getFormat());
							}

							ByteBuffer ib = ByteBuffer.allocate(image.getHeight() * image.getWidth() * 2);

							ByteBuffer y = image.getPlanes()[0].getBuffer();
							ByteBuffer cr = image.getPlanes()[1].getBuffer();
							ByteBuffer cb = image.getPlanes()[2].getBuffer();

							ib.put(y);
							ib.put(cr);
							ib.put(cb);

							YuvImage yuvImage = new YuvImage(ib.array(), ImageFormat.NV21, image.getWidth(), image.getHeight(), null);  // YUV -> NV21로 변환

							ByteArrayOutputStream out = new ByteArrayOutputStream();
							yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 50, out);

							byte[] imageBytes = out.toByteArray();
							Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

							Matrix matrix = new Matrix();
							matrix.postRotate(90);
							Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

							synchronized (this) {
								SaveTracked(augmentedImage.getName(), saveBitmapToJpeg(bitmap));
							}

						} catch (Exception e) {
							Log.e("Save","ImageSaveFail");
						}
					}
					break;

				case TRACKING:
					// Have to switch to UI Thread to update View.
					fitToScanView.setVisibility(View.GONE);

					if(!augmentedImageMap.containsKey(augmentedImage)) {
						AugmentedImageNode node = new AugmentedImageNode(this);
						node.setImage(augmentedImage);
						augmentedImageMap.put(augmentedImage, node);

						String augImageName = augmentedImage.getName();

						if (augImageName.contains("_")) {
							augImageName = augImageName.split("_")[0];
							Log.i("Save",augImageName);

							Log.i("Save",Boolean.toString(augmentedImgName.contains(augImageName)));
							if(!augmentedImgName.contains(augImageName)) {
								augmentedImgName.add(augImageName);
								arFragment.getArSceneView().getScene().addChild(node);

								Node augInfo = new Node();
								augInfo.setParent(node);
								augInfo.setRenderable(viewRenderable_auginfo);
								augInfo.setLocalPosition(new Vector3(0.0f, 0.0f, 0.0f));
								augInfo.setLocalRotation(Quaternion.eulerAngles(new Vector3(90, 90, -90)));
								((TextView) viewRenderable_auginfo.getView().findViewById(R.id.tv_renderable)).setText(augImageName);
							}
						}
					}


					/*// Create a new anchor for newly found images.
					if (!augmentedImageMap.containsKey(augmentedImage)) {
						AugmentedImageNode node = new AugmentedImageNode(this);
						node.setImage(augmentedImage);
						augmentedImageMap.put(augmentedImage, node);
						arFragment.getArSceneView().getScene().addChild(node);

						////////////////////////////////코딩함//////////////////////////////////////////////////////////
						Node augInfo = new Node();
						augInfo.setParent(node);
						augInfo.setRenderable(viewRenderable_auginfo);
						augInfo.setLocalPosition(new Vector3(0.0f, 0.0f, 0.0f));
						augInfo.setLocalRotation(Quaternion.eulerAngles(new Vector3(90, 90, -90)));
						String augImageName = augmentedImage.getName();

						if (augImageName.contains(".jpg"))
							augImageName = augImageName.split(".jpg")[0];
						else if (augImageName.contains(".png"))
							augImageName = augImageName.split(".png")[0];

						((TextView) viewRenderable_auginfo.getView().findViewById(R.id.tv_renderable)).setText(augImageName);
					}*/
					break;

				case STOPPED:
					augmentedImageMap.remove(augmentedImage);
					break;

				default:
					Log.i("Save", "Default");
					break;
			}
		}
	}

	private void getTrackedImgList() {
		trackedImgDao = ARCoreTestDB.getDatabase(getApplicationContext()).trackedImgDao();
		if (trackedImgDao != null)
			trackedImgList = trackedImgDao.getAll();
		else
			Log.e("Save", "TrackedImgDao is NULL");
	}

	private void SaveTracked(String name, String directory) {
		try {
			if (isSaving && c_Id > 0) {
				TrackedImgInfo trackedImgInfo = new TrackedImgInfo(c_Id, name, directory);
				trackedImgDao.insert(trackedImgInfo);

				Log.i("Save", "저장된 이미지 갯수 : " + Integer.toString(trackedImgDao.getAllCourseCount(c_Id)));

				Log.i("Save", "저장된 이미지 아이디 : " + Integer.toString(trackedImgDao.getAllCourse(c_Id).get(trackedImgDao.getAllCourseCount(c_Id) - 1).getCourseID()));

			} else {
				Toast.makeText(getApplicationContext(), "Error!!", Toast.LENGTH_SHORT).show();
			}
		} catch (SQLiteConstraintException e) {
			Log.e("Save", e.toString(), e);
		} catch (Exception e) {
			Log.i("Save", e.toString());
		}
	}

	private CourseInfo getCourseInfo(int position) {
		return courseInfoList == null ? null : courseInfoList.getValue() == null ? null : courseInfoList.getValue().get(position);
	}

	private int getCourseinfoSize() {
		return courseInfoList == null ? 0 : courseInfoList.getValue() == null ? 0 : courseInfoList.getValue().size();
	}

	private void getCourseInfoList() {
		courseDao = ARCoreTestDB.getDatabase(getApplicationContext()).courseDao();

		if (courseDao != null)
			courseInfoList = courseDao.getAll();
		else
			Log.e("Save", "CourseDao is NULL");
	}

	private void CheckStartCourse() {
		View view = getLayoutInflater().inflate(R.layout.d_edittext, null);
		EditText etInput = view.findViewById(R.id.etInput);

		new AlertDialog.Builder(this)
				.setTitle("내용 입력")
				.setView(view)
				.setPositiveButton(android.R.string.ok, (dialog, which) -> {
					String content = etInput.getText().toString();

					if (!TextUtils.isEmpty(content)) {
						isSaving = !isSaving;

						setButtonText();
						SaveCourse(System.currentTimeMillis(), totalImgCount, content);
					} else {
						Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton(android.R.string.cancel, (dialog, which) -> {

				})
				.show();
	}

	private void CheckStopCourse() {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.ADstopNoticeTitle))
				.setMessage(getString(R.string.ADstopNoticeMessage))
				.setPositiveButton(android.R.string.ok, (dialog, which) -> {
					try {
						isSaving = !isSaving;
						setButtonText();

						CourseInfo courseInfo = courseDao.getCourseInfo(c_Id);
						courseInfo.setEndTime(GetCurrTime());

						Update(courseInfo);
					} catch (Exception e) {
						Log.e("Save", e.toString(), e);
					}
				})
				.setNegativeButton(android.R.string.cancel, (dialog, which) -> {

				})
				.show();
	}

	private void SaveCourse(long time, int totalImgCount, String name) {
		CourseInfo courseInfo = new CourseInfo(name, time, totalImgCount);
		c_Id = (int) courseDao.insert(courseInfo);
	}

	private void Update(CourseInfo courseInfo) {
		courseDao.update(courseInfo);
	}

	private void setButtonText() {
		runOnUiThread(() -> button.setText(isSaving ? R.string.endcheck : R.string.startcheck));
	}

	private long GetCurrTime() {
		Long now = System.currentTimeMillis();

		return now;
	}

	private void CheckCourseTrackCount() {

		List<CourseInfo> courseInfos = courseDao.getAllTrackedCount(0);

		for (int i = 0; i < courseInfos.size(); i++) {
			int courseId = courseInfos.get(i).getId();

			List<TrackedImgInfo> trackedImg = trackedImgDao.getAllCourse(courseId);
			int count = trackedImgDao.getAllCourseCount(courseId);
			courseInfos.get(i).setTrackedImgCount(count);

			courseDao.update(courseInfos.get(i));
			Log.i("Save", Integer.toString(count));
		}
	}

	private String saveBitmapToJpeg(Bitmap bitmap) {
		String directory = "";

		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/ARCoreImage");

		if (!path.exists()) {
			path.mkdirs();
		}

		try {
			String fileName = Long.toString(GetCurrTime()) + ".jpg";
			directory = path + "/" + fileName;
			Log.i("Save", "Directory : " + directory);
			FileOutputStream fos = new FileOutputStream(directory, true);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.e("Save", "Saving Bitmap is failed.");
		}
		return directory;
	}
}
