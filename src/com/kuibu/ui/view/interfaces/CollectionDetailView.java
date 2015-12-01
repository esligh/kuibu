package com.kuibu.ui.view.interfaces;

import com.kuibu.custom.widget.MultiStateView.ViewState;

import android.content.Context;
import android.content.Intent;

public interface CollectionDetailView {
	
	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void setContent(String html);
	
	public void setToolsVisible(boolean visible);
	
	public void setCover(String url);
	
	public void setCommentCount(String count);
	
	public void setVoteCount(String count);
	
	public void setActionBarAlpha(int value);
	
	public void setMultiStateView(ViewState state);

	public String getCssFile();
	
	public void setVoteBtnDrawable(int resId);
	
	public void setFavMenuDrawable(int resId);
	
	public void setReportMenuRrawable(int resId);
}
