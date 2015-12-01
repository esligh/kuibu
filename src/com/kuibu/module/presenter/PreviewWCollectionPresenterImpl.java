package com.kuibu.module.presenter;

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

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.PreviewWCollectionModelImpl;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.interfaces.PreviewWCollectionModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.PreviewWCollectionPresenter;
import com.kuibu.module.request.listener.OnPreviewWCollectionListener;
import com.kuibu.ui.view.interfaces.PreviewWCollectionView;

public class PreviewWCollectionPresenterImpl implements 
	PreviewWCollectionPresenter,OnPreviewWCollectionListener{

	public static final int ABSTRACT_MAX = 150 ; 
	private PreviewWCollectionModel mModel ; 
	private PreviewWCollectionView mView; 
	private boolean bLockFlag1 = false; 
	private boolean bLockFlag2 = false ;
	private String htmlSource ; 
	private Map<String,String> imgurl_map;
	private CollectionBean collection;
	private CollectionVo collectionVo;
	private ImageLibVo imageVo;
	private ProgressDialog progressDialog;
	private String mCoverPath = "";
	
	public PreviewWCollectionPresenterImpl(PreviewWCollectionView view) {
		// TODO Auto-generated constructor stub
		this.mView = view; 
		this.mModel = new PreviewWCollectionModelImpl(this);	
	}

	@SuppressLint("SimpleDateFormat")
	@Override	
	public void publish() {
		if(collection ==null || TextUtils.isEmpty(collection.content)){
			KuibuUtils.showText(R.string.content_empty,Toast.LENGTH_SHORT);
			return ;
		}
		progressDialog = new ProgressDialog(mView.getInstance());
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(KuibuUtils.getString(R.string.publishing));
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
			String ext = FileUtils.getExtensionName(images.get(i));
			StringBuffer buffer = new StringBuffer(url_root);
			buffer.append(uuid).append(".").append(ext);
			imgurl_map.put(images.get(i), buffer.toString());
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
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
			params.put("cisn",SafeEDcoderUtil.MD5(descript));
			params.put("pack_id", collection.pid);			
			params.put("cover", "");
			params.put("images",images);
			mModel.doPublish(params);
		}
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
	public void loadCollection() {
		// TODO Auto-generated method stub
		imageVo = new ImageLibVo(mView.getInstance());
		collectionVo = new CollectionVo(mView.getInstance());
		String id = mView.getDataIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID);
		if (TextUtils.isEmpty(id)) {
			collection = (CollectionBean) mView.getDataIntent().getSerializableExtra(
					StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
		} else {
			collection = collectionVo.querywithkey(id);
		}		
		if(!TextUtils.isEmpty(collection.cover)){
			mCoverPath = collection.cover;
			mView.setCover(Constants.URI_PREFIX+collection.cover);
		}
		if (collection != null && !TextUtils.isEmpty(collection.getContent())){
			mView.setCollectionTitle(collection.title);
			mView.setPageContent(collection.getContent());
		}		
	}
	
	@SuppressLint("SimpleDateFormat")
	private void requestImginfo() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", collection.cid);
		mModel.requestImgList(params);
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
				String ext = FileUtils.getExtensionName(localImgs.get(i));
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
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("cid", collection.cid);
		params.put("title",collection.title);
		params.put("content", collection.content);
		params.put("type", collection.type);
		params.put("addImgs", localImgs);
		params.put("delImgs", ids.toString());
		mModel.requestUpdateImg(params);
	}
	
	private void uploadImgs(List<String> imgs,boolean bfirst) {
		AjaxParams params = new AjaxParams();
		params.put("cid", String.valueOf(collection.cid));				
		try {			
			if(!TextUtils.isEmpty(mCoverPath)){
				StringBuffer buffer = new StringBuffer();
				buffer.append(UUID.randomUUID().toString()).append(".").append(FileUtils.getExtensionName(mCoverPath));
				params.put("cover_name", mCoverPath);
				params.put("cover_url", buffer.toString());
				String dest = BitmapHelper.hasCompressFile(
						KuibuApplication.getContext(),mCoverPath);
				if(dest == null){ 
					dest = BitmapHelper.compressBitmap(KuibuApplication.getContext(), mCoverPath, 
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
				String dest = BitmapHelper.hasCompressFile(KuibuApplication.getContext(), path);
				if(dest == null){ //无相应压缩文件
					dest = BitmapHelper.compressBitmap(KuibuApplication.getContext(), path, 
							Constants.COMPRESS_WIDTH,Constants.COMPRESS_HEIGHT , false); //先压缩
				}				
				params.put("data_" + i, new File(dest));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mModel.upLoadImgs(params,bfirst);
	}
	
	
	private void requestDelImgs(String cid,String ids)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("cid", cid);
		params.put("ids", ids);
		mModel.requestDelImgs(params);
	}

	@Override
	public void closeDbConn() {
		// TODO Auto-generated method stub

		imageVo.closeDB();
		collectionVo.closeDB();
	}

	@Override
	public void updateCover(String url) {
		// TODO Auto-generated method stub
    	collectionVo.update(" cover = ? ", " _id = ? ", new String[]{url,collection._id});
	}

	@Override
	public void setHtmlSource(String html) {
		// TODO Auto-generated method stub
		htmlSource = html ; 
	}

	@Override
	public CollectionBean getCollection() {
		// TODO Auto-generated method stub
		return collection;
	}

	@Override
	public boolean needPublish() {
		// TODO Auto-generated method stub
		return !(collection == null || (collection.isPublish == 1 && collection.isSync == 1)
				|| collection.content.equals(""));
	}

	@Override
	public String getCoverPath() {
		// TODO Auto-generated method stub
		return mCoverPath;
	}

	@Override
	public void setCoverPath(String path) {
		// TODO Auto-generated method stub
		this.mCoverPath = path ; 
	}

	@Override
	public void OnImgListResponse(JSONObject response) {
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

	@Override
	public void OnDelImgsResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				bLockFlag1 = true; 
				if(bLockFlag1 && bLockFlag2){
					progressDialog.dismiss();
					KuibuUtils.showText(R.string.modify_success,Toast.LENGTH_SHORT);
				}						
			}else{
				progressDialog.dismiss();
				KuibuUtils.showText(R.string.modify_fail,Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnUpdateImgsResponse(Map<String, Object> params,
			JSONObject response) {
		// TODO Auto-generated method stub
		String delImgIds = (String)params.get("delImgs");
		@SuppressWarnings("unchecked")
		List<String> addImgs = (List<String>)params.get("addImgs");
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				if(!TextUtils.isEmpty(delImgIds)){
					requestDelImgs((String)params.get("cid"),delImgIds);
				}else{
					bLockFlag1 = true; 
				}
				
				if(addImgs != null && addImgs.size()>0){
					uploadImgs(addImgs,false);
				}else{
					bLockFlag2 = true;  
				}
				
				collectionVo.update(
						" is_pub = ?,is_sync = ? ",
						" _id = ? ",
						new String[] {String.valueOf(1),
								String.valueOf(1),
								String.valueOf(collection._id)});
				mView.setPubMenuVisible(false);
				if(bLockFlag1 && bLockFlag2 ){
					progressDialog.dismiss();
				}
			}else{						
				progressDialog.dismiss();
				KuibuUtils.showText(R.string.modify_fail,Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
				e.printStackTrace();
		}
	}

	@Override
	public void OnUploadImgsSuccess(String s,boolean bfirst) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(s)) {
			try {
				JSONObject obj = new JSONObject(s);
				String state = obj.getString("state");
				if (StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS
						.equals(state)) { //上传成功
					collectionVo.update(" cid = ? ,is_pub = ?,is_sync = ? "," _id = ? ",
							new String[] { collection.cid,
									String.valueOf(1),
									String.valueOf(1),
									String.valueOf(collection._id) });
					mView.setPubMenuVisible(false);
					
					bLockFlag2 = true; 
					if(bLockFlag1 && bLockFlag2){
						progressDialog.dismiss();
						
						KuibuUtils.showText(R.string.modify_success,Toast.LENGTH_SHORT);
					}
					
					if(bfirst){
						progressDialog.dismiss();
						KuibuUtils.showText(R.string.publish_success,Toast.LENGTH_SHORT);								
					}							
				} else if (StaticValue.RESPONSE_STATUS.UPLOAD_FAILD
						.equals(state)) { //上传失败
					progressDialog.dismiss();					
					if(bfirst){
						KuibuUtils.showText(R.string.publish_fail,Toast.LENGTH_SHORT);
					}else{
						KuibuUtils.showText(R.string.modify_fail,Toast.LENGTH_SHORT);
					}
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void OnUploadImgsFailed(int errno, String msg) {
		// TODO Auto-generated method stub
		KuibuUtils.showText(R.string.publish_fail,Toast.LENGTH_SHORT);
		progressDialog.dismiss();	
	}

	@Override
	public void OnPubulishResponse(Map<String,Object> params,JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				collection.cid = response.getString("cid");
				@SuppressWarnings("unchecked")
				List<String> images = (List<String>)params.get("images");
				if (images != null && images.size() > 0) {  //text-pic 
					uploadImgs(images,true);
				}else{  //just text 								
					collectionVo.update(" cid = ? ,is_pub = ?,is_sync = ? "," _id = ? ",
							new String[] { collection.cid,String.valueOf(1),String.valueOf(1),
							String.valueOf(collection._id) });
					mView.setPubMenuVisible(false);
					progressDialog.dismiss();
					
					KuibuUtils.showText(R.string.publish_success,Toast.LENGTH_SHORT);								
				}					
			}else{
				progressDialog.dismiss();
				KuibuUtils.showText(R.string.publish_fail,Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
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

}
