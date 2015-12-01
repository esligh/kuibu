package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnUserHomeListener {

	public void OnFollowCollectorResponse(JSONObject response);
	
	public void OnLoadUserinfoResponse(JSONObject response);
	
	public void OnVolleyError(VolleyError response);

}
