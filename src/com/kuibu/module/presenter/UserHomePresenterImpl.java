package com.kuibu.module.presenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.UserHomeModelImpl;
import com.kuibu.model.interfaces.UserHomeModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.UserHomePresenter;
import com.kuibu.module.request.listener.OnUserHomeListener;
import com.kuibu.ui.view.interfaces.UserHomeView;

public class UserHomePresenterImpl implements UserHomePresenter,OnUserHomeListener{

	private UserHomeView mView;
	private UserHomeModel mModel ;
	private Map<String,Object> uInfo = new HashMap<String,Object>() ; 
	private boolean bUserIsFollow;  	
	
	public UserHomePresenterImpl(UserHomeView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new UserHomeModelImpl(this);
	}

	@Override
	public void followCollector() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.COLLECTOR_TYPE);
		params.put("obj_id", (String)uInfo.get("uid"));
		final String URL;
		if(bUserIsFollow){ 
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_follows").toString();
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
				  .append(Constants.Config.REST_API_VERSION)
				  .append("/add_follows").toString();
		}
		params.put("URL", URL);
		mModel.followCollector(params);
	}

	@Override
	public void OnFollowCollectorResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				int count = Integer.parseInt(mView.getFollowCount());						
				if(bUserIsFollow){
					mView.setFollowMeCount(DataUtils.formatNumber(count-1));
					int btnColor= ContextCompat.getColor(KuibuApplication.getContext(),
							R.color.fbutton_color_green_sea);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_focus));
					bUserIsFollow = false; 
				}else{
					mView.setFollowMeCount(DataUtils.formatNumber(count+1));
					
					int btnColor= ContextCompat.getColor(KuibuApplication.getContext(),
							R.color.fbutton_color_concrete);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_cancel_focus));
					bUserIsFollow = true; 
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getUserinfo() {
		// TODO Auto-generated method stub
		return uInfo;
	}

	@Override
	public void loadUserinfo() {
		// TODO Auto-generated method stub	
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", (String)uInfo.get("uid"));
		params.put("requestor_id", Session.getSession().getuId());
		mModel.requestUserinfo(params);
	}

	@Override
	public void OnLoadUserinfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONObject obj = new JSONObject(response.getString("result"));
				mView.setPackCount(DataUtils.formatNumber(obj.getInt("pack_count")));
				mView.setMeFollowCount(DataUtils.formatNumber(obj.getInt("my_follow_count")));
				mView.setFollowMeCount(DataUtils.formatNumber(obj.getInt("follow_me_count")));
				mView.setEmail(obj.getString("email"));
				
				String profession = obj.getString("profession");
				if(TextUtils.isEmpty(profession) || profession.equals("null")){
					mView.setProfession("暂无职业描述");
				}else{
					
					mView.setProfession(profession);
				}
				String residence = obj.getString("residence");
				if(TextUtils.isEmpty(residence) || residence.equals("null")){
					mView.setResidence("暂无");
					
				}else{
					mView.setResidence(residence);
				}
				bUserIsFollow = obj.getBoolean("is_focus");
				if(bUserIsFollow && Session.getSession().isLogin()){
					if(mView.getFragment().isAdded()){
						int btnColor= ContextCompat.getColor(KuibuApplication.getContext(),
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
	public void OnVolleyError(VolleyError response) {
		// TODO Auto-generated method stub
		
	}	
}
