package com.kuibu.ui.fragment;

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
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HomeListViewItemAdapter;

/**
 * kuibu 主页
 * @author ThinkPad
 */
public class HomePageFragment extends Fragment {
	
	private HomeListViewItemAdapter homeListViewAdapter = null;
	private List <MateListItem> mHomeDatas = new ArrayList <MateListItem>();  
    private PullToRefreshListView mPullRefreshListView;
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
        mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pullToRefreshListView);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setPullToRefreshOverScrollEnabled(false);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			//下拉刷新
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub			
				loadData("DOWN",true);
				mPullRefreshListView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.HOME_LAST_REFRESH_TIME));
			
			}

			//向上拖动刷新
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData("UP",false);
				refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.HOME_LAST_REFRESH_TIME));
			}			
		});
		
		//到底部时自动加载
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				loadData("UP",false);
				mPullRefreshListView.setRefreshingFooter();
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
		KuibuApplication.getInstance().cancelPendingRequests(this);
	}


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onHiddenChanged(boolean) 
	 */

	private void showHomeView() {
		if (homeListViewAdapter == null) {
			homeListViewAdapter = new HomeListViewItemAdapter(getActivity(),
					mHomeDatas);
			mPullRefreshListView.setAdapter(homeListViewAdapter);
		} else {
			homeListViewAdapter.updateView(mHomeDatas);
		}
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
				mPullRefreshListView.onRefreshComplete();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {				
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				if(!mHomeDatas.isEmpty()){
					mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
				}else{
					mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
				}					
				mPullRefreshListView.onRefreshComplete();
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_SHORT, 
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req,this);	
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
