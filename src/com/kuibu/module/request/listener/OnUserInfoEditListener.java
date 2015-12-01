package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnUserInfoEditListener {

	public void onUserInfoResponse(JSONObject reponse);
	
	public void onUploadPicSuccess(String s);
	
	public void onUploadPicFailed();
	
	public void onUpdateInfoResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
	
}
