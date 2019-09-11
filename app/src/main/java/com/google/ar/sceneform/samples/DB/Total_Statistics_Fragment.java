package com.google.ar.sceneform.samples.DB;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.ar.sceneform.samples.DBActivity;
import com.google.ar.sceneform.samples.augmentedimage.R;

public class Total_Statistics_Fragment extends Fragment {
	private Total_Statistics_Adapter total_statistics_adapter;
	private Context context;

	private RecyclerView recyclerView;
	private CourseDao courseDao;
	private TrackedImgDao trackedImgDao;

	public static Total_Statistics_Fragment newInstance() {
		return new Total_Statistics_Fragment();
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_total_statistics, container, false);

		recyclerView = view.findViewById(R.id.fragment_total);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		courseDao = ARCoreTestDB.getDatabase(context).courseDao();
		trackedImgDao = ARCoreTestDB.getDatabase(context).trackedImgDao();

		total_statistics_adapter = new Total_Statistics_Adapter(getContext(), this, courseDao.getAll(), new OnItemClickListener() {
			@Override
			public void onItemClick(View v, int position) {
				try {
					CourseInfo courseInfo = total_statistics_adapter.getItem(position);

					((DBActivity) context).SetToolbar(context.getString(R.string.tbTitleDetail));
					((DBActivity) context).detail_statistics_fragment = Detail_Statistics_Fragment.newInstance(courseInfo.getId());
					((DBActivity) context).ShowFragment(((DBActivity) context).detail_statistics_fragment);

				} catch (Exception e) {
					Log.e("getID", e.toString(), e);
				}
			}

			@Override
			public void onLongClick(View v, int position) {
				String[] info = {"이름변경", "삭제"};
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				builder.setTitle("선택");
				builder.setItems(info, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								changeName(position);
								break;
							case 1:
								deleteItem(position);
								break;
						}
						dialog.dismiss();
					}
				}).show();
			}
		});
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(total_statistics_adapter);

		if (courseDao.getAllCount() == 0)
			((DBActivity) context).tvNothing.setVisibility(View.VISIBLE);
	}

	private void deleteItem(int position) {
		new AlertDialog.Builder(getContext())
				.setTitle(getString(R.string.ADdeleteTitle))
				.setMessage(getString(R.string.ADdeleteMessage))
				.setPositiveButton(android.R.string.ok, (dialog, which) -> {
					try {
						CourseInfo courseInfo = total_statistics_adapter.getItem(position);
						courseDao.delete(courseInfo);
					} catch (Exception e) {
						Log.e("delete", e.toString(), e);
					}
				})
				.setNegativeButton(android.R.string.cancel, (dialog, which) -> {

				}).show();
	}

	private void changeName(int position) {
		View view = getLayoutInflater().inflate(R.layout.d_edittext, null);
		EditText etInput = view.findViewById(R.id.etInput);
		etInput.setTextColor(Color.BLACK);

		new AlertDialog.Builder(getContext())
				.setTitle(getString(R.string.ADchangeNameTitle))
				.setView(view)
				.setMessage(getString(R.string.ADchangeNameMessage))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String setTitle = etInput.getText().toString();

						if (!TextUtils.isEmpty(setTitle)) {
							try {
								CourseInfo courseInfo = total_statistics_adapter.getItem(position);
								courseInfo.setName(setTitle);
								courseDao.update(courseInfo);
							} catch (Exception e) {
								Log.e("ChangeName", e.toString(), e);
							}
						} else {
							Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}
}