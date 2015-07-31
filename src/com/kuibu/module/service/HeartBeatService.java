package com.kuibu.module.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.net.NetUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HeartBeatService extends Service {

	private boolean bRunning = true ; 
	private static final int interval = 5* 1000 * 60 ;  //five minutes 
	private final IBinder mBinder = new HeartBeatBinder();
	
	public class HeartBeatBinder extends Binder{
		HeartBeatService getService()
		{
			return HeartBeatService.this; 
		}
	}
	
	public HeartBeatService() {		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(bRunning){
					try {						
						Thread.sleep(interval);
						if(Session.getSession().isLogin()
								&& NetUtils.isNetworkAvailable(getApplicationContext()))
							sayHello();
						Log.d("HeartBeat", "live");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}				
			}			
		}).start();	
		super.onCreate();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		bRunning = false; 
	}
	
	
	private void sayHello()
	{
		JSONObject obj = new JSONObject();
		try{			
			obj.put("event_type",StaticValue.EVENT.TYPE_KEEPALIVE);
			KuibuApplication.getSocketIoInstance().getSocketIO().
 				emit(StaticValue.EVENT.MSG_EVENT, obj);
		}catch(JSONException e) {
			e.printStackTrace();
		}		
	}
}
