package com.kuibu.model.interfaces;

import java.util.Map;

public interface FavoriteBoxModel {
	
	public void requestBoxList(Map<String,String> params);

	public void requestDelBox(Map<String,Object> params);
}
