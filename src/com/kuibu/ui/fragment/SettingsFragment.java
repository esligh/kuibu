package com.kuibu.ui.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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

import com.kuibu.common.utils.BufferManager;
import com.kuibu.common.utils.PhoneUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.SettingPresenterImpl;
import com.kuibu.module.presenter.interfaces.SettingPresenter;
import com.kuibu.ui.activity.AdviceFeedBackActivity;
import com.kuibu.ui.activity.MDHandBookActivity;
import com.kuibu.ui.view.interfaces.SettingView;

public class SettingsFragment extends PreferenceFragment implements
		OnPreferenceChangeListener ,OnPreferenceClickListener,SettingView{
	
	private OnPreChangeListener mListener = null;	
    private PreferenceScreen mVersion ; 
    
    private SettingPresenter mPresenter ;
    
	@SuppressWarnings("deprecation")
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
	//	findPreference(StaticValue.PrefKey.FLOW_STATISTICS).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.ADVICE_FEEDBACK).setOnPreferenceClickListener(this);
		findPreference(StaticValue.PrefKey.EXCEPTION_REPORT).setOnPreferenceClickListener(this);
		
		mPresenter = new SettingPresenterImpl(this);
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
			mPresenter.checkVersion();
		}else if(pref.getKey().equals(StaticValue.PrefKey.HAND_BOOK)){
			Intent intent = new Intent(getActivity(),MDHandBookActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

		}else if(pref.getKey().equals(StaticValue.PrefKey.FLOW_STATISTICS)){
			
			
		}else if(pref.getKey().equals(StaticValue.PrefKey.ADVICE_FEEDBACK)){
			Intent intent = new Intent(getActivity(),AdviceFeedBackActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
		}else if(pref.getKey().equals(StaticValue.PrefKey.EXCEPTION_REPORT)){
			
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

	@Override
	public Context getInstantce() {
		// TODO Auto-generated method stub
		return getActivity();
	}

}

