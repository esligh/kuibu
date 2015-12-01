package com.kuibu.model.interfaces;

import java.util.Map;

import com.kuibu.model.entity.CollectPackBean;

public interface CreateAlbumModel {

	public void requestAddAlbum(Map<String,String> params,CollectPackBean item);

	public void requestUpdateAlbum(Map<String,String> params,CollectPackBean item);
	
	public void requestAlbum(Map<String,String> params);
	
	public void requestTopicList(Map<String,String> params);
	
	
}
