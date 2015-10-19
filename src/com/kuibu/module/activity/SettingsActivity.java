package com.kuibu.module.activity;


import android.os.Bundle;
import android.view.MenuItem;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.module.fragment.SettingsFragment;
import com.kuibu.module.fragment.SettingsFragment.OnPreChangeListener;

public class SettingsActivity extends BaseActivity  implements OnPreChangeListener{
	private SettingsFragment settingsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        settingsFragment = new SettingsFragment();
        setContentView(R.layout.activity_preference);
        getFragmentManager().beginTransaction()
        .replace(R.id.frame, settingsFragment)
        .commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}

	@Override
	public void onChanged(boolean result) {
		// TODO Auto-generated method stub
		recreateActivity();
	}
}
