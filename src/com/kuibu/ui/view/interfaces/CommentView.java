package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import android.content.Intent;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.CommentItemBean;

public interface CommentView {

	public Intent getDataIntent();
		
	public void refreshList(List<CommentItemBean> data);
	
	public void setMultiStateView(ViewState state);

	public void stopRefresh();

	public String getComment();
	
	public void setCancelMenuVisibility(boolean visible);
	
	public boolean isCancelMenuVisible();
	
	public void setContentHint(String hint);
	
	public Map<String,String> getContentTag();
}
