package com.kuibu.module.presenter.interfaces;

public interface SearchViewPresenter {
	public void requestContent(String query);
	public void requestUsers(String query);
	public void requestTopics(String query);
	
	public void clearContent();
	public void clearUsers();
	public void clearTopics();
	
	public void cancelRequest();
}
