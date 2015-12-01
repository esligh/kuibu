package com.kuibu.ui.view.interfaces;

import java.util.List;

import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;

import android.content.Intent;

public interface FavoriteBoxInfoView {

	public Intent getDataIntent();
	
	public void setBoxTitle(String title);
	
	public void setBoxDesc(String desc);
	
	public void setFollowCount(String count);
	
	public void setFollowBtnColor(int color);
	
	public void setFollowBtnText(String text);
	
	public void refreshPList(List<CollectionBean> data);
	
	public void refreshWList(List<CollectionItemBean> data);
	
	
	public void setUserName(String name);
	
	public void setUserPic(String url);
	
	public void setUserSignature(String signature);
	
	public void setBoxDescVisible(int visibility);
	
	public void setMultiStateView(ViewState state);

	public void loadComplete();
	
	public void setFooterViewVisible(int visibility);
	
	public String getFollowCount();
	
	public void setBarTitle(String title);
	
}
