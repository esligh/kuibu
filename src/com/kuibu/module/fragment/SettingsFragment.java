package com.kuibu.module.fragment;

import android.app.Activity;
import android.app.Dialog;
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

public class SettingsFragment extends PreferenceFragment implements
		OnPreferenceChangeListener ,OnPreferenceClickListener{
	private OnPreChangeListener mListener = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPreChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnPreChangeListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		boolean isDarkTheme = mPerferences.getBoolean("dark_theme", false);

		if (isDarkTheme) {
			getActivity().setTheme(R.style.Theme_Kuibu_AppTheme_Dark);
		} else {
			getActivity().setTheme(R.style.Theme_Kuibu_AppTheme_Light);
		}

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		final PreferenceScreen clearBuffer = (PreferenceScreen) findPreference("clear_buffer");
		clearBuffer
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						BufferManager.clearAllCache(getActivity());
						clearBuffer.setSummary("当前缓存大小"
								+ BufferManager
										.getTotalCacheSize(getActivity()));
						return true;
					}
				});
		clearBuffer.setSummary("当前缓存大小"
				+ BufferManager.getTotalCacheSize(getActivity()));
		try {

			PreferenceScreen version = (PreferenceScreen) findPreference("latest_version");
			PackageManager pm = getActivity().getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
			version.setSummary(pi.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PreferenceScreen account = (PreferenceScreen) findPreference("account_protect");
		if (StaticValue.REGSTATE.ACCOUNT_ACTIVATED.equals(Session.getSession()
				.getRegState())) {
			account.setSummary("已保护 (邮箱已验证)");
		} else {
			account.setSummary("未保护 (邮箱待验证)");
		}

		findPreference(StaticValue.PrefKey.NO_PICTRUE_KEY).setOnPreferenceChangeListener(this);
		findPreference(StaticValue.PrefKey.DARK_THEME_KEY).setOnPreferenceChangeListener(this);
		findPreference(StaticValue.PrefKey.ABOUT_ME).setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		// TODO Auto-generated method stub
		if (pref.getKey().equals(StaticValue.PrefKey.DARK_THEME_KEY)) {
			if (newValue instanceof Boolean) {
				Boolean boolVal = (Boolean) newValue;
				SharedPreferences mPerferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());

				SharedPreferences.Editor mEditor = mPerferences.edit();
				mEditor.putBoolean("dark_theme", boolVal);
				mEditor.commit();

				mListener.onChanged(boolVal);
			}
		} else if (pref.getKey().equals(StaticValue.PrefKey.NO_PICTRUE_KEY)) {

		}
		return true;
	}

	public static interface OnPreChangeListener {
		public void onChanged(boolean result);
	}
	
	@Override
	public boolean onPreferenceClick(Preference pref) {
		// TODO Auto-generated method stub
		if(pref.getKey().equals(StaticValue.PrefKey.ABOUT_ME)){
			showAbout();
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


}
