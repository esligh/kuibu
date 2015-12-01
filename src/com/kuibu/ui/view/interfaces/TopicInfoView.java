package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

public interface TopicInfoView {
	public Intent getDataIntent();
	
	public Context getInstance();

	public void refreshList(List<Map<String,Object>> data);
	
	public void setTopicName(String name);
	
	public void setTopicDesc(String desc);
	
	public void setTopicPic(String url);
	
	public void setFollowCount(String count);
	
	public void setFollowBtnColor(int color);
	
	public void setFollowBtnText(String text);
	
	public String getFollowCount();
	
	public void setBarTitle(String title);
}
