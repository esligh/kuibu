package com.kuibu.common.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

public class StorageUtils {

	@SuppressLint("SdCardPath")
	public static File getFileDirectory(Context context) {
		File appFileDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException e) {
			externalStorageState = "";
		}
		if (MEDIA_MOUNTED.equals(externalStorageState)) {
			appFileDir = context.getExternalFilesDir(null);
		}
		if (appFileDir == null) {
			appFileDir = context.getFilesDir();
		}
		if (appFileDir == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/";
			appFileDir = new File(cacheDirPath);
		}
		return appFileDir;
	}
	
	
}
