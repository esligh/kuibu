package com.kuibu.ui.fragment;

import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.IConstructFragment;

public class NotifyConstruct 
		implements IConstructFragment{
	
	@Override
	public BaseFragment newInstance(String tag) {
		// TODO Auto-generated method stub
		BaseFragment fragment = null; 
		if(StaticValue.TAB_PAGE.TAB_PAGE_COMMENT.equals(tag)){
			fragment = new NotifyCommentFragment();
		}else if (StaticValue.TAB_PAGE.TAB_PAGE_MESSAGE.equals(tag)){
			fragment = new NotifyMessageFragment();
		}
		return fragment;
	}

}
