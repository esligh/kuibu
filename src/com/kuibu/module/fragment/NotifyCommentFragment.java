package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.CollectionDetailActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.NotifyCommentAdapter;

public class NotifyCommentFragment extends Fragment implements OnLoadListener{
	private PaginationListView listView = null;
	private NotifyCommentAdapter adapter = null; 
	private List<Map<String,String>> datas = new ArrayList<Map<String,String>>();
	private MultiStateView mMultiStateView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pagination_listview,container, false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
		listView = (PaginationListView) rootView.findViewById(R.id.pagination_lv);
		listView.setOnLoadListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpaterView, View view, int position,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String,String> item = (Map<String,String>)adpaterView.getAdapter().getItem(position);
				Intent intent = new Intent(getActivity(),CollectionDetailActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,item.get("cid"));
				intent.putExtra("title", item.get("title"));
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		loadData();
		showView();
		return rootView; 
	}
	
	private void showView()
	{
		if(adapter == null){
			adapter =new NotifyCommentAdapter(this.getActivity(),datas);
			listView.setAdapter(adapter);
		}else{
			adapter.updateView(datas);
		}
	}
	
	private void loadData()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_actionlist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						if(arr.length()>0){
							for(int i = 0 ;i<arr.length();i++){
								JSONObject obj = arr.getJSONObject(i);
								Map<String,String> item = new HashMap<String,String>();
								item.put("cid", obj.getString("cid"));
								item.put("name", obj.getString("name"));
								String action_type = obj.getString("action_type");
								if(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION.
										equals(action_type)){
									item.put("desc", " 赞了这条收集");
								}else if(StaticValue.USER_ACTION.ACTION_COMMENT_COLLECTION.
										equals(action_type)){
									item.put("desc", " 评论了这条收集");
								}else if(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION.
										equals(action_type)){
									item.put("desc", " 收藏了这条收集");
								}
								item.put("title", obj.getString("title"));
								item.put("content", obj.getString("content"));
								datas.add(item);
							}
							showView();
						}						
					}
					mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	

	
	@Override
	public void onLoad(String tag) {
		loadData();
	}	
}
