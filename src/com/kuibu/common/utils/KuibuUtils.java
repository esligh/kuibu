package com.kuibu.common.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.ImageView;

import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class KuibuUtils {

	/**
	 * 获取当前程序路径
	 * getApplicationContext().getFilesDir().getAbsolutePath();
	 * 获取该程序的安装包路径
	 * String path=getApplicationContext().getPackageResourcePath();
	 * 获取程序默认数据库路径
	 * getApplicationContext().getDatabasePath(s).getAbsolutePath();
	 * */
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
	
	public static Map<String,String> prepareReqHeader()
	{
		HashMap<String, String> headers = new HashMap<String, String>();
		String credentials = Session.getSession().getToken()+":unused";
		headers.put("Authorization","Basic "+
		SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
		return headers;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(String date)
	{				
		Calendar cal = Calendar.getInstance();
		try {
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar now = Calendar.getInstance();
		long l=now.getTimeInMillis()-cal.getTimeInMillis();
		long day=l/(24*60*60*1000);   
		System.out.println(cal);
		StringBuffer sb = new StringBuffer();		
		if(day >= 0 && day<1){
			sb.append("今天 ").append(cal.get(Calendar.HOUR_OF_DAY))
			.append(":").append(cal.get(Calendar.MINUTE));
		}else if(day<=1){
			sb.append("昨天 ").append(cal.get(Calendar.HOUR_OF_DAY))
			.append(":").append(cal.get(Calendar.MINUTE));
		}else{
			sb.append(cal.get(Calendar.MONTH)).append("月").
			append(cal.get(Calendar.DAY_OF_MONTH)).append("日");
		}
		return sb.toString();
	}
	
	public static String getRefreshLabel(Context context,String key)
	{
		String now = DateUtils.formatDateTime(context
				.getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		String last_time = PreferencesUtils.getString(context,key,"");
		String label ; 
		if(TextUtils.isEmpty(last_time)){
			label = now ;  						
		}else{
			label = last_time ; 
		}					
		PreferencesUtils.putString(context, key,now);
		return label ; 
	}
}
