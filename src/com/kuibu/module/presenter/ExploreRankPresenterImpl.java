package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.ExploreRankModelImpl;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.model.interfaces.ExploreRankModel;
import com.kuibu.module.presenter.interfaces.ExploreRankPresenter;
import com.kuibu.module.request.listener.OnExploreRankListener;
import com.kuibu.ui.view.interfaces.ExploreRankView;

public class ExploreRankPresenterImpl implements ExploreRankPresenter,
		OnExploreRankListener{
	
	private ExploreRankView mView; 
	private ExploreRankModel mModel ; 
	private List<MateListItem> mdatas = new ArrayList<MateListItem>();
	
	public ExploreRankPresenterImpl(ExploreRankView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new ExploreRankModelImpl(this);
	}

	@Override
	public void loadRankList(String action) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mdatas.size()));
		params.put("data_type", "HOT_RANK");
		params.put("action", action);
		int n = mdatas.size(); 
		if(action.equals("REQ_HISTORY")){			
			params.put("threshold",String.valueOf(mdatas.get(n-1).getVoteCount()));
		}
		else if(action.equals("REQ_NEWDATA") && n >0){
			params.put("threshold",String.valueOf(mdatas.get(0).getVoteCount()));
		}
		mModel.requestRankList(params);
	}

	@Override
	public void onLoadRankListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				String action = response.getString("aciton");
				JSONArray arr = new JSONArray(data);
				loadFromArray(arr,action);
				mView.stopRefresh();
				if(arr.length()>0){
					if(action.equals("REQ_NEWDATA")){
						JSONArray oldarr = KuibuApplication.getCacheInstance()
								.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RANK_CACHE);
				    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
				    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
				    	KuibuApplication.getCacheInstance()
				    	.put(StaticValue.LOCALCACHE.HOME_RANK_CACHE, newarr);
					}							
				}			
			}					
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		if(mdatas!=null && mdatas.isEmpty()){			
			mView.setMultiStateView(MultiStateView.ViewState.ERROR);
		}
		mView.stopRefresh();		
		KuibuUtils.showText(Toast.LENGTH_SHORT, Toast.LENGTH_SHORT);
	}	

	private void loadFromArray(JSONArray arr,String action)
	{
			try{
				int size = arr.length() ; 
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
					    bean.setTopText(temp.getString("name"));
					    bean.setTopUrl(temp.getString("photo"));
					    bean.setUserSex(temp.getString("sex"));
					    bean.setUserSignature(temp.getString("signature"));
					    bean.setPackId(temp.getString("pid"));
					    bean.setCreateBy(temp.getString("create_by"));
					    bean.setVoteCount(temp.getInt("vote_count"));
					    bean.setCommentCount(temp.getInt("comment_count"));		
					    if(action.equals("REQ_NEWDATA")){
					    	mdatas.add(0,bean);
					    }else{
					    	mdatas.add(bean);
					    }				    
					}
					mView.refreshList(mdatas);
			}
			if(mdatas.size()>0){
				mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}else{
				mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void loadFromLocal() {
		// TODO Auto-generated method stub
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_RANK_CACHE);
		if(arr!=null){
			loadFromArray(arr,"REQ_HISTORY");
		}
	}	
}
