package com.kuibu.module.activity;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionItemBean;
import com.kuibu.module.net.PublicRequestor;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CollectionImageDetailActivity extends Activity{	
	
	private ImageView backIv ,favIv,shareIv; 
	private PhotoView imageIv; 
	private TextView  titleTv ; 
	private TextView  descTv ; 
	private LinearLayout toolsLayout ; 
	private boolean isInFavorite = false;
	private boolean isSupport = false; 
	private FButton likeBtn , commentBtn ;
	private int voteCount ,commentCount; 
	private ImageButton reportBtn;  
	private CollectionItemBean collection = new CollectionItemBean(); 
	private MultiStateView mMultiStateView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.collection_image_detail_activity);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadContent();
			}   	
        });
		toolsLayout = (LinearLayout)findViewById(R.id.layout_tools);
		reportBtn = (ImageButton)findViewById(R.id.report_btn);
		backIv =(ImageView)findViewById(R.id.img_back);
		backIv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		reportBtn.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View view) {
				doReport();
			}
		});
		
		shareIv = (ImageView)findViewById(R.id.share_btn);
		favIv = (ImageView)findViewById(R.id.fav_btn);		
		imageIv = (PhotoView)findViewById(R.id.image_iv);
		imageIv.setAdjustViewBounds(true);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageIv.setMaxHeight(dm.heightPixels);
		imageIv.setMaxWidth(dm.widthPixels);
		
		titleTv = (TextView)findViewById(R.id.title_tv);
		descTv  = (TextView)findViewById(R.id.desc_tv);
		
		
		favIv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(Session.getSession().isLogin()){
					Intent intent = new Intent(CollectionImageDetailActivity.this,CollectFavoriteBoxActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, collection.getId());
					intent.putExtra("type",collection.getType());
					intent.putExtra(StaticValue.COLLECTION.IS_COLLECTED, isInFavorite);
					startActivityForResult(intent,StaticValue.RequestCode.FAVORITE_BOX_REQCODE);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else{
					Toast.makeText(CollectionImageDetailActivity.this, getString(R.string.need_login), Toast.LENGTH_SHORT).show();
				}
			}
		});		
		shareIv.setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View arg0) {
				
			}
		});
		likeBtn = (FButton) findViewById(R.id.like_bt);
		likeBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {				
				if(Session.getSession().isLogin()){
					doVote(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION, isSupport);
				}else{
					Toast.makeText(CollectionImageDetailActivity.this, getString(R.string.need_login), Toast.LENGTH_SHORT).show();
				}
			}
		});
		commentBtn = (FButton)findViewById(R.id.comment_bt);
		commentBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CollectionImageDetailActivity.this,CommentActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, collection.getId());
				intent.putExtra("create_by", collection.getCreateBy());
				startActivityForResult(intent, StaticValue.RequestCode.COMMENT_OVER);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
			
		});
		collection.setId(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID));
		loadContent();
        if(Session.getSession().isLogin())
        	loadActions();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);				
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void loadContent()
	{	
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", collection.getId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION) 
				.append("/get_collectiondetail").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {						
						JSONObject obj = new JSONObject(response.getString("result"));
						if(obj!=null){
							readFromJson(obj);
							Animation imgAnim = AnimationUtils.loadAnimation(CollectionImageDetailActivity.this, 
									R.anim.anim_slide_in_up);
							toolsLayout.setVisibility(View.VISIBLE);
							toolsLayout.startAnimation(imgAnim);												
							
							titleTv.setText(collection.getTitle());
							if(TextUtils.isEmpty(collection.getSummary())){
								descTv.setVisibility(View.GONE);
							}else{									
								Animation anim = AnimationUtils.loadAnimation(CollectionImageDetailActivity.this, 
											R.anim.anim_control_slide_in_left);
									descTv.setText(collection.getSummary());
									descTv.startAnimation(anim);
							}
							
							ImageLoader.getInstance().displayImage(collection.getCover(), imageIv,new SimpleImageLoadingListener(){
								@Override
								public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
									reportBtn.setVisibility(View.VISIBLE);
								}
							});
							imageIv.startAnimation(imgAnim);
							imageIv.setVisibility(View.VISIBLE);							
						}
						
						mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
			
		});
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}

	private void readFromJson(JSONObject obj) throws JSONException
	{
		collection.setTitle(obj.getString("title"));
		collection.setContent(obj.getString("content"));
		collection.setSummary(obj.getString("abstract"));
		collection.setCreateBy(obj.getString("create_by"));
		collection.setVoteCount(obj.getString("vote_count"));
		collection.setCommentCount(obj.getString("comment_count"));
		collection.setCover(obj.getString("cover"));
		collection.setPid(obj.getString("pid"));
		collection.setCisn(obj.getString("cisn"));
		collection.setCreateDate(obj.getString("create_time"));
		collection.setType(obj.getString("type"));
		
		voteCount = Integer.parseInt(collection.getVoteCount());
		commentCount = Integer.parseInt(collection.getCommentCount());
		
		if(commentCount>0){
        	StringBuilder buff = new StringBuilder(getString(R.string.comment_text))
        	.append(" ").append(DataUtils.formatNumber(commentCount));
        	commentBtn.setText(buff.toString());
        }
        voteCount = obj.getInt("vote_count");
        if(voteCount > 0){
        	StringBuilder buff = new StringBuilder(getString(R.string.like))
        	.append(" ").append(DataUtils.formatNumber(voteCount));
        	likeBtn.setText(buff.toString());
        }
        
        String uid = Session.getSession().getuId(); 
        if(uid != null && uid.equals(collection.getCreateBy())){
        	toolsLayout.setVisibility(View.GONE);
        }else{
        	toolsLayout.setVisibility(View.VISIBLE);
        }
	}
	
	private void doVote(String action_type,boolean isChecked)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("action_type", action_type);
		params.put("obj_id", collection.getId());
		String URL=""; 
		if(!isChecked){
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/add_useraction").toString();		
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_useraction").toString();			
		}
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Drawable drawable = null ; 
						if (isSupport) {
							voteCount -= 1; 
							drawable= getResources().getDrawable(R.drawable.ab_support_normal);												
							isSupport = false;						
						} else {
							voteCount += 1; 
							drawable= getResources().getDrawable(R.drawable.ab_support_active);
							isSupport = true;				
						}
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
						likeBtn.setCompoundDrawables(drawable, null, null, null);
						
						if(voteCount > 0){
							StringBuilder buff = new StringBuilder(getString(R.string.like))
				        	.append(" ").append(DataUtils.formatNumber(voteCount));
				        	likeBtn.setText(buff.toString());
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
				HashMap<String, String> headers = new HashMap<String, String>();
				String credentials = Session.getSession().getToken()
						+ ":unused";
				headers.put(
						"Authorization",
						"Basic "
								+ SafeEDcoderUtil.encryptBASE64(
										credentials.getBytes()).replaceAll(
										"\\s+", ""));
				return headers;
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req, 
				StaticValue.TAG_VLAUE.DETAIL_PAGE_VOLLEY);
	}
		
	private void doReport()
	{ 
		AlertDialog.Builder builder =null ; 
		boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(this,StaticValue.PrefKey.DARK_THEME_KEY, false);
		if(isDarkTheme){
			builder = new Builder(this,AlertDialog.THEME_HOLO_DARK);
		}else{
			builder = new Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		}
		
		builder.setTitle(getString(R.string.report_reason));
		builder.setItems(getResources().getStringArray(R.array.report_content), 
				new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(
							DialogInterface dialog,
							int position) {
						Map<String,String> params = new HashMap<String,String>();
						params.put("accuser_id", Session.getSession().getuId());
						params.put("defendant_id", collection.getCreateBy());
						params.put("cid", collection.getId());
						String[] items = getResources().getStringArray(
								R.array.report_content);
						
						if(items!=null && items.length>position)
							params.put("reason",items[position]);
						
						switch(position){
						case 0:							
							PublicRequestor.sendReport(params);
							break;
						case 1: case 2: case 3: case 4:
							PublicRequestor.sendReport(params);
							break;
						case 5:
							Intent intent = new Intent(CollectionImageDetailActivity.this,ReportActivity.class);
							intent.putExtra("defendant", collection.getCreateBy());
							startActivityForResult(intent, StaticValue.RequestCode.REPORT_COMPLETE);							
							break;
						}
					}									
		});
		builder.show();
	}
	
	public void loadActions()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", collection.getId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_useraction").toString();				
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String astr = response.getString("actions");
						List<String> codes = null; 
						if(!TextUtils.isEmpty(astr)){
							String[] actions = astr.split(",");
							codes = Arrays.asList(actions);
							if(codes.contains(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION)){
								Drawable drawable= getResources().getDrawable(R.drawable.ab_support_active);
								drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
								likeBtn.setCompoundDrawables(drawable, null, null, null);
								isSupport = true; 
							}
							if(codes.contains(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION)){
								favIv.setImageResource(R.drawable.ab_fav_active);
								isInFavorite = true;
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode){
			case StaticValue.RequestCode.FAVORITE_BOX_REQCODE:
				if(data!=null){
					isInFavorite = data.getBooleanExtra("isCollected", false);
					if (isInFavorite) {
						Toast.makeText(this, R.string.fav_add_success, Toast.LENGTH_SHORT).show();
						favIv.setImageResource(R.drawable.ab_fav_active);
							
					} else {
						Toast.makeText(this, R.string.fav_cancel_success, Toast.LENGTH_SHORT).show();
						favIv.setImageResource(R.drawable.ab_fav_normal);
										
					}				
				}
			break;
			case StaticValue.RequestCode.COMMENT_OVER:
				if(data!=null){
					int count = data.getIntExtra("comment_count", 0);
					if(count>0){
						commentCount += count;
						StringBuilder buff = new StringBuilder(getString(R.string.comment_text))
			        	.append(" ").append(DataUtils.formatNumber(commentCount));
			        	commentBtn.setText(buff.toString());		        	
					}
				}				
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
