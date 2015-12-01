/**
 * app:kuibu
 * @calss : KuibuMainActivity
 * @author: esli
 * @version: v-1.0
 * @date 2015/1/18
 * */

package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.NetUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.DrawerListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.LoginPresenterImpl;
import com.kuibu.module.presenter.interfaces.LoginPresenter;
import com.kuibu.ui.fragment.AlbumFragment;
import com.kuibu.ui.fragment.ExplorePageFragment;
import com.kuibu.ui.fragment.FavoriteBoxFragment;
import com.kuibu.ui.fragment.FollowedPageFragment;
import com.kuibu.ui.fragment.HomePageFragment;
import com.kuibu.ui.fragment.NavigationDrawerFragment;
import com.kuibu.ui.view.interfaces.LoginView;

public class KuibuMainActivity extends BaseActivity 
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, LoginView{
	
	private static final int PROGRESS_LEN  = 10 ;
	private static final int DEFAULT_POSITINO = 1;
	
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Fragment currentFragment;
	private Fragment lastFragment;
	private LoginPresenter loginPresenter ; 	
	private MenuItem mLogoutMenu,mNotifyMenu;
	private int mCurposition;	
	private AlertDialog mDialogLogin; 
	private ActionProcessButton  mBtnLogin; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kuibu_main);
		loginPresenter = new LoginPresenterImpl(this);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));	
		loginPresenter.autoLogin();		
		if (savedInstanceState != null) {		
			//#bug fragment overlay
			mCurposition = savedInstanceState.getInt(StaticValue.TAG_VLAUE.DRAWER_POSITION);
			mNavigationDrawerFragment.selectItem(mCurposition);		
			
		}else{//first launch 	
			mNavigationDrawerFragment.selectItem(DEFAULT_POSITINO);
			boolean bWithDlg = getIntent().getBooleanExtra(StaticValue.MAINWITHDLG, false);			
			if(bWithDlg){
				mNavigationDrawerFragment.selectItem(0);
			}
			if(!NetUtils.isNetworkAvailable(this)){
				Toast.makeText(this, getString(R.string.poor_net_state), 
						Toast.LENGTH_LONG).show();
			}		
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getSupportActionBar().setElevation(0); //remove actionbar shadow 
		}		
	}
	
	@Override
	public void onNavigationDrawerItemSelected(int position , String title, String tag) {
		if (Constants.Tag.LOGIN.equals(tag)) { 
			if (!Session.getSession().isLogin()) {
				showLoginView();
				return;
			} else {
				Intent intent = new Intent(this, UserInfoActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID, Session.getSession().getuId());
				startActivityForResult(intent,
						StaticValue.RequestCode.USER_INFO_RESULT_CODE);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				return;
			}
		} else if (Constants.Tag.REGISTER.equals(tag)) { // up
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			return;
		}else if(Constants.Tag.SETTING.equals(tag)){
			Intent intent = new Intent(this,SettingsActivity.class);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				
			} else {
				startActivityForResult(intent, StaticValue.RequestCode.REQ_CODE_SETTING);
			}
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			return ;
		}
		mCurposition = position ; 
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		currentFragment = fragmentManager.findFragmentByTag(tag);
		if (currentFragment == null) {
			if (Constants.Tag.COLLECT.equals(tag)) {
				currentFragment = new FavoriteBoxFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				args.putString(StaticValue.USERINFO.USER_ID, Session.getSession().getuId());
				currentFragment.setArguments(args);
			} else if (Constants.Tag.DRAFT.equals(tag)) {
				currentFragment = new AlbumFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			} else if(Constants.Tag.HOME.equals(tag)){
				currentFragment = new HomePageFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			}else if(Constants.Tag.FOCUS.equals(tag)){
				currentFragment = new FollowedPageFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			} else if(Constants.Tag.EXPLORE.equals(tag)){
				currentFragment = new ExplorePageFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			}
			ft.add(R.id.container, currentFragment, tag);
		}

		if (lastFragment != null) {
			ft.hide(lastFragment);
		}
		if (currentFragment.isDetached()) {
			ft.attach(currentFragment);
		}		
		ft.show(currentFragment);
		lastFragment = currentFragment;		
		ft.commit();
		setTitle(title);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		mLogoutMenu = menu.findItem(R.id.action_logout);
		mNotifyMenu = menu.findItem(R.id.action_notify);
		if(!Session.getSession().isLogin()){			
			mNotifyMenu.setVisible(false);
		}else{
			mNotifyMenu.setVisible(true);
		}
		if(!Session.getSession().isLogin()){
			mLogoutMenu.setVisible(false);
		}
		return true;
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	//	super.onSaveInstanceState(outState); ////#bug fragment overlay
		outState.putInt(StaticValue.TAG_VLAUE.DRAWER_POSITION, mCurposition);
	}
	
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(mNavigationDrawerFragment.isDrawerOpen()){
			mNavigationDrawerFragment.closeDrawer();
		}
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intent = null;
		switch (id) {
		case R.id.action_notify://msg menu 
			intent = new Intent(this,NotifyActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			mNotifyMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_notify));
			return true;
		case R.id.action_search_for: //search menu 
			intent = new Intent(this,SearchViewActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			return true;
		case R.id.action_logout:
			doLogout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void doLogout()
	{
		mLogoutMenu.setVisible(false);	
		mNotifyMenu.setVisible(false);
		Session.getSession().setLogin(false);
		//remove fragments cached 
		String[] tags = getResources().getStringArray(R.array.drawer_item_logout_tag);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		for(int i =0 ;i<tags.length;i++){
			Fragment fragment = fragmentManager.findFragmentByTag(tags[i]);
			if(fragment !=null ){
				ft.remove(fragment);
			}
		}		
		ft.commit();		
		KuibuApplication.getInstance().getPersistentCookieStore().clear(); 
	    switchDrawerList(false);
		loginPresenter.logout();
	}
		
	private boolean doubleBackToExitPressedOnce; //only used here 		
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
            super.onBackPressed();          
            return;
        }
		if(mNavigationDrawerFragment.isDrawerOpen()){
			mNavigationDrawerFragment.closeDrawer();
		} else {			
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.press_again_exit),Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constants.LEAVE_PRESS_DELAY);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case StaticValue.RequestCode.REQ_CODE_SETTING:				
				if (isDarkTheme != PreferencesUtils.getBooleanByDefault(this,
						StaticValue.PrefKey.DARK_THEME_KEY, false)) {
					recreateActivity();
				}
				break;
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public void showLoginView() {
		LinearLayout loginLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.login_dialog,null);
		final EditText userEmail = (EditText) loginLayout.findViewById(R.id.login_user_email);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String account = pref.getString(StaticValue.PrefKey.LOGIN_ACCOUNT, null);
		
		if(!TextUtils.isEmpty(account)){
			userEmail.setText(account);
		}
		
		final EditText userPwd = (EditText) loginLayout.findViewById(R.id.login_user_pwd);
		mBtnLogin = (ActionProcessButton)loginLayout.findViewById(R.id.btnLogIn);
		mBtnLogin.setMode(ActionProcessButton.Mode.ENDLESS);		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle(getString(R.string.login)).setView(loginLayout);
		mDialogLogin = builder.show();
		mBtnLogin.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				String email = userEmail.getText().toString().trim();
				String pwd = userPwd.getText().toString().trim();				
				if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)){
					Toast.makeText(getApplicationContext(), getString(R.string.input_account),
							Toast.LENGTH_SHORT).show();
				}else{
					mBtnLogin.setProgress(PROGRESS_LEN);
					loginPresenter.login(email, pwd);
				}
			}
		});
	}

	@Override
	public void switchDrawerList(boolean loginState) {
        Map<String,Object> params = new HashMap<String,Object>();
        List<DrawerListItem> newData = new ArrayList<DrawerListItem>();
        String[] itemTitle = null ; 
        String[] itemTag = null ;
        int[] iconRes;
		if (loginState) {
			itemTitle = getResources().getStringArray(
					R.array.drawer_item_title_login);
			itemTag = getResources().getStringArray(
					R.array.drawer_item_tag_login);
			int[] itemIconRes = { R.drawable.ic_drawer_home,
					R.drawable.ic_drawer_explore, R.drawable.ic_drawer_follow,
					R.drawable.ic_drawer_collect, R.drawable.ic_drawer_draft,
					R.drawable.ic_drawer_setting };
			iconRes = itemIconRes; 
		}else{			
			itemTitle = getResources().getStringArray(R.array.drawer_item_title_nolgoin);
	        itemTag = getResources().getStringArray(R.array.drawer_item_tag_nologin);
	        int[] itemIconRes = {
	        		R.drawable.ic_drawer_register,
	        		R.drawable.ic_drawer_explore,
	        		R.drawable.ic_drawer_setting
	        };
	        iconRes = itemIconRes; 
		}
		for (int i = 0; i < itemTitle.length; i++) {
			@SuppressWarnings("deprecation")
			DrawerListItem item = new DrawerListItem(getResources().
					getDrawable(iconRes[i]),itemTitle[i],itemTag[i]);
			newData.add(item);
		}
        params.put("itemData", newData);
        mNavigationDrawerFragment.updateDrawerList(params);
		mNavigationDrawerFragment.selectItem(DEFAULT_POSITINO);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setMsgMenuIcon(boolean state) {
		if(state){
			mNotifyMenu.setIcon(getResources().getDrawable
					(R.drawable.ic_action_notify_active));
		}else{
			mNotifyMenu.setIcon(getResources().getDrawable
					(R.drawable.ic_action_notify));
		}		
	}

	@Override
	public void hideLoginView() {
		if(mDialogLogin !=null){
			mDialogLogin.dismiss();
		}
	}

	@Override
	public void setLoginBtnProgress(int progress) {
		if(mBtnLogin !=null){
			mBtnLogin.setProgress(progress);
		}
	}	
}
