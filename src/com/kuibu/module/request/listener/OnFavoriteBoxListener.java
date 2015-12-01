package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnFavoriteBoxListener {
	
	public void OnLoadBoxListResponse(JSONObject response);
	
	public void OnDelBoxResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);

}
