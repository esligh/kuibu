package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.adapter.UserListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopicInfoActivity extends BaseActivity {
	
	private ImageView topic_pic_iv;
	private TextView topic_name_tv, topic_desc_tv;
	private TextView follow_count_tv;
	private FButton focusBtn;
	private ListView bestAuthorList;
	private List<Map<String,Object>> mDatas= new ArrayList<Map<String,Object>>(); 
	private UserListAdapter authorAdapter ; 
	private boolean bIsFocus ; 
	private String topic_id ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_info);
		topic_pic_iv = (ImageView) findViewById(R.id.topic_pic_iv);
		topic_name_tv = (TextView) findViewById(R.id.topic_name_tv);
		topic_desc_tv = (TextView) findViewById(R.id.topic_desc_tv);
		topic_desc_tv.setText(getIntent().getStringExtra(StaticValue.TOPICINFO.TOPIC_EXTRA));
		follow_count_tv = (TextView) findViewById(R.id.follow_count_tv);
		focusBtn = (FButton) findViewById(R.id.focus_collectpack_bt);
		bestAuthorList = (ListView) findViewById(R.id.best_author_list);
		
		bestAuthorList.setOnTouchListener(new OnTouchListener() {				
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_MOVE:
						return true; 
					default : break; 
				}
				return false;
			}
		});
		
		bestAuthorList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String,Object> uInfo = (Map<String,Object>)viewAdapter.getAdapter().getItem(position);
				Intent intent = new Intent(TopicInfoActivity.this,UserInfoActivity.class);
				intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);	
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
				intent.putExtra(StaticValue.USERINFO.USER_SEX, (String)uInfo.get("sex"));
				intent.putExtra(StaticValue.USERINFO.USER_NAME, (String)uInfo.get("name"));
				intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE, (String)uInfo.get("signature"));
				intent.putExtra(StaticValue.USERINFO.USER_PHOTO, (String)uInfo.get("photo"));
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
			
		});
		focusBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(Session.getSession().isLogin()){
					doFocus();
				}else{
					Toast.makeText(TopicInfoActivity.this, getString(R.string.need_login), 
							Toast.LENGTH_SHORT).show();
				}		
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		loadData();
		loadAuthors();
		showView();
	}

	
	private void showView() {
		if ( authorAdapter== null) {
			authorAdapter = new UserListAdapter(this,
					mDatas); 
			bestAuthorList.setAdapter(authorAdapter);
		} else {
			authorAdapter.updateView(mDatas);
			
		}
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
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
	
	void loadData() {
		String topic_name =  this.getIntent().
				getStringExtra(StaticValue.TOPICINFO.TOPIC_NAME);
		setTitle(topic_name);
		String pic_url =  this.getIntent().getStringExtra(StaticValue.TOPICINFO.TOPIC_PIC);
		topic_id =  this.getIntent().
				getStringExtra(StaticValue.TOPICINFO.TOPIC_ID);
		topic_name_tv.setText(topic_name);
		ImageLoader.getInstance().displayImage(pic_url, topic_pic_iv);		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", topic_id);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_topicinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
	
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response
								.getString("result"));
						if (obj != null) {
							follow_count_tv.setText(DataUtils.formatNumber(obj.getInt("focus_count")));
							bIsFocus = obj.getBoolean("is_focus");
							if(bIsFocus){
								int btnColor= getResources().getColor(R.color.fbutton_color_concrete);
								focusBtn.setButtonColor(btnColor);						
								focusBtn.setText(getString(R.string.btn_cancel_focus));								
							}
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
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	void loadAuthors()
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("tid", topic_id);	
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_bestauthor").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONArray arr = new JSONArray(response
								.getString("result"));
						if (arr != null) {
							for(int i=0;i<arr.length();i++){
							    JSONObject obj = (JSONObject) arr.get(i);
							    Map<String,Object> item = new HashMap<String,Object>();
							    item.put("uid", obj.getString("author_id"));
							    item.put("sex",obj.getString("author_sex"));
							    item.put("name", obj.getString("author_name"));
							    item.put("signature", obj.getString("author_signature"));
							    item.put("photo", obj.getString("author_pic"));
							    mDatas.add(item);
							}
						}
						showView();
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
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
		
	}
	
	private void doFocus()
	{		
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.TOPIC_TYPE);
		params.put("obj_id", topic_id);
		final String URL;
		if(bIsFocus){ 
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/del_follows";
		}else{
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/add_follows";
		}
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						int count = Integer.parseInt(follow_count_tv.getText().toString().trim());
						if(bIsFocus){
							follow_count_tv.setText(DataUtils.formatNumber(count-1));
							int btnColor= getResources().getColor(R.color.fbutton_color_green_sea);
							focusBtn.setButtonColor(btnColor);						
							focusBtn.setText(getString(R.string.btn_focus));	
							bIsFocus = false; 
						}else{							
							follow_count_tv.setText(DataUtils.formatNumber(count+1));
							int btnColor= getResources().getColor(R.color.fbutton_color_concrete);
							focusBtn.setButtonColor(btnColor);						
							focusBtn.setText(getString(R.string.btn_cancel_focus));
							bIsFocus = true; 
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
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
}
