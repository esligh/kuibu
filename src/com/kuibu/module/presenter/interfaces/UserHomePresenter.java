package com.kuibu.module.presenter.interfaces;

import java.util.Map;

public interface UserHomePresenter {

	public Map<String,Object> getUserinfo();
	public void followCollector();
	
	public void loadUserinfo();
}
