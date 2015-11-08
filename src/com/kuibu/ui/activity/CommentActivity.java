package com.kuibu.ui.activity;

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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CommentItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.net.PublicRequestor;

public class CommentActivity extends BaseActivity implements
	OnItemClickListener {
	
	private CommonAdapter<CommentItemBean> commentItemAdapter;
	private List<CommentItemBean> datas = new ArrayList<CommentItemBean>();
	private PullToRefreshListView commentList;
	private ImageButton btnSend;
	private EditText editContent;
	private String cid;
	private String collection_creator_id;
	private MenuItem cancelReply;
	private MultiStateView mMultiStateView;
	private int commentCount = 0;
	
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
		commentList = (PullToRefreshListView) findViewById(R.id.comment_list);
		commentList.setMode(Mode.PULL_FROM_END);
		commentList.setPullToRefreshOverScrollEnabled(false);
		commentList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData();
				String label = DateUtils.formatDateTime(getApplicationContext(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
			}
		});
		editContent = (EditText) findViewById(R.id.edit_comment);
		editContent.setOnFocusChangeListener(new OnFocusChangeListener() {			
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
	//				commentList.setSelection(datas.size()-1);
				}
			}
		});
		btnSend = (ImageButton) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(Session.getSession().isLogin()){
					requestAdd();
					editContent.setText("");
				}else{
					Toast.makeText(CommentActivity.this, "请先注册登录", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		commentList.setOnItemClickListener(this);
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
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/get_comments").toString();
		
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
						}
						mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
					}
					commentList.onRefreshComplete();
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void showView() {
		if (commentItemAdapter == null) {
			commentItemAdapter = new CommonAdapter<CommentItemBean>(this, datas,R.layout.comment_list_item){

				@Override
				public void convert(ViewHolder holder, CommentItemBean item) {
					// TODO Auto-generated method stub
					holder.setTvText(R.id.user_name_tv,item.getUserName());
					holder.setTvText(R.id.content_tv,item.getContent());
					holder.setTvText(R.id.date_tv,item.getGenDate());		
					String url = item.getUserPicUrl();
					
					if(TextUtils.isEmpty(url) || url.equals("null")){
						holder.setImageResource(R.id.user_photo_iv,R.drawable.default_pic_avata);						
					}else{
						holder.setImageByUrl(R.id.user_photo_iv, url);
					}		
				}
				
			};
			commentList.setAdapter(commentItemAdapter);
		} else {
			commentItemAdapter.refreshView(datas);
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
							case 0: // reply
								if (Session.getSession().isLogin()) {
									editContent.setHint(new StringBuffer(getString(R.string.reply))
											.append(" ").append(item.getUserName()));
									cancelReply.setVisible(true);
									Map<String, String> tag = new HashMap<String, String>();
									tag.put("receiver_id", item.getCreateBy());
									tag.put("receiver_name", item.getUserName());
									editContent.setTag(tag);
								} else {
									Toast.makeText(CommentActivity.this,
											getString(R.string.need_login), Toast.LENGTH_SHORT)
											.show();
								}
								break;
							case 1:// report
								AlertDialog.Builder builder = new Builder(CommentActivity.this);
								builder.setTitle(getString(R.string.report_reason));
								builder.setItems(getResources().getStringArray(R.array.report_comment), 
										new OnClickListener(){
											@Override
											public void onClick(
													DialogInterface dialog,
													int position) {
												Map<String,String> params = new HashMap<String,String>();
												params.put("accuser_id", Session.getSession().getuId());
												params.put("defendant_id", item.getCreateBy());
												
												String[] items = getResources().getStringArray(
														R.array.report_comment);
												
												if(items != null && items.length > position)
													params.put("reason",items[position]);
												
												switch(position){
													case 0:case 1:case 2:case 3:case 4:
														PublicRequestor.sendReport(params);
														break;
													case 5:
														Intent intent = new Intent(CommentActivity.this,ReportActivity.class);
														intent.putExtra("defendant", item.getCreateBy());
														startActivity(intent);
														break;
												}
											}									
								});
								builder.show();
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
				StaticValue.MENU_ORDER.CANCEL_ORDER_ID, getString(R.string.cancel_reply));

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
				editContent.setHint(getString(R.string.write_down_comment));
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void requestAdd() {
		final String content = editContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, getString(R.string.comment_no_empty), 
					Toast.LENGTH_SHORT).show();
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("create_by", Session.getSession().getuId());
		params.put("content", content);
		params.put("cid", cid);
		if (cancelReply.isVisible()) {
			params.put("type", StaticValue.COMMENT.TYPE_REPLY);
			@SuppressWarnings("unchecked")
			Map<String, String> tag = (Map<String, String>) 
								editContent.getTag();
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
							bean.setContent(new StringBuffer(getString(R.string.reply))
									.append(tag.get("receiver_name"))
									.append(":").append(content).toString());
						} else {
							bean.setContent(content);
						}
						datas.add(bean);
						showView();
						if(!cancelReply.isVisible()){
							++commentCount;
						}						
					}
					cancelReply.setVisible(false);
					editContent.setHint(getString(R.string.write_down_comment));
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("comment_count", commentCount);
		setResult(RESULT_OK, intent);
		
		super.onBackPressed(); //afeter setResult 
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
	
}
