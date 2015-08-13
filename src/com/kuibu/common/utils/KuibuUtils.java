package com.kuibu.common.utils;

import java.io.File;

import android.content.Context;

import com.kuibu.data.global.Constants;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class KuibuUtils {

	public static String getImgCacheDir(Context context)
	{
		File imgDir = new File(StorageUtils.getCacheDirectory(context).getAbsolutePath(),
				Constants.Config.WEBVIEW_IMG_CACHE_DIR);
		if(!imgDir.exists()){
			imgDir.mkdirs();
		}
		return imgDir.getAbsolutePath();		
	}
	
	public static String getCacheImgFilePath(Context context, String imageUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append(getImgCacheDir(context)).append(File.separator)
		.append(SafeEDcoderUtil.MD5(imageUrl)).append(".bin");
		return  sb.toString();
	}
	
}
