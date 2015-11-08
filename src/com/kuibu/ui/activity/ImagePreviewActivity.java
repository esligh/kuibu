package com.kuibu.ui.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagePreviewActivity extends BaseActivity{
	
	public static final int EDIT_REQ_CODE = 0x2001 ; 
	private PhotoView imageIv; 
	private TextView  titleTv ; 
	private TextView  descTv ; 
	private CollectionBean collection ; 
	private ProgressDialog progressDialog;
	private FinalHttp finalHttp = null;
	private CollectionVo collectionVo ; 
	private String action ;  
	private boolean bEdit =false ;  
	private MenuItem pubMenu ; 
	private ImageView actionBtn ;  
	private final Handler handler = new Handler();
    private boolean rotating = false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_preview_activity);
		ActionBar toolbar =getSupportActionBar(); 
		if(toolbar != null){
			toolbar.setDisplayHomeAsUpEnabled(true);
			toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_ab_solid_dark_holo));
		}		
		collectionVo = new CollectionVo(this);
		imageIv = (PhotoView)findViewById(R.id.image_iv);
		imageIv.setAdjustViewBounds(true);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageIv.setMaxHeight(dm.heightPixels);
		imageIv.setMaxWidth(dm.widthPixels);
		
		titleTv = (TextView)findViewById(R.id.title_tv);
		descTv  = (TextView)findViewById(R.id.desc_tv);
		collection = (CollectionBean)getIntent().getSerializableExtra(
				StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
		if(collection != null ){
			if(!collection.cover.startsWith(Constants.URI_PREFIX)){
				collection.cover = Constants.URI_PREFIX + collection.cover ; 
			}						
			titleTv.setText(collection.title);
			if(TextUtils.isEmpty(collection.content)){
				descTv.setVisibility(View.GONE);
			}else{
				descTv.setText(collection.content);
				Animation anim = AnimationUtils.loadAnimation(ImagePreviewActivity.this, 
						R.anim.anim_control_slide_in_left);		
				descTv.startAnimation(anim);
			}
			ImageLoader.getInstance().displayImage(collection.cover, imageIv);			 			
			Animation anim = AnimationUtils.loadAnimation(ImagePreviewActivity.this, 
					R.anim.anim_slide_in_up);
			imageIv.startAnimation(anim);			
			actionBtn = (ImageView)findViewById(R.id.action_btn);
			actionBtn.startAnimation(anim);
			actionBtn.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View view) {
					if(!rotating){
						toggleRotation();
						actionBtn.setImageResource(R.drawable.iconfont_stop);
					}else{
						handler.removeCallbacksAndMessages(null);
						imageIv.setRotationTo(0);
						actionBtn.setImageResource(R.drawable.iconfont_start);
					}
					rotating = !rotating;
				}
			});
		}	
		finalHttp = new FinalHttp();
		setTitle(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preview_menu, menu);
		pubMenu = menu.findItem(R.id.action_publish);
		MenuItem edit = menu.findItem(R.id.action_edit);
		int from_who = getIntent().getIntExtra(
				StaticValue.EDITOR_VALUE.FROM_WHO, 0);
		if (from_who == StaticValue.EDITOR_VALUE.EDITOR_TO_PREVIEW) {
			edit.setVisible(false);
		} else {
			edit.setVisible(true);
		}

		if (collection == null || (collection.isPublish == 1 && collection.isSync == 1)
				|| collection.content.equals("")) {
			pubMenu.setVisible(false);
		} else {
			pubMenu.setVisible(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_right);
			return true;
		case R.id.action_publish:
			publishCollection();			
			return true;
		case R.id.action_edit:
			bEdit  = false ; 
			Intent intent = new Intent(ImagePreviewActivity.this, CollectionImageActivity.class);
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
			startActivityForResult(intent,EDIT_REQ_CODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
			case EDIT_REQ_CODE:
				if(result != null){
					CollectionBean bean = (CollectionBean)result.getSerializableExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
					if(bean !=null){
						bEdit = true ; 
						collection.title = bean.title ; 
						collection.content = bean.content ; 
						titleTv.setText(bean.title);
						descTv.setText(bean.content);
					}
				}
				break;
		}
	}
	@Override
	public void onBackPressed() {
		if(bEdit){
			Intent intent = new Intent();
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
			setResult(RESULT_OK, intent);
		}
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		super.onBackPressed();						
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(handler != null){
			handler.removeCallbacksAndMessages(null);
		}
		collectionVo.closeDB();
	}
	
	@SuppressLint("SimpleDateFormat")
	private void publishCollection() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getString(R.string.publishing));
		progressDialog.show();
		

		StringBuffer url_root = new StringBuffer('/');
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new java.util.Date();
		String today = sdf.format(date);
		url_root.append(today).append("/");
		
		String uuid = UUID.randomUUID().toString();
		String ext = FileUtils.getExtensionName(collection.cover);
		StringBuffer buffer = new StringBuffer(url_root);
		buffer.append(uuid).append(".").append(ext);
		final String img_map_url = buffer.toString() ; 
		
		Map<String, String> params = new HashMap<String, String>();
		collection.title = collection.getTitle();
		params.put("title", collection.title);		
		collection.type = StaticValue.EDITOR_VALUE.COLLECTION_IMAGE; 
		params.put("type",collection.type);
		params.put("content", ""); // image description
		params.put("abstract", collection.content);
		if (collection.isPublish == 1 && collection.isSync == 0) { // update collection
			params.put("cid", collection.cid);
			action = "MOD";			
		}else{
			action = "ADD" ;
			params.put("cover", img_map_url);
			params.put("create_by", Session.getSession().getuId());
			String descript = new StringBuffer(Session.getSession().getuId()).append(":")
					.append(collection.content)
					.toString(); 
			params.put("cisn",SafeEDcoderUtil.MD5(descript));
			params.put("pack_id", collection.pid);			
		}	
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/add_collection").toString();			
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
							.equals(state)) {
						if("ADD".equals(action)){
							collection.cid = response.getString("cid");							 
							uploadImgs(img_map_url);							
						}else{
							Toast.makeText(ImagePreviewActivity.this, getString(R.string.modify_success),
									Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
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
				progressDialog.dismiss();
				if("ADD".equals(action)){
					Toast.makeText(ImagePreviewActivity.this, getString(R.string.publish_success),
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ImagePreviewActivity.this, getString(R.string.modify_fail),
							Toast.LENGTH_SHORT).show();
				}
				
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
		
	@SuppressLint("SimpleDateFormat")
	private void uploadImgs(String img_map_url) {
		AjaxParams params = new AjaxParams();
		params.put("cid", String.valueOf(collection.cid));				
		params.put("cover_url", "");		
		try {			
			if(!TextUtils.isEmpty(collection.cover)){				
				
				params.put("size", "1");
				params.put("map_url_0", img_map_url);
				params.put("url_0", collection.cover);
				String path = collection.cover.substring(Constants.URI_PREFIX.length());
				String dest = BitmapHelper.hasCompressFile(this, path);
				if(dest == null){ 
					dest = BitmapHelper.compressBitmap(this, path, 
							Constants.COMPRESS_WIDTH,Constants.COMPRESS_HEIGHT , false);
				}				
				params.put("data_0", new File(dest));
			}else{
				params.put("size", "0");
				params.put("map_url_0", "");
				params.put("url_0", "");
				params.put("data_0", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
			.append(Constants.Config.REST_API_VERSION)
			.append("/simple_upload").toString();
		
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(ImagePreviewActivity.this, getString(R.string.publish_fail),
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();				
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (!TextUtils.isEmpty(t)) {
					try {
						JSONObject obj = new JSONObject(t);
						String state = obj.getString("state");
						if (StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS
								.equals(state)) { //上传成功
							collectionVo.update(
									" cid = ? ,is_pub = ?,is_sync = ? ",
									" _id = ? ",
									new String[] { collection.cid,
											String.valueOf(1),
											String.valueOf(1),
											String.valueOf(collection._id) });
							bEdit = true ; 
							collection.isPublish=collection.isSync = 1 ; 
							pubMenu.setVisible(false);							
							Toast.makeText(ImagePreviewActivity.this, getString(R.string.publish_success),
									Toast.LENGTH_SHORT).show();
							
						} else if (StaticValue.RESPONSE_STATUS.UPLOAD_FAILD
								.equals(state)) { //上传失败
							
							Toast.makeText(ImagePreviewActivity.this, getString(R.string.publish_fail),
									Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		});
	}
	
	private void toggleRotation() {
        if (rotating) {
            handler.removeCallbacksAndMessages(null);            
        } else {
            rotateLoop();
        }
    }

    private void rotateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageIv.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }
}
