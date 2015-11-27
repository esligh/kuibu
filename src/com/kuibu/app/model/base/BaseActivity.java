package com.kuibu.app.model.base;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterfaces.IEventHandler;

public abstract class BaseActivity extends AppCompatActivity implements IEventHandler{
	
	protected Activity mInstance = null;
	protected boolean isDarkTheme = false;
	protected ActionBar mActionBar  ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		isDarkTheme = mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false); 
		if (isDarkTheme){
			setTheme(R.style.Theme_Kuibu_AppTheme_Dark);
		} else {
			setTheme(R.style.Theme_Kuibu_AppTheme_Light);
		}
		super.onCreate(savedInstanceState);		
		initActionBar();
		mInstance = this;
	}
	
	private void initActionBar()
	{
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {				
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		KuibuApplication.getInstance().cancelPendingRequests(this);
//		super.onStop();		
//	}

	protected Fragment getFragment()
	{
		return null;
	}
	
	@SuppressLint("NewApi")
	public void recreateActivity() {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					finish();
					startActivity(getIntent());
				} else {
					recreate();
				}
			}
		}, 1);
	}
	
	@Override
	public void eventResponse(JSONObject entity){	
		
	}
	
}