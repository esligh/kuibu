package com.kuibu.common.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

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
	
	
	public static void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
