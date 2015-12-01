package com.kuibu.model.interfaces;

import java.util.Map;

public interface FavoriteBoxInfoModel {

	public void requestBoxInfo(Map<String,String> params);
	
	public void followBox(Map<String,String> params);
	
	public void requestBoxList(Map<String,String> params);
	
	public void requestUserinfo(Map<String,String> params);


}

