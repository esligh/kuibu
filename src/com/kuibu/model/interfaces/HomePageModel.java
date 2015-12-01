package com.kuibu.model.interfaces;

import java.util.Map;

public interface HomePageModel {
	
	public void requestData(Map<String, String> params,boolean bcache);	
	
	public void cancelRequest();
}
