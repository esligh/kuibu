package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.LetterModel;
import com.kuibu.module.request.listener.OnLetterListener;

public class LetterModelImpl implements LetterModel{

	private OnLetterListener mListener ; 
	
	public LetterModelImpl(OnLetterListener l)
	{
		this.mListener = l ;
	}

	@Override
	public void requestSenderList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_msgusers").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					mListener.OnLoadSenderListResponse(response);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
					VolleyLog.e("Error:", error.getCause());
					error.printStackTrace();
					mListener.OnVolleyError(error);
				}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req,this);		
	}

	@Override
	public void readLetter(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/update_message";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
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
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req,this);
	}
	
}
