package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;

import com.kuibu.custom.widget.MultiStateView.ViewState;

public interface FavoriteBoxView {

	public Intent getDataIntent();
	
	public Bundle getDataArguments();

    public void refreshList(List<Map<String,String>> data);	
	
	public void setMultiStateView(ViewState state);
	
		
}
