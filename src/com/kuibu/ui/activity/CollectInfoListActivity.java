package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;

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
import com.kuibu.common.utils.SafeEDcoderUtil;
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

public class CollectInfoListActivity extends BaseActivity implements
		OnBorderListener {
	
	private static final String VOLLEY_REQUEST_TAG = "collectpack_info";
	private ListView mList;
	private MultiColumnListView mCardList ; 
	private RelativeLayout focusLayout;
	private View footerView;
	private BorderScrollView borderScrollView;
	private CommonAdapter<CollectionItemBean> infoAdapter;
	private ImageGridAdapter mCardApdater ; 
	private List<CollectionBean> card_datas = null; 
	private List<CollectionItemBean> item_datas = null ; 
	private String packId;
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
	private Map<String,String> packInfo = new HashMap<String,String>();
	private String type ; 
	
	@SuppressWarnings("deprecation")
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
				loadPack();
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
			tagGroup.setTagBackGroundColor(color);
		}else{
			int color = getResources().getColor(R.color.white);
			tagGroup.setTagBackGroundColor(color);
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
		packId = getIntent().getStringExtra("pack_id");
		focusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Session.getSession().isLogin()) {
					doFocus();
				} else {
					Toast.makeText(CollectInfoListActivity.this, getString(R.string.need_login),
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
		type = getIntent().getStringExtra("type");		
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
					Intent intent = new Intent(CollectInfoListActivity.this,
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
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> viewAdapter, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CollectInfoListActivity.this,
							CollectionDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, item_datas
							.get(position).getId());
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN, item_datas
							.get(position).getCisn());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			infoAdapter = new CommonAdapter<CollectionItemBean>(this, item_datas,
					R.layout.collectpack_info_list_item){

					@Override
					public void convert(ViewHolder holder,
							CollectionItemBean item) {
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
		}
		loadPack();
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
	protected void onDestroy(){
		// TODO Auto-generated method stub
		super.onDestroy();
		KuibuApplication.getInstance().cancelPendingRequests(VOLLEY_REQUEST_TAG);
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
		if (StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)) {
			mCardApdater.updateView(card_datas);
			setListViewHeightBasedOnChildren(mCardList);
		} else {
			infoAdapter.refreshView(item_datas);
		}
		
	}
	
	@Override
	public void onBottom() {
		// TODO Auto-generated method stub
		footerView.setVisibility(View.VISIBLE);
		loadList();
	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();
	}
	
	private void showUserview() {
		Intent intent = new Intent(CollectInfoListActivity.this,
				UserInfoActivity.class);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.USER_ID,
				packInfo.get("create_by"));
		intent.putExtra(StaticValue.USERINFO.USER_NAME,
				packInfo.get("name"));
		intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
				packInfo.get("signature"));
		intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
				packInfo.get("photo"));
		intent.putExtra(StaticValue.USERINFO.USER_SEX,
				packInfo.get("sex"));		
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}

	private void loadPack() {
		Map<String, String> params = new HashMap<String, String>();
		if(Session.getSession().isLogin()){
			params.put("uid", Session.getSession().getuId());
		}else{
			params.put("uid", "null");
		}		
		params.put("pack_id", packId);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_collectpack").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response.getString("result"));
						if (obj != null) {
							String packName = obj.getString("pack_name");
							packInfo.put("pack_name", packName);
							titleView.setText(packName);
							String desc = obj.getString("pack_desc"); 
							if(TextUtils.isEmpty(desc)){
								descView.setText(getString(R.string.no_desc));
							}else{
								descView.setText(desc);
							}
							String createBy = obj.getString("create_by");
							packInfo.put("create_by", createBy);
							if(Session.getSession().getuId().equals(createBy)){
								focusLayout.setVisibility(View.GONE);
							}else{
								focusLayout.setVisibility(View.VISIBLE);
								isfocus = obj.getBoolean("is_focus");
								if (isfocus) {
									int btnColor = getResources().getColor(
											R.color.fbutton_color_concrete);
									focusBtn.setButtonColor(btnColor);
									focusBtn.setText(getString(R.string.btn_cancel_focus));
								}
								followCount.setText(DataUtils.formatNumber(obj.getInt("focus_count")));
							}
							topic_id = obj.getString("topic_id");
							String uName = obj.getString("name");
							creatorName.setText(uName);
							packInfo.put("name", uName);
							packInfo.put("sex", obj.getString("sex"));
							String signature = obj.getString("signature");
							creatorSignature.setText(signature);
							packInfo.put("signature", signature);
							String url  = obj.getString("photo");
							packInfo.put("photo",url);
							if (TextUtils.isEmpty(url)) {
								creatorPicView.setImageResource(R.drawable.default_pic_avata);	
							} else {
								ImageLoader.getInstance().displayImage(url,creatorPicView);
							}						
							String topic_names = obj.getString("topic_names");
							if(!topic_names.equals("null")){
								tagGroup.setTags(topic_names.split(","));
							}else{
								tagGroup.setVisibility(View.GONE); //not likely 
							}														
							loadList();
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req,VOLLEY_REQUEST_TAG);
	}

	private void doFocus() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.COLLECTION_TYPE);
		params.put("obj_id", packId);
		final String URL;
		if(isfocus){ 
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_follows").toString();
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
				  .append(Constants.Config.REST_API_VERSION)
				  .append("/add_follows").toString();
		}
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						int count = Integer.parseInt(followCount.getText().toString().trim());
						if (isfocus) {
							followCount.setText(DataUtils.formatNumber(count-1));
							int btnColor = getResources().getColor(
									R.color.fbutton_color_green_sea);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText(getString(R.string.btn_focus));
							isfocus = false; 
						} else {
							followCount.setText(DataUtils.formatNumber(count+1));
							int btnColor = getResources().getColor(
									R.color.fbutton_color_concrete);
							focusBtn.setButtonColor(btnColor);
							focusBtn.setText(getString(R.string.btn_cancel_focus));
							isfocus = true ; 
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
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
		KuibuApplication.getInstance().addToRequestQueue(req,VOLLEY_REQUEST_TAG);
	}

	private void loadList() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("pack_id", packId);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			params.put("off", String.valueOf(card_datas.size()));
		}else{
			params.put("off", String.valueOf(item_datas.size()));
		}
		
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
								if(arr!=null && arr.length()>0){
									parsePackList(arr);
									showView();
								}																
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
						Toast.makeText(getApplicationContext(), 
								VolleyErrorHelper.getMessage(error, getApplicationContext()), 
								Toast.LENGTH_SHORT).show();
					}
				});
		KuibuApplication.getInstance().addToRequestQueue(req,VOLLEY_REQUEST_TAG);
	}
	
	private void parsePackList(JSONArray arr) throws JSONException
	{	 
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionBean bean = new CollectionBean();
				bean.cid = temp.getString("cid");
				bean.title = temp.getString("title");
				bean.cover = temp.getString("cover");
				bean.content = temp.getString("abstract");
				bean.createBy = temp.getString("create_by");
				bean.isPublish = 1; 
				card_datas.add(bean);
			}
			CollectInfoListActivity.this.setTitle("共有"
					+ card_datas.size() + "条收集");
		}else{
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				CollectionItemBean bean = new CollectionItemBean();
				bean.setId(temp.getString("cid"));
				bean.setCisn(temp.getString("cisn"));
				bean.setTitle(temp.getString("title"));
				bean.setSummary(temp.getString("abstract"));
				bean.setCreateBy(temp.getString("create_by"));
				bean.setVoteCount(temp.getString("vote_count"));
				item_datas.add(bean);
			}
			CollectInfoListActivity.this.setTitle("共有"
					+ item_datas.size() + "条收集");
		}		
	}
}
