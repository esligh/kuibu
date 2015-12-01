package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnExploreRecommendListener {
	public void onLoadRecommendListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
}
