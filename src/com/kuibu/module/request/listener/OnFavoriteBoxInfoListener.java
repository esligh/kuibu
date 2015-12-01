package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnFavoriteBoxInfoListener {
	public void OnLoadBoxInfoResponse(JSONObject response);
	
	public void OnFollowBoxResponse(JSONObject response);
	
	public void OnLoadBoxListResponse(JSONObject response);
	
	public void OnLoadUserinfoResponse(JSONObject response);
	
	public void OnVolleyError(VolleyError error);
}
