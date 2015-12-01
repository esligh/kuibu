package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.ExploreFavoriteModelImpl;
import com.kuibu.model.interfaces.ExploreFavoriteModel;
import com.kuibu.module.presenter.interfaces.ExploreFavoritePresenter;
import com.kuibu.module.request.listener.OnExploreFavoriteListener;
import com.kuibu.ui.view.interfaces.ExploreFavoriteView;

public class ExploreFavoritePresenterImpl implements 
		ExploreFavoritePresenter,OnExploreFavoriteListener{
	
	private List<Map<String, String>> mdatas = new ArrayList<Map<String, String>>();
	private ExploreFavoriteModel mModel ;
	private ExploreFavoriteView mView ; 
	
	public ExploreFavoritePresenterImpl(ExploreFavoriteView view) {
		// TODO Auto-generated constructor stub
		this.mView = view; 
		this.mModel = new ExploreFavoriteModelImpl(this);
	}	
	
	@Override
	public void loadFavoriteList(String action) {
		// TODO Auto-generated method stub
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
		mModel.requestFavoriteList(params);
	}
	
	@Override
	public void onLoadFavoriteListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				String action = response.getString("action");
				JSONArray arr = new JSONArray(data);						
				loadFromArray(arr,action);
				mView.stopRefresh();
			}
		} catch (JSONException e) {
			e.printStackTrace();					
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		if(mdatas.isEmpty()){
			mView.setMultiStateView(MultiStateView.ViewState.ERROR);
		}
		mView.stopRefresh();
	}
	
	private void loadFromArray(JSONArray arr,String action) {
		try {
			if(arr.length()>0){
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					Map<String, String> item = new HashMap<String, String>();
					item.put("box_id", temp.getString("box_id"));
					item.put("box_type", temp.getString("box_type"));
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
				mView.refreshList(mdatas);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mdatas.size() > 0) {
			mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
		} else {
			mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
		}
	}

	@Override
	public Map<String, String> getDataItem(int position) {
		// TODO Auto-generated method stub
		return mdatas == null ? new HashMap<String,String>() : mdatas.get(position);
	}
}
