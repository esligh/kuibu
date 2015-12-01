package com.kuibu.module.request.listener;

import org.json.JSONObject;

public interface OnSettingListener {
	
	public void OnReqVersionResponse(JSONObject response);
	
	public void OnDownLoading(long count ,long current );
	
	public void OnDownLoadSuccess();
	
	public void OnDownLoadFailed();
	
	public void OnUpLoading(long count,long current);
	
	public void OnUpLoadSuccess();
	
	public void OnUpLoadFailed();
}
