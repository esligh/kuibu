package com.kuibu.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.fragment.UserInfoContentFragment;

public class UserInfoActivity extends BaseActivity {	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if(fragment == null){
			fragment = new UserInfoContentFragment();
			fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
		}
		setTitle(getString(R.string.home_page));
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);	
	        String uid = getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
	        if(Session.getSession().isLogin()){
	        	if(Session.getSession().getuId().equals(uid)){
	        		MenuItem edit=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
			        		StaticValue.MENU_ITEM.EDIT_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,
			        		getString(R.string.action_modify));	        
			        edit.setIcon(getResources().getDrawable(R.drawable.ic_edit_light));  
			        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        	}else {
	        		MenuItem sendMsg = menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
	        				StaticValue.MENU_ITEM.SEND_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,
	        				getString(R.string.private_letter));
	        		sendMsg.setIcon(getResources().getDrawable(R.drawable.ic_action_message));
	        		sendMsg.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        	}     	
	        }   
	        MenuItem share=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
	        		StaticValue.MENU_ITEM.SHARE_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID+1,
	        		getString(R.string.share));
	        share.setIcon(getResources().getDrawable(R.drawable.ic_action_share));
	        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        return true;
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 Intent intent = null;  
	        switch(item.getItemId()){
	        	case android.R.id.home:
	        		this.onBackPressed();
	        		break;
	        	case StaticValue.MENU_ITEM.EDIT_ID:
	        		intent = new Intent(UserInfoActivity.this,UserInfoEditActivity.class);
	        		startActivity(intent);
	        		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	        		break;
	        	case StaticValue.MENU_ITEM.SHARE_ID:
	        		Toast.makeText(getApplicationContext(), "开发中...", Toast.LENGTH_SHORT).show();
	        		break;
	        	case StaticValue.MENU_ITEM.SEND_ID:
	        		if(Session.getSession().isLogin()){
		        		String uid = getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
		        		intent = new Intent(UserInfoActivity.this,SendMessageActivity.class);
		        		intent.putExtra("sender_id", uid);
		        		startActivity(intent);
		        		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	        		}else{
	        			Toast.makeText(this,getString(R.string.need_login), 
	        					Toast.LENGTH_SHORT).show();
	        		}
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
