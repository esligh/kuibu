package com.kuibu.model;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.SettingModel;
import com.kuibu.module.request.listener.OnSettingListener;

public class SettingModelImpl implements SettingModel{
	
	private OnSettingListener mListener ; 
	private FinalHttp finalHttp = null;
	
	public SettingModelImpl(OnSettingListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ; 
	}

	@Override
	public void requestVersion() {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_appinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, null, 
		new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnReqVersionResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public void downLoadApp(String URL,String apkPath) {
		if(finalHttp == null) 
				finalHttp = new FinalHttp() ; 
		finalHttp.download(URL, apkPath, new AjaxCallBack<File>() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				mListener.OnDownLoading(count, current);
			}

			@Override
			public void onSuccess(File t) {				
				super.onSuccess(t);
				mListener.OnDownLoadSuccess();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mListener.OnDownLoadFailed();
			}
		});    
	}

	@Override
	public void uploadFile(String URL, AjaxParams params) {
		// TODO Auto-generated method stub
		if(finalHttp == null){ 
			finalHttp = new FinalHttp() ;
		}
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mListener.OnUpLoadFailed();
			}
			
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				mListener.OnUpLoading(count, current);
			}
			
		@Override
			public void onSuccess(String t) {									
				super.onSuccess(t);
				mListener.OnUpLoadSuccess();
			}						
		});
	}

}
