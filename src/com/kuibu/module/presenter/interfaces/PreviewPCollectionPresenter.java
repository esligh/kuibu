package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CollectionBean;

public interface PreviewPCollectionPresenter {
	
	public void publish();
	
	public void loadCollection();
	
	public void closeDbConn();
	
	public CollectionBean getCollection();
	
	public boolean needPublish();
	
	public void toggleRotation();
	
	public boolean hasChanged();
	
	public void setChanged(boolean state);
	
	public void setContentDesc(String desc);
	
	public void setContentTitle(String title);
	
	public void trigger();
	
	public void removeCallback();
}
