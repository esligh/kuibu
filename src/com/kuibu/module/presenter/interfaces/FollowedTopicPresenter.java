package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.TopicItemBean;

public interface FollowedTopicPresenter {
	
	public void loadFollowedTopicList();
	
	public TopicItemBean getDataItem(int position);
}
