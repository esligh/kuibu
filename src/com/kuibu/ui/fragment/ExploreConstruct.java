package com.kuibu.ui.fragment;

import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.IConstructFragment;

public final class ExploreConstruct 
	implements IConstructFragment{
	
	@Override
	public BaseFragment newInstance(String tag) {
		BaseFragment fragment = null; 
		if(StaticValue.TAB_PAGE.TAB_PAGE_RANK.equals(tag)){
			fragment = new ExploreRankFragment();
		}else if (StaticValue.TAB_PAGE.TAB_PAGE_HOT.equals(tag)){
			fragment = new ExploreFavoriteFragment();
		}else if (StaticValue.TAB_PAGE.TAB_PAGE_RECOMMEND.equals(tag)) {
			fragment = new ExploreRecommendFragment();
		}
		return fragment;
	}
	
}
