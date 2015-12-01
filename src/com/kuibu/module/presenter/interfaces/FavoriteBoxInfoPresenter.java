package com.kuibu.module.presenter.interfaces;

import java.util.Map;

import com.kuibu.model.entity.CollectionItemBean;

public interface FavoriteBoxInfoPresenter {

	public void loadBoxInfo();
	
	public void followBox();
	
	public void loadBoxList();
	
	public void loadUserinfo();
	
	public Map<String,Object> getUserinfo();
	
	public String getType();
	
	public boolean isFollow();
	
	public CollectionItemBean getDateItem(int position);
	
}
