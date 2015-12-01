package com.kuibu.module.presenter.interfaces;

public interface HomePagePresenter {
	
	public void loadNetData(String action,boolean bcache);
	
	public boolean loadLocalData();
	
}
