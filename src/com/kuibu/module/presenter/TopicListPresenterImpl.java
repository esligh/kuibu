package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kuibu.data.global.StaticValue;
import com.kuibu.model.TopicListModelImpl;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.model.interfaces.TopicListModel;
import com.kuibu.module.presenter.interfaces.TopicListPresenter;
import com.kuibu.module.request.listener.OnTopicListListener;
import com.kuibu.ui.view.interfaces.TopicListView;

public class TopicListPresenterImpl implements TopicListPresenter,OnTopicListListener{

	private TopicListModel mModel ; 
	private TopicListView mView ;  
	private List<TopicItemBean> datas = new ArrayList<TopicItemBean>();

	public TopicListPresenterImpl(TopicListView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new TopicListModelImpl(this);
	}

	@Override
	public void OnTopicListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data); 
				for(int i=0;i<arr.length();i++){
					JSONObject obj = arr.getJSONObject(i);
					TopicItemBean item = new TopicItemBean();
					item.setId(obj.getString("id"));
					item.setTopic(obj.getString("topic_name"));
					item.setIntroduce(obj.getString("topic_desc"));
					
					item.setTopicPicUrl(obj.getString("topic_pic"));
					datas.add(item);
				}
				mView.refreshList(datas);
			}					
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void loadTopicList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("topic_ids", mView.getDataIntent().getStringExtra("topic_id"));
		mModel.requestTopicList(params);
	}

	@Override
	public List<TopicItemBean> getListData() {
		// TODO Auto-generated method stub
		return datas;
	}
	
}
