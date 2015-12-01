package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.FollowedPackModel;
import com.kuibu.module.request.listener.OnFollowedPackListener;

public class FollowedPackModelImpl implements FollowedPackModel{

	private OnFollowedPackListener mListener ; 
	
	public FollowedPackModelImpl(OnFollowedPackListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ; 
	}

	@Override
	public void requestFollowedPackList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_focuslist";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnFollwedPackListResponse(response);
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
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	

}
