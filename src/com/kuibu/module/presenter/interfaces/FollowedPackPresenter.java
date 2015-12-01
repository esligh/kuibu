package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CollectPackItemBean;

public interface FollowedPackPresenter {
	public void loadFollowedPackList();

	public CollectPackItemBean getDataItem(int position);
}
