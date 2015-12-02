package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnPCollectionDetailListener {
	
	public void onLoadContentResponse(JSONObject response);
	
	public void onLoadActionsResponse(JSONObject response);
	
	public void onVoteResponse(JSONObject response);
	
	public void onReportResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
	
}
