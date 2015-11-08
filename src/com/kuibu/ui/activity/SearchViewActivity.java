package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TopicListAdapter;
import com.kuibu.module.adapter.UserListAdapter;

public class SearchViewActivity extends AppCompatActivity{
	
	private PullToRefreshListView thingList ;	
	private TextView collection_tv,collector_tv ,topic_tv; 
	private String  target ;
	private TopicListAdapter topicAdapter;
	private List<TopicItemBean> topicDatas ;
	private UserListAdapter userAdapter;
	private List<Map<String,Object>> userDatas  ;
	private CommonAdapter<Map<String,String>> contentAdapter ; 
	private List<Map<String,String>> contentDatas ; 
	private boolean isDarkTheme ; 
	private EditText searchView ; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		isDarkTheme= PreferencesUtils.getBooleanByDefault(this,StaticValue.PrefKey.DARK_THEME_KEY, false);
		if (isDarkTheme) {
			setTheme(R.style.AppTheme_Dark);			
		}else{
			setTheme(R.style.AppTheme_Light);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		searchView = (EditText)findViewById(R.id.search_content);
		thingList = (PullToRefreshListView)findViewById(R.id.search_view_list);
		thingList.setMode(Mode.PULL_FROM_END);
		thingList.setPullToRefreshOverScrollEnabled(false);
		thingList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				onLoadMore();
			}
		});
		collection_tv = (TextView)findViewById(R.id.collection_tv);
		collector_tv = (TextView)findViewById(R.id.collector_tv);
		topic_tv = (TextView)findViewById(R.id.topic_tv);
		collection_tv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "CONTENT";
				switchBackground(target);
				if(contentDatas!=null)
					contentDatas.clear();
				if(contentAdapter==null)
					contentAdapter = new CommonAdapter<Map<String,String>>(SearchViewActivity.this, contentDatas,R.layout.search_content_list_item){

						@Override
						public void convert(ViewHolder holder,
								Map<String, String> item) {
							// TODO Auto-generated method stub
							content_convert(holder,item);
						}					
				};				
				thingList.setAdapter(contentAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					requestContent(query);
				}				
			}
		});
		collector_tv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "COLLECTOR";
				switchBackground(target);
				if(userDatas!=null)
					userDatas.clear();
				if(userAdapter==null)
					userAdapter = new UserListAdapter(SearchViewActivity.this, userDatas);				
				thingList.setAdapter(userAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					requestUsers(query);
				}
				
			}
		});
		topic_tv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "TOPIC";
				switchBackground(target);				
				if(topicDatas!=null)
					topicDatas.clear();
				if(topicAdapter==null)
					topicAdapter = new TopicListAdapter(SearchViewActivity.this, topicDatas);				
				thingList.setAdapter(topicAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					requestTopics(query);
				}
			}
		});
		thingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub				
				Object obj = viewAdapter.getAdapter().getItem(position);
				if(obj == null)  return ; 
				if(target.equals("TOPIC")){
					TopicItemBean item = (TopicItemBean)obj;
					Intent intent = new Intent(SearchViewActivity.this,
							TopicInfoActivity.class);
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,
							item.getId());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME, 
							item.getTopic());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC, 
							item.getTopicPicUrl());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA, 
							item.getIntroduce());
					startActivity(intent);		
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else if(target.equals("COLLECTOR")){
					@SuppressWarnings("unchecked")
					Map<String,Object> uInfo = (Map<String,Object>)obj;
					Intent intent = new Intent(SearchViewActivity.this,UserInfoActivity.class);
					intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);	
					intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
					intent.putExtra(StaticValue.USERINFO.USER_SEX, (String)uInfo.get("sex"));
					intent.putExtra(StaticValue.USERINFO.USER_NAME, (String)uInfo.get("name"));
					intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE, (String)uInfo.get("signature"));
					intent.putExtra(StaticValue.USERINFO.USER_PHOTO, (String)uInfo.get("photo"));
					startActivity(intent);					
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else if(target.equals("CONTENT")){
					
					@SuppressWarnings("unchecked")
					Map<String,String> item =(Map<String,String>) obj ;
					String tag = item.get("tag");
					if(tag.equals("collectpack")){
						Intent intent = new Intent(SearchViewActivity.this,
								CollectInfoListActivity.class);
						intent.putExtra("pack_id", item.get("item_id"));
						intent.putExtra("create_by", item.get("create_by"));
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
					}else if(tag.equals("collection")){
						Intent intent = new Intent(SearchViewActivity.this,CollectionDetailActivity.class);
						intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,item.get("item_id"));
						intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN,
								item.get("cisn"));
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
					}
				}
			}			
		});
		searchView.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				boolean flag = true; 
				if(TextUtils.isEmpty(s.toString())){
					flag = false;
				}								
				if(target.equals("TOPIC")){
					if(topicDatas!=null)
						topicDatas.clear();
					if(flag)
						requestTopics(s.toString());
					else
						showTopicView();
				}else if(target.equals("COLLECTOR")){
					if(userDatas!=null)
						userDatas.clear();
					if(flag)
						requestUsers(s.toString());
					else
						showUserView();
				}else if(target.equals("CONTENT")){
					if(contentDatas != null)
						contentDatas.clear();
					if(flag)
						requestContent(s.toString());
					else
						showContentView();
				}
			}
		});
		target = "CONTENT";
		switchBackground(target);
		showTopicView();
		
	}
	private void switchBackground(String target){
		if(TextUtils.isEmpty(target)) 
			return ; 
		if(isDarkTheme){
			if(target.equals("TOPIC")){
				topic_tv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collection_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				collector_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));				
			}else if(target.equals("COLLECTOR")){
				collector_tv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collection_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				topic_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));										
			}else if(target.equals("CONTENT")){
				collection_tv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collector_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				topic_tv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));			
			}
		}else{
			if(target.equals("TOPIC")){
				topic_tv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collection_tv.setBackgroundColor(getResources().getColor(R.color.white));
				collector_tv.setBackgroundColor(getResources().getColor(R.color.white));
				
			}else if(target.equals("COLLECTOR")){
				collector_tv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collection_tv.setBackgroundColor(getResources().getColor(R.color.white));
				topic_tv.setBackgroundColor(getResources().getColor(R.color.white));										
			}else if(target.equals("CONTENT")){
				collection_tv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collector_tv.setBackgroundColor(getResources().getColor(R.color.white));
				topic_tv.setBackgroundColor(getResources().getColor(R.color.white));			
			}
		}		
	}

	void showTopicView()
	{
		if(topicAdapter==null){
			topicAdapter = new TopicListAdapter(this,topicDatas);
			thingList.setAdapter(topicAdapter);
		}else{
			topicAdapter.updateView(topicDatas);
		}
	}
	
	void showUserView()
	{
		if(userAdapter==null){
			userAdapter = new UserListAdapter(this, userDatas);
			thingList.setAdapter(userAdapter);
		}else{
			userAdapter.updateView(userDatas);
		}
	}
	
	void showContentView()
	{
		if(contentAdapter==null){
			contentAdapter = new CommonAdapter<Map<String,String>>(this, contentDatas,
					R.layout.search_content_list_item){

						@Override
						public void convert(ViewHolder holder,
								Map<String, String> item) {
							// TODO Auto-generated method stub
							content_convert(holder,item);
						}
				
			};
			thingList.setAdapter(contentAdapter);
		}else{
			contentAdapter.refreshView(contentDatas);
		}
	}
	private void content_convert(ViewHolder holder,
			Map<String, String> item)
	{
		if(item.get("tag").equals("collectpack")){
			holder.setTvText(R.id.search_content_item_title,item.get("item_title"));
			holder.setTvText(R.id.search_content_item_sub1,item.get("item_count_1")+"条收集");
			holder.setTvText(R.id.search_content_item_sub2,item.get("item_count_2")+"人关注");			
		}else if(item.get("tag").equals("collection")){
			holder.setTvText(R.id.search_content_item_title,item.get("item_title"));
			holder.setTvText(R.id.search_content_item_sub1,item.get("item_count_1")+"人赞");
			holder.setTvText(R.id.search_content_item_sub2,"");
		}
	}
	void requestTopics(String query)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("off",topicDatas==null ? "0":String.valueOf(topicDatas.size()));
		params.put("slice", query);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_topiclist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
							.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						if(topicDatas == null)
							topicDatas = new ArrayList<TopicItemBean>();
						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							TopicItemBean item = new TopicItemBean();
							item.setId(obj.getString("tid"));
							item.setTopic(obj.getString("topic_name"));
							item.setIntroduce(obj.getString("topic_desc"));
							item.setTopicPicUrl(obj.getString("topic_pic"));
							topicDatas.add(item);
						}
						showTopicView();
					}
					thingList.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	void requestUsers(String query)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("off",userDatas==null ? "0":String.valueOf(userDatas.size()));
		params.put("slice", query);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_userlist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONArray arr = new JSONArray(response
								.getString("result"));
						if (arr != null) {
							if(userDatas == null)
								userDatas = new ArrayList<Map<String,Object>>();
							for(int i=0;i<arr.length();i++){
							    JSONObject obj = (JSONObject) arr.get(i);
							    Map<String,Object> item = new HashMap<String,Object>();
							    item.put("uid", obj.getString("id"));
							    item.put("sex",obj.getString("sex"));
							    item.put("name", obj.getString("name"));
							    item.put("signature", obj.getString("signature"));
							    item.put("photo", obj.getString("photo"));							    
							    userDatas.add(item);
							}
							showUserView();
						}
					}
					thingList.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
	
	
	void requestContent(String query)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("off",contentDatas==null ? "0":String.valueOf(contentDatas.size()));
		params.put("slice", query);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_contentlist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data); 
						if(contentDatas == null)
							contentDatas = new ArrayList<Map<String,String>>();
						for(int i=0;i<arr.length();i++){
							JSONObject obj = arr.getJSONObject(i);
							HashMap<String,String> item = new HashMap<String,String>();
							String tag = obj.getString("tag");
							item.put("tag", tag);
							if(tag.equals("collectpack")){
								item.put("item_id", obj.getString("id"));
								item.put("item_title", obj.getString("pack_name"));
								item.put("item_count_1", obj.getString("collect_count"));
								item.put("item_count_2", obj.getString("focus_count"));		
								item.put("create_by", obj.getString("create_by"));
								item.put("csn", obj.getString("csn"));
							}else{
								item.put("item_id", obj.getString("id"));
								item.put("cisn", obj.getString("cisn"));
								item.put("item_title", obj.getString("title"));
								item.put("item_count_1", obj.getString("vote_count"));
							}
							contentDatas.add(item);
						}
						showContentView();
					}
					thingList.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case android.R.id.home:
				this.onBackPressed();
				finish();
				overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);				
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);	
	}

	private void onLoadMore() {
		String query  = searchView.getText().toString();
		if(TextUtils.isEmpty(query))
			return  ;
		if(target.equals("TOPIC")){
			requestTopics(query);
		}else if(target.equals("COLLECTOR")){
			requestUsers(query);
		}else if(target.equals("CONTENT")){
			requestContent(query);
		}
	}	
}
