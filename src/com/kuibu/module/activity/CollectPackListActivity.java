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
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.module.adapter.CollectPackItemAdapter;

public class CollectPackListActivity extends BaseActivity implements
		OnLoadListener {

	private PaginationListView packList;
	private CollectPackItemAdapter packAdapter;
	private List<CollectPackBean> datas = new ArrayList<CollectPackBean>();
	private String uid;
	private MultiStateView mMultiStateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pagination_listview);
		;
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR)
				.findViewById(R.id.retry)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
						loadData();
					}
				});

		packList = (PaginationListView) findViewById(R.id.pagination_lv);
		packList.setOnLoadListener(this);
		packList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CollectPackBean item = datas.get(position);
				Intent intent = new Intent(CollectPackListActivity.this,
						CollectionListActivity.class);
				intent.putExtra("pack_id", item.get_id());
				intent.putExtra("pack_name", item.getPack_name());
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,
						R.anim.anim_slide_out_left);
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		loadData();
		showView();
	}

	private void loadData() {
		uid = getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("off", String.valueOf(datas.size()));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/get_userpacks").toString();
		
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
						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							CollectPackBean item = new CollectPackBean();
							item.set_id(obj.getString("id"));
							item.setPack_name(obj.getString("pack_name"));
							item.setPack_desc(obj.getString("pack_desc"));
							item.setTopic_id(obj.getString("topic_ids"));
							item.setCollect_count(obj.getString("collect_count"));
							datas.add(item);
						}
						showView();
						if (datas.size() > 0) {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.CONTENT);
						} else {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.EMPTY);
						}
					}
					packList.loadComplete();
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void showView() {
		if (packAdapter == null) {
			packAdapter = new CollectPackItemAdapter(this, datas);
			packList.setAdapter(packAdapter);
		} else {
			packAdapter.updateView(datas);
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadData();
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
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
}
