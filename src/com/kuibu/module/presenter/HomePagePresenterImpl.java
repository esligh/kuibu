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
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.HomePageModelImpl;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.model.interfaces.HomePageModel;
import com.kuibu.module.presenter.interfaces.HomePagePresenter;
import com.kuibu.module.request.listener.OnHomePageListener;
import com.kuibu.ui.view.interfaces.HomePageView;

public class HomePagePresenterImpl implements HomePagePresenter ,OnHomePageListener{

	private HomePageView mHomePageView; 
	private HomePageModel mHomePageModel ; 
	private List <MateListItem> mDatas = new ArrayList <MateListItem>();
	
	public HomePagePresenterImpl(HomePageView view) {
		// TODO Auto-generated constructor stub
		this.mHomePageView = view ; 
		this.mHomePageModel = new HomePageModelImpl(this);
	}
	
	@Override
	public void loadNetData(final String action ,final boolean bcache) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		int size = mDatas.size(); 
		params.put("data_type", "HOME_LIST");
		params.put("uid", Session.getSession().getuId());
		params.put("action", action);
		if(action.equals("UP") && size>0)
			params.put("threshold",mDatas.get(size-1).getId());
		else if(action.equals("DOWN") && size>0)
			params.put("threshold",mDatas.get(0).getId());
		else 
			params.put("threshold",Constants.THRESHOLD_INVALID);
		mHomePageModel.requestData(params,bcache);
	}

	@Override
	public boolean loadLocalData() {
		// TODO Auto-generated method stub
		JSONArray arr = KuibuApplication.getCacheInstance()
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_LIST_CACHE);
		if(arr!=null && arr.length()>0){
			parseFromJson(arr,"UP");
			return true; 
		}
		return false;
	}
	
	@Override
	public void parseFromJson(JSONArray arr,String action)
	{
		try{
			int size = arr.length();
			if(size > 0 ){ 
				for (int i = 0; i < size; i++) {
				    JSONObject temp = (JSONObject) arr.get(i);
				    MateListItem bean = new MateListItem();
				    bean.setId(temp.getString("cid"));
				    bean.setCisn(temp.getString("cisn"));
				    bean.setType(Integer.parseInt(temp.getString("type")));
				    bean.setTitle(temp.getString("title"));
				    bean.setSummary(temp.getString("abstract"));
				    bean.setItemPic(temp.getString("image_url"));
				    bean.setCover(temp.getString("cover"));
				    bean.setPackId(temp.getString("pid"));
				    bean.setCreateBy(temp.getString("create_by"));
				    bean.setVoteCount(temp.getInt("vote_count"));
				    bean.setUserSex(temp.getString("sex"));
				    bean.setUserSignature(temp.getString("signature"));
				    bean.setCommentCount(temp.getInt("comment_count"));
				    bean.setTopText(temp.getString("name"));
				    bean.setTopUrl(temp.getString("photo"));
				    bean.setLastModify(temp.getString("last_modify"));
				    if(action.equals("DOWN")){
				    	mDatas.add(0,bean);
				    }else{
				    	mDatas.add(bean);
				    }						
				}
				mHomePageView.refreshListView(mDatas);	
			}			
			if(mDatas.size()>0){
				mHomePageView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}else{
				mHomePageView.setMultiStateView(MultiStateView.ViewState.EMPTY);
			}	
		}catch (JSONException e) {
			e.printStackTrace();
			mHomePageView.setMultiStateView(MultiStateView.ViewState.ERROR);
		}		
	}
	
	@Override
	public void onLoadDataSuccess(JSONObject response) {
		// TODO Auto-generated method stub		
		mHomePageView.stopRefresh();
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		if(!mDatas.isEmpty()){
			mHomePageView.setMultiStateView(MultiStateView.ViewState.CONTENT);
		}else{
			mHomePageView.setMultiStateView(MultiStateView.ViewState.ERROR);
		}
		mHomePageView.stopRefresh();
	}	
}
