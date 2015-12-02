package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnCollectionListListener {
	
	public void onCollectionListResponse(JSONObject response);

	public void onVolleyError(VolleyError error);
}
