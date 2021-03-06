package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.MessageModel;
import com.kuibu.module.request.listener.OnMessageListener;

public class MessageModelImpl implements MessageModel{
	private OnMessageListener mListener ; 
	
	public MessageModelImpl(OnMessageListener l)
	{
		this.mListener = l ;
	}

	@Override
	public void requestMessageList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_actionlist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnLoadMessageListResponse(response);
			}}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();				
				mListener.OnVolleyError(error);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req,this);	
	}

	
}
