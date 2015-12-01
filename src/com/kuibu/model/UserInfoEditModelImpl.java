package com.kuibu.model;

import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.UserInfoEditModel;
import com.kuibu.module.request.listener.OnUserInfoEditListener;

public class UserInfoEditModelImpl implements UserInfoEditModel{

	private OnUserInfoEditListener mListener ;
	private FinalHttp finalHttp = null;
	
	public UserInfoEditModelImpl(OnUserInfoEditListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ;
		finalHttp = new FinalHttp() ;		
	}

	@Override
	public void requestUserInfo(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_userinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
			// TODO Auto-generated method stub
				mListener.onUserInfoResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onVolleyError(error);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req,this);
	}

	@Override
	public void uploadUserPic(AjaxParams params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/upload_userpic").toString();
		
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				mListener.onUploadPicSuccess(t);
			}
		});
	}

	@Override
	public void requestUpdateInfo(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/update_userinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
			// TODO Auto-generated method stub
				mListener.onUpdateInfoResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onVolleyError(error);
			}
		}){
		@Override  
		public Map<String, String> getHeaders() throws AuthFailureError {  
		return KuibuUtils.prepareReqHeader();
		} 
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

}
