package com.kuibu.module.request.listener;

import org.json.JSONObject;

public interface OnSearchViewListener {
	public void OnLoadContentSuccess(JSONObject response);
	public void OnLoadUsersSuccess(JSONObject response);
	public void OnLoadTopicsSuccess(JSONObject response);
}
