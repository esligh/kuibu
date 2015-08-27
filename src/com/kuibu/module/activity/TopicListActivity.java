package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.TopicItemBean;
import com.kuibu.module.adapter.TopicListAdapter;

public class TopicListActivity extends BaseActivity {
	private ListView topicList ; 
	private TopicListAdapter topicAdapter ; 
	private List<TopicItemBean> datas = new ArrayList<TopicItemBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_listview);
		topicList = (ListView)findViewById(R.id.simple_listview_lv);
		topicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TopicListActivity.this,TopicInfoActivity.class);
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,datas.get(position).getId());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME, datas.get(position).getTopic());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA, datas.get(position).getIntroduce());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC, datas.get(position).getTopicPicUrl());
				startActivity(intent);		
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		loadData();
	}
	
	void showView()
	{	
		if(topicAdapter == null )
		{
			topicAdapter = new TopicListAdapter(this,datas);
			topicList.setAdapter(topicAdapter);
		}else{
			topicAdapter.updateView(datas);
		}		
	}
	
	void loadData()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("topic_ids", getIntent().getStringExtra("topic_id"));		
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_packtopics";
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
						for(int i=0;i<arr.length();i++){
							JSONObject obj = arr.getJSONObject(i);
							TopicItemBean item = new TopicItemBean();
							item.setId(obj.getString("id"));
							item.setTopic(obj.getString("topic_name"));
							item.setIntroduce(obj.getString("topic_desc"));
							
							item.setTopicPicUrl(obj.getString("topic_pic"));
							datas.add(item);
						}
						showView();
					}					
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
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
}
