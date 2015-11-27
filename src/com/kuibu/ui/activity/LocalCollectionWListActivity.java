package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.ButtonFloat;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.db.CollectPackVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.LocalCollectionWAdapter;
import com.kuibu.module.adapter.LocalCollectionWAdapter.HolderView;

public class LocalCollectionWListActivity extends BaseActivity {
	
	private ButtonFloat fbutton;
	private ListView collectionList;
	private List<CollectionBean> mData = new ArrayList<CollectionBean>();
	private LocalCollectionWAdapter collectionAdapter= null;
	private boolean showContextMenu = false;      
	private boolean isMulChoice = false; 
	private List<CollectionBean> selItems = null; 
	private Context context = null; 
	private CollectionVo collectionVo = null ;
	private CollectPackVo packVo = null ; 
	private ImageLibVo  imageVo = null ; 
	private String pid ;
	private ProgressDialog progressDlg ; 
	private int packCount ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		context = this ;
		collectionVo = new CollectionVo(this);
		packVo = new CollectPackVo(this);
		imageVo = new ImageLibVo(this);
		pid = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
		String count = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT);
		packCount = Integer.parseInt(count);
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		setContentView(R.layout.local_collection_list_activity);	
		fbutton = (ButtonFloat) findViewById(R.id.buttonFloat);
		collectionList = (ListView) findViewById(R.id.colleciton_lv);

		collectionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				CollectionBean item = (CollectionBean) adapterView
						.getAdapter().getItem(position); 
				if(!isMulChoice){
					Intent intent = new Intent(LocalCollectionWListActivity.this,
							PreviewActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID,String.valueOf(pid));
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID, item.get_id()+"");
					intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
							StaticValue.EDITOR_VALUE.LIST_TO_PREVIEW);
					startActivity(intent);					
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else{					
						if(selItems == null)
							selItems = new LinkedList<CollectionBean>();
						HolderView  holderView = (HolderView)view.getTag();
						if(holderView ==null)
							return ;
						if(holderView.check.isChecked()){
							holderView.check.setChecked(false);
							selItems.remove(item);
						}else{
							holderView.check.setChecked(true);
							selItems.add(item);
						}
						StringBuilder buffer =  new StringBuilder("已选").append(selItems.size()).append("项");
						setTitle(buffer.toString());
					}
				}
		});
		
		fbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocalCollectionWListActivity.this,
						CollectionEditorActivity.class);
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID, pid);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		mData = collectionVo.queryWithcons("pid = ?", new String[]{String.valueOf(pid)});		
		showView();		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		collectionVo.closeDB();
		packVo.closeDB();
		imageVo.closeDB();
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_list_context_menu, menu);
		MenuItem moveMenu = menu.findItem(R.id.context_menu_move);
		MenuItem delMenu = menu.findItem(R.id.context_menu_delete);
		MenuItem manageItem = menu.findItem(R.id.context_menu_manage);
		if(showContextMenu){
			delMenu.setVisible(true);
			manageItem.setVisible(false);
		}else{
			delMenu.setVisible(false);
			manageItem.setVisible(true);
		}
		moveMenu.setVisible(false); // no support now . 
        return true;
    }
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	this.onBackPressed();
	            	overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	            	return true;
	            case R.id.context_menu_move:       
	            	Toast.makeText(this, "开发中...", Toast.LENGTH_SHORT).show();
	            	return true ; 
	            case R.id.context_menu_manage:
	            	
	            	isMulChoice = true;
					showContextMenu = true ; 
					invalidateOptionsMenu();
					fbutton.setVisibility(View.GONE);				
					collectionAdapter = new LocalCollectionWAdapter(context,mData,isMulChoice);
					collectionList.setAdapter(collectionAdapter);
					
	            	return true ; 
	            case R.id.context_menu_delete:
	            	if(selItems == null || selItems.size()<=0){
	            		Toast.makeText(this, getString(R.string.choose_one), Toast.LENGTH_SHORT).show();
	            		return false; 
	            	}
	            	new  AlertDialog.Builder(this)  
	            	.setTitle(getString(R.string.action_delete))    
	            	.setMessage("确定要删除"+selItems.size()+"条收集?" )  
	            	.setPositiveButton(getString(R.string.btn_confirm) , new DialogInterface.OnClickListener() {
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
				            		showView();		
			            		}
			            		if(packCount>= delSize && delSize>0){
			            			packVo.update("collect_count = collect_count - " + delSize, 
			            					" pack_id = ? ", new String[]{String.valueOf(pid)});
			            		}
			            		
			            		if(selItems.size()>0)
			            			requestDel(selItems);
			            		}else{
			            			RestoreToolBar();
			            		}
						}						

					})  
	            	.setNegativeButton(getString(R.string.btn_cancel) , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							RestoreToolBar();
						}						
					})  
	            	.show();  
	            	return true ; 
	            default:
	                return super.onOptionsItemSelected(item);
	            	
	        }
	 }       

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(showContextMenu){
			RestoreToolBar();
		}else{
			super.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		}		
	}	
	
	private void showView()
	{
		if(collectionAdapter == null){
			collectionAdapter = new LocalCollectionWAdapter(this, mData,isMulChoice);
			collectionList.setAdapter(collectionAdapter);
		}else{
			collectionAdapter.updateView(mData);
		}
	}
	
	private void RestoreToolBar()
	{
		isMulChoice = false ; 
		showContextMenu = false;
		if(selItems!=null)
			selItems.clear();
		invalidateOptionsMenu();
		fbutton.setVisibility(View.VISIBLE);
		collectionAdapter = new LocalCollectionWAdapter(this,mData,isMulChoice);
		collectionList.setAdapter(collectionAdapter);
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
	}	
	
	private void requestDel(final List<CollectionBean> items)
	{
		if(progressDlg == null){
			progressDlg = new ProgressDialog(this);
			progressDlg.setTitle(getString(R.string.deleting));
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
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/del_collection").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
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
						packVo.update("collect_count = collect_count - " + items.size(), 
            					" pack_id = ? ", new String[]{String.valueOf(pid)});
						showView();						
						Toast.makeText(LocalCollectionWListActivity.this, 
								getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LocalCollectionWListActivity.this, 
								getString(R.string.delete_fail),Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				progressDlg.dismiss();
				RestoreToolBar();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());				
				progressDlg.dismiss();
				Toast.makeText(getApplicationContext(), 
						getString(R.string.net_error),Toast.LENGTH_SHORT).show();
				RestoreToolBar();
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
		
}