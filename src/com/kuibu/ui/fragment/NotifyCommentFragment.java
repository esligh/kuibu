package com.kuibu.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.ui.activity.CollectionDetailActivity;
import com.kuibu.ui.activity.CollectionImageDetailActivity;

public class NotifyCommentFragment extends BaseFragment{
	
	private PullToRefreshListView listView = null;
	private CommonAdapter<Map<String,String>> adapter = null; 
	private List<Map<String,String>> datas = new ArrayList<Map<String,String>>();
	private MultiStateView mMultiStateView;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pullrefresh_listview,container, false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
		listView = (PullToRefreshListView) rootView.findViewById(R.id.pagination_lv);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setPullToRefreshOverScrollEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData();
				String label = DateUtils.formatDateTime(getActivity(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpaterView, View view, int position,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String,String> item = (Map<String,String>)adpaterView.getAdapter().getItem(position);
				Intent intent = new Intent();
				String type = item.get("type");
				if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
					intent.setClass(getActivity(), CollectionImageDetailActivity.class);
				}else{
					intent.setClass(getActivity(), CollectionDetailActivity.class);
				}
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,item.get("cid"));
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
			adapter =new CommonAdapter<Map<String,String>>(this.getActivity(),datas,R.layout.notifycomment_list_item){

				@Override
				public void convert(ViewHolder holder, Map<String,String> item) {
					// TODO Auto-generated method stub
					holder.setTvText(R.id.top_text_left_tv,item.get("name"));
					holder.setTvText(R.id.top_text_Right_tv,item.get("desc"));
					holder.setTvText(R.id.title_tv,item.get("title"));
					holder.setTvText(R.id.content_tv,item.get("abstract"));
				}				
			};
			listView.setAdapter(adapter);
		}else{
			adapter.refreshView(datas);
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
									item.put("desc", getActivity().getString(R.string.vote_prompt));
								}else if(StaticValue.USER_ACTION.ACTION_COMMENT_COLLECTION.
										equals(action_type)){
									item.put("desc", getActivity().getString(R.string.comment_prompt));
								}else if(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION.
										equals(action_type)){
									item.put("desc", getActivity().getString(R.string.collect_prompt));
								}
								item.put("title", obj.getString("title"));
								item.put("type", obj.getString("type"));
								item.put("abstract", obj.getString("abstract"));
								datas.add(item);
							}
							showView();
						}						
					}
					if(datas.size()>0){
						mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
					}else{
						mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
					}
					listView.onRefreshComplete();
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
		KuibuApplication.getInstance().addToRequestQueue(req,this);
	}

	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		datas.clear();
	}
	
}
