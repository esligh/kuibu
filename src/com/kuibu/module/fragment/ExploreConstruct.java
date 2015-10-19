package com.kuibu.module.fragment;

import android.support.v4.app.Fragment;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.IConstructFragment;

public final class ExploreConstruct 
	implements IConstructFragment{
	
	@Override
	public Fragment newInstance(String tag) {
		Fragment fragment = null; 
		if(StaticValue.TAB_PAGE.TAB_PAGE_RANK.equals(tag)){
			fragment = new ExploreRankFragment();
		}else if (StaticValue.TAB_PAGE.TAB_PAGE_HOT.equals(tag)){
			fragment = new ExploreHotFragment();
		}else if (StaticValue.TAB_PAGE.TAB_PAGE_RECOMMEND.equals(tag)) {
			fragment = new ExploreRecommendFragment();
		}
		return fragment;
	}
	
}
