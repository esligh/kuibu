package com.kuibu.model.interfaces;

import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

public interface UserInfoEditModel {

	public void requestUserInfo(Map<String,String> params);
	
	public void uploadUserPic(AjaxParams params);
	
	public void requestUpdateInfo(Map<String,String> params);
	
}
