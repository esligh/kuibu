package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnCommentListener {

	public void onAddCommentResponse(JSONObject response);
	
	public void onDelCommentResponse(JSONObject response);
	
	public void onCommentListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
}
