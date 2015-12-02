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
import com.kuibu.model.CollectionListModelImpl;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.model.interfaces.CollectionListModel;
import com.kuibu.module.presenter.interfaces.CollectionListPresenter;
import com.kuibu.module.request.listener.OnCollectionListListener;
import com.kuibu.ui.view.interfaces.CollectionListView;

public class CollectionListPresenterImpl implements CollectionListPresenter,
	OnCollectionListListener{

	private CollectionListModel mModel ; 
	private CollectionListView mView ; 
	private List<MateListItem> datas = new ArrayList<MateListItem>();
	private String mPackId ; 
	
	public CollectionListPresenterImpl(CollectionListView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new CollectionListModelImpl(this);		
	}

	@Override
	public void onCollectionListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data); 
				if(arr.length()>0){
					for (int i = 0; i < arr.length(); i++) {
					    JSONObject temp = (JSONObject) arr.get(i);
					    MateListItem bean = new MateListItem();
					    bean.setId(temp.getString("cid"));
					    bean.setCisn(temp.getString("cisn"));
					    bean.setType(Integer.parseInt(temp.getString("type")));
					    bean.setTitle(temp.getString("title"));
					    bean.setSummary(temp.getString("abstract"));
					    bean.setItemPic(temp.getString("image_url"));
					    bean.setCreateBy(temp.getString("create_by"));
					    bean.setPackId(temp.getString("pid"));
					    bean.setVoteCount(temp.getInt("vote_count"));
					    bean.setUserSex(temp.getString("sex"));
					    bean.setUserSignature(temp.getString("signature"));
					    bean.setCommentCount(temp.getInt("comment_count"));
					    bean.setTopText(temp.getString("name"));
					    bean.setTopUrl(temp.getString("photo"));
					    datas.add(bean);
					}
					mView.refreshList(datas);
				}
				if(datas.size()>0){
					mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
				}else{
					mView.setMultiStateView(MultiStateView.ViewState.EMPTY);
				}
			}	
			mView.stopRefresh();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void loadCollectionList() {
		// TODO Auto-generated method stub
		mPackId = mView.getDataIntent().getStringExtra("pack_id");
		mView.setBarTitle(mView.getDataIntent().getStringExtra("pack_name"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(datas.size()));
		params.put("pid", mPackId);
		params.put("data_type", "PACK_LIST");
		mModel.requestCollectionList(params);
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}
}
