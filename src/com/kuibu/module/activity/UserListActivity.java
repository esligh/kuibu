package com.kuibu.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.fragment.UserListFragment;
 
public class UserListActivity extends UserInfoFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("follow_who", getIntent().getStringExtra("follow_who")); 
		params.put("uid", getIntent().getStringExtra(StaticValue.USERINFO.USER_ID));
		return new UserListFragment(params);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
}
