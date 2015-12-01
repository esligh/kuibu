package com.kuibu.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.data.global.StaticValue;
import com.kuibu.ui.fragment.FollowedTopicFragment;

public class UserTopicListActivity extends UserInfoFragmentActivity{
	
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		Fragment fragment = new FollowedTopicFragment();
		Bundle bundle = new Bundle();
		bundle.putString("uid", getIntent().getStringExtra(StaticValue.USERINFO.USER_ID));
		fragment.setArguments(bundle);
		return fragment ; 
	}
	
}
