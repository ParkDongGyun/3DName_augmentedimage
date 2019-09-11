package com.google.ar.sceneform.samples.DB;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.samples.augmentedimage.R;

import java.io.File;
import java.util.List;

public class Detail_statistics_Adapter extends RecyclerView.Adapter<Detail_statistics_Adapter.ViewHolder> {

	private Context context;
	private List<TrackedImgInfo> trackedImgs;
	private static OnItemClickListener listener;
	private String packName;
	private String directoryName;

	public Detail_statistics_Adapter(Context context, LifecycleOwner lifecycleOwner, List<TrackedImgInfo> itemTable, OnItemClickListener listener) {
		this.context = context;
		this.trackedImgs = itemTable;
		//trackedImgs.observe();
		/*this.trackedImgs.observe(lifecycleOwner, trackedImgInfos -> {
			for(TrackedImgInfo data : trackedImgInfos)
				Log.i("Save", data.toString());

			notifyDataSetChanged();
		});*/
		this.listener = listener;
		this.packName = context.getPackageName();
		this.directoryName = "drawable";
	}

	@Override
	public int getItemCount() {
		return trackedImgs == null ? 0 : trackedImgs == null ? 0 :trackedImgs.size();
	}

	public TrackedImgInfo getItem(int position) {
		return trackedImgs == null ? null : trackedImgs == null ? null : trackedImgs.get(position) == null ? null : trackedImgs.get(position);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_detail_statistics,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		TrackedImgInfo trackedImgInfo = getItem(position);

		if (trackedImgInfo == null)
			return;

		String resName = "";
		if(trackedImgInfo.getImgName().contains(".jpg")) {
			resName = trackedImgInfo.getImgName().split(".jpg")[0];
		} else if(trackedImgInfo.getImgName().contains(".png")) {
			resName = trackedImgInfo.getImgName().split(".png")[0];
		}
		int resID = context.getResources().getIdentifier(resName, directoryName, packName);
		Glide.with(context).load(resID).into(holder.ivImg);

		File dirImg = new File(trackedImgInfo.getDirectoryImg());
		if(dirImg.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(dirImg.getAbsolutePath());
			holder.ivDir.setImageBitmap(bitmap);
		}

		String imgName = trackedImgInfo.getImgName();
		holder.tvImgName.setText(!TextUtils.isEmpty(imgName) ? imgName : context.getString(R.string.unknown));
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		LinearLayout llTotal;
		ImageView ivImg;
		ImageView ivDir;
		TextView tvImgName;
		TextView tvImgCount;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);

			llTotal = itemView.findViewById(R.id.ll_total);
			ivImg = itemView.findViewById(R.id.ivImg);
			ivDir = itemView.findViewById(R.id.ivdir);
			tvImgName = itemView.findViewById(R.id.tvImgName);
			tvImgCount = itemView.findViewById(R.id.tvImgCount);

			llTotal.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			listener.onItemClick(v, getAdapterPosition());
		}

		@Override
		public boolean onLongClick(View v) {
			listener.onLongClick(v, getAdapterPosition());
			return true;
		}
	}
}