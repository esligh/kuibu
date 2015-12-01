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
import com.kuibu.model.LetterModelImpl;
import com.kuibu.model.interfaces.LetterModel;
import com.kuibu.module.presenter.interfaces.LetterPresenter;
import com.kuibu.module.request.listener.OnLetterListener;
import com.kuibu.ui.view.interfaces.LetterView;

public class LetterPresenterImpl implements LetterPresenter,OnLetterListener{
	
	private LetterView mView ;
	private LetterModel mModel ; 
	private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	
	public LetterPresenterImpl(LetterView view)
	{
		this.mView = view;
		this.mModel = new LetterModelImpl(this);
	}
	
	@Override
	public void OnReadLetterResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void OnLoadSenderListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);

				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = (JSONObject) arr.get(i);
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("sender_id", obj.getString("sender_id"));
					item.put("sex", obj.getString("sex"));
					item.put("name", obj.getString("name"));
					item.put("signature", obj.getString("signature"));
					item.put("photo", obj.getString("photo_url"));
					item.put("msg_count", Integer.valueOf(obj.getInt("msg_count")));
					datas.add(item);
				}
				mView.refreshList(datas);
				if (datas.size() > 0) {
					mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
				} else {
					mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
				}
				mView.stopRefresh();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void loadSenderList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		mModel.requestSenderList(params);
	}
	
	@Override
	public void readLetter(String senderId) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("receiver_id", Session.getSession().getuId());
		params.put("sender_id", senderId);
		
	}
	
	@Override
	public void OnVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}
	
}
