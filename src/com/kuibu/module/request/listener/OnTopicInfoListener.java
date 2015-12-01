package com.kuibu.module.request.listener;

import org.json.JSONObject;

public interface OnTopicInfoListener {

	public void onTopicInfoResponse(JSONObject response);
	
	public void onUserListResponse(JSONObject response);

	public void onFollowResponse(JSONObject response);
	
}
