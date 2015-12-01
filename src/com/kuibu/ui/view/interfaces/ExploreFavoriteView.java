package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import com.kuibu.custom.widget.MultiStateView.ViewState;

public interface ExploreFavoriteView {
	public void refreshList(List<Map<String, String>> data);	
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);
}
