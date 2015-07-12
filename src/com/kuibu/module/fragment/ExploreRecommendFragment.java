package com.kuibu.module.fragment;

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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.MateListViewItemAdapter;

public class ExploreRecommendFragment extends BaseFragment implements
		OnLoadListener {

	private PaginationListView recommendList = null;
	private MateListViewItemAdapter recommendAdapter = null;
	private List<MateListItem> mdatas = new ArrayList<MateListItem>();
	private MultiStateView mMultiStateView;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pagination_listview,
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
		recommendList = (PaginationListView) rootView
				.findViewById(R.id.pagination_lv);
		recommendList.setOnLoadListener(this);
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE);
		if (arr != null) {
			loadFromArray(arr,"REQ_HISTORY");
		}
		loadData("REQ_NEWDATA");
		showView();
		return rootView;
	}

	private void loadFromArray(JSONArray arr,String action) {
		try {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				MateListItem bean = new MateListItem();
				bean.set_id(temp.getString("cid"));
				bean.setType(Integer.parseInt(temp.getString("type")));
				bean.setTitle(temp.getString("title"));
				bean.setSummary(temp.getString("content"));
				bean.setItemPic(temp.getString("image_url"));
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
		params.put("data_type", "HOT_RECOMMEND");
		params.put("action",action);
		int n = mdatas.size(); 
		if(action.equals("REQ_HISTORY")){			
			params.put("threshold",String.valueOf(mdatas.get(n-1).getShareCount()));
		}
		else if(action.equals("REQ_NEWDATA") && n >0){
			params.put("threshold",String.valueOf(mdatas.get(0).getShareCount()));
		}
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_collections";
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
							if(action.equals("REQ_NEWDATA")){
								JSONArray oldarr = KuibuApplication.getCacheInstance()
										.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE);
						    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
						    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
						    	KuibuApplication.getCacheInstance()
						    		.put(StaticValue.LOCALCACHE.HOME_RECOMMAND_CACHE, newarr);
							}
							loadFromArray(arr,action);
						}else{
							Toast.makeText(getActivity(), 
									"没有数据啦！",Toast.LENGTH_SHORT).show();
						}
					}
					recommendList.loadComplete();
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

	private void showView() {
		if (recommendAdapter == null) {
			recommendAdapter = new MateListViewItemAdapter(getActivity(),
					mdatas);
			recommendList.setAdapter(recommendAdapter);
		} else {
			recommendAdapter.updateView(mdatas);
		}
	}

	@Override
	public void onLoad(String tag) {
		// TODO Auto-generated method stub
		loadData("REQ_HISTORY");
	}

}
