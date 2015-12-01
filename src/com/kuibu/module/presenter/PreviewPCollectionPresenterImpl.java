package com.kuibu.module.presenter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.PreviewPCollectionModelImpl;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.interfaces.PreviewPCollectionModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.PreviewPCollectionPresenter;
import com.kuibu.module.request.listener.OnPreviewPCollectionListener;
import com.kuibu.ui.view.interfaces.PreviewPCollectionView;

public class PreviewPCollectionPresenterImpl 
	implements PreviewPCollectionPresenter,OnPreviewPCollectionListener{

	private PreviewPCollectionModel mModel ; 
	private PreviewPCollectionView mView ; 
	private CollectionBean collection ; 
	private ProgressDialog progressDialog;
	private CollectionVo collectionVo ; 
	private String action ;  
	private final Handler handler;
    private boolean rotating=false;
    private boolean bModify = false ;
    
	public PreviewPCollectionPresenterImpl(PreviewPCollectionView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new PreviewPCollectionModelImpl(this);	
		collectionVo = new CollectionVo(mView.getInstance());
		handler = new Handler();
		collection = (CollectionBean)mView.getDataIntent().getSerializableExtra(
				StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void publish() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(mView.getInstance());
		}
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(KuibuUtils.getString(R.string.publishing));
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
		
		Map<String, Object> params = new HashMap<String, Object>();
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
		mModel.doPublish(params);
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
				String dest = BitmapHelper.hasCompressFile(mView.getInstance(), path);
				if(dest == null){ 
					dest = BitmapHelper.compressBitmap(mView.getInstance(), path, 
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

	}

	@Override
	public void loadCollection() {
		// TODO Auto-generated method stub
		if(collection != null ){
			if(!collection.cover.startsWith(Constants.URI_PREFIX)){
				collection.cover = Constants.URI_PREFIX + collection.cover ; 
			}						
			mView.setCollectionTitle(collection.title);
			if(TextUtils.isEmpty(collection.content)){
				mView.setCollectionDescVisible(View.GONE);
			}else{
				mView.setCollectionDesc(collection.content);				
			}
			mView.setImage(collection.cover);
		}
	}

	@Override
	public void closeDbConn() {
		// TODO Auto-generated method stub
		collectionVo.closeDB();
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
	public void OnPubulishResponse(Map<String, Object> params,
			JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				if("ADD".equals(action)){
					collection.cid = response.getString("cid");							 
					uploadImgs((String)params.get("cover"));							
				}else{
					KuibuUtils.showText(R.string.modify_success, Toast.LENGTH_SHORT);
				}
				progressDialog.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnUploadImgsSuccess(String s) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(s)) {
			try {
				JSONObject obj = new JSONObject(s);
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
					bModify = true ; 
					collection.isPublish=collection.isSync = 1 ; 
					mView.setPubMenuVisible(false);
					KuibuUtils.showText(R.string.publish_success, Toast.LENGTH_SHORT);
				} else if (StaticValue.RESPONSE_STATUS.UPLOAD_FAILD
						.equals(state)) { //上传失败
					KuibuUtils.showText(R.string.publish_fail, Toast.LENGTH_SHORT);
				}
				progressDialog.dismiss();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void OnUploadImgsFailed(int errno, String msg) {
		// TODO Auto-generated method stub
		KuibuUtils.showText(R.string.publish_fail, Toast.LENGTH_SHORT);
		progressDialog.dismiss();
	}

	@Override
	public void OnVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		if("ADD".equals(action)){
			KuibuUtils.showText(R.string.publish_fail, Toast.LENGTH_SHORT);
		}else{
			KuibuUtils.showText(R.string.modify_fail, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void toggleRotation() {
		// TODO Auto-generated method stub
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
            	mView.setImageRotationBy(1);
            	rotateLoop();
            }
        }, 15);
    }

	@Override
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return bModify;
	}

	@Override
	public void setChanged(boolean state) {
		// TODO Auto-generated method stub
		this.bModify = state; 
	}

	@Override
	public void setContentDesc(String desc) {
		// TODO Auto-generated method stub
		collection.content = desc ; 
	}

	@Override
	public void setContentTitle(String title) {
		// TODO Auto-generated method stub
		collection.title = title ; 
	}

	@Override
	public void trigger() {
		// TODO Auto-generated method stub
		if(!rotating){
			toggleRotation();
			mView.setRotateBtnIcon(R.drawable.iconfont_stop);		
		}else{
			handler.removeCallbacksAndMessages(null);
			mView.setImageRotationTo(0);
			mView.setRotateBtnIcon(R.drawable.iconfont_start);
		}
		rotating = !rotating;
	}

	@Override
	public void removeCallback() {
		// TODO Auto-generated method stub
		if(handler != null){
			handler.removeCallbacksAndMessages(null);
		}
	}
}
