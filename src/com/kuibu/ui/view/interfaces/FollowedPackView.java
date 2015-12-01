package com.kuibu.ui.view.interfaces;

import java.util.List;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.CollectPackItemBean;

public interface FollowedPackView {
	public void refreshList(List<CollectPackItemBean> data);	
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);
	
}
