package com.kuibu.module.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;

public class ReportActivity extends BaseActivity {

	public TextView reportText ; 
	
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
		        		StaticValue.MENU_ITEM.SEND_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,"发送");		          
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
        		sendReport();
        		break;
        }
        return super.onOptionsItemSelected(item);	
	}
	 	 
	private void sendReport()
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("accuser_id", Session.getSession().getuId());
		params.put("reason", reportText.getText().toString().trim());
		params.put("defendant_id", getIntent().getStringExtra("defendant"));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/add_report";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Toast.makeText(ReportActivity.this, "感谢您的举报,我们会尽快处理",Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String credentials = Session.getSession().getToken()
						+ ":unused";
				headers.put("Authorization", "Basic "+
								SafeEDcoderUtil.encryptBASE64(credentials.getBytes())
								.replaceAll("\\s+", ""));
				return headers;
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);		
	}
}
