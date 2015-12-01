package com.kuibu.ui.view.interfaces;

import java.util.List;

import com.kuibu.model.entity.CollectPackBean;

import android.content.Context;

public interface AlbumView {
	
	public Context getInstance();

	public void refreshList(List<CollectPackBean> data);
}
