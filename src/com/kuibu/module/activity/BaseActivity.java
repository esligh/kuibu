package com.kuibu.module.activity;

import com.kuibu.data.global.StaticValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity {
	protected Activity mInstance = null;
	protected boolean isDarkTheme = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		isDarkTheme = mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false); 
		if (isDarkTheme) {
			setTheme(R.style.Theme_Kuibu_AppTheme_Dark);
		} else {
			setTheme(R.style.Theme_Kuibu_AppTheme_Light);
		}
		super.onCreate(savedInstanceState);		
		mInstance = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
	//	MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected Fragment getFragment() {
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

}