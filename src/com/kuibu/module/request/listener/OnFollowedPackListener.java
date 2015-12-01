package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnFollowedPackListener {

	public void OnFollwedPackListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);

}
