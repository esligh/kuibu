package com.kuibu.module.presenter.interfaces;

import java.util.List;

import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.entity.TopicItemBean;

public interface CreateAlbumPresenter {

	public void addAlbum(CollectPackBean item);
	
	public void updateAlbum(CollectPackBean item);
	
	public void getTopicList(String query);
	
	public void loadAlbum();
	
	public void closeDbConn();
	
	public String getOperType();
	
	public String getPackId();
	
	public List<TopicItemBean> getTopicTags();
	
}
