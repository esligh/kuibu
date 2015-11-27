package com.kuibu.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.MateListViewItemAdapter;

public class ExploreRecommendFragment extends BaseFragment {
	
	private PullToRefreshListView recommendList = null;
	private MateListViewItemAdapter recommendAdapter = null;
	private List<MateListItem> mdatas = new ArrayList<MateListItem>();
	private MultiStateView mMultiStateView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pullrefresh_listview,
				container, false);
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
		recommendList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		recommendList.setVerticalScrollBarEnabled(false);
		recommendList.setMode(Mode.PULL_FROM_END);
		recommendList.setPullToRefreshOverScrollEnabled(false);
		recommendList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData("REQ_HISTORY");
				refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.REC_LAST_REFRESH_TIME));
			}
		});
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE);
		if (arr != null) {
			loadFromArray(arr,"REQ_HISTORY");
		}
		loadData("REQ_NEWDATA");
		showView();
		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){			
			
		}else{
			loadData("REQ_NEWDATA");
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mdatas.clear();
	}
	

	private void loadFromArray(JSONArray arr,String action) {
		try {
			int size = arr.length(); 
			if(size > 0){	
				for (int i = 0; i < size; i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					MateListItem bean = new MateListItem();
					bean.setId(temp.getString("cid"));
				    bean.setCisn(temp.getString("cisn"));
					bean.setType(Integer.parseInt(temp.getString("type")));
					bean.setTitle(temp.getString("title"));
					bean.setSummary(temp.getString("abstract"));
					bean.setItemPic(temp.getString("image_url"));
					bean.setCover(temp.getString("cover"));
					bean.setCreateBy(temp.getString("create_by"));
					bean.setPackId(temp.getString("pid"));
					bean.setTopText(temp.getString("name"));
					bean.setTopUrl(temp.getString("photo"));
				    bean.setUserSignature(temp.getString("signature"));
					bean.setUserSex(temp.getString("sex"));
					bean.setCreateBy(temp.getString("create_by"));
					bean.setVoteCount(temp.getInt("vote_count"));
					bean.setCommentCount(temp.getInt("comment_count"));
				    if(action.equals("REQ_NEWDATA")){
				    	mdatas.add(0,bean);
				    }else{
				    	mdatas.add(bean);
				    }
				}
				showView();
			}
			if (mdatas.size() > 0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
			} else {
				mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
		}
	}

	private void loadData(final String action) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mdatas.size()));
		params.put("data_type", "HOT_RECOMMEND");
		params.put("action",action);
		int n = mdatas.size(); 
		if(action.equals("REQ_HISTORY")){			
			params.put("threshold",String.valueOf(mdatas.get(n-1).getShareCount()));
		}
		else if(action.equals("REQ_NEWDATA") && n >0){
			params.put("threshold",String.valueOf(mdatas.get(0).getShareCount()));
		}
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_collections").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						loadFromArray(arr,action);
						recommendList.onRefreshComplete();						
						if(arr.length()>0){							
							if(action.equals("REQ_NEWDATA")){
								JSONArray oldarr = KuibuApplication.getCacheInstance()
										.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE);
						    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
						    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
						    	KuibuApplication.getCacheInstance()
						    		.put(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE, newarr);
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
				if(mdatas!=null && mdatas.isEmpty())
					mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
				recommendList.onRefreshComplete();
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_SHORT, 
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req,this);
	}

	private void showView() {
		if (recommendAdapter == null) {
			recommendAdapter = new MateListViewItemAdapter(getActivity(),
					mdatas,true);
			recommendList.setAdapter(recommendAdapter);
		} else {
			recommendAdapter.updateView(mdatas);
		}
	}

}
