package com.kuibu.module.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.DrawerListItem;
import com.kuibu.model.bean.UserInfoBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NavigationDrawerFragment extends Fragment {
    private static final String STATE_SELECTED_CUR_POSITION = "drawer_cur_position";
    private static final String STATE_SELECTED_LAST_POSITION = "drawer_last_position";

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean loginState = false; 
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private DrawerListAdapter drawerListAdapter=null;     
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private View headerView;      
    private TextView headerText ; 
    private ImageView headerIcon ; 
    private List<DrawerListItem> mData = new ArrayList<DrawerListItem>();
    
	private BroadcastReceiver userUpdateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			UserInfoBean info = (UserInfoBean)intent.getSerializableExtra(StaticValue.USERINFO.USERINFOENTITY);
			headerText.setText(info.getName());
			ImageLoader.getInstance().displayImage(info.getPhoto(), headerIcon);
		}		
	};
	
    public NavigationDrawerFragment() {
    }
    
    public boolean isLoginState() {
		return loginState;
	}

	public void setLoginState(boolean loginState) {
		this.loginState = loginState;
	}
	
	//for login 
	@SuppressWarnings("unchecked")
	public void updateDrawerList(Map<String,Object> params) {
		this.mData = (List<DrawerListItem>)params.get("itemData");
		headerText.setText((String)params.get("uName"));		
		if(Session.getSession().isLogin()){
			String url = Session.getSession().getuPic();
			if(TextUtils.isEmpty(url) || url.equals("null")){
					headerIcon.setImageResource(R.drawable.default_pic_avata);
			}else{
				ImageLoader.getInstance().displayImage(url, headerIcon);					
			}				
		}else{
			headerIcon.setImageResource(R.drawable.ic_drawer_login);
		}			
		drawerListAdapter.updateView(mData);
	}

	//end 		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());        
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_CUR_POSITION);
            mFromSavedInstanceState = true;
        }
		IntentFilter ifilterPickpic = new IntentFilter();
		ifilterPickpic.addAction(StaticValue.USER_INFO_UPDATE);
		getActivity().registerReceiver(userUpdateReceiver, ifilterPickpic);			
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        headerView = inflater.inflate(R.layout.list_header, null);
        headerText = (TextView)headerView.findViewById(R.id.drawerheader_title);
        headerIcon = (ImageView)headerView.findViewById(R.id.drawerheader_icon);
        headerView.setTag("login"); 
        mDrawerListView.addHeaderView(headerView);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        boolean isLogin = false ; 
        if(savedInstanceState!=null)
        	isLogin = savedInstanceState.getBoolean("isLogin", false);
    
        if(!isLogin){
        	String[]  itemTitle = getResources().getStringArray(R.array.drawer_item_title_nolgoin);
        	String[]  itemTag = getResources().getStringArray(R.array.drawer_item_tag_nologin);
            int[] itemIconRes = {
            		R.drawable.ic_drawer_register,
            		R.drawable.ic_drawer_explore,
            		R.drawable.ic_drawer_setting
            };           
            for (int i = 0; i < itemTitle.length; i++) {
    			DrawerListItem item = new DrawerListItem(getResources().getDrawable(itemIconRes[i]),
    					itemTitle[i],itemTag[i]);
    			mData.add(item);
    		}
        }else{
        	String[] itemTitle = getResources().getStringArray(
					R.array.drawer_item_title_login);
			String[] itemTag = getResources().getStringArray(
					R.array.drawer_item_tag_login);

			int[] itemIconRes = { R.drawable.ic_drawer_home,
					R.drawable.ic_drawer_explore, R.drawable.ic_drawer_follow,
					R.drawable.ic_drawer_collect, R.drawable.ic_drawer_draft,
					R.drawable.ic_drawer_setting };

			for (int i = 0; i < itemTitle.length; i++) {
				DrawerListItem item = new DrawerListItem(getResources()
						.getDrawable(itemIconRes[i]), itemTitle[i], itemTag[i]);
				mData.add(item);
			}
        }
        drawerListAdapter = new DrawerListAdapter(this.getActivity(), mData);
        mDrawerListView.setAdapter(drawerListAdapter);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }
    
    public void closeDrawer()
    {
    	mDrawerLayout.closeDrawer(mFragmentContainerView);
    }
    
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    
                mDrawerLayout,                    
                R.drawable.ic_drawer,             
                R.string.navigation_drawer_open,  
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
    	//		getActionBar().setIcon(R.drawable.ic_drawer);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); 
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
    	//		getActionBar().setIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }
                
                getActivity().supportInvalidateOptionsMenu(); 
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
        	if(mCurrentSelectedPosition == 0) {
        		mCallbacks.onNavigationDrawerItemSelected(0,getString(R.string.app_name),"login");
        		return;
        	}
            mCallbacks.onNavigationDrawerItemSelected(position,mData.get(position - 1).getTitle(),mData.get(position - 1).getTag());
        }
    }

    public int getCurrentPosition()
    {
    	return mCurrentSelectedPosition;
    }
   
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_CUR_POSITION, mCurrentSelectedPosition);
        outState.putBoolean("isLogin", Session.getSession().isLogin());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }   
        return super.onOptionsItemSelected(item);
    }
    
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public static interface NavigationDrawerCallbacks {    	
        void onNavigationDrawerItemSelected(int position , String title,String tag);
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(userUpdateReceiver);
	} 
}
