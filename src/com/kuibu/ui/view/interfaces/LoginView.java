package com.kuibu.ui.view.interfaces;

/**
 * login view 的接口类*/
public interface LoginView {	
	
	public void showLoginView(); //显示登录界面
	public void showError();  //显示出错信息	
	public void switchDrawerList(boolean loginState); //切换抽屉列表
	public void setMsgMenuIcon(int resId);
	public void setMsgMenuVisibility();
	public void setLogoutMenuVisibility();
		
	
}
