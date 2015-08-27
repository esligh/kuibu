/**
 * app:kuibu
 * @calss : KuibuMainActivity
 * @author: esli
 * @version: v-1.0
 * @date 2015/1/18
 * */

package com.kuibu.module.activity;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.NetUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.DrawerListItem;
import com.kuibu.module.adapter.NavigationDrawerFragment;
import com.kuibu.module.dlg.LoginDialog;
import com.kuibu.module.dlg.LoginDialog.OnLoginLisener;
import com.kuibu.module.fragment.CollectionMainFragment;
import com.kuibu.module.fragment.ExplorePageFragment;
import com.kuibu.module.fragment.FavoriteBoxFragment;
import com.kuibu.module.fragment.FocusPageFragment;
import com.kuibu.module.fragment.HomePageFragment;



public class KuibuMainActivity extends BaseActivity 
	implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnLoginLisener {
	
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Fragment currentFragment;
	private Fragment lastFragment;
	private LoginDialog mLoginDlg = null;
	private static final int DEFAULT_POSITINO = 1; //登录状态抽屉选项的默认位置
	private MenuItem mLogoutMenu,mNotifyMenu;
	private int mCurposition = 0  ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kuibu_main);

		mLoginDlg = new LoginDialog(this);
		mLoginDlg.setOnLoginLisener(this);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));	
		getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		mLoginDlg.autoLogin(); //自动登录
		
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
			if(Session.getSession().isLogin()){
				try {
					JSONObject obj = new JSONObject();
					obj.put("uid", Session.getSession().getuId());
					obj.put("name", Session.getSession().getuName());
					
					KuibuApplication.getSocketIoInstance().SetUp();
					KuibuApplication.getSocketIoInstance().getSocketIO().
					emit(StaticValue.EVENT.LOGIN_EVENT, obj);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(!NetUtils.isNetworkAvailable(this)){
				Toast.makeText(this, getString(R.string.poor_net_state), 
						Toast.LENGTH_LONG).show();
			}else{ 			
		//		this.startService(new Intent(this,HeartBeatService.class));
			}		
		}
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getSupportActionBar().setElevation(0); //remove actionbar shadow 
		}
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	//	this.startService(new Intent(this,HeartBeatService.class));
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	//	this.stopService(new Intent(this,HeartBeatService.class));
	}


	@Override
	public void onNavigationDrawerItemSelected(int position , String title, String tag) {
		if (Constants.Tag.LOGIN.equals(tag)) { 
			if (mLoginDlg == null)
				return;
			if (!Session.getSession().isLogin()) {
				mLoginDlg.show();
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
				//startActivityForResult(new Intent(this, PrefsActivity.class), REQUESTCODE_SETTING);
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
				currentFragment = new CollectionMainFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			} else if(Constants.Tag.HOME.equals(tag)){
				currentFragment = new HomePageFragment();
				Bundle args = new Bundle();
				args.putString(StaticValue.TAG_VLAUE.ARG_SECTION_TAG, tag);
				currentFragment.setArguments(args);
			}else if(Constants.Tag.FOCUS.equals(tag)){
				currentFragment = new FocusPageFragment();
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
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			restoreActionBar();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intent = null;
		switch (id) {
		case R.id.action_notify://msg menu 
			intent = new Intent(this,NotifyMessageActivity.class);
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

	
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.launch_main, container,
					false);
			return rootView;
		}
	}

	/* (non-Javadoc)
	 * @description: 登录完成回调接口
	 * @see com.kuibu.module.dlg.LoginDialog.OnLoginLisener#onLoginComplete(java.util.Map)
	 */
	@Override
	public void onLoginComplete(Map<String,Object> params) {
		String errCode = (String)params.get("errCode");
		if (StaticValue.RESPONSE_STATUS.LOGIN_SUCCESS.equals(errCode)) {
			if(!Boolean.valueOf((String)params.get("isAuto"))){
				mNotifyMenu.setVisible(true);
				mLogoutMenu.setVisible(true);
			}
			//登录成功，准备切换DrawerList
			String[] itemTitle = getResources().getStringArray(
					R.array.drawer_item_title_login);
			String[] itemTag = getResources().getStringArray(
					R.array.drawer_item_tag_login);

			int[] itemIconRes = { R.drawable.ic_drawer_home,
					R.drawable.ic_drawer_explore, R.drawable.ic_drawer_follow,
					R.drawable.ic_drawer_collect, R.drawable.ic_drawer_draft,
					R.drawable.ic_drawer_setting };

			List<DrawerListItem> newData = new ArrayList<DrawerListItem>();
			for (int i = 0; i < itemTitle.length; i++) {
				DrawerListItem item = new DrawerListItem(getResources()
						.getDrawable(itemIconRes[i]), itemTitle[i], itemTag[i]);
				newData.add(item);
			}
			params.put("itemData", newData);			
			mNavigationDrawerFragment.updateDrawerList(params);
		}
		mNavigationDrawerFragment.selectItem(DEFAULT_POSITINO);
		//login success 
		login();
	}

	
	/**
	 * 注销当前用户  
	 */
	
	private void doLogout()
	{
		mLogoutMenu.setVisible(false);	
		mNotifyMenu.setVisible(false);
		Session.getSession().setLogin(false);
		//移除涉及到缓存的Framgent
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
		KuibuApplication.getInstance().getPersistentCookieStore().clear(); //清理cookie
		//准备切换DrawerList
		List<DrawerListItem> newData = new ArrayList<DrawerListItem>();
		String[] itemTitle = getResources().getStringArray(R.array.drawer_item_title_nolgoin);
        String[] itemTag = getResources().getStringArray(R.array.drawer_item_tag_nologin);
        int[] itemIconRes = {
        		R.drawable.ic_drawer_register,
        		R.drawable.ic_drawer_explore,
        		R.drawable.ic_drawer_setting
        };
        for (int i = 0; i < itemTitle.length; i++) {
			DrawerListItem item = new DrawerListItem(getResources().getDrawable(itemIconRes[i]),
					itemTitle[i],itemTag[i]);
			newData.add(item);
		}

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("itemData", newData);
        params.put("uName", this.getString(R.string.login));
        mNavigationDrawerFragment.updateDrawerList(params);
		mNavigationDrawerFragment.selectItem(DEFAULT_POSITINO);
		logout();
	}
	
	private void login()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/login_over").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						//logout success
						boolean have_message = response.getBoolean("have_message");
						if(have_message){
							mNotifyMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_notify_active));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void logout()
	{
		if(!Session.getSession().isLogin())
			return ;
		if(KuibuApplication.getSocketIoInstance().
				getSocketIO()!=null){
			if(KuibuApplication.getSocketIoInstance().getSocketIO().isConnected()){
				try {
					JSONObject obj = new JSONObject();
					obj.put("uid", Session.getSession().getuId());
					KuibuApplication.getSocketIoInstance().getSocketIO().
						emit(StaticValue.EVENT.LOGOUT_EVENT, obj);
				} catch (JSONException e) {
					e.printStackTrace();
				} 				
				KuibuApplication.getSocketIoInstance().getSocketIO().disconnect();
			}			
		}
				
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/user_logout").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						//logout success
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private boolean doubleBackToExitPressedOnce;	
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
	
	@Override
	public void eventResponse(JSONObject entity)
	{
		try {			
		    String type = entity.getString("event_type");
			if(StaticValue.EVENT.TYPE_NEWLETTERS.equals(type)){ 
				Toast.makeText(this,getString(R.string.received_letter) , 
						Toast.LENGTH_LONG).show();
				mNotifyMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_notify_active));
			}else if(StaticValue.EVENT.TYPE_NEWCOMMETN.equals(type)){
				Toast.makeText(this, getString(R.string.received_comment), 
						Toast.LENGTH_LONG).show();
			}else if(StaticValue.EVENT.TYPE_KEEPALIVE.equals(type)){
				Toast.makeText(this, "HELLO", Toast.LENGTH_SHORT).show();
			}				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();	
				
	}

	@Override  
	public void onTrimMemory(int level) {  
	    super.onTrimMemory(level);  
	    switch (level) {  
	    case TRIM_MEMORY_UI_HIDDEN:  
	        
	        break;  
	    }  
	} 
}
