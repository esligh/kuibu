package com.kuibu.module.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import us.feras.mdv.MarkdownView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.NotifyingScrollView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;
import com.kuibu.model.webview.InJavaScriptObject;
import com.kuibu.module.iterf.OnPageLoadFinished;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class PreviewActivity extends BaseActivity implements OnPageLoadFinished{
	
	public static final int ABSTRACT_MAX = 150 ; 
	private MarkdownView previewWebView;
	private boolean isEditIncoming = false;
	private Map<String,String> imgurl_map;
	private ProgressDialog progressDialog;
	private FinalHttp finalHttp = null;
	private CollectionBean collection;
	private CollectionVo collectionVo;
	private ImageLibVo imageVo;
	private MenuItem pubMenu;
	private String cssFile ; 
	private String htmlSource ; 
	private ImageView imageHeader ; 
	private TextView titleTv ;  
	private String mCoverPath = "";
	private LinearLayout webViewcontainer;
	private boolean bLockFlag1 = false; 
	private boolean bLockFlag2 = false ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.collection_preview);
        NotifyingScrollView scrollView = (NotifyingScrollView)findViewById(R.id.scroll_view);
		if (isDarkTheme) {
			scrollView.setBackgroundColor(getResources().getColor(R.color.webview_dark));
			cssFile = Constants.WEBVIEW_DARK_CSSFILE;
		}else{
			scrollView.setBackgroundColor(getResources().getColor(R.color.webview_light));
			cssFile = Constants.WEBVIEW_LIGHT_CSSFILE;
		}
		webViewcontainer = (LinearLayout) findViewById(R.id.content_ll);
		previewWebView = new MarkdownView(getApplicationContext());
		webViewcontainer.addView(previewWebView);
		setUpWebViewDefaults();	
		imageHeader =(ImageView)findViewById(R.id.image_header);
		imageHeader.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				//修改封面
				AlertDialog.Builder builder = new Builder(PreviewActivity.this);
				builder.setTitle(getString(R.string.setting_cover));
				builder.setItems(getResources().getStringArray(R.array.popup_menu_item), 
						new android.content.DialogInterface.OnClickListener(){
							@Override
							public void onClick(
									DialogInterface dialog,
									int position) {
								switch(position){
									case 0:
										mCoverPath = takePhotoByCamera();
										break;
									case 1:
										Crop.pickImage(PreviewActivity.this);
										break;
								}
						}
				});
				builder.show();
			}
		});			    
		titleTv = (TextView)findViewById(R.id.title);
		initData();		
		finalHttp = new FinalHttp();		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebViewDefaults()
	{
		previewWebView.setBackgroundColor(0);
		previewWebView.getSettings().setJavaScriptEnabled(true);
		previewWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		InJavaScriptObject jsObj = new InJavaScriptObject(this);
		jsObj.setOnPageLoadFinishedListener(this);
		previewWebView.addJavascriptInterface(jsObj, "injectedObject");
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			previewWebView.getSettings().setLoadsImagesAutomatically(true);
	    } else {
	    	previewWebView.getSettings().setLoadsImagesAutomatically(false);
	    }
		previewWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {   
				 Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				 startActivity(i);
		         return true;
		    }  
			
			@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("WebView", "onPageFinished ");
				if(!previewWebView.getSettings().getLoadsImagesAutomatically()) {
					previewWebView.getSettings().setLoadsImagesAutomatically(true);
			    }
				super.onPageFinished(view, url);
				//get html source  
				String javascript = "javascript:window.injectedObject.getHtml('<html>'+" +
		                    "document.getElementsByTagName('html')[0].innerHTML+'</html>');";
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
					view.evaluateJavascript(javascript, new ValueCallback<String>() {
			                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
			                @Override
			                public void onReceiveValue(String s) {
			            		Toast.makeText(PreviewActivity.this, s, Toast.LENGTH_SHORT).show();
			                }
					});
				}else{
					view.loadUrl(javascript);
				}
				
			}	
		});
		previewWebView.setWebChromeClient(new WebChromeClient() {			 
		    @Override
		    public boolean onJsAlert(WebView view, String url, String message,
		            final JsResult result) {  
	            result.cancel();  
	            return true; 
		    }		 
		    @Override
		    public boolean onJsConfirm(WebView view, String url,
		            String message, final JsResult result) {
		    	
		        return true;
		    }
		});
		previewWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
	}
	
	@Override
	protected void onPause() {
		if (isEditIncoming) {
			finish();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		imageVo.closeDB();
		collectionVo.closeDB();
		webViewcontainer.removeView(previewWebView);
		previewWebView.destroyDrawingCache();
		previewWebView.removeAllViews();
		previewWebView.destroy();
		if(imgurl_map!=null){
			imgurl_map.clear();
			imgurl_map = null ;
		}		 
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
			if(collection ==null || TextUtils.isEmpty(collection.content)){
				Toast.makeText(this, getString(R.string.content_empty), 
						Toast.LENGTH_SHORT).show();
			}else{
				publishCollection();
			}
			
			return true;
		case R.id.action_edit:
			editNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK){
			switch (requestCode){
			case StaticValue.TAKE_PHOTO_OK:
            	ImageLoader.getInstance().displayImage(Constants.URI_PREFIX+mCoverPath, imageHeader);
            	collectionVo.update(" cover = ? ", " _id = ? ", new String[]{mCoverPath,collection._id});
				break;
			case Crop.REQUEST_PICK:
				Uri uri = data.getData();
				imageHeader.setImageURI(uri);
				mCoverPath = StorageUtils.getImageAbsolutePath(this, uri);
				collectionVo.update(" cover = ? ", " _id = ? ", new String[]{mCoverPath,collection._id});
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("TAKE_PHOTO_URL", mCoverPath);
	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mCoverPath = savedInstanceState.getString("TAKE_PHOTO_URL");
	}

	public String takePhotoByCamera() {
		File dir = new File(StorageUtils.getFileDirectory(getApplicationContext())
				.getAbsolutePath()+Constants.Config.CAMERA_IMG_DIR);		
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+".jpg");
		Uri imageUri = Uri.fromFile(file);
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, StaticValue.TAKE_PHOTO_OK);
		return file.getPath();  
	}

	private void initData() {
		imageVo = new ImageLibVo(this);
		collectionVo = new CollectionVo(this);
		String id = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID);
		if (TextUtils.isEmpty(id)) {
			collection = (CollectionBean) getIntent().getSerializableExtra(
					StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
		} else {
			collection = collectionVo.querywithkey(id);
		}		
		if(!TextUtils.isEmpty(collection.cover)){
			mCoverPath = collection.cover ; 
			ImageLoader.getInstance().displayImage(Constants.URI_PREFIX+collection.cover, 
					imageHeader);
		}
		if (collection != null && !TextUtils.isEmpty(collection.getContent())){
			titleTv.setText(collection.title);
			previewWebView.loadMarkdown(collection.getContent(),cssFile);
		}		
		setTitle(getString(R.string.preview));
	}
	
	private void editNote() {
		isEditIncoming = true;
		Intent intent = new Intent(this, CollectionEditorActivity.class);
		intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,
				R.anim.anim_slide_out_left);
	}

	@SuppressLint("SimpleDateFormat")
	private void publishCollection() {
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getString(R.string.publishing));
		progressDialog.show();
		
		final List<String> images = imageVo.getImgList(collection._id);
		
		if (imgurl_map == null) {
			imgurl_map = new HashMap<String,String>();
		}
		
		StringBuffer url_root = new StringBuffer('/');
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new java.util.Date();
		String today = sdf.format(date);
		url_root.append(today).append("/");
		
		for (int i = 0; i < images.size(); i++) {
			String uuid = UUID.randomUUID().toString();
			String ext = getExtensionName(images.get(i));
			StringBuffer buffer = new StringBuffer(url_root);
			buffer.append(uuid).append(".").append(ext);
			imgurl_map.put(images.get(i), buffer.toString());
		}
		
		Map<String, String> params = new HashMap<String, String>();
		collection.title = collection.getTitle();
		params.put("title", collection.title);
		
		collection.type = images.size() > 0 ? String
				.valueOf(StaticValue.EDITOR_VALUE.COLLECTION_TEXTIMAGE)
				: String.valueOf(StaticValue.EDITOR_VALUE.COLLECTION_TEXT);
		params.put("type",collection.type);
		if (collection.isPublish == 1 && collection.isSync == 0) { // 已经发布，未同步  update collection
			params.put("cid", collection.cid);
			bLockFlag1 = bLockFlag2 = false ; 
			requestImginfo();			
		} else {
			collection.content = adjustMarkDownText(collection.getContent());
			params.put("content", collection.content);
			params.put("abstract", getAbstract());
			params.put("create_by", Session.getSession().getuId());
			String descript = new StringBuffer(Session.getSession().getuId()).append(":")
					.append(collection.content)
					.toString(); 
			params.put("csn",SafeEDcoderUtil.MD5(descript));
			params.put("pack_id", collection.pid);			
			
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
							collection.cid = response.getString("cid");
							if (images != null && images.size() > 0) {  //text-pic 
								uploadImgs(images,true);
							}else{  //just text 								
								collectionVo.update(
										" cid = ? ,is_pub = ?,is_sync = ? ",
										" _id = ? ",
										new String[] { collection.cid,
												String.valueOf(1),
												String.valueOf(1),
												String.valueOf(collection._id) });
								pubMenu.setVisible(false);
								progressDialog.dismiss();
								
								Toast.makeText(PreviewActivity.this, getString(R.string.publish_success),
										Toast.LENGTH_SHORT).show();
								
							}					
						}else{
							progressDialog.dismiss();
							Toast.makeText(PreviewActivity.this, getString(R.string.publish_fail),
									Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getApplicationContext(), 
							VolleyErrorHelper.getMessage(error, getApplicationContext()), 
							Toast.LENGTH_SHORT).show();
				}
			}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					HashMap<String, String> headers = new HashMap<String, String>();
					String credentials = Session.getSession().getToken() + ":unused";
					headers.put(
							"Authorization",
							"Basic " + SafeEDcoderUtil.encryptBASE64(
											credentials.getBytes()).replaceAll(
											"\\s+", ""));
					return headers;
				}
			};
			req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
					Constants.Config.RETRY_TIMES, 1.0f));
			KuibuApplication.getInstance().addToRequestQueue(req);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void requestImginfo() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", collection.cid);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/get_imageinfo").toString();
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
						dealModify(arr);						
					}else{
						progressDialog.dismiss();
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
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	@SuppressLint("SimpleDateFormat")
	private void dealModify(JSONArray arr) throws JSONException
	{
		List<String> localImgs = imageVo.getImgList(collection._id);
		List<Map<String,String>>  farImgInfo = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			Map<String, String> item = new HashMap<String, String>();
			item.put("id", obj.getString("id"));
			item.put("imgUri", obj.getString("img_uri"));
			item.put("imageUrl", obj.getString("img_url"));
			farImgInfo.add(item);
		}
		
		Iterator<Map<String,String>> it = farImgInfo.iterator();
		while(it.hasNext()){
			Map<String,String> m = it.next();
			String uri = m.get("imgUri");
			if(localImgs.contains(uri)){
				imgurl_map.put(uri, m.get("imageUrl"));
				it.remove();
				localImgs.remove(uri);
			}
		}
		
		if(localImgs.size()>0){ //需要添加							
			StringBuffer url_root = new StringBuffer('/');
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = new java.util.Date();
			String today = sdf.format(date);
			url_root.append(today).append("/");
			
			for (int i = 0; i < localImgs.size(); i++) {
				String uuid = UUID.randomUUID().toString();
				String ext = getExtensionName(localImgs.get(i));
				StringBuffer buffer = new StringBuffer(url_root);
				buffer.append(uuid).append(".").append(ext);
				imgurl_map.put(localImgs.get(i), buffer.toString());
			}
		}
		
		StringBuffer ids = new StringBuffer();
		if(farImgInfo.size()>0){ //需要删除
			for(Map<String,String> m:farImgInfo){
				String uri = m.get("imgUri");
				imgurl_map.put(uri, "");			
				ids.append(m.get("id")).append(",");
			}
		}	
		
		collection.content = adjustMarkDownText(collection.getContent());
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("cid", collection.cid);
		params.put("title",collection.title);
		params.put("content", collection.content);
		params.put("type", collection.type);
		requestUpdate(params,localImgs,ids.toString());
	}
	
	private void uploadImgs(List<String> imgs,final boolean flag) {
		AjaxParams params = new AjaxParams();
		params.put("cid", String.valueOf(collection.cid));				
		try {			
			if(!TextUtils.isEmpty(mCoverPath)){
				StringBuffer buffer = new StringBuffer();
				buffer.append(UUID.randomUUID().toString()).append(".").append(getExtensionName(mCoverPath));
				params.put("cover_name", mCoverPath);
				params.put("cover_url", buffer.toString());
				String dest = BitmapHelper.hasCompressFile(this, mCoverPath);
				if(dest == null){ 
					dest = BitmapHelper.compressBitmap(this, mCoverPath, 
							Constants.COMPRESS_WIDTH,Constants.COMPRESS_HEIGHT , false);
				}				
				params.put("cover_data", new File(dest));
			}else{
				params.put("cover_url", "");		
				params.put("cover_name", "");
			}
			params.put("size", imgs.size() + "");
			for (int i = 0; i < imgs.size(); i++) {
				String uri = imgs.get(i);
				String map_uri = imgurl_map.get(uri);
				params.put("map_url_" + i, map_uri);
				params.put("url_" + i, uri);
				String path = uri.substring(Constants.URI_PREFIX.length());
				String dest = BitmapHelper.hasCompressFile(this, path);
				if(dest == null){ //无相应压缩文件
					dest = BitmapHelper.compressBitmap(this, path, 
							Constants.COMPRESS_WIDTH,Constants.COMPRESS_HEIGHT , false); //先压缩
				}				
				params.put("data_" + i, new File(dest));
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
				Toast.makeText(PreviewActivity.this, getString(R.string.publish_fail),
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
							collectionVo.update(" cid = ? ,is_pub = ?,is_sync = ? "," _id = ? ",
									new String[] { collection.cid,
											String.valueOf(1),
											String.valueOf(1),
											String.valueOf(collection._id) });
							pubMenu.setVisible(false);
							
							bLockFlag2 = true; 
							if(bLockFlag1 && bLockFlag2){
								progressDialog.dismiss();
								Toast.makeText(PreviewActivity.this, getString(R.string.modify_success),
										Toast.LENGTH_SHORT).show();
							}
							
							if(flag){
								progressDialog.dismiss();
								Toast.makeText(PreviewActivity.this, getString(R.string.publish_success),
										Toast.LENGTH_SHORT).show();
							}							
						} else if (StaticValue.RESPONSE_STATUS.UPLOAD_FAILD
								.equals(state)) { //上传失败
							progressDialog.dismiss();
							
							if(flag){
								
								Toast.makeText(PreviewActivity.this, getString(R.string.publish_fail),
										Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(PreviewActivity.this, getString(R.string.modify_fail),
										Toast.LENGTH_SHORT).show();
							}
							
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
	}

	private void requestUpdate(final HashMap<String, String> params,final List<String> addImgInfo,
			final String delImgIds) {
		
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION) 
							.append("/update_collection").toString();
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						if(!TextUtils.isEmpty(delImgIds)){
							requestDelImgs(params.get("cid"),delImgIds);
						}else{
							bLockFlag1 = true; 
						}
						
						if(addImgInfo != null && addImgInfo.size()>0){
							uploadImgs(addImgInfo,false);
						}else{
							bLockFlag2 = true;  
						}
						
						collectionVo.update(
								" is_pub = ?,is_sync = ? ",
								" _id = ? ",
								new String[] {String.valueOf(1),
										String.valueOf(1),
										String.valueOf(collection._id)});
						pubMenu.setVisible(false);		
						if(bLockFlag1 && bLockFlag2 ){
							progressDialog.dismiss();
						}
					}else{
						progressDialog.dismiss();
						Toast.makeText(PreviewActivity.this, getString(R.string.modify_fail),
								Toast.LENGTH_SHORT).show();
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
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
		
	private void requestDelImgs(String cid , String ids)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("cid", cid);
		params.put("ids", ids);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/del_imgs").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						bLockFlag1 = true; 
						if(bLockFlag1 && bLockFlag2){
							progressDialog.dismiss();
							Toast.makeText(PreviewActivity.this, getString(R.string.modify_success),
									Toast.LENGTH_SHORT).show();
						}						
					}else{
						progressDialog.dismiss();
						Toast.makeText(PreviewActivity.this, getString(R.string.modify_fail),
								Toast.LENGTH_SHORT).show();	
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
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		intent.putExtra("cover_path", mCoverPath);
		setResult(RESULT_OK, intent);
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
	
	private String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return null;
	}
	
	private String adjustMarkDownText(String markdownText) {
		String pattern = "!\\[.*\\]\\(\\s*(file:.*)\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(markdownText);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {			
			String uri = m.group(1);
			if(!TextUtils.isEmpty(imgurl_map.get(uri))){
				StringBuffer http_url = new StringBuffer("![](");
				http_url.append(imgurl_map.get(uri)).append(")");
				m.appendReplacement(sb, http_url.toString());
			}else{
				m.appendReplacement(sb, "");
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	
	/**获取摘要
	 * @return
	 */
	private String getAbstract()
	{
		String result = null;
		Document doc = Jsoup.parse(htmlSource);
		String ptext = doc.getElementsByTag("p").text();
		if(TextUtils.isEmpty(ptext)){
			StringBuffer buffer = new StringBuffer();
			String s = doc.getElementsByTag("h1").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s= doc.getElementsByTag("h2").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s= doc.getElementsByTag("h3").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s=doc.getElementsByTag("h4").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s=doc.getElementsByTag("h5").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s=doc.getElementsByTag("h6").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s=doc.getElementsByTag("ol").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			s=doc.getElementsByTag("ul").remove().text();
			if(!TextUtils.isEmpty(s)) buffer.append(s).append("\n");
			String body = doc.body().text();
			if(TextUtils.isEmpty(body)){
				result = buffer.substring(0, 
						buffer.length() > ABSTRACT_MAX ? ABSTRACT_MAX:buffer.length());
			}else{
				result = body.substring(0, 
						buffer.length() > ABSTRACT_MAX ? ABSTRACT_MAX:buffer.length());
			}			
		}else{
			result = ptext.substring(0, 
					ptext.length() > ABSTRACT_MAX ? ABSTRACT_MAX:ptext.length());
		}		
		return result ; 
	}

	@Override
	public void getHtmlSource(String html) {
		htmlSource = html ; 
	}

	@Override
	public void pageLoadFinished() {
		// TODO Auto-generated method stub
		
	}
}
