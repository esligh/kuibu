/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.kuibu.data.global;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.kuibu.common.utils.ACache;
import com.kuibu.common.utils.PersistentCookieStore;
import com.kuibu.model.db.SqLiteHelper;
import com.kuibu.module.net.EventSocket;
import com.kuibu.module.net.SocketIOCallBack;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class KuibuApplication extends Application {
	private RequestQueue mRequestQueue; // volley request queue ;
	public static final String TAG = "VolleyPatterns";
	private static KuibuApplication sInstance;
	private static PersistentCookieStore persistentCookieStore;
	private static ACache mCache;
	private static EventSocket mSocketIo;
	private static SqLiteHelper mSqlHelper;
	private File extStorageCachePath;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
		mCache = ACache.get(getApplicationContext());
		sInstance = this;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File externalStorageDir = Environment.getExternalStorageDirectory();// SD
			File extStorageBasePath = null;
			if (externalStorageDir != null) {
				extStorageBasePath = new File(
						externalStorageDir.getAbsolutePath() + File.separator
								+ "Android" + File.separator + "data"
								+ File.separator + getPackageName());
			}
			if (extStorageBasePath != null) {
				// {SD_PATH}/Android/data/{package}/cache
				extStorageCachePath = new File(
						extStorageBasePath.getAbsolutePath() + File.separator
								+ "cache");
				if (!extStorageCachePath.exists()) {
					if (!extStorageCachePath.mkdirs()) {
						extStorageCachePath = null;
					}
				}
			}
		}
	}

	public static Context getContext() {
		return sInstance.getApplicationContext();
	}

	@Override
	public File getCacheDir() {
		if (extStorageCachePath != null) {
			// Use the external storage for the cache
			return extStorageCachePath;
		} else {
			// /data/data/com.devahead.androidwebviewcacheonsd/cache
			return super.getCacheDir();
		}
	}

	public static EventSocket getSocketIoInstance() {
		if (mSocketIo == null)
			mSocketIo = new EventSocket(new SocketIOCallBack());
		return mSocketIo;
	}

	public static SqLiteHelper getSqLiteHelper(Context context) {
		if (mSqlHelper == null)
			mSqlHelper = new SqLiteHelper(context);
		return mSqlHelper;
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public static synchronized ACache getCacheInstance() {
		return mCache;
	}

	public static synchronized KuibuApplication getInstance() {
		return sInstance;
	}

	public synchronized PersistentCookieStore getPersistentCookieStore() {
		if (persistentCookieStore == null) {
			persistentCookieStore = new PersistentCookieStore(this);
		}
		return persistentCookieStore;
	}

	/**
	 * @return The Volley Request queue, the queue will be created if it is null
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			// create the request queue
			mRequestQueue = Volley.newRequestQueue(this);
		}

		return mRequestQueue;
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag if tag is empty
		req.setTag(TAG);

		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}