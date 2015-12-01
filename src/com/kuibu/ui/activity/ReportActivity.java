package com.kuibu.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.net.PublicRequestor;

public class ReportActivity extends BaseActivity {

	public TextView reportText ; 
	public boolean bReport = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_activity);
		reportText = (TextView)findViewById(R.id.report);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);	
		MenuItem edit=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
		        		StaticValue.MENU_ITEM.SEND_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,
		        		getString(R.string.send));		          
		edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
	}
	 
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {  
        switch(item.getItemId()){
        	case android.R.id.home:
        		this.onBackPressed();
        		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
        		break;
        	case StaticValue.MENU_ITEM.SEND_ID:
        		Map<String,String> params = new HashMap<String,String>();
        		params.put("accuser_id", Session.getSession().getuId());
        		params.put("reason", reportText.getText().toString().trim());
        		params.put("defendant_id", getIntent().getStringExtra("defendant_id"));
        		PublicRequestor.sendReport(params);
        		bReport = true;  
        		break;
        }
        return super.onOptionsItemSelected(item);	
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		intent.putExtra("is_report", bReport);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	} 	 
}
