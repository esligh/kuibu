package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import com.kuibu.custom.widget.MultiStateView.ViewState;

public interface LetterView {
	public void refreshList(List<Map<String,Object>> data);	
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);
}
