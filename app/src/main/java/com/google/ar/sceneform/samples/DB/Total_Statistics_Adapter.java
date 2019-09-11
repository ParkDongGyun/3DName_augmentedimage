package com.google.ar.sceneform.samples.DB;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.sceneform.samples.augmentedimage.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Total_Statistics_Adapter extends RecyclerView.Adapter<Total_Statistics_Adapter.ViewHolder> {
	private Context context;
	private LiveData<List<CourseInfo>> totalItem;
	private static OnItemClickListener listener;

	public Total_Statistics_Adapter(Context context, LifecycleOwner lifecycleOwner, LiveData<List<CourseInfo>> itemTable, OnItemClickListener listener) {

		this.context = context;
		this.totalItem = itemTable;
		this.totalItem.observe(lifecycleOwner, totalItems -> {
//			for(CourseInfo data : totalItems)
//				Log.i("Save", data.toString());

			notifyDataSetChanged();
		});

		this.listener = listener;
	}

	@Override
	public int getItemCount() {
		return totalItem == null ? 0 : totalItem.getValue() == null ? 0 : totalItem.getValue().size();
	}

	public CourseInfo getItem(int position) {
		return totalItem == null ? null : totalItem.getValue() == null ? null : totalItem.getValue().get(position);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_total_statistics, parent, false));
	}

	private String GetStringDate(long now) {
		Date date = new Date(now);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = simpleDateFormat.format(date);

		return dateTime;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		CourseInfo courseInfo = totalItem.getValue().get(position);

		if (courseInfo != null) {
			holder.textViewID.setText(String.valueOf(courseInfo.getId()));

			String content = courseInfo.getName();
			holder.tvContent.setText(!TextUtils.isEmpty(content) ? content : "Unknown");
			holder.tvContent.setSelected(true);

			long startDate = courseInfo.getStartTime();
			holder.textViewStart.setText(startDate > 0 ? GetStringDate(startDate) : "Unknown");

			long endDate = courseInfo.getEndTime();
			holder.textViewEnd.setText(endDate > 0 ? GetStringDate(endDate) : "Unknown");

			holder.textViewCount.setText(context.getString(R.string.text_count, courseInfo.getTrackedImgCount(), courseInfo.getTotalImgCount()));
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		LinearLayout linearLayoutitem;
		TextView textViewID;
		TextView tvContent;
		TextView textViewStart;
		TextView textViewEnd;
		TextView textViewCount;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			linearLayoutitem = itemView.findViewById(R.id.lineaer_total);
			textViewID = itemView.findViewById(R.id.textview_total_id);
			tvContent = itemView.findViewById(R.id.tvContent);
			textViewStart = itemView.findViewById(R.id.textview_start);
			textViewEnd = itemView.findViewById(R.id.textview_end);
			textViewCount = itemView.findViewById(R.id.textview_count);

			linearLayoutitem.setOnClickListener(this);
			linearLayoutitem.setOnLongClickListener(this);
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