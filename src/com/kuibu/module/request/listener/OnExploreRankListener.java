package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnExploreRankListener {
	
	public void onLoadRankListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
}
