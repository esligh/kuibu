package com.kuibu.module.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.kuibu.app.model.base.BaseActivity;

public abstract class UserInfoFragmentActivity extends BaseActivity {
	
	protected abstract Fragment createFragment();	
	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if(fragment == null){
			fragment = createFragment();
			fm.beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
		}
		
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
			// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
}
