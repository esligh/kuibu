package com.kuibu.common.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.net.PublicRequestor;
import com.kuibu.ui.activity.ReportActivity;
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
	
	@SuppressWarnings("deprecation")
	public static void showReportView(final Context context, final int requestCode, final Map<String,String> params)
	{
		AlertDialog.Builder builder = null;
		boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(context,
				StaticValue.PrefKey.DARK_THEME_KEY, false);
		if(isDarkTheme){
			builder = new Builder(context,AlertDialog.THEME_HOLO_DARK);
		}else{
			builder = new Builder(context,AlertDialog.THEME_HOLO_LIGHT);
		}		
		builder.setTitle(getString(R.string.report_reason));
		builder.setItems(context.getResources().getStringArray(R.array.report_comment), 
			new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int position) {						
					String[] items = context.getResources().getStringArray(
							R.array.report_comment);
					if(items != null && items.length > position)
						params.put("reason",items[position]);
					
					switch(position){
						case 0:case 1:case 2:case 3:case 4:
							PublicRequestor.sendReport(params);
							break;
						case 5:
							Intent intent = new Intent(context,ReportActivity.class);
							intent.putExtra("defendant_id", params.get("defendant_id"));
							((Activity)context).startActivityForResult(intent,requestCode);								
							break;
				}
			}									
		});
		builder.show();
	}
	
	public static void showText(String text,int duration)
	{
		Toast.makeText(KuibuApplication.getContext(), text, duration).show();
	}
	
	public static void showText(int resid,int duration)
	{
		Toast.makeText(KuibuApplication.getContext(),
				KuibuApplication.getContext().getString(resid), duration).show();
	}
	
	public static String getString(int resId)
	{
		return KuibuApplication.getContext().getString(resId);
	}
	
}
