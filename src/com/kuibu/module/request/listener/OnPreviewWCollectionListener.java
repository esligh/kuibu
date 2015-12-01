package com.kuibu.module.request.listener;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnPreviewWCollectionListener {

	public void OnPubulishResponse(Map<String,Object> params,JSONObject response);
	
	public void OnUpdateImgsResponse(Map<String,Object> params,JSONObject response);

	public void OnUploadImgsSuccess(String s,boolean bfirst);
	
	public void OnUploadImgsFailed(int errno,String msg);
	
	public void OnVolleyError(VolleyError error);
	
	public void OnImgListResponse(JSONObject response);
	
	public void OnDelImgsResponse(JSONObject response);


}
