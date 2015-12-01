package com.kuibu.ui.view.interfaces;

import java.util.List;

import android.os.Bundle;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.TopicItemBean;

public interface FollowedTopicView {
	
	public void refreshList(List<TopicItemBean> data);	
	
	public void stopRefresh();
	
	public void setMultiStateView(ViewState state);

	public Bundle getAuguments();
}
