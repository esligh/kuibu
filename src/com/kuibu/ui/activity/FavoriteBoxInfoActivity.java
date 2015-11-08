package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.ImageGridAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FavoriteBoxInfoActivity extends BaseActivity implements
		OnBorderListener {
	
	private ListView mList;
	private MultiColumnListView mCardList ; 
	private RelativeLayout focusLayout;
	private View footerView;
	private BorderScrollView borderScrollView;
	private CommonAdapter<CollectionItemBean> infoAdapter;

	private ImageGridAdapter mCardApdater ; 
	private List<CollectionBean> card_datas = null; 
	private List<CollectionItemBean> item_datas = null ;
	
	private String box_id;
	private TextView titleView;
	private TextView descView;
	private ImageView creatorPicView;
	private TextView creatorName, creatorSignature, followCount;
	private FButton focusBtn;
	private Map<String, Object> userInfo;
	private boolean isfocus;
	private boolean bUserIsFollow;
	private RelativeLayout tagLayout;
	private MultiStateView mMultiStateView;
	private String type ; 
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collectpack_infolist);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadPack();
			}   	
        });
		focusLayout = (RelativeLayout) findViewById(R.id.collect_pack_info_focus_rl);
		String uid = Session.getSession().getuId();
		String createBy = getIntent().getStringExtra("create_by");
		if (!TextUtils.isEmpty(uid) && uid.equals(createBy)){
			focusLayout.setVisibility(View.GONE);
		}else{
			focusLayout.setVisibility(View.VISIBLE);
		}		
		tagLayout = (RelativeLayout) findViewById(R.id.tags_layout);
		tagLayout.setVisibility(View.GONE);
		
		borderScrollView = (BorderScrollView) findViewById(R.id.collect_pack_scroll_view);
		borderScrollView.setOnBorderListener(this);
		footerView = LayoutInflater.from(this).inflate(R.layout.footer, null);
		footerView.setVisibility(View.GONE);

		titleView = (TextView) findViewById(R.id.collect_pack_title_tv);
		descView = (TextView) findViewById(R.id.collect_pack_desc_tv);
		creatorPicView = (ImageView) findViewById(R.id.pack_creator_pic_iv);
		creatorName = (TextView) findViewById(R.id.pack_creator_name_tv);
		creatorSignature = (TextView) findViewById(R.id.pack_creator_signature_tv);
		focusBtn = (FButton) findViewById(R.id.focus_collectpack_bt);
		followCount = (TextView) findViewById(R.id.follow_count_tv);
		box_id = getIntent().getStringExtra("box_id");
		
		focusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Session.getSession().isLogin()) {
					do_focus();
				} else {
					Toast.makeText(FavoriteBoxInfoActivity.this, getString(R.string.need_login),
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
				showUserview();
			}
		});
		type = getIntent().getStringExtra("box_type");		
		mList = (ListView) findViewById(R.id.collectpack_cards_list);
		mCardList = (MultiColumnListView)findViewById(R.id.grid_cards_list);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			mCardList.setVisibility(View.VISIBLE);
			mCardList.setVerticalScrollBarEnabled(false);
			mCardList.addFooterView(footerView);
			card_datas = new LinkedList<CollectionBean>(); 			
			mCardApdater = new ImageGridAdapter(this, card_datas,false);
			mCardList.setAdapter(mCardApdater);
			mCardList.addFooterView(footerView);
			mCardList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(PLA_AdapterView<?> viewAdapter, View view,
						int position, long id) {
					CollectionBean bean = (CollectionBean)viewAdapter.getAdapter().getItem(position);
					Intent intent = new Intent(FavoriteBoxInfoActivity.this,
							CollectionImageDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, bean.getCid());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}				
			});
		}else{
			item_datas = new ArrayList<CollectionItemBean>();
			mList.setVisibility(View.VISIBLE);
			mList.addFooterView(footerView);
			infoAdapter = new CommonAdapter<CollectionItemBean>(this, item_datas,
					R.layout.collectpack_info_list_item){
				@Override
				public void convert(ViewHolder holder, CollectionItemBean item) {
					// TODO Auto-generated method stub
					holder.setTvText(R.id.pack_info_collection_title,item.getTitle());
					holder.setTvText(R.id.pack_info_collection_vote_count,item.getVoteCount());

					String summary = item.getSummary().trim().replace("\n", "");
					if(TextUtils.isEmpty(summary) || summary.equals("null")){
						holder.setTvText(R.id.pack_info_collection_content,"(多图)");
					}else{
						holder.setTvText(R.id.pack_info_collection_content,summary);
					}
				}
				
			};
			mList.setAdapter(infoAdapter);
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> viewAdapter, View view,
						int position, long id) {
					Intent intent = new Intent(FavoriteBoxInfoActivity.this,
							CollectionDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, item_datas
							.get(position).getId());	
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN, item_datas.get(position).getCisn());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
		}
		loadPack();
		loadUserinfo();
		loadList();
		showView();
	}

	private void showUserview() {
		Intent intent = new Intent(FavoriteBoxInfoActivity.this,
				UserInfoActivity.class);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.USER_ID,
				(String) userInfo.get("uid"));
		intent.putExtra(StaticValue.USERINFO.USER_NAME,
				(String) userInfo.get("name"));
		intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
				(String) userInfo.get("signature"));
		intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
				(String) userInfo.get("photo"));
		intent.putExtra(StaticValue.USERINFO.USER_SEX,
				(String) userInfo.get("sex"));
		intent.putExtra(StaticValue.USERINFO.USER_ISFOLLOW, bUserIsFollow);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	

	private void showView() {
		if (StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)) {
			mCardApdater.updateView(card_datas);
			setListViewHeightBasedOnChildren(mCardList);
		} else {
			infoAdapter.refreshView(item_datas);
		}
	}

	public void setListViewHeightBasedOnChildren(MultiColumnListView listView) {    
        ListAdapter listAdapter = listView.getAdapter();    
        if (listAdapter == null) {  
            return;    
        }    
  
        int totalHeight =0; 
        int count = (listAdapter.getCount()+1)/2; 
        for (int i = 0; i < count; i++) {    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0);   
            totalHeight += listItem.getMeasuredHeight();    
        }    
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight;    
        listView.setLayoutParams(params);    
   }
	@Override
	public void onBottom() {
		footerView.setVisibility(View.VISIBLE);
		loadList();
	}

	@Override
	public void onTop() {
		borderScrollView.loadComplete();
	}
	
	private void loadPack() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("box_id", box_id);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_boxinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response
								.getString("result"));
						if (obj != null) {
							titleView.setText(obj.getString("box_name"));
							String desc = obj.getString("box_desc");
							if(TextUtils.isEmpty(desc)){
								descView.setVisibility(View.GONE);
							}else{
								descView.setText(desc);
							}													
							isfocus = obj.getBoolean("is_focus");
							followCount.setText(obj.getString("focus_count"));
							if (isfocus) {
								int btnColor = getResources().getColor(
										R.color.fbutton_color_concrete);
								focusBtn.setButtonColor(btnColor);
								focusBtn.setText(getString(R.string.btn_cancel_focus));
							}
						}
					}
				} catch (JSONException e) {
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
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void do_focus() {
		if(!Session.getSession().isLogin()){
			Toast.makeText(this, getString(R.string.need_login), Toast.LENGTH_SHORT).show();
			return ;
		}		
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.FAVORITE_TYPE);
		params.put("obj_id", box_id);
		final String URL;
		if (isfocus) {
			URL = new StringBuilder(Constants.Config.SERVER_URI)
			.append(Constants.Config.REST_API_VERSION)
			.append("/del_follows").toString();
		} else {
			URL = new StringBuilder(Constants.Config.SERVER_URI)
			.append(Constants.Config.REST_API_VERSION)
			.append("/add_follows").toString();
		}
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						int count = Integer.parseInt(followCount.getText().toString().trim());
						if (isfocus){
							followCount.setText(DataUtils.formatNumber(count-1));
							int btnColor = getResources().getColor(R.color.fbutton_color_green_sea);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText(getString(R.string.btn_focus));
						}else{
							followCount.setText(DataUtils.formatNumber(count+1));
							int btnColor = getResources().getColor(R.color.fbutton_color_concrete);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText(getString(R.string.btn_cancel_focus));
						}
					}
				} catch (JSONException e) {
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

	private void loadList() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("box_id", box_id);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			params.put("off", card_datas.size()+"");
		}else{
			params.put("off", item_datas.size() + "");
		}

		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_boxlist").toString();

		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							String state = response.getString("state");
							if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
									.equals(state)) {
								String data = response.getString("result");
								JSONArray arr = new JSONArray(data);
								if(arr!=null && arr.length()>0){
									parseBoxList(arr);
									showView();
								}								
							} 
							mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
							borderScrollView.loadComplete();
							footerView.setVisibility(View.GONE);
						} catch (JSONException e) {
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
	
	private void parseBoxList(JSONArray arr) throws JSONException
	{
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionBean bean = new CollectionBean();
				bean.cid = temp.getString("id");
				bean.title = temp.getString("title");
				bean.cover = temp.getString("cover");
				bean.content = temp.getString("abstract");
				bean.createBy = temp.getString("create_by");
				bean.isPublish = 1; 
				card_datas.add(bean);
			}
			FavoriteBoxInfoActivity.this.setTitle(new StringBuilder("共有").append(card_datas.size()).append("条收集"));
		}else{
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionItemBean bean = new CollectionItemBean();
				bean.setId(temp.getString("id"));
				bean.setTitle(temp.getString("title"));
				bean.setCisn(temp.getString("cisn"));
				bean.setSummary(temp.getString("abstract"));
				bean.setCreateBy(temp.getString("create_by"));
				bean.setVoteCount(temp.getString("vote_count"));
			    bean.setCreatorSex(temp.getString("sex"));
			    bean.setCreatorSignature(temp.getString("signature"));
			    bean.setCreatorName(temp.getString("name"));
			    bean.setCreatorPic(temp.getString("photo"));								    
				item_datas.add(bean);
			}
			FavoriteBoxInfoActivity.this.setTitle(new StringBuilder("共有").append(item_datas.size()).append("条收集"));
		}		
	}
	
	private void loadUserinfo() {
		Map<String, String> params = new HashMap<String, String>();
		String create_by = getIntent().getStringExtra("create_by");
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", create_by);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_userinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response
								.getString("result"));
						if (obj != null) {
							userInfo = new HashMap<String, Object>();
							userInfo.put("uid", obj.getString("id"));
							userInfo.put("name", obj.getString("name"));
							userInfo.put("signature",
									obj.getString("signature"));
							userInfo.put("sex", obj.getString("sex"));
							bUserIsFollow = obj.getBoolean("is_focus");
							creatorName.setText((String) userInfo.get("name"));
							creatorSignature.setText((String) userInfo
									.get("signature"));
							String url = obj.getString("photo");
							userInfo.put("photo", url);
							if (TextUtils.isEmpty(url)) {								
									creatorPicView.setImageResource(R.drawable.default_pic_avata); 
							} else {								
								ImageLoader.getInstance().displayImage((String)userInfo.get("photo"),
										creatorPicView);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
}
