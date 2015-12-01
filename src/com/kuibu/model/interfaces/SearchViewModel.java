package com.kuibu.model.interfaces;

import java.util.Map;

public interface SearchViewModel {

	public void requestConent(Map<String,String> params);
	
	public void requestTopics(Map<String,String> params);
	
	public void requestUsers(Map<String,String> params);
	
	public void cancelRequest();
}
