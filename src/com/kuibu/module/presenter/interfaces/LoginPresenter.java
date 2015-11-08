package com.kuibu.module.presenter.interfaces;

public interface  LoginPresenter {		
	
	public int login(String name,String pwd); //登录接口
	public void autoLogin(); //自动登录接口
	public boolean validate(String name,String pwd); //账号的校验 
	public int logout();
	public void setUpSocketIoClient(); //建立socketio 客户
	
}
