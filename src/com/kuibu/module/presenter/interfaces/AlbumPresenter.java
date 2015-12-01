package com.kuibu.module.presenter.interfaces;

import com.kuibu.model.entity.CollectPackBean;

public interface AlbumPresenter {

	public void delAlbum(CollectPackBean item);
	
	public void delLocalAlbum(int position);
	
	public void queryAlbum();
	
	public void closeDbConn();
	
}
