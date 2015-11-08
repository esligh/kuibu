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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
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
    private MultiStateView mMultiStateView;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_homepage, container,false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData("INIT",true);				
			}
        });
		mListView = (PaginationListView) rootView.findViewById(R.id.pagination_lv);
		pullFreshlayout = (PullRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		
		pullFreshlayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
		mListView.setOnLoadListener(this);
		pullFreshlayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData("DOWN",true);
            }
        });
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_LIST_CACHE);
		if(arr!=null && arr.length()>0){
			parseFromJson(arr,"UP");
		}else{
			loadData("INIT",true);
		}
		showHomeView();
		return rootView;
	}

	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		KuibuApplication.getInstance().cancelPendingRequests(
				StaticValue.TAG_VLAUE.HOME_PAGE_VOLLEY);
	}


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onHiddenChanged(boolean) 
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){ //hiding now 
			
		}else{//showing now 
			loadData("DOWN",false);
		}
	}

	private void showHomeView() {
		if (homeListViewAdapter == null) {
			homeListViewAdapter = new HomeListViewItemAdapter(getActivity(),
					mHomeDatas);
			mListView.setAdapter(homeListViewAdapter);
		} else {
			homeListViewAdapter.updateView(mHomeDatas);
		}
	}

	@Override
	public void onLoadMore() {
		loadData("UP",false);
	}
		
	public void loadData(final String action,final boolean bcache)
	{
		Map<String, String> params = new HashMap<String, String>();
		int size = mHomeDatas.size(); 
		params.put("data_type", "HOME_LIST");
		params.put("uid", Session.getSession().getuId());
		params.put("action", action);
		if(action.equals("UP") && size>0)
			params.put("threshold",mHomeDatas.get(size-1).getId());
		else if(action.equals("DOWN") && size>0)
			params.put("threshold",mHomeDatas.get(0).getId());
		else 
			params.put("threshold",Constants.THRESHOLD_INVALID);
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
						parseFromJson(arr,action);
						pullFreshlayout.setRefreshing(false);
						mListView.loadComplete();
						
						if(arr.length()>0){
							if(action.equals("INIT")&& bcache){
							    	KuibuApplication.getCacheInstance()
							    	.put(StaticValue.LOCALCACHE.HOME_LIST_CACHE, arr);
							}else if(action.equals("DOWN")&& bcache){
							    	JSONArray oldarr = KuibuApplication.getCacheInstance()
							    			.getAsJSONArray(StaticValue.LOCALCACHE.HOME_LIST_CACHE);
							    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
							    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
							    	KuibuApplication.getCacheInstance()
							    	.put(StaticValue.LOCALCACHE.HOME_LIST_CACHE, newarr);
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
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				if(mHomeDatas.isEmpty())
					mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
				pullFreshlayout.setRefreshing(false);
				mListView.loadComplete();
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_SHORT, 
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req,
				StaticValue.TAG_VLAUE.HOME_PAGE_VOLLEY);	
	}
	
	private void parseFromJson(JSONArray arr,String action)
	{
		try{
			int size = arr.length();
			if(size > 0 ){ 
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
				    bean.setPackId(temp.getString("pid"));
				    bean.setCreateBy(temp.getString("create_by"));
				    bean.setVoteCount(temp.getInt("vote_count"));
				    bean.setUserSex(temp.getString("sex"));
				    bean.setUserSignature(temp.getString("signature"));
				    bean.setCommentCount(temp.getInt("comment_count"));
				    bean.setTopText(temp.getString("name"));
				    bean.setTopUrl(temp.getString("photo"));
				    bean.setLastModify(temp.getString("last_modify"));
				    if(action.equals("DOWN")){
				    	mHomeDatas.add(0,bean);
				    }else{
				    	mHomeDatas.add(bean);
				    }						
				}
				showHomeView();	//有数据 刷新
			}
			
			if(mHomeDatas.size()>0){
				mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
			}else{
				mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
			}	
		}catch (JSONException e) {
			e.printStackTrace();
			mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
		}		
	}
}
