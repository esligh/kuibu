package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.MessageModelImpl;
import com.kuibu.model.interfaces.MessageModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.MessagePresenter;
import com.kuibu.module.request.listener.OnMessageListener;
import com.kuibu.ui.view.interfaces.MessageView;

public class MessagePresenterImpl implements MessagePresenter,OnMessageListener{

	private MessageView mView; 
	private MessageModel mModel; 
	private List<Map<String,String>> datas = new ArrayList<Map<String,String>>();

	public MessagePresenterImpl(MessageView view)
	{
		this.mView = view;
		this.mModel = new MessageModelImpl(this);
	}
			
	@Override
	public void loadMessageList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		mModel.requestMessageList(params);
	}

	@Override
	public void OnLoadMessageListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr.length()>0){
					for(int i = 0 ;i<arr.length();i++){
						JSONObject obj = arr.getJSONObject(i);
						Map<String,String> item = new HashMap<String,String>();
						item.put("cid", obj.getString("cid"));
						item.put("name", obj.getString("name"));
						String action_type = obj.getString("action_type");
						if(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION.
								equals(action_type)){
							item.put("desc", KuibuUtils.getString(R.string.vote_prompt));
						}else if(StaticValue.USER_ACTION.ACTION_COMMENT_COLLECTION.
								equals(action_type)){
							item.put("desc", KuibuUtils.getString(R.string.comment_prompt));
						}else if(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION.
								equals(action_type)){
							item.put("desc", KuibuUtils.getString(R.string.collect_prompt));
						}
						item.put("title", obj.getString("title"));
						item.put("type", obj.getString("type"));
						item.put("abstract", obj.getString("abstract"));
						datas.add(item);
					}
					mView.refreshList(datas);
				}						
			}
			if(datas.size()>0){
				mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}else{
				mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
			}
			mView.stopRefresh();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}

}
