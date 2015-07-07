package com.kuibu.module.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @class : 网络工具类  
 * @author ThinkPad
 */
public class NetUtils {
	
	public static final int NO_NET = -1;
	public static final int NET_WIFI = 1;
	public static final int NET_CMNET = 2;
	public static final int NET_CMWAP = 3;

	/** 获得当前的网络类型
	 * @param context
	 * @return Net Type
	 */
	@SuppressLint("DefaultLocale")
	public static int getAPNType(Context context) {
		int netType = NO_NET;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = NET_CMNET;
			} else {
				netType = NET_CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NET_WIFI;
		}
		return netType;
	}

	/** 判断当前网络是否可用
	 * @param context
	 * @return boolean 
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			if (info != null) {
				return info.isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
