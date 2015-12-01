package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.kuibu.model.entity.CollectPackBean;

public interface OnCreateAlbumListener {

	public void onLoadAlbumResponse(JSONObject response);

	public void onAddAlbumResponse(JSONObject response,CollectPackBean item);
	
	public void onUpdateAlbumResponse(JSONObject response,CollectPackBean item);
	
	public void onTopicListResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
	
	
	
}
