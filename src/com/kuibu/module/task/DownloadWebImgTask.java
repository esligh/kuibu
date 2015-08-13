package com.kuibu.module.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.Logger;
import com.kuibu.common.utils.SDCardUtils;
import com.kuibu.common.utils.StreamUtils;
import com.kuibu.module.iterf.ResponseListener;
import com.kuibu.module.net.HttpClientUtils;

public class DownloadWebImgTask extends AsyncTask<String, String, String>{
	
	private  Context mContext = null;
	private  ResponseListener mListener = null;
	protected Exception mException = null;	
	protected boolean isRefreshSuccess = true;
	
	public DownloadWebImgTask(Context context,ResponseListener listener )
	{
		this.mContext = context ;
		this.mListener = listener ; 
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if (mListener != null) {
			mListener.onPreExecute();
		}
	}

	@Override
	protected void onPostExecute(String content) {
		super.onPostExecute(content);
		// 如果当前任务已经取消了，则直接返回
		if (isCancelled()) {
			return;
		}

		if (mListener != null) {
			if (isRefreshSuccess) {
				mListener.onPostExecute(content);
			} else {
				mListener.onFail(mException);
			}
		}
	}
	
    @Override  
    protected String doInBackground(String... params) {  
    	
    	String externalCacheDir = SDCardUtils.getExternalCacheDir(mContext);
		
		if (params.length == 0 || TextUtils.isEmpty(externalCacheDir))
			return null;
	
		File file = null;
		for (String param : params) {

			if (TextUtils.isEmpty(param)) {
				Logger.getLogger().e("image url is empty");
				continue;
			}

			String filePath = KuibuUtils.getCacheImgFilePath(mContext, param);
			file = new File(filePath);

			boolean needDownload = true;

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				long fileSize = FileUtils.getFileSize(filePath);

				if (fileSize == 0) {
					
				} else {
					needDownload = false;
				}			
			}
			if (needDownload) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = HttpClientUtils.getStream(mContext, param, null);
					out = new FileOutputStream(file);
					StreamUtils.copy(in, out);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					StreamUtils.close(out);
					StreamUtils.close(in);
				}
			} 
			publishProgress(param);
		}

		return null;
    }  
      
}  
