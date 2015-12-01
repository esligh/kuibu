package com.kuibu.module.request.listener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnHomePageListener {
	
	public void onLoadDataSuccess(JSONObject response);
	
	public void onVolleyError(VolleyError error);
	
	public void parseFromJson(JSONArray arr,String action);
}
