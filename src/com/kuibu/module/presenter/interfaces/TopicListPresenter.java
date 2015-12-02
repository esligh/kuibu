package com.kuibu.module.presenter.interfaces;

import java.util.List;

import com.kuibu.model.entity.TopicItemBean;

public interface TopicListPresenter {
	
	public void loadTopicList();
	
	public List<TopicItemBean> getListData();
}
