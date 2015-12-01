package com.kuibu.module.presenter.interfaces;

import java.util.Map;

public interface PackInfoPresenter {
	public void loadPackInfo(String packId);
	public void loadPackList(String packId);
	
	public void follow(String packId);
	
	public String getTopids();
	public Map<String,String> getPackInfo();
	
	public boolean isFollowed();
	public void setFollow(boolean state);
	
}
