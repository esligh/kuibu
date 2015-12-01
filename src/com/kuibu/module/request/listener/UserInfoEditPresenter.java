package com.kuibu.module.request.listener;

import com.kuibu.model.entity.UserInfoBean;

public interface UserInfoEditPresenter {

	public void loadUserInfo();
	
	public void uploadUserPic();
	
	public void updateUserInfo();
	
	public UserInfoBean getUserInfo();
	
}
