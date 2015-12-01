package com.kuibu.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.module.activity.R;
import com.kuibu.ui.fragment.NotifyTabPageFragment;

public class NotifyActivity extends BaseActivity{
		
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if(fragment == null){
			fragment = new NotifyTabPageFragment();
			fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()){
	        	case android.R.id.home:
	        		this.onBackPressed();
	        		break;
	        }
	        return super.onOptionsItemSelected(item);	 
	}	 
	 
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	

}
