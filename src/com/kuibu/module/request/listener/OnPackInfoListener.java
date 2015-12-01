package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnPackInfoListener {
	public void onLoadPackInfoSuccess(JSONObject response);
	public void onLoadPackListSuccess(JSONObject response);
	public void onFollowSuccess(JSONObject response);
	public void onLoadError(VolleyError error);
}
