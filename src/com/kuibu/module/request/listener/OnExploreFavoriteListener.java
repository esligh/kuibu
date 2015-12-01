package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnExploreFavoriteListener {
	
	public void onLoadFavoriteListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
}
