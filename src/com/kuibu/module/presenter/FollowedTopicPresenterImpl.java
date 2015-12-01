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
import com.kuibu.model.FollowedTopicModelImpl;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.model.interfaces.FollowedTopicModel;
import com.kuibu.module.presenter.interfaces.FollowedTopicPresenter;
import com.kuibu.module.request.listener.OnFollowedTopicListener;
import com.kuibu.ui.view.interfaces.FollowedTopicView;

public class FollowedTopicPresenterImpl implements 
	FollowedTopicPresenter,OnFollowedTopicListener{
	
	private List<TopicItemBean> mdatas = new ArrayList<TopicItemBean>();
	private FollowedTopicView mView; 
	private FollowedTopicModel mModel ; 
	
	public FollowedTopicPresenterImpl(FollowedTopicView view) 
	{
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new FollowedTopicModelImpl(this);
	}
			

	@Override
	public void loadFollowedTopicList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", mView.getAuguments().getString("uid"));
		params.put("target", StaticValue.SERMODLE.FOCUS_TARGET_TOPIC);
		params.put("off", String.valueOf(mdatas.size()));
		mModel.reqeustFollowedTopicList(params);
	}

	
	@Override
	public void OnFollwedTopicListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr.length()>0){
					for (int i = 0; i < arr.length(); i++) {
						JSONObject temp = (JSONObject) arr.get(i);
						TopicItemBean item = new TopicItemBean();
						item.setId(temp.getString("tid"));
						item.setTopic(temp.getString("topic_name"));
						item.setIntroduce(temp.getString("topic_desc"));
						item.setFocusCount(temp.getString("focus_count"));

						item.setTopicPicUrl(temp.getString("topic_pic"));
						mdatas.add(item);
					}
				}
//				adapter.notifyDataSetChanged();
				mView.refreshList(mdatas);
				if (!mdatas.isEmpty()) {
					mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
				} else {
					mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
				}
				mView.stopRefresh();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}


	@Override
	public TopicItemBean getDataItem(int position) {
		// TODO Auto-generated method stub
		return mdatas.get(position);
	}
}
