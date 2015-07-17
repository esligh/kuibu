package com.kuibu.module.activity;

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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CommentItemBean;
import com.kuibu.module.adapter.CommentItemAdapter;

public class CommentActivity extends BaseActivity implements
		OnLoadListener, OnItemClickListener {
	private CommentItemAdapter commentItemAdapter;
	private List<CommentItemBean> datas = new ArrayList<CommentItemBean>();
	private PaginationListView commentList;
	private ImageButton btnSend;
	private EditText editContent;
	private String cid;
	private String collection_creator_id;
	private MenuItem cancelReply;
	private MultiStateView mMultiStateView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_list_detail);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
		commentList = (PaginationListView) findViewById(R.id.comment_list);
		editContent = (EditText) findViewById(R.id.edit_comment);
		editContent.setOnFocusChangeListener(new OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					commentList.setSelection(datas.size()-1);
				}
			}
		});
		btnSend = (ImageButton) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				request_add();
				editContent.setText("");
			}
		});
		commentList.setOnLoadListener(this);
		commentList.setOnItemClickListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		loadData();
		showView();
	}

	private void loadData() {
		cid = getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
		collection_creator_id = getIntent().getStringExtra("create_by");
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		params.put("cid", cid);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_comments";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
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
							showView();
							mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
						}else{
							mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
						}
							
					}
					commentList.loadComplete();
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
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void showView() {
		if (commentItemAdapter == null) {
			commentItemAdapter = new CommentItemAdapter(this, datas);
			commentList.setAdapter(commentItemAdapter);
		} else {
			commentItemAdapter.updateView(datas);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> viewAdpater, View view,
			int position, long id) {
		final CommentItemBean item = (CommentItemBean) viewAdpater.getAdapter()
				.getItem(position);
		if(!Session.getSession().isLogin())
			return ; 
		AlertDialog.Builder builder = new Builder(CommentActivity.this);
		if (Session.getSession().getuId().equals(item.getCreateBy())) {
			builder.setItems(getResources().getStringArray(R.array.comment_menu_item1),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int position) {
							switch(position){
								case 0:
									requestDel(item,position);
								break;
							}
						}
					});
		} else {
			// context
			builder.setItems(getResources().getStringArray(R.array.comment_menu_item0),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int position) {
							switch (position) {
							case 0: // vote
								break;
							case 1: // reply
								if (Session.getSession().isLogin()) {
									editContent.setHint(new StringBuffer("回复")
											.append(item.getUserName()).append(
													"的评论"));
									cancelReply.setVisible(true);
									Map<String, String> tag = new HashMap<String, String>();
									tag.put("receiver_id", item.getCreateBy());
									tag.put("receiver_name", item.getUserName());
									editContent.setTag(tag);
								} else {
									Toast.makeText(CommentActivity.this,
											"登录后才能评论哦", Toast.LENGTH_SHORT)
											.show();
								}
								break;
							case 2: // copy
								boolean isConn = KuibuApplication.getSocketIoInstance().getSocketIO().
										isConnected(); 
								if(isConn){
									KuibuApplication.getSocketIoInstance().getSocketIO().send("I'm alive");
									KuibuApplication.getSocketIoInstance().getSocketIO().emit("my event", "I'm alive.");
								}
								break;
							case 3:// report
								break;
							}
						}
					});

		}
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		cancelReply = menu.add(StaticValue.MENU_GROUP.CANCEL_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.CANCEL_ID,
				StaticValue.MENU_ORDER.CANCEL_ORDER_ID, "取消回复");

		cancelReply.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		cancelReply.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				break;			
			case StaticValue.MENU_ITEM.CANCEL_ID:
				cancelReply.setVisible(false);
				editContent.setHint("写下你的评论...");
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void request_add() {
		final String content = editContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "评论内容不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("create_by", Session.getSession().getuId());
		params.put("content", content);
		params.put("cid", cid);
		if (cancelReply.isVisible()) {
			params.put("type", StaticValue.COMMENT.TYPE_REPLY);
			@SuppressWarnings("unchecked")
			Map<String, String> tag = (Map<String, String>) editContent
					.getTag();
			params.put("receiver_id", tag.get("receiver_id"));
		} else {
			params.put("type", StaticValue.COMMENT.TYPE_COMMON);
			params.put("receiver_id", collection_creator_id);
		}
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/add_comment";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						CommentItemBean bean = new CommentItemBean();
						bean.setUserName(Session.getSession().getuName());
						String url = KuibuApplication.getInstance()
								.getPersistentCookieStore()
								.getCookie("user_photo").getValue();
						bean.setUserPicUrl(url);
						String dateStr = new SimpleDateFormat("yyyy-MM-dd")
								.format(new Date());
						bean.setGenDate(dateStr);
						bean.setVoteCount("0");
						if (cancelReply.isVisible()) {
							@SuppressWarnings("unchecked")
							Map<String, String> tag = (Map<String, String>) editContent
									.getTag();
							bean.setContent(new StringBuffer("回复")
									.append(tag.get("receiver_name"))
									.append(":").append(content).toString());
						} else {
							bean.setContent(content);
						}

						datas.add(bean);
						showView();
					}
					cancelReply.setVisible(false);
					editContent.setHint("写下你的评论...");
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
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String credentials = Session.getSession().getToken()
						+ ":unused";
				headers.put("Authorization", "Basic "
						+ SafeEDcoderUtil.encryptBASE64(credentials.getBytes())
								.replaceAll("\\s+", ""));
				return headers;
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void requestDel(CommentItemBean item,final int position)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", item.getCid());
		params.put("comment_id", item.getId());
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/del_comment";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						datas.remove(position);
						showView();
					}
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
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String credentials = Session.getSession().getToken()
						+ ":unused";
				headers.put("Authorization", "Basic "
						+ SafeEDcoderUtil.encryptBASE64(credentials.getBytes())
								.replaceAll("\\s+", ""));
				return headers;
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	@Override
	public void onLoad(String tag) {
		loadData();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
}
