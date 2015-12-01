package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;

public interface PackInfoView {
	public void loadComplete();
	public void setFooterVisible(int visibility);
	public void setMultiStateView(ViewState state);
	
	public void setPackInfoView(Map<String,String> info);
	public void setFollowCount();
	public void setBarTitle(String title);
	

	public void refreshCardView(List<CollectionBean> data);
	public void refreshListView(List<CollectionItemBean> data);
	
	public void showList(String type);
	
}
