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
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.FollowedPackModelImpl;
import com.kuibu.model.entity.CollectPackItemBean;
import com.kuibu.model.interfaces.FollowedPackModel;
import com.kuibu.module.presenter.interfaces.FollowedPackPresenter;
import com.kuibu.module.request.listener.OnFollowedPackListener;
import com.kuibu.ui.view.interfaces.FollowedPackView;

public class FollowedPackPresenterImpl implements
	FollowedPackPresenter,OnFollowedPackListener{
	
	private FollowedPackModel mModel ;
	private FollowedPackView mView ; 
	private List<CollectPackItemBean> datas = new ArrayList<CollectPackItemBean>();
	
	public FollowedPackPresenterImpl(FollowedPackView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new FollowedPackModelImpl(this);
	}

	@Override
	public void loadFollowedPackList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("target", StaticValue.SERMODLE.FOCUS_TARGET_COLLECTPACK);
		params.put("off", String.valueOf(datas.size()));
		mModel.requestFollowedPackList(params);
	}
	
	@Override
	public void OnFollwedPackListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr.length()>0){
					for (int i = 0; i < arr.length(); i++) {
						JSONObject temp = (JSONObject) arr.get(i);
						CollectPackItemBean bean = new CollectPackItemBean();
						bean.setId(temp.getString("pid"));
						bean.setPackName(temp.getString("pack_name"));
						bean.setPackDesc(temp.getString("pack_desc"));
						bean.setPackType(temp.getString("pack_type"));
						bean.setFollowCount(temp.getString("follow_count"));
						bean.setCollectCount(temp.getString("collect_count"));
						bean.setCreateBy(temp.getString("create_by"));
						datas.add(bean);
					}
				}	
				//adapter.notifyDataSetChanged();
				mView.refreshList(datas);
			}
			if (datas.isEmpty()){
				mView.setMultiStateView(MultiStateView.ViewState.EMPTY);						
			} else {
				mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}
			mView.stopRefresh();
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

	@Override
	public CollectPackItemBean getDataItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

}
