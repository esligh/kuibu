package com.kuibu.model.interfaces;

import java.util.Map;

public interface CollectionDetailModel {

	public void requestContent(Map<String,String> params);
	
	public void requestUserActions(Map<String,String> params);
	
	public void doVote(Map<String,String> params);
	
	public void doReport(Map<String,String> params);
	
}
