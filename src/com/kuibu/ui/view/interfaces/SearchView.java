package com.kuibu.ui.view.interfaces;

import java.util.List;
import java.util.Map;

import com.kuibu.model.entity.TopicItemBean;

public interface SearchView {

	public void refreshTopicList(List<TopicItemBean> data);
	public void refreshUserList(List<Map<String,Object>> data);
	public void refreshContentList(List<Map<String,String>> data);

	public void loadComplete();
}
