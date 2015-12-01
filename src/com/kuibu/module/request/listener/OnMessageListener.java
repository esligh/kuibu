package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnMessageListener {
	
	public void OnLoadMessageListResponse(JSONObject response);
	
	public void OnVolleyError(VolleyError error);
}
