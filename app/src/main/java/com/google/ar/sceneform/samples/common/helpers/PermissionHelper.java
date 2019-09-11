package com.google.ar.sceneform.samples.common.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {
	private static final int CAMERA_PERMISSION_CODE = 0;
	private static final String PERMISSION[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

	/**
	 * Check to see we have the necessary permissions for this app.
	 */
	public static boolean hasPermission(Activity activity) {
		for (int i = 0; i < PERMISSION.length; i++) {
			if (ContextCompat.checkSelfPermission(activity, PERMISSION[i]) != PackageManager.PERMISSION_GRANTED)
				return false;
		}
		return true;
	}

	/**
	 * Check to see we have the necessary permissions for this app, and ask for them if we don't.
	 */
	public static void requestPermission(Activity activity) {
		ActivityCompat.requestPermissions(activity, PERMISSION, CAMERA_PERMISSION_CODE);
	}

	/**
	 * Check to see if we need to show the rationale for this permission.
	 */
	public static boolean shouldShowRequestPermissionRationale(Activity activity) {
		for (int i = 0; i < PERMISSION.length; i++) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION[i]))
				return true;
		}
		return false;
	}

	/**
	 * Launch Application Setting to grant permission.
	 */
	public static void launchPermissionSettings(Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
		activity.startActivity(intent);
	}
}
