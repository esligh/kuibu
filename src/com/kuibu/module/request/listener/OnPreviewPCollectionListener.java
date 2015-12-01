package com.kuibu.module.request.listener;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface OnPreviewPCollectionListener {
	
	public void OnPubulishResponse(Map<String,Object> params,JSONObject response);
	
	public void OnUploadImgsSuccess(String s);
	
	public void OnUploadImgsFailed(int errno,String msg);
	
	public void OnVolleyError(VolleyError error);

}
