package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CollectionBean;

public interface PreviewWCollectionPresenter {

	public void publish();
	
	public void loadCollection();
	
	public void closeDbConn();
	
	public void updateCover(String url);
	
	public void setHtmlSource(String html);
	
	public CollectionBean getCollection();
	
	public boolean needPublish();
	
	public String getCoverPath();
	
	public void setCoverPath(String path);
}
