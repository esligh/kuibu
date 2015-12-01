package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.content.ContextCompat;

import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.TopicInfoModelImpl;
import com.kuibu.model.interfaces.TopicInfoModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.TopicInfoPresenter;
import com.kuibu.module.request.listener.OnTopicInfoListener;
import com.kuibu.ui.view.interfaces.TopicInfoView;

public class TopicInfoPresenterImpl implements TopicInfoPresenter,OnTopicInfoListener{

	private TopicInfoModel mModel ;
	private TopicInfoView mView ;

	private List<Map<String,Object>> mDatas= new ArrayList<Map<String,Object>>();
	private boolean bIsFocus ; 
	private String topic_id ;
	
	public TopicInfoPresenterImpl(TopicInfoView view)
	{
		this.mView = view ;
		this.mModel = new TopicInfoModelImpl(this);
	}
	

	@Override
	public void loadTopicInfo() {
		String topic_name =  mView.getDataIntent().
				getStringExtra(StaticValue.TOPICINFO.TOPIC_NAME);
		mView.setBarTitle(topic_name);
		String pic_url =  mView.getDataIntent().getStringExtra(StaticValue.TOPICINFO.TOPIC_PIC);
		topic_id =  mView.getDataIntent().
				getStringExtra(StaticValue.TOPICINFO.TOPIC_ID);
		mView.setTopicName(topic_name);
		
		mView.setTopicPic(pic_url);		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", topic_id);
		mModel.requestTopicInfo(params);
	}
	

	@Override
	public void loadUserList() {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("tid", topic_id);	
		mModel.requestAuthorList(params);	
	}
	

	@Override
	public void follow() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.TOPIC_TYPE);
		params.put("obj_id", topic_id);
		final String URL;
		if(bIsFocus){ 
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/del_follows";
		}else{
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/add_follows";
		}
		params.put("URL", URL);
		mModel.doFollow(params);			
	}


	@Override
	public void onTopicInfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
			try {
				String state = response.getString("state");
				if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
					JSONObject obj = new JSONObject(response
							.getString("result"));
					if (obj != null) {
						mView.setFollowCount(DataUtils.formatNumber(obj.getInt("focus_count")));
						bIsFocus = obj.getBoolean("is_focus");
						if(bIsFocus){
							int btnColor= ContextCompat.getColor(mView.getInstance(),R.color.fbutton_color_concrete);
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
	public void onUserListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONArray arr = new JSONArray(response
						.getString("result"));
				if (arr != null) {
					for(int i=0;i<arr.length();i++){
					    JSONObject obj = (JSONObject) arr.get(i);
					    Map<String,Object> item = new HashMap<String,Object>();
					    item.put("uid", obj.getString("author_id"));
					    item.put("sex",obj.getString("author_sex"));
					    item.put("name", obj.getString("author_name"));
					    item.put("signature", obj.getString("author_signature"));
					    item.put("photo", obj.getString("author_pic"));
					    mDatas.add(item);
					}
				}
				mView.refreshList(mDatas);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onFollowResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				int count = Integer.parseInt(mView.getFollowCount());
				if(bIsFocus){
					mView.setFollowCount(DataUtils.formatNumber(count-1));
					int btnColor= ContextCompat.getColor(mView.getInstance(),R.color.fbutton_color_green_sea);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_focus));
					bIsFocus = false; 
				}else{
					mView.setFollowCount(DataUtils.formatNumber(count+1));					
					int btnColor= ContextCompat.getColor(mView.getInstance(),R.color.fbutton_color_concrete);
					mView.setFollowBtnColor(btnColor);
					mView.setFollowBtnText(KuibuUtils.getString(R.string.btn_cancel_focus));
					bIsFocus = true; 
				}
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}

}
