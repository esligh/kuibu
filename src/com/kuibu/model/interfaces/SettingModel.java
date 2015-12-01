package com.kuibu.model.interfaces;

import net.tsz.afinal.http.AjaxParams;


public interface SettingModel {

	public void requestVersion();
	
	public void downLoadApp(String URL,String apkPath);
	
	public void uploadFile(String URL,AjaxParams params);
}
