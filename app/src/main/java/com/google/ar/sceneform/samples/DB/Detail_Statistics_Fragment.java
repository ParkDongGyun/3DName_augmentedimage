package com.google.ar.sceneform.samples.DB;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.ar.sceneform.samples.DBActivity;
import com.google.ar.sceneform.samples.augmentedimage.R;

import java.util.List;

public class Detail_Statistics_Fragment  extends Fragment {
	private Detail_statistics_Adapter detail_statistics_adapter;
	private Context context;

	private RecyclerView recyclerView;
	private TrackedImgDao trackedImgDao;
	private CourseDao courseDao;

	private int cid;
	public LinearLayoutManager linearLayoutManager;

	public static Detail_Statistics_Fragment newInstance(int cid) { return new Detail_Statistics_Fragment(cid); }

	public Detail_Statistics_Fragment(int cid) {
		this.cid = cid;
	}

	//fragment가 activity에서 불려질때 들어오는 함수
	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		trackedImgDao = ARCoreTestDB.getDatabase(context).trackedImgDao();

//		Log.i("Save", "리스트 크기 : "+Integer.toString(trackedImgDao.getAllCount()));
//
//		for(int i=0;i<trackedImgDao.getAllCount();i++) {
//			Log.i("Save", "리스트 아이디 : " +Integer.toString(trackedImgDao.getAll().get(i).getId()));
//		}


		List<TrackedImgInfo> trackedImgList = trackedImgDao.getAllCourse(cid);

		Log.i("Save", "리스트 갯수 : "+Integer.toString(trackedImgDao.getAllCourseCount(cid)));
		Log.i("Save", "리스트 사이즈 : "+Integer.toString(trackedImgDao.getAllCourse(cid).size()));

		for(int i=0;i<trackedImgDao.getAllCourseCount(cid);i++) {
			Log.i("Save", "리스트 아이디 : " +Integer.toString(trackedImgDao.getAllCourse(cid).get(i).getId()));
		}
		detail_statistics_adapter = new Detail_statistics_Adapter(context,this,  trackedImgList, new OnItemClickListener() {

			@Override
			public void onItemClick(View v, int position) {
				ImageDialog imageDialog = new ImageDialog(context, trackedImgList.get(position).getImgName(), trackedImgList.get(position).getDirectoryImg());
				WindowManager.LayoutParams wmLayoutParams = imageDialog.getWindow().getAttributes();
				wmLayoutParams.copyFrom(imageDialog.getWindow().getAttributes());

				imageDialog.show();
			}

			@Nullable
			@Override
			public void onLongClick(View v, int position) {

			}
		});

		recyclerView.setAdapter(detail_statistics_adapter);
		linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		try {
			if (detail_statistics_adapter.getItemCount() == 0)
				((DBActivity) context).tvNothing.setVisibility(View.VISIBLE);
		}catch (Exception e) {
			Log.e("Save",e.toString());
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_detail_statistics, container, false);

		recyclerView = view.findViewById(R.id.fragment_detail);

		return view;
	}
}