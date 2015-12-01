package com.kuibu.ui.view.interfaces;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.kuibu.model.entity.TopicItemBean;

public interface CreateAlbumView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void refreshTopicList(List<TopicItemBean> data);
	
	public void close();
	
	public void setBarTitle(String title);
		
	public void setAlbumName(String name);
	
	public void setAlbumDesc(String desc);
	
	public void setAlbumType(int id);
	
	public void setTopicTag(String[] tags);
	
	public void setAlbumPrivate(boolean bprivate);
	
	
}
