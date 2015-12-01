package com.kuibu.module.presenter.interfaces;

import java.util.Map;


public interface FavoriteBoxPresenter {
	
	public void loadBoxList();
	
	public void delBox(int position);
	
	public Map<String,String> getDataItem(int position);
	
}
