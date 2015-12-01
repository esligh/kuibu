package com.kuibu.ui.view.interfaces;

import android.support.v4.app.Fragment;

public interface UserHomeView {

	public void setFollowBtnColor(int color);
	public void setFollowBtnText(String text);
	public void setFollowMeCount(String text);
	public void setMeFollowCount(String text);
	
	public void setPackCount(String text);
	
	public void setEmail(String email);
	public void setProfession(String profession);
	
	public void setResidence(String residence);
	
	public String getFollowCount();
	
	public Fragment getFragment();
	
}
