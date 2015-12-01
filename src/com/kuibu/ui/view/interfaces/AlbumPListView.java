package com.kuibu.ui.view.interfaces;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.adapter.ImageGridAdapter;

public interface AlbumPListView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void refreshList(List<CollectionBean> data);
	
	public void refreshOptionMenu();
	
	public void setButtonVisible(int visibility);
	
	public void resetAdapter(List<CollectionBean> data ,boolean isMulChoice);
	
	public void setBarTitle(String title);
	
	public void stopRefresh();
	
	public ImageGridAdapter getAdapter();
	
	public void setPullEnable(boolean enable);
	
	
}
