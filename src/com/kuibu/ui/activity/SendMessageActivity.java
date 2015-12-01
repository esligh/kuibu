package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MessageItemBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SendMessageActivity extends BaseActivity {
	
	private PullToRefreshListView msgList; 
	private EditText msgEt;
	private ImageButton btnSend;
	private CommonAdapter<MessageItemBean> adapter; 
	private List<MessageItemBean> mDatas = new ArrayList<MessageItemBean>();
	private MultiStateView mMultiStateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message_activity);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
		msgList = (PullToRefreshListView)findViewById(R.id.message_list);
		msgList.setMode(Mode.PULL_FROM_END);
		msgList.setPullToRefreshOverScrollEnabled(false);
		msgList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData();
			}
			
		});
		msgEt = (EditText)findViewById(R.id.edit_message);
		msgEt.setOnFocusChangeListener(new OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
	//				msgList.setSelection(mDatas.size()-1);
				}
			}
		});
		
		btnSend = (ImageButton)findViewById(R.id.btn_send);		
		btnSend.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub	
				sendMsg();
			}
		});
		msgEt.setFocusable(true);
		msgEt.setFocusableInTouchMode(true);
		msgEt.requestFocus();
		showView();
		loadData();
	}

	private void showView()
	{
		if(adapter == null){
			adapter = new CommonAdapter<MessageItemBean>(this,mDatas,R.layout.message_list_item){
				@Override
				public void convert(ViewHolder holder, MessageItemBean item) {
					// TODO Auto-generated method stub
					String photo = item.getCreatorPic();
					if(TextUtils.isEmpty(photo) || photo.equals("null")){
						holder.setImageResource(R.id.user_photo_iv,R.drawable.default_pic_avata);
					}else{
						holder.setImageByUrl(R.id.user_photo_iv, photo);
					}		
					holder.setTvText(R.id.user_name_tv,item.getCreatorName());
					holder.setTvText(R.id.message_tv,item.getMessage());
					holder.setTvText(R.id.date_tv,item.getCreateTime());
				}				
			};
			msgList.setAdapter(adapter);
		}else{
			adapter.refreshView(mDatas);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				break;			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
	
	private void loadData()
	{
		Map<String,String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mDatas.size()));
		params.put("requestor_id", Session.getSession().getuId());
		params.put("sender_id", getIntent().getStringExtra("sender_id"));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_messages";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						for (int i = 0; i < arr.length(); i++) {
							JSONObject temp = (JSONObject) arr.get(i);
							MessageItemBean bean = new MessageItemBean();
							bean.setMsgId(temp.getString("id"));
							bean.setMessage(temp.getString("message"));
							bean.setCreatorPic(temp.getString("creator_pic"));
							bean.setCreatorName(temp.getString("creator_name"));
							bean.setCreateTime(temp.getString("create_time"));
							mDatas.add(bean);
						}
						showView();
					}
					msgList.onRefreshComplete();
					mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
					return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);		
	}
	
	private void sendMsg()
	{
		final String msg = msgEt.getText().toString().trim();
		if(TextUtils.isEmpty(msg)){
			Toast.makeText(this, "请输入私信的内容",Toast.LENGTH_SHORT).show();
		}else{
			Map<String,String> params =new HashMap<String,String>();
			params.put("sender_id", Session.getSession().getuId());
			params.put("receiver_id", getIntent().getStringExtra("sender_id"));
			params.put("message", msg);			
			final String URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/send_message";
			JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
					params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					try {
						String state = response.getString("state");
						if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){	
							msgEt.setText("");
							MessageItemBean item = new MessageItemBean();
							item.setMsgId(response.getString("msg_id"));
							item.setMessage(msg);
							item.setCreatorPic(Session.getSession().getuPic());
							item.setCreatorName(Session.getSession().getuName());
							item.setCreateTime(response.getString("create_time"));
							mDatas.add(item);							
						}
						showView();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
					VolleyLog.e("Error:", error.getCause());
					error.printStackTrace();
				}
			}){
				@Override  
		 		public Map<String, String> getHeaders() throws AuthFailureError {  
		 			return KuibuUtils.prepareReqHeader();
		 		}
			};
			KuibuApplication.getInstance().addToRequestQueue(req);		
		}
	}
}
