package com.kuibu.model.interfaces;


/**
 * login model*/
public interface LoginModel {
	
	public void doLogin(String name,String pwd); 
	
	public void doLogout() ;
	
	public boolean doAutoLogin();
}
