package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CollectionItemBean;


public interface PCollectionDetailPresenter {
	
	public void loadContent();
	
	public void loadActions();
		
	public void doReport();
	
	public void doVote(String action_type,boolean isVoted);
	
	public boolean isReport();
	
	public void setReport(boolean state);
	
	public boolean isInfavorite();
	
	public boolean isVoted();
	
	public void setInFavorite(boolean state);
	
	public int getCommentCount();
	
	public void setCommonCount(int count);
	
	public CollectionItemBean getCollection();
	
}
