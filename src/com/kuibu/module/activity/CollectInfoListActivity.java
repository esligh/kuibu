package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionItemBean;
import com.kuibu.module.adapter.CollectPackInfoAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectInfoListActivity extends BaseActivity implements
		OnBorderListener {
	private ListView cardListView;
	private RelativeLayout focusLayout;
	private View footerView;
	private BorderScrollView borderScrollView;
	private CollectPackInfoAdapter infoAdapter;
	private List<CollectionItemBean> item_datas = new ArrayList<CollectionItemBean>();
	private String pack_id;
	private TextView titleView;
	private TextView descView;
	private ImageView creatorPicView;
	private TextView creatorName, creatorSignature, followCount;
	private FButton focusBtn;
	private boolean isfocus;
	private TagGroup tagGroup;
	private RelativeLayout tagLayout;
	private String topic_id;
	private MultiStateView mMultiStateView;
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collectpack_infolist);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				load_pack();
			}   	
        });
		
		focusLayout = (RelativeLayout) findViewById(R.id.collect_pack_info_focus_rl);
		boolean flag = getIntent().getBooleanExtra(
				StaticValue.HIDE_FOCUS, false);
		if (flag)
			focusLayout.setVisibility(View.GONE);
		tagGroup = (TagGroup) findViewById(R.id.topic_name_tags);
				
		if(isDarkTheme){
			int color =getResources().getColor(R.color.list_view_bg_dark);
			tagGroup.setBackGroudColor(color);
			tagGroup.setPressedColor(color);
		}else{
			int color = getResources().getColor(R.color.white);
			tagGroup.setBackgroundColor(color);
			tagGroup.setPressedColor(getResources().getColor(R.color.SkyBlue));
		}
		tagLayout = (RelativeLayout) findViewById(R.id.tags_layout);
		tagLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {				
				Intent intent = new Intent(CollectInfoListActivity.this,
						TopicListActivity.class);
				intent.putExtra("topic_id", topic_id);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		cardListView = (ListView) findViewById(R.id.collectpack_cards_list);
		borderScrollView = (BorderScrollView) findViewById(R.id.collect_pack_scroll_view);
		borderScrollView.setOnBorderListener(this);
		footerView = LayoutInflater.from(this).inflate(R.layout.footer, null);
		footerView.setVisibility(View.GONE);
		cardListView.addFooterView(footerView);

		cardListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CollectInfoListActivity.this,
						ShowCollectionActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, item_datas
						.get(position).getId());
				intent.putExtra("title", item_datas.get(position).getTitle());
				intent.putExtra("content", item_datas.get(position)
						.getContent());
				intent.putExtra("create_by", item_datas.get(position)
						.getCreateBy());
				intent.putExtra("name", item_datas.get(position).getCreatorName());
				intent.putExtra("photo",item_datas.get(position).getCreatorPic() );
				intent.putExtra("signature", item_datas.get(position).getCreatorSignature());
				intent.putExtra("sex", item_datas.get(position).getCreatorSex());				
				intent.putExtra("vote_count", item_datas.get(position).getVoteCount());
				intent.putExtra("comment_count", item_datas.get(position).getCommentCount());								
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});

		titleView = (TextView) findViewById(R.id.collect_pack_title_tv);
		descView = (TextView) findViewById(R.id.collect_pack_desc_tv);
		creatorPicView = (ImageView) findViewById(R.id.pack_creator_pic_iv);
		creatorName = (TextView) findViewById(R.id.pack_creator_name_tv);
		creatorSignature = (TextView) findViewById(R.id.pack_creator_signature_tv);
		focusBtn = (FButton) findViewById(R.id.focus_collectpack_bt);
		followCount = (TextView) findViewById(R.id.follow_count_tv);
		pack_id = getIntent().getStringExtra("pack_id");
		focusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Session.getSession().isLogin()) {
					do_focus(isfocus);
				} else {
					Toast.makeText(CollectInfoListActivity.this, "请先登录或注册用户.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		creatorPicView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showUserview();
			}
		});
		creatorName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				showUserview();
			}
		});
		load_pack();
		showView();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void showUserview() {
		Intent intent = new Intent(CollectInfoListActivity.this,
				UserInfoActivity.class);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.USER_ID,
				getIntent().getStringExtra("create_by"));
		intent.putExtra(StaticValue.USERINFO.USER_NAME,
				getIntent().getStringExtra("name"));
		intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
				getIntent().getStringExtra("signature"));
		intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
				getIntent().getStringExtra("photo"));
		intent.putExtra(StaticValue.USERINFO.USER_SEX,
				getIntent().getStringExtra("sex"));
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}

	private void load_pack() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("pack_id", pack_id);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_collectpack";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response.getString("result"));
						if (obj != null) {
							titleView.setText(obj.getString("pack_name"));
							descView.setText(obj.getString("pack_desc"));
							isfocus = obj.getBoolean("is_focus");
							followCount.setText(DataUtils.formatNumber(obj.getInt("focus_count")));
							topic_id = obj.getString("topic_id");
							creatorName.setText(obj.getString("name"));
							creatorSignature.setText(obj.getString("signature"));
							String url  = obj.getString("photo");
							if (TextUtils.isEmpty(url)) {
								String sex = obj.getString("sex");
								if (StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)) {
									creatorPicView.setImageResource(R.drawable.default_pic_avatar_male);
								} else {
									creatorPicView.setImageResource(R.drawable.default_pic_avatar_female);
								}
							} else {
								ImageLoader.getInstance().displayImage(url,creatorPicView);
							}
							
							String topic_names = obj.getString("topic_names");
							if(!topic_names.equals("null")){
								tagGroup.setTags(topic_names.split(","));
							}else{
								tagGroup.setVisibility(View.GONE); //not likely 
							}														
							if (isfocus) {
								int btnColor = getResources().getColor(
										R.color.fbutton_color_concrete);
								focusBtn.setButtonColor(btnColor);
								focusBtn.setText("取消关注");
							}
							load_list();
						}
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void do_focus(final boolean bfocus) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.COLLECTION_TYPE);
		params.put("obj_id", pack_id);
		final String URL;
		if (bfocus) {
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/del_follows";
		} else {
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/add_follows";
		}
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						if (bfocus) {
							int btnColor = getResources().getColor(
									R.color.fbutton_color_green_sea);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText("关注");
						} else {
							int btnColor = getResources().getColor(
									R.color.fbutton_color_concrete);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText("取消关注");
						}
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
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		} 
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void load_list() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pack_id", pack_id);
		params.put("off", String.valueOf(item_datas.size()));
		params.put("uid", Session.getSession().getuId());
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_packlist";
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							String state = response.getString("state");
							if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
									.equals(state)) {
								String data = response.getString("result");
								JSONArray arr = new JSONArray(data);
								for (int i = 0; i < arr.length(); i++) {
									JSONObject temp = (JSONObject) arr.get(i);
									CollectionItemBean bean = new CollectionItemBean();
									bean.setTitle(temp.getString("title"));
									bean.setContent(temp.getString("content"));
									bean.setCreateBy(temp.getString("create_by"));
									bean.setVoteCount(temp.getString("vote_count"));
									bean.setCreatorName(temp.getString("name"));
									bean.setCreatorPic(temp.getString("photo"));
									bean.setCreatorSex(temp.getString("sex"));
									bean.setCreatorSignature(temp.getString("signature"));
									bean.setCommentCount(temp.getString("comment_count"));
									item_datas.add(bean);
								}
								CollectInfoListActivity.this.setTitle("共有"
										+ arr.length() + "条收集");
								showView();																
							}
							borderScrollView.loadComplete();
							footerView.setVisibility(View.GONE);
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
				});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// packVo.closeDB();
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
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	

	private void showView() {
		if (infoAdapter == null) {
			infoAdapter = new CollectPackInfoAdapter(this, item_datas);
			cardListView.setAdapter(infoAdapter);
		} else {
			infoAdapter.updateView(item_datas);
		}
	}

	@Override
	public void onBottom() {
		// TODO Auto-generated method stub
		footerView.setVisibility(View.VISIBLE);
		load_list();
	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();
	}
}
