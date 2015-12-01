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
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.AlbumWListModeImpl;
import com.kuibu.model.db.AlbumVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.interfaces.AlbumWListModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.AlbumWListPresenter;
import com.kuibu.module.request.listener.OnAlbumWListListener;
import com.kuibu.ui.view.interfaces.AlbumWListView;

public class AlbumWListPresenterImpl implements AlbumWListPresenter ,OnAlbumWListListener{

	private AlbumWListView mView ; 
	private AlbumWListModel mModel; 	
	private boolean showContextMenu = false;      
	private boolean isMulChoice = false; 
	private List<CollectionBean> selItems; 
	private CollectionVo collectionVo;
	private AlbumVo packVo; 
	private ImageLibVo  imageVo; 
	private String pid ;
	private ProgressDialog progressDlg ; 
	private int packCount ; 
	private List<CollectionBean> mData = new ArrayList<CollectionBean>();

	public AlbumWListPresenterImpl(AlbumWListView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new AlbumWListModeImpl(this);
		collectionVo = new CollectionVo(mView.getInstance());
		packVo = new AlbumVo(mView.getInstance());
		imageVo = new ImageLibVo(mView.getInstance());

		pid = mView.getDataIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
		String count = mView.getDataIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT);
		packCount = Integer.parseInt(count);
	}

	private void requestCollections(List<CollectionBean> items) {
		// TODO Auto-generated method stub		
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
				//删除剩余的已经发布的记录
				collectionVo.delete(cids);
				imageVo.deleteBycids(cids);
				int size = response.getInt("size");
				if(size > 0){
					packVo.update("collect_count = collect_count - " + size, 
						" pack_id = ? ", new String[]{String.valueOf(pid)});
				}
				mView.refreshList(mData);		
				KuibuUtils.showText(R.string.delete_success, Toast.LENGTH_SHORT);
			}else{
				KuibuUtils.showText(R.string.delete_fail, Toast.LENGTH_SHORT);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			progressDlg.dismiss();
			switchToolBar(false);
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
		KuibuUtils.showText(R.string.net_error, Toast.LENGTH_SHORT);
		switchToolBar(false);
	}
	
	@Override
	public void queryCollection() {
		// TODO Auto-generated method stub
		mData = collectionVo.queryWithcons("pid = ?", new String[]{String.valueOf(pid)});
		mView.refreshList(mData);
	}

	@Override
	public void closeDbConn() {
		// TODO Auto-generated method stub
		collectionVo.closeDB();
		packVo.closeDB();
		imageVo.closeDB();
	}

	@Override
	public void switchToolBar(boolean mode) {
		// TODO Auto-generated method stub
		if(mode){ //multichoice 
        	isMulChoice = true;
			showContextMenu = true ; 
			mView.setButtonVisible(View.GONE);
		}else{
			isMulChoice = false ; 
			showContextMenu = false;
			if(selItems!=null){
				selItems.clear();
			}
			mView.setButtonVisible(View.VISIBLE);
			mView.setBarTitle(mView.getDataIntent().getStringExtra(
					StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		}
		mView.refreshOptionMenu();					
		mView.resetAdapter(mData, isMulChoice);
	}

	@Override
	public void delCollections() {
		// TODO Auto-generated method stub
		if(selItems == null || selItems.size()<=0){
    		KuibuUtils.showText(R.string.choose_one,Toast.LENGTH_SHORT);
    		return ; 
    	}
    	new AlertDialog.Builder(mView.getInstance())  
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
            		
            		//删除本地还未发布的记录  
            		if(cids.size()>0){
	            		collectionVo.delete(cids);
	            		imageVo.deleteBycids(cids);		
	            		mView.refreshList(mData);		
            		}
            		if(packCount>= delSize && delSize>0){
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
			public void onClick(DialogInterface arg0, int arg1) {
				switchToolBar(false);
			}						
		})  
    	.show();  
	}

	@Override
	public boolean isMultiChoice() {
		// TODO Auto-generated method stub
		return isMulChoice;
	}

	@Override
	public boolean isMenuVisible() {
		// TODO Auto-generated method stub
		return showContextMenu;
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
			selItems = new LinkedList<CollectionBean>();
		}
		return selItems ; 
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
}

