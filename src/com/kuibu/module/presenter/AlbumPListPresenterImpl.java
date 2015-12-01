package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.AlbumPListModeImpl;
import com.kuibu.model.db.AlbumVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.interfaces.AlbumPListModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.AlbumPListPresenter;
import com.kuibu.module.request.listener.OnAlbumPListListener;
import com.kuibu.ui.view.interfaces.AlbumPListView;

public class AlbumPListPresenterImpl implements AlbumPListPresenter ,OnAlbumPListListener{
	
	private static final int OPER_TYPE_REFRESH = 1 ; 
	private static final int OPER_TYPE_LOADMORE = 2; 
	private static final int PAGE_SIZE = 6 ; 
	
	private AlbumPListView mView ; 
	private AlbumPListModel mModel ; 
	private String pid;
	private CollectionVo collectionVo ; 
	private AlbumVo packVo = null ; 
	private ImageLibVo  imageVo = null ; 
	private ContentTask mTask; 
	private boolean bMultiChoice = false ;
	private LinkedList<CollectionBean> mData = new LinkedList<CollectionBean>();
	private List<CollectionBean> selItems; 
	private int curPosition  ; 
	private int packCount ; 
	private ProgressDialog progressDlg ;
	
	public AlbumPListPresenterImpl(AlbumPListView view) {
		// TODO Auto-generated constructor stub
		this.mView = view; 
		this.mModel = new AlbumPListModeImpl(this);
		pid = mView.getDataIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);		
		packCount = Integer.parseInt(
				mView.getDataIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT));
		collectionVo = new CollectionVo(mView.getInstance());
		packVo = new AlbumVo(mView.getInstance());
		imageVo = new ImageLibVo(mView.getInstance());		
		mTask = new ContentTask(OPER_TYPE_LOADMORE);
	}

	private void requestCollections(List<CollectionBean> items) {
		if(progressDlg == null){
			progressDlg = new ProgressDialog(mView.getInstance());
			progressDlg.setTitle(KuibuUtils.getString(R.string.deleting));
			progressDlg.setCanceledOnTouchOutside(false);
		}
		progressDlg.show();

		Map<String, String> params = new HashMap<String, String>();		
		params.put("uid", Session.getSession().getuId());
		params.put("size", String.valueOf(items.size()));
		params.put("pack_id", String.valueOf(pid));
		StringBuffer buffer= new StringBuffer();
		for(int i=0;i<items.size();i++){
			buffer.append(items.get(i).cid).append(",");
		}
		params.put("collection_ids", buffer.toString());
		mModel.requestDelCollection(params);
	}
	
	@Override
	public void delCollections() {
		// TODO Auto-generated method stub
		if(selItems == null || selItems.size()<=0){
			KuibuUtils.showText(R.string.choose_one, Toast.LENGTH_SHORT);
    		return; 
    	}
    	new  AlertDialog.Builder(mView.getInstance())  
    	.setTitle(KuibuUtils.getString(R.string.action_delete))    
    	.setMessage("确定要删除"+selItems.size()+"条收集?" )  
    	.setPositiveButton(KuibuUtils.getString(R.string.btn_confirm) , 
    			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {	
            	//添加提示框
            	if(selItems != null && selItems.size()>0){         					            		
            		ArrayList<String> cids = new ArrayList<String>();
            		int delSize = 0 ; 
            		for(int i =0 ;i<selItems.size();i++){
            			CollectionBean item = selItems.get(i);
            			if(item.isPublish == 0){//未发布的
            				cids.add(String.valueOf(item._id));			            				
            				selItems.remove(item);
            				mData.remove(item);
            				++delSize ; 
            			}			            			
            		}
            		
            		if(cids.size()>0){
            			collectionVo.delete(cids);
            			imageVo.deleteBycids(cids);	
            			mView.refreshList(mData);
            		}
            		
            		if(packCount >= delSize && delSize>0){
            			packVo.update("collect_count = collect_count - " + delSize, 
            					" pack_id = ? ", new String[]{String.valueOf(pid)});
            		}
            		
            		if(selItems.size()>0)
            			requestCollections(selItems);
            		}else{
            			switchToolBar(false);
            		}
			}						

		})  
    	.setNegativeButton(KuibuUtils.getString(R.string.btn_cancel) , 
    			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {}						
		})  
    	.show();  

	}

	@Override
	public void queryCollection() {
		
		// TODO Auto-generated method stub
		if(mTask.getStatus() != Status.RUNNING){
			mTask = new ContentTask(OPER_TYPE_LOADMORE);
			mTask.execute("");
		}
	}

	@Override
	public void closeDbConn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchToolBar(boolean mode) {
		// TODO Auto-generated method stub
		if(mode){

			bMultiChoice = true; 
			mView.setButtonVisible(View.GONE);		
		}else{
			bMultiChoice = false ;  		
			if(selItems!=null)
				selItems.clear();
			mView.setButtonVisible(View.VISIBLE);
			mView.setBarTitle(mView.getDataIntent().getStringExtra(
					StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		}
		mView.refreshOptionMenu();
		mView.resetAdapter(mData, bMultiChoice);
	}

	@Override
	public boolean isMultiChoice() {
		// TODO Auto-generated method stub
		return bMultiChoice;
	}

	@Override
	public String getPackId() {
		// TODO Auto-generated method stub
		return pid;
	}

	@Override
	public List<CollectionBean> getSelectItems() {
		// TODO Auto-generated method stub
		if(selItems == null){
			selItems = new LinkedList<CollectionBean>() ; 
		}
		return selItems;
	}

	@Override
	public void selectOne(CollectionBean item) {
		// TODO Auto-generated method stub
		getSelectItems().add(item);
	}

	@Override
	public void unSelectOne(CollectionBean item) {
		// TODO Auto-generated method stub
		getSelectItems().remove(item);
	}
	
	private class ContentTask extends AsyncTask<String,Integer,List<CollectionBean>>{
		private int oType ; 
		
		public ContentTask(int type)
		{
			this.oType = type ; 
		}
		
		@Override
		protected List<CollectionBean> doInBackground(String... arg0) {
			List<CollectionBean> result = new ArrayList<CollectionBean>();
			if(oType == OPER_TYPE_REFRESH){
				
			}else if(oType == OPER_TYPE_LOADMORE){
				result = collectionVo.queryWithByPage(" _id < ? and pid = ? ",
						new String[]{String.valueOf(mView.getAdapter().getMinId()),pid},PAGE_SIZE,0);
				
				for(CollectionBean item : result){
					item.cover  = Constants.URI_PREFIX + item.cover ; 
				}
			}
			return result;
		}
		
		@Override
	    protected void onPostExecute(List<CollectionBean> result) {
			if(result != null ){
				if(oType == OPER_TYPE_REFRESH){
					
				}else if(oType == OPER_TYPE_LOADMORE){
					if(result.size() < PAGE_SIZE){
						mView.setPullEnable(false);
					}
					mData.addAll(result);
					mView.refreshList(mData);
	                mView.stopRefresh();
				}	
			}		
		}
	}

	@Override
	public void OnDelCollectionResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {		
				ArrayList<String> cids = new ArrayList<String>(); 
	    		for(int i =0 ;i<selItems.size();i++){
	    			CollectionBean item = selItems.get(i);
	    			cids.add(String.valueOf(item._id));
	    			mData.remove(item);
	    		}
	    		collectionVo.delete(cids);
	    		imageVo.deleteBycids(cids);
	    		int size = response.getInt("size");
	    		if(size>0){
	    			packVo.update("collect_count = collect_count - " + size, 
						" pack_id = ? ", new String[]{String.valueOf(pid)});
	    		}
				mView.refreshList(mData);
			}else{
				KuibuUtils.showText(R.string.delete_fail, Toast.LENGTH_SHORT);
			}
			progressDlg.dismiss();
			switchToolBar(false);
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		KuibuUtils.showText(R.string.net_error,Toast.LENGTH_SHORT);
		progressDlg.dismiss();
		switchToolBar(false);
	}

	@Override
	public void setCurClick(int position) {
		// TODO Auto-generated method stub
		curPosition = position ; 
	}

	@Override
	public int getCurClick() {
		// TODO Auto-generated method stub
		return curPosition;
	}

	@Override
	public void addCollection(CollectionBean item) {
		// TODO Auto-generated method stub
		mData.addFirst(item);
		mView.refreshList(mData);
	}
	

}
