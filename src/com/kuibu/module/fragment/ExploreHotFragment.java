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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.FavoriteBoxInfoActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HotListViewItemAdapter;

public class ExploreHotFragment extends BaseFragment implements OnLoadListener {

	private PaginationListView hotList = null;
	private HotListViewItemAdapter hotAdapter = null;
	private List<Map<String, String>> mdatas = new ArrayList<Map<String, String>>();
	private MultiStateView mMultiStateView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_pagination_hotcollect, container, false);
		mMultiStateView = (MultiStateView) rootView
				.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR)
				.findViewById(R.id.retry)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mMultiStateView
								.setViewState(MultiStateView.ViewState.LOADING);
						loadData("REQ_NEWDATA");
					}
				});

		hotList = (PaginationListView) rootView
				.findViewById(R.id.pagination_lv);
		hotList.setOnLoadListener(this);
		hotList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						FavoriteBoxInfoActivity.class);
				intent.putExtra("box_id", mdatas.get(position).get("box_id"));
				intent.putExtra("create_by", mdatas.get(position).get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_HOT_CACHE);
		if (arr != null && arr.length()>0) {
			loadFromArray(arr,"REQ_HISTORY");
		}
		loadData("REQ_NEWDATA");
		showView();
		return rootView;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){			
			
		}else{
			loadData("REQ_NEWDATA");
		}
	}

	@Override
	public void onLoadMore() {
		loadData("REQ_HISTORY");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mdatas.clear();
		mdatas = null ;
	}
		

	private void loadFromArray(JSONArray arr,String action) {
		try {
			if(arr.length()>0){

				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					Map<String, String> item = new HashMap<String, String>();
					item.put("box_id", temp.getString("box_id"));
					item.put("box_name", temp.getString("box_name"));
					item.put("focus_count", temp.getString("focus_count"));
					item.put("box_desc", temp.getString("box_desc"));
					item.put("box_count", temp.getString("box_count"));
					item.put("uid", temp.getString("uid"));
					item.put("user_name", temp.getString("name"));
					item.put("user_sex", temp.getString("sex"));
					item.put("user_pic", temp.getString("photo"));
				    if(action.equals("REQ_NEWDATA")){
				    	mdatas.add(0,item);
				    }else{
				    	mdatas.add(item);
				    }
				}
				showView();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mdatas.size() > 0) {
			mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
		} else {
			mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
		}
	}
	
	
	
	private void loadData(final String action) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mdatas.size()));
		params.put("action", action);
		int n = mdatas.size(); 
		if(action.equals("REQ_HISTORY")){		
			params.put("threshold",String.valueOf(mdatas.get(n-1).get("focus_count")));
		}
		else if(action.equals("REQ_NEWDATA") && n >0){
			params.put("threshold",String.valueOf(mdatas.get(0).get("focus_count")));
		}
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_hotpacks").toString();
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
						loadFromArray(arr,action);
						hotList.loadComplete();
						
						if(arr.length()>0){
							if(action.equals("REQ_NEWDATA")){
								JSONArray oldarr = KuibuApplication.getCacheInstance()
										.getAsJSONArray(StaticValue.LOCALCACHE.HOME_HOT_CACHE);
						    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
						    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
						    	KuibuApplication.getCacheInstance()
						    	.put(StaticValue.LOCALCACHE.HOME_HOT_CACHE, newarr);
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
				if(mdatas.isEmpty())
					mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
				hotList.loadComplete();
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_SHORT, 
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void showView() {
		if (hotAdapter == null) {
			hotAdapter = new HotListViewItemAdapter(getActivity(), mdatas);
			hotList.setAdapter(hotAdapter);
		} else {
			hotAdapter.updateView(mdatas);
		}
	}

}
