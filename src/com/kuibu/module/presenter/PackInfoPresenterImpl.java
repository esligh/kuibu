package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

import com.android.volley.VolleyError;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.PackInfoModelImpl;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.model.interfaces.PackInfoModel;
import com.kuibu.module.presenter.interfaces.PackInfoPresenter;
import com.kuibu.module.request.listener.OnPackInfoListener;
import com.kuibu.ui.view.interfaces.PackInfoView;

public class PackInfoPresenterImpl implements PackInfoPresenter,OnPackInfoListener{	
	
	private PackInfoView mView ; 
	private PackInfoModel mModel; 	
	private List<CollectionBean> cardDatas = new ArrayList<CollectionBean>(); 
	private List<CollectionItemBean> itemDatas = new ArrayList<CollectionItemBean>();
	private boolean bFollow;
	private Map<String,String> packInfo = new HashMap<String,String>();
	private String topicIds;
	private String type ; 
	private String packId; 
	public PackInfoPresenterImpl(PackInfoView view) {
		this.mView  = view; 
		this.mModel = new PackInfoModelImpl(this);
	}
			
	@Override
	public void loadPackInfo(final String packId) {
		this.packId = packId; 
		Map<String, String> params = new HashMap<String, String>();
		if(Session.getSession().isLogin()){
			params.put("uid", Session.getSession().getuId());
		}else{
			params.put("uid", "null");
		}		
		params.put("pack_id", packId);
		mModel.requestPackInfo(params);
	}


	@Override
	public void onLoadPackInfoSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONObject obj = new JSONObject(response.getString("result"));
				if (obj != null) {					
					type = obj.getString("pack_type");
					String packName = obj.getString("pack_name");
					packInfo.put("pack_name", packName);
					String createBy = obj.getString("create_by");
					packInfo.put("create_by", createBy);
					String uName = obj.getString("name");
					packInfo.put("user_name", uName);
					packInfo.put("sex", obj.getString("sex"));
					String signature = obj.getString("signature");
					packInfo.put("signature", signature);
					String url  = obj.getString("photo");
					packInfo.put("photo",url);
					String desc = obj.getString("pack_desc"); 
					packInfo.put("desc", desc);
					topicIds = obj.getString("topic_id");
					packInfo.put("topic_names", obj.getString("topic_names"));
					packInfo.put("focus_count", String.valueOf(obj.getInt("focus_count")));
					bFollow = obj.getBoolean("is_focus");					
				}
				mView.setPackInfoView(packInfo);
				mView.showList(type);
				loadPackList(packId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadPackListSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr!=null && arr.length()>0){
					parsePackList(arr,type);
					refreshView(type);
				}																
			}
			mView.loadComplete();
			mView.setFooterVisible(View.GONE);							
			mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void follow(String packId) {
		this.packId = packId; 
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.COLLECTION_TYPE);
		params.put("obj_id", packId);
		mModel.doFollow(params, bFollow);
	}
	
	@Override
	public void loadPackList(String packId) {
		this.packId = packId; 
		Map<String, String> params = new HashMap<String, String>();
		params.put("pack_id", packId);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			params.put("off", String.valueOf(cardDatas.size()));
		}else{
			params.put("off", String.valueOf(itemDatas.size()));
		}
		params.put("uid", Session.getSession().getuId());
		mModel.requestPackList(params);
	}
	
	
	@Override
	public void onFollowSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				mView.setFollowCount();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private void parsePackList(JSONArray arr,String type) throws JSONException
	{	 
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionBean bean = new CollectionBean();
				bean.cid = temp.getString("cid");
				bean.title = temp.getString("title");
				bean.cover = temp.getString("cover");
				bean.content = temp.getString("abstract");
				bean.createBy = temp.getString("create_by");
				bean.isPublish = 1; 
				cardDatas.add(bean);
			}
			mView.setBarTitle("共有"+ cardDatas.size() + "条收集");
		}else{
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionItemBean bean = new CollectionItemBean();
				bean.setId(temp.getString("cid"));
				bean.setCisn(temp.getString("cisn"));
				bean.setTitle(temp.getString("title"));
				bean.setSummary(temp.getString("abstract"));
				bean.setCreateBy(temp.getString("create_by"));
				bean.setVoteCount(temp.getString("vote_count"));
				itemDatas.add(bean);
			}
			mView.setBarTitle("共有"+ itemDatas.size() + "条收集");
		}		
	}
	
	private void refreshView(String type) {
		if (StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)) {
			mView.refreshCardView(cardDatas);
		} else {
			mView.refreshListView(itemDatas);
		}		
	}

	@Override
	public String getTopids() {
		// TODO Auto-generated method stub
		return topicIds;
	}

	@Override
	public Map<String, String> getPackInfo() {
		// TODO Auto-generated method stub
		return packInfo;
	}


	@Override
	public boolean isFollowed() {
		// TODO Auto-generated method stub
		return bFollow;
	}


	@Override
	public void setFollow(boolean state) {
		// TODO Auto-generated method stub
		this.bFollow =state ; 
	}

	@Override
	public void onLoadError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);	
	}

	
}
