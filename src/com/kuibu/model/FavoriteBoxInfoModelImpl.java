package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.FavoriteBoxInfoModel;
import com.kuibu.module.request.listener.OnFavoriteBoxInfoListener;

public class FavoriteBoxInfoModelImpl implements FavoriteBoxInfoModel{

	private OnFavoriteBoxInfoListener mListener ; 
	
	public FavoriteBoxInfoModelImpl(OnFavoriteBoxInfoListener l)
	{
		this.mListener = l ;
	}

	@Override
	public void requestBoxInfo(Map<String,String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_boxinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnLoadBoxInfoResponse(response);
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
	public void followBox(Map<String,String> params) {
		String URL = params.get("URL");
		// TODO Auto-generated method stub
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader(); 
	 		} 
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	
	}

	@Override
	public void requestBoxList(Map<String,String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_boxlist").toString();
		
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
		new JSONObject(params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnLoadBoxListResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.OnVolleyError(error);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);

	}

	@Override
	public void requestUserinfo(Map<String,String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_userinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnLoadUserinfoResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
}
