package com.kuibu.module.presenter.interfaces;

public interface  LoginPresenter {		
	
	public void login(String name,String pwd); //登录接口
	
	public void autoLogin(); //自动登录接口
		
	public void logout();
}
