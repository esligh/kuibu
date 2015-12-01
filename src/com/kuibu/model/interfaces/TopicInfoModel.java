package com.kuibu.model.interfaces;

import java.util.Map;

public interface TopicInfoModel {
	public void requestTopicInfo(Map<String,String> params);
		
	public void requestAuthorList(Map<String,String> params);
	
	public void doFollow(Map<String,String> params);
}
