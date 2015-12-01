package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.FavoriteBoxModelImpl;
import com.kuibu.model.interfaces.FavoriteBoxModel;
import com.kuibu.module.presenter.interfaces.FavoriteBoxPresenter;
import com.kuibu.module.request.listener.OnFavoriteBoxListener;
import com.kuibu.ui.view.interfaces.FavoriteBoxView;

public class FavoriteBoxPresenterImpl implements 
	FavoriteBoxPresenter,OnFavoriteBoxListener{

	private List<Map<String,String>> datas= new ArrayList<Map<String,String>>() ; 

	private FavoriteBoxView mView ; 
	private FavoriteBoxModel mModel; 
	
	public FavoriteBoxPresenterImpl(FavoriteBoxView view) {
		// TODO Auto-generated constructor stub
		this.mView  = view;
		this.mModel = new FavoriteBoxModelImpl(this) ; 
	}
		
	@Override
	public void loadBoxList() {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
    	String uid = mView.getDataIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
    	if(TextUtils.isEmpty(uid)){
    		Bundle args = mView.getDataArguments() ;
    		uid = args.getString(StaticValue.USERINFO.USER_ID);
    	}
		params.put("uid",uid);
		params.put("off", String.valueOf(datas.size()));
		mModel.requestBoxList(params);
	}

	@Override
	public void delBox(final int position) {
		// TODO Auto-generated method stub
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid",Session.getSession().getuId());
		params.put("box_id", datas.get(position).get("box_id"));
		params.put("position", Integer.valueOf(position));
		mModel.requestDelBox(params);
	}

	@Override
	public void OnLoadBoxListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				if(!TextUtils.isEmpty(data)){
					JSONArray arr = new JSONArray(data);														
					loadFromArray(arr);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnDelBoxResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				datas.remove(response.getInt("position"));
				mView.refreshList(datas);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		if(datas.isEmpty()){
			mView.setMultiStateView(MultiStateView.ViewState.ERROR);
		}
	}
	
	private void loadFromArray(JSONArray arr)
    {
		try{
			for(int i=0;i<arr.length();i++){
				JSONObject obj = arr.getJSONObject(i);
				Map<String,String> item = new HashMap<String,String>();
				item.put("box_id", obj.getString("box_id"));
				item.put("box_type", obj.getString("box_type"));
				item.put("box_name", obj.getString("box_name"));
				item.put("box_desc", obj.getString("box_desc"));
				item.put("box_count", obj.getString("box_count"));
				item.put("focus_count", obj.getString("focus_count"));
				item.put("titles", obj.getString("titles"));
				datas.add(item);
			}
			mView.refreshList(datas);
    	}catch (JSONException e) {
			e.printStackTrace();
		}
    	if(datas.size()>0){
    		mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
    	}else{
    		mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
    	} 	
    }

	@Override
	public Map<String, String> getDataItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

}
