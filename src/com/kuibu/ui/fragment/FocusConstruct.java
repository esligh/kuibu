package com.kuibu.ui.fragment;

import android.os.Bundle;

import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.IConstructFragment;

public final class FocusConstruct implements
		IConstructFragment {
	
	@Override
	public BaseFragment newInstance(String tag) {		
		BaseFragment  fragment = null; 
		if(StaticValue.TAB_PAGE.TAB_PAGE_FOCUS_COLLECT.equals(tag)){
			fragment = new FollowedPackFragment();
		}else if(StaticValue.TAB_PAGE.TAB_PAGE_FOCUS_TOPIC.equals(tag)){			
			fragment = new FollowedTopicFragment();
			Bundle bundle = new Bundle();
			bundle.putString("uid", Session.getSession().getuId());
			fragment.setArguments(bundle);
		}
		return fragment;
	}
	
}
