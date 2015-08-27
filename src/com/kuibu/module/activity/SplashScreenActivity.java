package com.kuibu.module.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.kuibu.common.utils.PhoneUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.model.welcome.WelcomeActivity;

/**
 * 启动页*/

public class SplashScreenActivity extends Activity {	
	private final static int DURABLE_TIME =  3000 ; 	
	private boolean bFirstLaunch = false ;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		PackageInfo info = PhoneUtils.getPackageInfo(this); 
		int currentVersion = info.versionCode;  
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);  
		int lastVersion = prefs.getInt(Constants.VERISION_CODE, 0);  
		if (currentVersion > lastVersion) {
			bFirstLaunch  = true ; 
		    prefs.edit().putInt(Constants.VERISION_CODE,currentVersion).commit();  
		}
		  		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				Intent intent = null; 
				if(bFirstLaunch){
					intent = new Intent(SplashScreenActivity.this,WelcomeActivity.class);
				}else{
					 intent = new Intent(SplashScreenActivity.this, 
								KuibuMainActivity.class);
					 overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}				
				startActivity(intent);
				finish();
			}
		}, DURABLE_TIME);
	}	
}
