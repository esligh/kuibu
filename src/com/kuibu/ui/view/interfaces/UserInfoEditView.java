package com.kuibu.ui.view.interfaces;

import android.content.Context;
import android.content.Intent;

public interface UserInfoEditView {
	
	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void setUserName(String name);
	
	public void setUserSignature(String signature);
	
	public void setProfession(String profession);
	
	public void setResidence(String residence);
	
	public void setUserPhoto(String url);
	
	public void setEducation(String edu);
	
	public void switchSex(String sex);
	
	public void setUserPhoto(int resId);
	
}
