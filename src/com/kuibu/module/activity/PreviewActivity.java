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

import us.feras.mdv.MarkdownView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;

public class PreviewActivity extends ActionBarActivity {
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);		
		boolean isDarkTheme= mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false);
		if (isDarkTheme) {
			setTheme(R.style.AppTheme_Dark);
			cssFile = Constants.WEBVIEW_DARK_CSSFILE;
		}else{
			setTheme(R.style.AppTheme_Light);
			cssFile = Constants.WEBVIEW_LIGHT_CSSFILE;
		}	
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_preview);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		initData();
		previewWebView = (MarkdownView) findViewById(R.id.preview_webview);
		previewWebView.setBackgroundColor(0);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		if (collection != null && !TextUtils.isEmpty(collection.getContent()))
			previewWebView.loadMarkdown(collection.getContent(),cssFile);

		finalHttp = new FinalHttp();
	}

	void initData() {
		imageVo = new ImageLibVo(this);
		collectionVo = new CollectionVo(this);
		String id = getIntent().getStringExtra(
				StaticValue.EDITOR_VALUE.COLLECTION_ID);
		if (TextUtils.isEmpty(id)) {
			collection = (CollectionBean) getIntent().getSerializableExtra(
					StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
		} else {
			collection = collectionVo.querywithkey(id);
		}

		if (collection != null) {
			setTitle(collection.getTitle());
		} else {
			setTitle("预览");
		}
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
		imageVo.closeDB();
		collectionVo.closeDB();
		super.onDestroy();
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

		if (collection == null
				|| (collection.isPublish == 1 && collection.isSync == 1)) {
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
			super.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_right);
			return true;
		case R.id.action_publish:
			publish_collection();
			return true;
		case R.id.action_edit:
			editNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	private void publish_collection() {
		progressDialog.setMessage("发布中...");
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
		params.put("title", collection.getTitle());
		params.put(
			"type",images.size() > 0 ? String
					.valueOf(StaticValue.EDITOR_VALUE.COLLECTION_TEXTIMAGE)
					: String.valueOf(StaticValue.EDITOR_VALUE.COLLECTION_TEXT));
		if (collection.isPublish == 1 && collection.isSync == 0) { // 已经发布，未同步  update collection
			params.put("cid", collection.cid);
			request_imginfo();			
		} else {
			collection.content = adjustMarkDownText(collection.getContent());
			params.put("content", collection.content);		
			params.put("create_by", Session.getSession().getuId());
			params.put("topic_id", "1");
			String pid = getIntent().getStringExtra(
					StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
			params.put("pack_id", pid);			
			final String URL = StaticValue.SERVER_INFO.SERVER_URI
					+ StaticValue.SERVER_INFO.REST_API_VERSION
					+ "/add_collection";
			JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
					params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					try {
						String state = response.getString("state");
						if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
								.equals(state)) {
							collection.cid = response.getString("cid");
							if (images != null && images.size() > 0) {
								upload_imgs(images);
							}							
							collectionVo.update(
									" cid = ? ,is_pub = ?,is_sync = ? ",
									" _id = ? ",
									new String[] { collection.cid,
											String.valueOf(1),
											String.valueOf(1),
											String.valueOf(collection._id) });
							pubMenu.setVisible(false);
							Toast.makeText(PreviewActivity.this, "发布成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(PreviewActivity.this, "发布失败",
									Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
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
	}
	
	@SuppressLint("SimpleDateFormat")
	private void request_imginfo() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", collection.cid);
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/get_imageinfo";
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
			upload_imgs(localImgs);
		}
		if(farImgInfo.size()>0){ //需要删除
			StringBuffer ids = new StringBuffer();
			for(Map<String,String> m:farImgInfo){
				String uri = m.get("imgUri");
				imgurl_map.put(uri, "");			
				ids.append(m.get("id")).append(",");
			}
			request_delImgs(ids.toString());
		}												
		collection.content = adjustMarkDownText(collection.getContent());
		Map<String,String> params = new HashMap<String,String>();
		params.put("cid", collection.cid);
		params.put("title",collection.title);
		params.put("content", collection.content);
		request_update(params);
	}
	
	private void upload_imgs(List<String> imgs) {
		AjaxParams params = new AjaxParams();
		params.put("cid", String.valueOf(collection.cid));
		try {
			params.put("size", imgs.size() + "");
			for (int i = 0; i < imgs.size(); i++) {
				String uri = imgs.get(i);
				String map_uri = imgurl_map.get(uri);
				params.put("map_url_" + i, map_uri);
				params.put("url_" + i, uri);
				String path = uri.substring(Constants.URI_PREFIX.length());
				params.put("data_" + i, new File(path));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/simple_upload";
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (!TextUtils.isEmpty(t)) {
					try {
						JSONObject obj = new JSONObject(t);
						String state = obj.getString("state");
						if (StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS
								.equals(state)) {
						} else if (StaticValue.RESPONSE_STATUS.UPLOAD_FAILD
								.equals(state)) {
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

	private void request_update(Map<String, String> params) {
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/update_collection";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Toast.makeText(PreviewActivity.this, "修改成功",
								Toast.LENGTH_SHORT).show();
						collectionVo.update(
								" is_pub = ?,is_sync = ? ",
								" _id = ? ",
								new String[] {String.valueOf(1),
										String.valueOf(1),
										String.valueOf(collection._id)});
						pubMenu.setVisible(false);						
					}else{
						Toast.makeText(PreviewActivity.this, "修改失败",
								Toast.LENGTH_SHORT).show();
					}
					progressDialog.dismiss();
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
		
	private void request_delImgs(String ids)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("ids", ids);
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/update_collection";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Toast.makeText(PreviewActivity.this, "修改成功",
								Toast.LENGTH_SHORT).show();					
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
}
