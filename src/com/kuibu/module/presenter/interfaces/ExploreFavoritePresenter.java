package com.kuibu.module.presenter.interfaces;

import java.util.Map;


public interface ExploreFavoritePresenter {
	//public void loadFromLocal();
	
	public void loadFavoriteList(String action);
	public Map<String,String> getDataItem(int position);
}
