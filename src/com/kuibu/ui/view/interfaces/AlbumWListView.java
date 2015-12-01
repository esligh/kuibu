package com.kuibu.ui.view.interfaces;

import java.util.List;

import com.kuibu.model.entity.CollectionBean;

import android.content.Context;
import android.content.Intent;

public interface AlbumWListView {
	
	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void refreshList(List<CollectionBean> data);
	
	public void refreshOptionMenu();
	
	public void setButtonVisible(int visibility);
	
	public void resetAdapter(List<CollectionBean> data ,boolean isMulChoice);
	
	public void setBarTitle(String title);
	
}
