package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.kuibu.common.utils.ACache;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HomeListViewItemAdapter;

/**
 * kuibu 主页
 * @author ThinkPad
 */
public class HomePageFragment extends Fragment implements OnLoadListener{
	private HomeListViewItemAdapter homeListViewAdapter = null;
	private List <MateListItem> mHomeDatas = new ArrayList <MateListItem>();
	private PaginationListView mListView;  
    private PullRefreshLayout pullFreshlayout;
    private ACache mCache  ;
    private boolean isCache = false;
    private MultiStateView mMultiStateView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				initHomeListData();				
			}   	
        });          
		mCache = ACache.get(this.getActivity());
		mListView = (PaginationListView) rootView.findViewById(R.id.pagination_lv);
		pullFreshlayout = (PullRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		pullFreshlayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
		mListView.setOnLoadListener(this);
		pullFreshlayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoreData("DOWN");
            }
        });
		JSONArray arr = mCache.getAsJSONArray(StaticValue.LOCALCACHE.HOME_LIST_CACHE);
		if(arr!=null){
			parseFromJson(arr);
		}else{
			initHomeListData();
		}		
		showHomeView();
		return rootView;
	}
	
	private void parseFromJson(JSONArray arr)
	{
		if(arr.length()>0){
			try{
				for (int i = 0; i < arr.length(); i++) {
				    JSONObject temp = (JSONObject) arr.get(i);
				    MateListItem bean = new MateListItem();
				    bean.set_id(temp.getString("cid"));
				    bean.setType(Integer.parseInt(temp.getString("type")));
				    bean.setTitle(temp.getString("title"));
				    bean.setSummary(temp.getString("content"));
				    bean.setItemPic(temp.getString("image_url"));			    
				    bean.setPackId(temp.getString("pid"));
				    bean.setCreateBy(temp.getString("create_by"));
				    bean.setVoteCount(temp.getInt("vote_count"));
				    bean.setUserSex(temp.getString("sex"));
				    bean.setUserSignature(temp.getString("signature"));
				    bean.setCommentCount(temp.getInt("comment_count"));
				    bean.setTopText(temp.getString("name"));
				    bean.setTopUrl(temp.getString("photo"));
				    bean.setLastModify(temp.getString("last_modify"));
					mHomeDatas.add(bean);	
				}
				showHomeView();	
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
		}else{
			mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
		}		
	}

	private void initHomeListData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("data_type", "HOME_LIST");
		params.put("off", mHomeDatas.size()+"");
		params.put("uid", Session.getSession().getuId());
		params.put("action", "INIT");
		params.put("threshold","");
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
						parseFromJson(arr);
						if(!isCache){ 
							mCache.put(StaticValue.LOCALCACHE.HOME_LIST_CACHE, arr);
							isCache = true ; 
						}						
					}else{
							mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	private void showHomeView() {
		if (homeListViewAdapter == null) {
			homeListViewAdapter = new HomeListViewItemAdapter(getActivity(),
					mHomeDatas); // NullPointException ??
			mListView.setAdapter(homeListViewAdapter);
		} else {
			homeListViewAdapter.updateView(mHomeDatas);
		}
	}

	@Override
	public void onLoad(String tag) {
		// TODO Auto-generated method stub
		getMoreData("UP");
	}
	
	public void getMoreData(final String action)
	{
		Map<String, String> params = new HashMap<String, String>();
		int n = mHomeDatas.size(); 
		params.put("data_type", "HOME_LIST");
		params.put("uid", Session.getSession().getuId());
		params.put("action", action);
		if(action.equals("UP"))
			params.put("threshold",mHomeDatas.get(n-1).get_id());
		else if(action.equals("DOWN"))
			params.put("threshold",mHomeDatas.get(0).get_id());
			
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
							for (int i = 0; i < arr.length(); i++) {
							    JSONObject temp = (JSONObject) arr.get(i);
							    MateListItem bean = new MateListItem();
							    bean.set_id(temp.getString("cid"));
							    bean.setType(Integer.parseInt(temp.getString("type")));
							    bean.setTitle(temp.getString("title"));
							    bean.setSummary(temp.getString("content"));
							    bean.setItemPic(temp.getString("image_url"));
							    bean.setPackId(temp.getString("pid"));
							    bean.setTopText(temp.getString("name"));
							    bean.setVoteCount(temp.getInt("vote_count"));
							    bean.setCreateBy(temp.getString("create_by"));
							    bean.setUserSex(temp.getString("sex"));
							    bean.setUserSignature(temp.getString("signature"));
							    bean.setLastModify(temp.getString("last_modify"));
							    bean.setCommentCount(temp.getInt("comment_count"));
							    bean.setTopUrl(temp.getString("photo"));
							    if(action.equals("DOWN"))
							    	mHomeDatas.add(0,bean);
							    else 
							    	mHomeDatas.add(bean);
							}
							showHomeView();
						}else{
							Toast.makeText(getActivity(), 
									"没有数据啦！",Toast.LENGTH_SHORT).show();
						}
					}
					pullFreshlayout.setRefreshing(false);
					mListView.loadComplete();
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
				
				pullFreshlayout.setRefreshing(false);
				mListView.loadComplete();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
}
