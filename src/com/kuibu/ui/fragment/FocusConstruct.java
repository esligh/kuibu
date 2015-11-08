package com.kuibu.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.IConstructFragment;

public final class FocusConstruct implements
		IConstructFragment {
	
	@Override
	public Fragment newInstance(String tag) {		
		Fragment  fragment = null; 
		if(StaticValue.TAB_PAGE.TAB_PAGE_FOCUS_COLLECT.equals(tag)){
			fragment = new FocusCollectPackFragment();
		}else if(StaticValue.TAB_PAGE.TAB_PAGE_FOCUS_TOPIC.equals(tag)){			
			fragment = new FocusTopicFragment();
			Bundle bundle = new Bundle();
			bundle.putString("uid", Session.getSession().getuId());
			fragment.setArguments(bundle);
		}
//		else if(tag.equals("focus_column")){
//			fragment = new FocusColumnFragment();
//		}		
		return fragment;
	}
	
}
