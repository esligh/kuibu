package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnLetterListener {
	public void OnReadLetterResponse(JSONObject response);
	
	public void OnLoadSenderListResponse(JSONObject response);	
	
	public void OnVolleyError(VolleyError error);
}
