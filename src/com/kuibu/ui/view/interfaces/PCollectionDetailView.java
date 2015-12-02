package com.kuibu.ui.view.interfaces;

import android.content.Context;
import android.content.Intent;

import com.kuibu.custom.widget.MultiStateView.ViewState;

public interface PCollectionDetailView {
	
	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void setCollectionTitle(String title);
	
	public void setCollectionDesc(String desc);
	
	public void setToolsVisible(boolean visible);
	
	public void setImage(String url);
	
	public void setCommentCount(String count);
	
	public void setVoteCount(String count);
	
	public void setMultiStateView(ViewState state);

	public void setVoteBtnDrawable(int resId);
	
	public void setFavMenuDrawable(int resId);
	
	public void setReportMenuRrawable(int resId);
	
	public void setCollectionDescVisible(int visibility);
}
