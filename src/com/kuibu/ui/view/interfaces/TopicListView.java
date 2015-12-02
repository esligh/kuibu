package com.kuibu.ui.view.interfaces;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.kuibu.model.entity.TopicItemBean;

public interface TopicListView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void refreshList(List<TopicItemBean> data);
	
}
