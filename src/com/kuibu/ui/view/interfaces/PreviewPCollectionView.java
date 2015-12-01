package com.kuibu.ui.view.interfaces;

import android.content.Context;
import android.content.Intent;

public interface PreviewPCollectionView {

	public Intent getDataIntent();
	
	public Context getInstance();
	
	public void setPubMenuVisible(boolean visible);
	
	public void setCollectionTitle(String title);

	public void setCollectionDesc(String desc);
	
	public void setCollectionDescVisible(int visibility);
		
	public void setImage(String url);
	
	public void setImageRotationBy(int degree);

	public void setRotateBtnIcon(int resId);
	
	public void setImageRotationTo(int degree);
}
