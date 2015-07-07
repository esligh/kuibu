package com.kuibu.module.fragment;

import android.support.v4.app.Fragment;

import com.kuibu.module.iterf.IConstructFragment;

public class NotifyConstructFragment extends Fragment 
		implements IConstructFragment{

	@Override
	public Fragment newInstance(String tag) {
		// TODO Auto-generated method stub
		Fragment fragment = null; 
		if(tag.equals("comment")){
			fragment = new NotifyCommentFragment();
		}else if (tag.equals("message")){
			fragment = new NotifyMessageFragment();
		}
		return fragment;
	}

}
