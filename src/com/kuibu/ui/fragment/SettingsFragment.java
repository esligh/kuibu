package com.kuibu.ui.fragment;
import java.io.File;
import java.io.FileNotFoundException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.BufferManager;
import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.PhoneUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.ui.activity.AdviceFeedBackActivity;
import com.kuibu.ui.activity.MDHandBookActivity;

public class SettingsFragment extends PreferenceFragment implements
		OnPreferenceChangeListener ,OnPreferenceClickListener{
	
	private OnPreChangeListener mListener = null;
	private FinalHttp finalHttp = null;
	private int mNewVersionCode ; 
	private String mAppVersionName ; 
    private ProgressDialog mProDlg;  
    private ProgressDialog progressDlg; 
    private PreferenceScreen mVersion ; 
    private final int MAX_PROGRESS = 100  ; 
    private String mUpdateUrl; 
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			}
		}
    };
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPreChangeListener) activity;
		} catch (ClassCastException e) {
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		
		boolean isDarkTheme = mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY,
				false);

		if (isDarkTheme) {
			getActivity().setTheme(R.style.Theme_Kuibu_AppTheme_Dark);
		} else {
			getActivity().setTheme(R.style.Theme_Kuibu_AppTheme_Light);
		}
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		final PreferenceScreen clearBuffer = (PreferenceScreen) findPreference(
				StaticValue.PrefKey.CLEAR_BUFFER);
		clearBuffer.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						BufferManager.clearAllCache(getActivity());
						clearBuffer.setSummary(getActivity().getString(R.string.cache_size)
								+ BufferManager.getTotalCacheSize(getActivity()));
						return true;
					}
				});
		clearBuffer.setSummary(getActivity().getString(R.string.cache_size)+ BufferManager.getTotalCacheSize(getActivity()));
		try {
			mVersion= (PreferenceScreen) findPreference(StaticValue.PrefKey.LASTEST_VERSION);
			mVersion.setOnPreferenceClickListener(this);					
			PackageManager pm = getActivity().getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
			mVersion.setSummary(pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		PreferenceScreen account = (PreferenceScreen) findPreference(StaticValue.PrefKey.ACCOUNT_PROTECT);
		if (StaticValue.REGSTATE.ACCOUNT_ACTIVATED.equals(Session.getSession().getRegState())) {
			account.setSummary(getString(R.string.account_protect));
		} else {
			account.setSummary(getString(R.string.account_no_protect));
		}

		findPreference(StaticValue.PrefKey.NO_PICTRUE_KEY).setOnPreferenceChangeListener(this);
		findPreference(StaticValue.PrefKey.DARK_THEME_KEY).setOnPreferenceChangeListener(this);
		findPreference(StaticValue.PrefKey.ABOUT_ME).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.HAND_BOOK).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.FLOW_STATISTICS).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.ADVICE_FEEDBACK).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.EXCEPTION_REPORT).setOnPreferenceClickListener(this);					
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		if (pref.getKey().equals(StaticValue.PrefKey.DARK_THEME_KEY)) {
			if (newValue instanceof Boolean) {
				Boolean boolVal = (Boolean) newValue;
				SharedPreferences mPerferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor mEditor = mPerferences.edit();
				mEditor.putBoolean(StaticValue.PrefKey.DARK_THEME_KEY, boolVal);
				mEditor.commit();				
				mListener.onChanged(boolVal);
			}
		} 
		return true;
	}

	public static interface OnPreChangeListener {
		public void onChanged(boolean result);
	}
	
	@Override
	public boolean onPreferenceClick(Preference pref) {
		if(pref.getKey().equals(StaticValue.PrefKey.ABOUT_ME)){
			showAbout();
		}else if (pref.getKey().equals(StaticValue.PrefKey.LASTEST_VERSION)) {
			reqAppVersion();
		}else if(pref.getKey().equals(StaticValue.PrefKey.HAND_BOOK)){
			Intent intent = new Intent(getActivity(),MDHandBookActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

		}else if(pref.getKey().equals(StaticValue.PrefKey.FLOW_STATISTICS)){
			Toast.makeText(getActivity(), "开发中...", Toast.LENGTH_SHORT).show();
		}else if(pref.getKey().equals(StaticValue.PrefKey.ADVICE_FEEDBACK)){
			Intent intent = new Intent(getActivity(),AdviceFeedBackActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
		}else if(pref.getKey().equals(StaticValue.PrefKey.EXCEPTION_REPORT)){ 
			final String fileName = new StringBuilder("crash-")
				.append(SafeEDcoderUtil.MD5(PhoneUtils.getDeviceId(getActivity())))
				.append(".log").toString();
			final String path = new StringBuilder(StorageUtils.getFileDirectory(getActivity())
						.getAbsolutePath()).append(Constants.Config.CRASH_DIR)
						.append(fileName).toString(); 
			if(!FileUtils.isFileExist(path)){
					Toast.makeText(getActivity(), "暂无异常日志", Toast.LENGTH_SHORT).show();
					return true; 
			}
              new AlertDialog.Builder(getActivity()).setTitle("异常")
              .setMessage("上传异常日志?")
              .setPositiveButton(getActivity().getString(R.string.btn_confirm),
            		  new  DialogInterface.OnClickListener(){
      			@Override
      			public void onClick(DialogInterface arg0, int arg1) {
      				
      				String URL = new StringBuilder(Constants.Config.SERVER_URI)
      				.append(Constants.Config.REST_API_VERSION)
      				.append("/crash_collector").toString();
      				try {
      		    			mProDlg.setTitle(getString(R.string.preference_exception));   
                            mProDlg.show();
      		    			AjaxParams params = new AjaxParams();
      		    			params.put("file_name", fileName);
      						params.put("data", new File(path));
      						if(finalHttp == null){ 
      							finalHttp = new FinalHttp() ;
      						}
      						finalHttp.post(URL, params, new AjaxCallBack<String>() {
      							@Override
      							public void onFailure(Throwable t, int errorNo,
      									String strMsg) {
      								super.onFailure(t, errorNo, strMsg);
      								mProDlg.cancel();
      							}
      							
      							@Override
      							public void onLoading(long count, long current) {
      								super.onLoading(count, current);
      								int progress = 0;
      								if (current != count && current != 0) {
      									progress = (int) (current / (float) count * 100);
      								} else {
      									progress = 100;
      								}
      								mProDlg.setProgress(progress);
      							}
      							
								@Override
      							public void onSuccess(String t) {
									mProDlg.dismiss();
      								super.onSuccess(t);
      								FileUtils.delFile(path);
      							}						
      						});  
      					} catch (FileNotFoundException e) {
      						//do nothing 
      						e.printStackTrace();
      					}		   	 			
      	    	}
              }).setNegativeButton(getActivity().getString(R.string.btn_confirm), null).show();	
		}
		return true;
	}
	
	private void showAbout() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.dialog_version);
		TextView textView = (TextView) dialog.findViewById(R.id.dialog_text);
		textView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		String title = new StringBuilder()
				.append(PhoneUtils.getApplicationName(getActivity()))
				.append("<br/>").toString();
		String subTitle = new StringBuilder()
				.append(getResources().getString(R.string.tip_guide_first))
				.append("<br/>").toString();
		String githubUrl = new StringBuilder().append("<a href='")
				.append(Constants.GITGUB_PROJECT).append("'>")
				.append(Constants.GITGUB_PROJECT).append("</a>")
				.append("<br/>").toString();
		String data = getResources().getString(R.string.setting_aboutme_text);
		data = String.format(data, title, subTitle, githubUrl);
		CharSequence charSequence = Html.fromHtml(data);
		textView.setText(charSequence);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		dialog.show();
	}
	
    
	private void reqAppVersion()
	{	
		if(progressDlg == null){
			progressDlg = new ProgressDialog(getActivity());
			progressDlg.setCanceledOnTouchOutside(false);
			progressDlg.setMessage(getString(R.string.get_versioninfo));
		}	
		progressDlg.show();
		
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_appinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, null, 
				new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						String versionName =  "";
						for(int i =0;i<arr.length();i++){
							JSONObject obj = arr.getJSONObject(i);
							String key = obj.getString("cfg_key");
							if(StaticValue.SERMODLE.VERSION_CODE.equals(key)){
								mNewVersionCode = obj.getInt("cfg_value_a");
								versionName = obj.getString("cfg_name");
								mUpdateUrl = obj.getString("cfg_value_b");										
							}
						}
						mAppVersionName = new StringBuffer(Constants.APP_NAME).append("-").
								append(versionName).append(".apk").toString();
						int curVersionCode = PhoneUtils.getPackageInfo(getActivity()).versionCode;
						progressDlg.dismiss();
						if(mNewVersionCode > curVersionCode){
							doUpdate();
						}else{							
							Toast.makeText(getActivity(), getString(R.string.no_new_version), Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void doUpdate()
	{   
		if(mProDlg == null){
			mProDlg =  new ProgressDialog(getActivity());
		}
		mProDlg.setMax(MAX_PROGRESS);
		mProDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 	
	
         Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.update_version))
        		 .setMessage(getString(R.string.quest_update))
                 .setPositiveButton(getString(R.string.btn_confirm),    
                         new DialogInterface.OnClickListener() {    
                             @Override    
                             public void onClick(DialogInterface dialog,    
                                     int witch) {    
                            	 mProDlg.setTitle(getString(R.string.updating));   
                                 mProDlg.show();
                                 downLoadApp();    
                             }    
                         })    
                 .setNegativeButton(getString(R.string.btn_cancel),    
                         new DialogInterface.OnClickListener() {    
                             public void onClick(DialogInterface dialog,    
                                     int witch) {                                
                             }    
                         }).create();   
         dialog.show();    
	}
	
	private void downLoadApp() {
		String apkPath = new StringBuilder(
				Environment.getExternalStorageDirectory().getAbsolutePath()) 
				.append('/').append(mAppVersionName).toString();
		File file = new File(apkPath);
		if (file.exists()) {
			file.delete();
		}
		final String URL = mUpdateUrl;
		if(finalHttp == null) 
				finalHttp = new FinalHttp() ; 
		finalHttp.download(URL, apkPath, new AjaxCallBack<File>() {
			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
				int progress = 0;
				if (current != count && current != 0) {
					progress = (int) (current / (float) count * 100);
				} else {
					progress = 100;
				}
				mProDlg.setProgress(progress);
			}

			@Override
			public void onSuccess(File t) {				
				super.onSuccess(t);
				mProDlg.dismiss();
				Intent intent = new Intent(Intent.ACTION_VIEW);  
	            intent.setDataAndType(Uri.fromFile(new File(Environment  
	                    .getExternalStorageDirectory(), mAppVersionName)),  
	                    "application/vnd.android.package-archive");  
	            startActivity(intent);  
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(getActivity(), getString(R.string.update_fail), Toast.LENGTH_SHORT)
						.show();
				mProDlg.cancel();
			}
		});    
	}
}

