package com.kuibu.module.presenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.PCollectionDetailModelImpl;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.model.interfaces.PCollectionDetailModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.PCollectionDetailPresenter;
import com.kuibu.module.request.listener.OnPCollectionDetailListener;
import com.kuibu.ui.view.interfaces.PCollectionDetailView;

public class PCollectionDetailPresenterImpl implements 
	PCollectionDetailPresenter,OnPCollectionDetailListener{

	private PCollectionDetailModel mModel ; 
	private PCollectionDetailView mView ; 
	private boolean isInFavorite = false;
	private boolean isVoted = false; 
    private boolean bReport = false;
    private String createBy ; 
	private CollectionItemBean collection = new CollectionItemBean(); 
	private int voteCount ,commentCount;   

	public PCollectionDetailPresenterImpl(PCollectionDetailView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new PCollectionDetailModelImpl(this);
	}

	private void readFromJson(JSONObject obj) throws JSONException
	{
		createBy = obj.getString("create_by");
		collection.setTitle(obj.getString("title"));
		collection.setContent(obj.getString("content"));
		collection.setSummary(obj.getString("abstract"));
		collection.setCreateBy(createBy);
		collection.setVoteCount(obj.getString("vote_count"));
		collection.setCommentCount(obj.getString("comment_count"));
		collection.setCover(obj.getString("cover"));
		collection.setPid(obj.getString("pid"));
		collection.setCisn(obj.getString("cisn"));
		collection.setCreateDate(obj.getString("create_time"));
		collection.setType(obj.getString("type"));
		String uid = Session.getSession().getuId();
		if(uid != null && uid.equals(createBy)){
			mView.setToolsVisible(false);
		}else{
			mView.setToolsVisible(true);	
		}
		voteCount = Integer.parseInt(collection.getVoteCount());
		commentCount = Integer.parseInt(collection.getCommentCount());
		
		if(commentCount>0){
        	StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.comment_text))
        	.append(" ").append(DataUtils.formatNumber(commentCount));
        	mView.setCommentCount(buff.toString());
        	
        }
        voteCount = obj.getInt("vote_count");
        if(voteCount > 0){
        	StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.like))
        	.append(" ").append(DataUtils.formatNumber(voteCount));
        	mView.setVoteCount(buff.toString());
        }
	}
			
	@Override
	public void loadContent() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", collection.getId());
		mModel.requestContent(params);
	}

	@Override
	public boolean isReport() {
		// TODO Auto-generated method stub
		return bReport;
	}

	@Override
	public void setReport(boolean state) {
		// TODO Auto-generated method stub
		this.bReport = state ; 
	}

	@Override
	public boolean isInfavorite() {
		// TODO Auto-generated method stub
		return isInFavorite;
	}

	@Override
	public boolean isVoted() {
		// TODO Auto-generated method stub
		return isVoted;
	}

	@Override
	public void setInFavorite(boolean state) {
		// TODO Auto-generated method stub
		this.isInFavorite = state; 
	}


	@Override
	public int getCommentCount() {
		// TODO Auto-generated method stub
		return commentCount;
	}

	@Override
	public void setCommonCount(int count) {
		// TODO Auto-generated method stub
		this.commentCount  = count ; 
	}

	@Override
	public void onLoadContentResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {						
				JSONObject obj = new JSONObject(response.getString("result"));
				if(obj!=null){
					readFromJson(obj);							
					mView.setCollectionTitle(collection.getTitle());
					if(TextUtils.isEmpty(collection.getSummary())){
						mView.setCollectionDescVisible(View.GONE);
					}else{
						mView.setCollectionDesc(collection.getSummary());
					}					
					mView.setImage(collection.getCover());							
				}
				mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadActionsResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String astr = response.getString("actions");
				List<String> codes = null; 
				if(!TextUtils.isEmpty(astr)){
					String[] actions = astr.split(",");
					codes = Arrays.asList(actions);
					if(codes.contains(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION)){
						mView.setVoteBtnDrawable(R.drawable.ab_support_active);
						isVoted = true; 
					}
					if(codes.contains(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION)){
						mView.setFavMenuDrawable(R.drawable.ab_fav_active);
						isInFavorite = true;
					}
					if(codes.contains(StaticValue.USER_ACTION.ACTION_REPORT_COLLECTION)){
						mView.setReportMenuRrawable(R.drawable.ic_action_report_disabled);
						bReport = true ; 								
					}
				}						
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onVoteResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				if (isVoted) {
					voteCount -= 1;
					mView.setVoteBtnDrawable(R.drawable.ab_support_normal);								
					isVoted = false;						
				} else {
					voteCount += 1;
					mView.setVoteBtnDrawable(R.drawable.ab_support_active);
					isVoted = true;				
				}
				
				if(voteCount > 0){
					StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.like))
		        	.append(" ").append(DataUtils.formatNumber(voteCount));
		        	mView.setVoteCount(buff.toString());
				}						
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReportResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				bReport = true ;
				mView.setReportMenuRrawable(R.drawable.ic_action_report_disabled);
				KuibuUtils.showText(R.string.thanks_for_report, Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}

	@Override
	public void doReport() {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("accuser_id", Session.getSession().getuId());
		params.put("defendant_id", collection.getCreateBy());
		params.put("cid", collection.getId());
		KuibuUtils.showReportView(mView.getInstance(), 
				StaticValue.RequestCode.REPORT_COMPLETE, params);
	}

	@Override
	public void doVote(String action_type, boolean isVoted) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("action_type", action_type);
		params.put("obj_id", collection.getId());
		String URL=""; 
		if(!isVoted){
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/add_useraction").toString();		
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_useraction").toString();			
		}
		params.put("URL", URL);
		mModel.doVote(params);		
	}

	@Override
	public void loadActions() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", collection.getId());
		mModel.requestUserActions(params);
	}

	@Override
	public CollectionItemBean getCollection() {
		// TODO Auto-generated method stub
		return collection;
	}

}
