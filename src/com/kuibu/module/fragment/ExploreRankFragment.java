package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.ACache;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.MateListViewItemAdapter;

public class ExploreRankFragment extends BaseFragment implements
	OnLoadListener{
	
	private PaginationListView rankList = null;
	private MateListViewItemAdapter rankAdapter = null;
	private List<MateListItem> mdatas = new ArrayList<MateListItem>();
	private ACache mCache  ;
	private boolean isCache = false;
	private MultiStateView mMultiStateView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			mCache = ACache.get(this.getActivity());
			View rootView = inflater.inflate(R.layout.activity_pagination_listview,container,false);
			mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
			mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
	        .setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
					loadData();
				}   	
	        });
			rankList = (PaginationListView) rootView
					.findViewById(R.id.pagination_lv);
			rankList.setOnLoadListener(this);
			JSONArray arr = mCache.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RANK_CACHE);
			if(arr!=null){
				loadFromArray(arr);
			}else{
				loadData();
			}					
			showView();
			return rootView;
	}

	private void loadFromArray(JSONArray arr)
	{
			try{
				for (int i = 0; i < arr.length(); i++) {
				    JSONObject temp = (JSONObject) arr.get(i);
				    MateListItem bean = new MateListItem();
				    bean.set_id(temp.getString("cid"));
				    bean.setType(Integer.parseInt(temp.getString("type")));
				    bean.setTitle(temp.getString("title"));
				    bean.setSummary(temp.getString("content"));
				    bean.setItemPic(temp.getString("image_url"));
				    bean.setCreateBy(temp.getString("create_by"));
				    bean.setTopText(temp.getString("name"));
				    bean.setTopUrl(temp.getString("photo"));
				    bean.setUserSex(temp.getString("sex"));
				    bean.setUserSignature(temp.getString("signature"));
				    bean.setPackId(temp.getString("pid"));
				    bean.setCreateBy(temp.getString("create_by"));
				    bean.setVoteCount(temp.getInt("vote_count"));
				    bean.setCommentCount(temp.getInt("comment_count"));			    
				    mdatas.add(bean);
				}
				showView();
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(mdatas.size()>0){
			mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
		}else{
			mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
		}
	}
	
	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mdatas.size()));
		params.put("data_type", "HOT_RANK");
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/get_collections";
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
							if(!isCache){
								mCache.put(StaticValue.LOCALCACHE.HOME_RANK_CACHE, arr);
								isCache = true;
							}
							loadFromArray(arr);
						}else{
							Toast.makeText(getActivity(), 
									"没有数据啦！",Toast.LENGTH_SHORT).show();
						}
									
					}
					rankList.loadComplete();
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
		if (rankAdapter == null) {
			rankAdapter = new MateListViewItemAdapter(getActivity(),
					mdatas);
			rankList.setAdapter(rankAdapter);
		} else {
			rankAdapter.updateView(mdatas);
		}
	}
	
	@Override
	public void onLoad(String tag) {
		// TODO Auto-generated method stub		
		loadData();
	}
}
