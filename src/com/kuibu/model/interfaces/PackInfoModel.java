package com.kuibu.model.interfaces;

import java.util.Map;

public interface PackInfoModel {
	
	public void requestPackInfo(Map<String,String> params);
	
	public void requestPackList(Map<String,String> params);
	
	public void doFollow(Map<String,String> params,boolean bFollow);
}
