package com.kuibu.ui.view.interfaces;

/**
 * @author ThinkPad
 */
public interface LoginView {		
	
	/**
	 * open dialog to login 
	 */
	public void showLoginView();
	
	/**
	 * hide dialog 
	 */
	public void hideLoginView();
	
	/**
	 * switch drawer list 
	 * @param loginState 
	 * true indicates state of login  
	 */
	public void switchDrawerList(boolean loginState);
	
	/**
	 * when user received messages the menu item need to update .  
	 */
	public void setMsgMenuIcon(boolean state);
	
	public void setLoginBtnProgress(int progress);
}
