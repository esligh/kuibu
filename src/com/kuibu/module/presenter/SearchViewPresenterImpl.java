package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kuibu.data.global.StaticValue;
import com.kuibu.model.SearchViewModelImpl;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.model.interfaces.SearchViewModel;
import com.kuibu.module.presenter.interfaces.SearchViewPresenter;
import com.kuibu.module.request.listener.OnSearchViewListener;
import com.kuibu.ui.view.interfaces.SearchView;

public class SearchViewPresenterImpl implements SearchViewPresenter,OnSearchViewListener{

	private SearchView mView; 
	private SearchViewModel mModel;

	private List<TopicItemBean> topicDatas ;
	private List<Map<String,Object>> userDatas  ;
	private List<Map<String,String>> contentDatas ;
	
	public SearchViewPresenterImpl(SearchView view) {
		this.mView = view ; 
		this.mModel = new SearchViewModelImpl(this);
	}
	
	@Override
	public void requestContent(String query)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("off",contentDatas==null ? "0":String.valueOf(contentDatas.size()));
		params.put("slice", query);
		mModel.requestConent(params);
	}
	
	@Override
	public void requestUsers(String query)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("off",userDatas==null ? "0":String.valueOf(userDatas.size()));
		params.put("slice", query);
		mModel.requestUsers(params);
	}
	
	@Override
	public void requestTopics(String query)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("off",topicDatas==null ? "0":String.valueOf(topicDatas.size()));
		params.put("slice", query);
		mModel.requestTopics(params);
	}
	
	@Override
	public void clearContent() {
		// TODO Auto-generated method stub
		if(contentDatas != null){
			contentDatas.clear();
		}
	}
	
	@Override
	public void clearUsers() {
		// TODO Auto-generated method stub
		if(userDatas != null){
			userDatas.clear();
		}
	}
	
	@Override
	public void clearTopics() {
		// TODO Auto-generated method stub
		if(topicDatas != null){
			topicDatas.clear();
		}
	}

	@Override
	public void OnLoadUsersSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONArray arr = new JSONArray(response
						.getString("result"));
				if (arr != null) {
					if(userDatas == null)
						userDatas = new ArrayList<Map<String,Object>>();
					for(int i=0;i<arr.length();i++){
					    JSONObject obj = (JSONObject) arr.get(i);
					    Map<String,Object> item = new HashMap<String,Object>();
					    item.put("uid", obj.getString("id"));
					    item.put("sex",obj.getString("sex"));
					    item.put("name", obj.getString("name"));
					    item.put("signature", obj.getString("signature"));
					    item.put("photo", obj.getString("photo"));							    
					    userDatas.add(item);
					}
					mView.refreshUserList(userDatas);
				}
			}
			mView.loadComplete();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnLoadContentSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data); 
				if(contentDatas == null)
					contentDatas = new ArrayList<Map<String,String>>();
				for(int i=0;i<arr.length();i++){
					JSONObject obj = arr.getJSONObject(i);
					HashMap<String,String> item = new HashMap<String,String>();
					String tag = obj.getString("tag");
					item.put("tag", tag);
					if(tag.equals("collectpack")){
						item.put("item_id", obj.getString("id"));
						item.put("item_title", obj.getString("pack_name"));
						item.put("item_count_1", obj.getString("collect_count"));
						item.put("item_count_2", obj.getString("focus_count"));		
						item.put("create_by", obj.getString("create_by"));
						item.put("csn", obj.getString("csn"));
					}else{
						item.put("item_id", obj.getString("id"));
						item.put("cisn", obj.getString("cisn"));
						item.put("item_title", obj.getString("title"));
						item.put("item_count_1", obj.getString("vote_count"));
					}
					contentDatas.add(item);
				}
				mView.refreshContentList(contentDatas);
			}
			mView.loadComplete();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnLoadTopicsSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(topicDatas == null)
					topicDatas = new ArrayList<TopicItemBean>();
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					TopicItemBean item = new TopicItemBean();
					item.setId(obj.getString("tid"));
					item.setTopic(obj.getString("topic_name"));
					item.setIntroduce(obj.getString("topic_desc"));
					item.setTopicPicUrl(obj.getString("topic_pic"));
					topicDatas.add(item);
				}
				mView.refreshTopicList(topicDatas);
			}
			mView.loadComplete();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void cancelRequest() {
		// TODO Auto-generated method stub
		mModel.cancelRequest();
	}	
	
}
