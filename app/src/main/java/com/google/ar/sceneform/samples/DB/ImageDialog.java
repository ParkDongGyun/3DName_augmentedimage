package com.google.ar.sceneform.samples.DB;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ar.sceneform.samples.augmentedimage.R;

import java.io.File;

public class ImageDialog extends Dialog implements View.OnClickListener {

	//mageView imageView;
	public ImageDialog(Context context, String imgName, String dirName) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_image);
		ImageView imageView = findViewById(R.id.iv_detailImg);

		String resName = "";
		if(imgName.contains(".jpg")) {
			resName = imgName.split(".jpg")[0];
		} else if(imgName.contains(".png")) {
			resName = imgName.split(".png")[0];
		}

		String packName = context.getPackageName();
		String directoryName = "drawable";

		int resID = context.getResources().getIdentifier(resName, directoryName, packName);
		imageView.setImageResource(resID);
		//Glide.with(context).load(resID).into(imageView);

		ImageView iv_dirImg = findViewById(R.id.iv_detaildir);

		File dirImg = new File(dirName);

		if(dirImg.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(dirImg.getAbsolutePath());
			iv_dirImg.setImageBitmap(bitmap);
		}

		LinearLayout linearLayout = findViewById(R.id.ll_detail_img);
		linearLayout.setOnClickListener(this);
	}
	public void onClick(View v) {
		dismiss();
	}
}
