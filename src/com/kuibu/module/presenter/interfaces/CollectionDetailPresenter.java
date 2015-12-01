package com.kuibu.module.presenter.interfaces;

import java.util.List;

public interface CollectionDetailPresenter {

	public void loadContent();
	
	public void loadActions();
		
	public void doReport();
	
	public void doVote(String action_type,boolean isVoted);
	
	public boolean isReport();
	
	public void setReport(boolean state);
	
	public boolean isInfavorite();
	
	public boolean isVoted();
	
	public void setInFavorite(boolean state);
	
	public String getCid();
	
	public int getCommentCount();
	
	public void setCommonCount(int count);
	
	public String getCreateBy();
	
	public List<String> getImageList();
	
	public void lazyShowTools();
	
	public void removeCallback();
}
