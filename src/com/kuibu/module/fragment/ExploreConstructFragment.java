package com.kuibu.module.fragment;

import android.support.v4.app.Fragment;

import com.kuibu.module.iterf.IConstructFragment;

public final class ExploreConstructFragment extends Fragment 
	implements IConstructFragment {
	
	@Override
	public Fragment newInstance(String tag) {
		Fragment fragment = null; 
		if(tag.equals("rank")){
			fragment = new ExploreRankFragment();
		}else if (tag.equals("hot")){
			fragment = new ExploreHotFragment();
		}else if (tag.equals("recommend")) {
			fragment = new ExploreRecommendFragment();
		}
		return fragment;
	}
	
}
