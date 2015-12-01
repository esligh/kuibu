package com.kuibu.ui.view.interfaces;

import android.content.Context;
import android.content.Intent;

public interface PreviewWCollectionView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void setPubMenuVisible(boolean visible);
	
	public void setCollectionTitle(String title);
	
	public void setPageContent(String content);
	
	public void setCover(String cover);
}
