package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnFollowedTopicListener {
	public void OnFollwedTopicListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);

}
