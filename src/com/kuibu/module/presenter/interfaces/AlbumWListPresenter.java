package com.kuibu.module.presenter.interfaces;

import java.util.List;

import com.kuibu.model.entity.CollectionBean;


public interface AlbumWListPresenter {

	public void delCollections();
			
	public void queryCollection();
	
	public void closeDbConn();
		
	public void switchToolBar(boolean mode);
	
	public boolean isMultiChoice();
	
	public boolean isMenuVisible();
	
	public String getPackId();
	
	public List<CollectionBean> getSelectItems();
	
	public void selectOne(CollectionBean item);
	
	public void unSelectOne(CollectionBean item);
}
