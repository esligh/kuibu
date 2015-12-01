package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnAlbumPListListener {

	public void OnDelCollectionResponse(JSONObject response);
	
	public void onVolleyError(VolleyError error);
}
