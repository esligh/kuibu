/**
 * app:kuibu
 * @calss : KuibuMainActivity
 * @author: esli
 * @version: v-1.0
 * @date 2015/1/18
 * */

package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.kuibu.common.utils.ACache;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.BadgeView;
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
import com.kuibu.module.net.NetUtils;

public class KuibuMainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnLoginLisener {
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Fragment currentFragment;
	private Fragment lastFragment;
	private LoginDialog mLoginDlg = null;
	private static final int NOLOGIN_DEFAULT_POSITION = 2; //未登录状态抽屉选项的默认位置
	private static final int LOGIN_DEFAULT_POSITINO = 1; //登录状态抽屉选项的默认位置
	private MenuItem mLogoutMenu,mNotifyMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kuibu_main);
		if (savedInstanceState != null) {
			
		}else{
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new PlaceholderFragment()).commit();
		}
		mLoginDlg = new LoginDialog(this);
		mLoginDlg.setOnLoginLisener(this);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavigationDrawerFragment.selectItem(NOLOGIN_DEFAULT_POSITION);
		
		mLoginDlg.autoLogin(); //自动登录
		boolean bWithDlg = getIntent().getBooleanExtra(StaticValue.MAINWITHDLG, false);
		if(bWithDlg){
			mNavigationDrawerFragment.selectItem(0);
		}
		int netType = NetUtils.getAPNType(this);
		if(netType == NetUtils.NO_NET){
			Toast.makeText(this, "当前网络状态不好.", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onNavigationDrawerItemSelected(String title, String tag) {
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
			//	startActivityForResult(new Intent(this, PrefsActivity.class), REQUESTCODE_SETTING);
			} else {
				startActivityForResult(intent, StaticValue.RequestCode.REQ_CODE_SETTING);
			}
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			return ;
		}
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
	//	ft.commitAllowingStateLoss(); // 处理IllegalStateException:can't perform
										// action after onSaveInstanceState.
										// detail @ http://stackoverflow.com/questions/7469082
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
			BadgeView badge = new BadgeView(this, mNotifyMenu.getActionView());
			badge.setText("12");
			badge.setBadgePosition(BadgeView.POSITION_BOTTOM_LEFT);
			badge.show();
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
		super.onSaveInstanceState(outState);		
	}
	
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(mNavigationDrawerFragment.isDrawerOpen()){
			mNavigationDrawerFragment.closeDrawer();
		}
        if(Session.getSession().isLogin()){
        	mNavigationDrawerFragment.selectItem(LOGIN_DEFAULT_POSITINO);
        }else{
        	mNavigationDrawerFragment.selectItem(NOLOGIN_DEFAULT_POSITION);
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
			return true;
		case R.id.action_search_for: //search menu 
			intent = new Intent(this,SearchViewActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			return true;
		case R.id.action_logout:
			do_logout();
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
			mNavigationDrawerFragment.selectItem(LOGIN_DEFAULT_POSITINO);			
		}
	}

	/**
	 * 注销当前用户  
	 */
	private void do_logout()
	{
		mLogoutMenu.setVisible(false);	
		mNotifyMenu.setVisible(false);
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
        params.put("uName", "登录");
        mNavigationDrawerFragment.updateDrawerList(params);
		mNavigationDrawerFragment.selectItem(NOLOGIN_DEFAULT_POSITION);
		
		//清理缓存 
		ACache aCache = ACache.get(this);
		aCache.clear();
		
		logout();
		Session.getSession().setLogin(false);
	}
	
	private void logout()
	{
		if(!Session.getSession().isLogin())
			return ;		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION
				+ "/user_logout";
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
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case StaticValue.RequestCode.REQ_CODE_SETTING:
				SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
				if (isDarkTheme != mPerferences.getBoolean("dark_theme", false)) {
					recreateActivity();
				}
				break;
		}
	}
		
	@Override
	public void eventResponse(JSONObject entity)
	{
		String type;
		try {			
			type = entity.getString("event_type");
			if(StaticValue.EVENT.TYPE_NEWLETTERS.equals(type)){ //private letter come.
				Toast.makeText(this, "收到新私信", Toast.LENGTH_LONG).show();
			}else if(StaticValue.EVENT.TYPE_NEWCOMMETN.equals(type)){
				Toast.makeText(this, "收到新评论", Toast.LENGTH_LONG).show();
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
