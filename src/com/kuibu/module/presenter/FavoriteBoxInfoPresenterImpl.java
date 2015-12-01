package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.FavoriteBoxInfoModelImpl;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.model.interfaces.FavoriteBoxInfoModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.FavoriteBoxInfoPresenter;
import com.kuibu.module.request.listener.OnFavoriteBoxInfoListener;
import com.kuibu.ui.view.interfaces.FavoriteBoxInfoView;

public class FavoriteBoxInfoPresenterImpl implements 
	FavoriteBoxInfoPresenter,OnFavoriteBoxInfoListener{
	
	private FavoriteBoxInfoModel mModel; 
	private FavoriteBoxInfoView mView ; 	
	private List<CollectionBean> cardDatas = new ArrayList<CollectionBean>(); 
	private List<CollectionItemBean> itemDatas = new ArrayList<CollectionItemBean>();
	private Map<String, Object> userInfo;
	private boolean isfocus;
	private boolean bUserIsFollow;
	private String type ; 
	private String box_id;
	
	public FavoriteBoxInfoPresenterImpl(FavoriteBoxInfoView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new FavoriteBoxInfoModelImpl(this);
		box_id = mView.getDataIntent().getStringExtra("box_id");
	}
		
	private void parseBoxList(JSONArray arr) throws JSONException
	{
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionBean bean = new CollectionBean();
				bean.cid = temp.getString("id");
				bean.title = temp.getString("title");
				bean.cover = temp.getString("cover");
				bean.content = temp.getString("abstract");
				bean.createBy = temp.getString("create_by");
				bean.isPublish = 1; 
				cardDatas.add(bean);
			}
			mView.setBarTitle(new StringBuilder("共有").
					append(cardDatas.size()).append("条收集").toString());
		}else{
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionItemBean bean = new CollectionItemBean();
				bean.setId(temp.getString("id"));
				bean.setTitle(temp.getString("title"));
				bean.setCisn(temp.getString("cisn"));
				bean.setSummary(temp.getString("abstract"));
				bean.setCreateBy(temp.getString("create_by"));
				bean.setVoteCount(temp.getString("vote_count"));
			    bean.setCreatorSex(temp.getString("sex"));
			    bean.setCreatorSignature(temp.getString("signature"));
			    bean.setCreatorName(temp.getString("name"));
			    bean.setCreatorPic(temp.getString("photo"));								    
				itemDatas.add(bean);
			}
			mView.setBarTitle(new StringBuilder("共有").
					append(itemDatas.size()).append("条收集").toString());
		}		
	}
	
	@Override
	public void loadUserinfo() {
		Map<String, String> params = new HashMap<String, String>();
		String create_by = mView.getDataIntent().getStringExtra("create_by");
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", create_by);	
		mModel.requestUserinfo(params);
	}

	@Override
	public void loadBoxInfo() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("box_id", box_id);
		mModel.requestBoxInfo(params);
	}

	@Override
	public void followBox() {
		// TODO Auto-generated method stub
		if(!Session.getSession().isLogin()){
			KuibuUtils.showText(R.string.need_login, Toast.LENGTH_SHORT);
			return ;
		}		
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.FAVORITE_TYPE);
		params.put("obj_id", box_id);
		final String URL;
		if (isfocus) {
			URL = new StringBuilder(Constants.Config.SERVER_URI)
			.append(Constants.Config.REST_API_VERSION)
			.append("/del_follows").toString();
		} else {
			URL = new StringBuilder(Constants.Config.SERVER_URI)
			.append(Constants.Config.REST_API_VERSION)
			.append("/add_follows").toString();
		}
		params.put("URL", URL);
		mModel.followBox(params);
	}

	@Override
	public void loadBoxList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("box_id", box_id);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			params.put("off", cardDatas.size()+"");
		}else{
			params.put("off", itemDatas.size() + "");
		}
		mModel.requestBoxList(params);
	}

	@Override
	public void OnLoadBoxInfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONObject obj = new JSONObject(response
						.getString("result"));
				if (obj != null) {
					mView.setBoxTitle(obj.getString("box_name"));
					String desc = obj.getString("box_desc");
					if(TextUtils.isEmpty(desc)){
						mView.setBoxDescVisible(View.GONE);
					}else{
						mView.setBoxDesc(desc);
					}													
					isfocus = obj.getBoolean("is_focus");
					mView.setFollowCount(obj.getString("focus_count"));
					if (isfocus) {
						int btnColor = ContextCompat.getColor(KuibuApplication.getContext(),
								R.color.fbutton_color_concrete);
						mView.setFollowBtnColor(btnColor);
						mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_cancel_focus));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnFollowBoxResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				int count = Integer.parseInt(mView.getFollowCount());
				if (isfocus){
					mView.setFollowCount(DataUtils.formatNumber(count-1));
					int btnColor = ContextCompat.getColor(KuibuApplication.getContext(),
							R.color.fbutton_color_green_sea);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_focus));
				}else{
					mView.setFollowCount(DataUtils.formatNumber(count+1));

					int btnColor = ContextCompat.getColor(KuibuApplication.getContext(),
							R.color.fbutton_color_concrete);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_cancel_focus));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void OnLoadBoxListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr!=null && arr.length()>0){
					parseBoxList(arr);
					showView();
				}								
			} 
			mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			mView.loadComplete();
			mView.setFooterViewVisible(View.GONE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnLoadUserinfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONObject obj = new JSONObject(response
						.getString("result"));
				if (obj != null) {
					userInfo = new HashMap<String, Object>();
					userInfo.put("uid", obj.getString("id"));
					userInfo.put("name", obj.getString("name"));
					userInfo.put("signature",
							obj.getString("signature"));
					userInfo.put("sex", obj.getString("sex"));
					bUserIsFollow = obj.getBoolean("is_focus");
					mView.setUserName((String) userInfo.get("name"));
					mView.setUserSignature((String) userInfo.get("signature"));
					String url = obj.getString("photo");
					userInfo.put("photo", url);
					mView.setUserPic(url);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		
	}
	
	private void showView() {
		if (StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)) {
			mView.refreshPList(cardDatas);
		} else {
			mView.refreshWList(itemDatas);
		}
	}

	@Override
	public Map<String, Object> getUserinfo() {
		// TODO Auto-generated method stub
		return userInfo;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public boolean isFollow() {
		// TODO Auto-generated method stub
		return bUserIsFollow;
	}

	@Override
	public CollectionItemBean getDateItem(int position) {
		// TODO Auto-generated method stub
		return itemDatas.get(position);
	}
	
}
