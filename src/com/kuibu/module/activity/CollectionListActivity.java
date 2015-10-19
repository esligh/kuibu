package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.adapter.MateListViewItemAdapter;

public class CollectionListActivity extends BaseActivity implements OnLoadListener{	
	private PaginationListView listView ; 
	private List<MateListItem> datas = new ArrayList<MateListItem>();
	private MateListViewItemAdapter mAdapter ; 
	private String mPackId ; 
	private MultiStateView mMultiStateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pagination_listview);
		mMultiStateView = (MultiStateView)findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
		listView = (PaginationListView)findViewById(R.id.pagination_lv);
		listView.setOnLoadListener(this);
		loadData();
		showView();
	}
	
	private void loadData()
	{
		mPackId = getIntent().getStringExtra("pack_id");
		setTitle(getIntent().getStringExtra("pack_name"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(datas.size()));
		params.put("pid", mPackId);
		params.put("data_type", "PACK_LIST");
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/get_collections").toString();
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
						if(arr.length()>0){
							for (int i = 0; i < arr.length(); i++) {
							    JSONObject temp = (JSONObject) arr.get(i);
							    MateListItem bean = new MateListItem();
							    bean.setId(temp.getString("cid"));
							    bean.setCisn(temp.getString("cisn"));
							    bean.setType(Integer.parseInt(temp.getString("type")));
							    bean.setTitle(temp.getString("title"));
							    bean.setSummary(temp.getString("abstract"));
							    bean.setItemPic(temp.getString("image_url"));
							    bean.setCreateBy(temp.getString("create_by"));
							    bean.setPackId(temp.getString("pid"));
							    bean.setVoteCount(temp.getInt("vote_count"));
							    bean.setUserSex(temp.getString("sex"));
							    bean.setUserSignature(temp.getString("signature"));
							    bean.setCommentCount(temp.getInt("comment_count"));
							    bean.setTopText(temp.getString("name"));
							    bean.setTopUrl(temp.getString("photo"));
							    datas.add(bean);
							}
						}
						if(datas.size()>0){
							mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
						}else{
							mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
						}
						showView();
					}	
					listView.loadComplete();
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void showView()
	{
		if(mAdapter == null){
			mAdapter = new MateListViewItemAdapter(this, datas,false);
			listView.setAdapter(mAdapter);
		}else{
			mAdapter.updateView(datas);
		}
	}

	@Override
	public void onLoadMore() {
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
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
}
