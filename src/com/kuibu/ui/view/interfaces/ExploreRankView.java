package com.kuibu.ui.view.interfaces;

import java.util.List;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.MateListItem;

public interface ExploreRankView {

	public void refreshList(List<MateListItem> data);	
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);

}
