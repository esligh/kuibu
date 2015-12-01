package com.kuibu.module.presenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.CommentModelImpl;
import com.kuibu.model.entity.CommentItemBean;
import com.kuibu.model.interfaces.CommentModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.CommentPresenter;
import com.kuibu.module.request.listener.OnCommentListener;
import com.kuibu.ui.view.interfaces.CommentView;

public class CommentPresenterImpl implements CommentPresenter,OnCommentListener{
	
	private CommentView mView ;
	private CommentModel mModel;
	private List<CommentItemBean> datas = new ArrayList<CommentItemBean>();
	private int commentCount;
	private CommentItemBean mCurItem ;
	private int curPos ;
	private String cid;
	private String creatorId;
	
	public CommentPresenterImpl(CommentView view) {
		// TODO Auto-generated constructor stub
		this.mView = view;
		this.mModel = new CommentModelImpl(this);
		commentCount = mView.getDataIntent().getIntExtra("commont_count",0);
		cid = mView.getDataIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
		creatorId = mView.getDataIntent().getStringExtra("create_by");
	}
	
	@Override
	public void getCommentList() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		params.put("cid", cid);	
		mModel.requestCommentList(params);
	}
	
	@Override
	public void onCommentListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				if(arr.length()>0){
					for (int i = 0; i < arr.length(); i++) {
						JSONObject temp = (JSONObject) arr.get(i);
						CommentItemBean item = new CommentItemBean();
						item.setCid(temp.getString("cid"));								
						item.setId(temp.getString("comment_id"));
						item.setReceiverName(temp.getString("receiver_name"));
						item.setUserName(temp.getString("user_name"));
						item.setVoteCount(temp.getString("vote_count"));
						item.setGenDate(temp.getString("create_time"));
						item.setCreateBy(temp.getString("create_by"));
						item.setUserSex(temp.getString("user_sex"));
						String type = temp.getString("type"); 
						item.setType(type);
						if (StaticValue.COMMENT.TYPE_REPLY.equals(type)) {
							item.setContent(new StringBuffer("回复")
									.append(item.getReceiverName())
									.append(":")
									.append(temp.getString("content"))
									.toString());
						} else {
							item.setContent(temp.getString("content"));
						}
						item.setUserPicUrl(temp.getString("user_pic"));
						datas.add(item);
					}
					mView.refreshList(datas);							
				}
				mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void addComment() {
		// TODO Auto-generated method stub
		final String content = mView.getComment();
		if (TextUtils.isEmpty(content)) {
			KuibuUtils.showText(R.string.comment_no_empty, 
					Toast.LENGTH_SHORT);
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("create_by", Session.getSession().getuId());
		params.put("content", content);
		params.put("cid", cid);
		if (mView.isCancelMenuVisible()) {
			params.put("type", StaticValue.COMMENT.TYPE_REPLY);
			Map<String, String> tag = mView.getContentTag();
			params.put("receiver_id", tag.get("receiver_id"));
		} else {
			params.put("type", StaticValue.COMMENT.TYPE_COMMON);
			params.put("receiver_id", creatorId);
		}
		mModel.requestAdd(params);
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onAddCommentResponse(JSONObject response) {
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				CommentItemBean bean = new CommentItemBean();
				bean.setCreateBy(Session.getSession().getuId());
				bean.setUserName(Session.getSession().getuName());
				String url = KuibuApplication.getInstance()
						.getPersistentCookieStore()
						.getCookie("user_photo").getValue();
				bean.setUserPicUrl(url);
				String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				bean.setGenDate(dateStr);
				bean.setVoteCount("0");
				String content = response.getString("content");
				if (mView.isCancelMenuVisible()) {
					Map<String, String> tag = mView.getContentTag();
					bean.setContent(new StringBuffer(KuibuUtils.getString(R.string.reply))
							.append(tag.get("receiver_name"))
							.append(":").append(content).toString());
				} else {
					bean.setContent(content);
				}
				datas.add(bean);
				mView.refreshList(datas);
				if(!mView.isCancelMenuVisible()){
					++commentCount;
				}						
			}
			mView.setCancelMenuVisibility(false);
			mView.setContentHint(KuibuUtils.getString(R.string.write_down_comment));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void delComment(CommentItemBean item,int position) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", item.getCid());
		params.put("comment_id", item.getId());
		params.put("position", String.valueOf(position));
		mModel.requestDel(params);
	}

	@Override
	public void onDelCommentResponse(JSONObject response) {
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				int position = Integer.parseInt(response.getString("position"));
				datas.remove(position);
				if(!mView.isCancelMenuVisible()){
					--commentCount;
				}
				mView.refreshList(datas);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getCommentCount() {
		// TODO Auto-generated method stub
		return commentCount ; 
	}

	@Override
	public void setCurrentItem(CommentItemBean item) {
		// TODO Auto-generated method stub
		this.mCurItem = item; 
	}

	@Override
	public CommentItemBean getCurrentItem() {
		// TODO Auto-generated method stub
		return mCurItem; 
	}

	@Override
	public void setCurretPos(int position) {
		// TODO Auto-generated method stub
		this.curPos = position; 
	}

	@Override
	public int getCurrentPos() {
		// TODO Auto-generated method stub
		return curPos;
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}
		
}
