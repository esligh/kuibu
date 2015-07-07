package com.kuibu.module.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.data.global.Session;
import com.kuibu.module.iterf.IConstructFragment;

public final class FocusConstructFrament extends Fragment implements
		IConstructFragment {
	
	@Override
	public Fragment newInstance(String tag) {		
		Fragment  fragment = null; 
		if(tag.equals("focus_collect")){
			fragment = new FocusCollectPackFragment();
		}else if(tag.equals("focus_topic")){			
			fragment = new FocusTopicFragment();
			Bundle bundle = new Bundle();
			bundle.putString("uid", Session.getSession().getuId());
			fragment.setArguments(bundle);
		}else if(tag.equals("focus_column")){
			fragment = new FocusColumnFragment();
		}		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		// ((ViewGroup)rootView.getParent()).removeView(rootView);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
