package com.kuibu.ui.view.interfaces;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.MateListItem;

public interface CollectionListView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void refreshList(List<MateListItem> datas);
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);
	
	public void setBarTitle(String title);
}
