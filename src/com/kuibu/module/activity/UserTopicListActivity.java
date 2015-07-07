package com.kuibu.module.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.fragment.FocusTopicFragment;

public class UserTopicListActivity extends UserInfoListFragmentActivity {
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		Fragment fragment = new FocusTopicFragment();
		Bundle bundle = new Bundle();
		bundle.putString("uid", getIntent().getStringExtra(StaticValue.USERINFO.USER_ID));
		fragment.setArguments(bundle);
		return fragment ; 
	}	
}
