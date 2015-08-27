package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectPackVo;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;
import com.kuibu.module.adapter.LocalCollectionAdapter;
import com.kuibu.module.adapter.LocalCollectionAdapter.HolderView;

public class LocalCollectionListActivity extends BaseActivity {
	
	private FloatingActionsMenu fabMenu;
	private FloatingActionButton fabCreateNote;
	private FloatingActionButton fabCreateFolder;
	private ListView collectionList;
	private List<CollectionBean> mData = new ArrayList<CollectionBean>();
	private LocalCollectionAdapter collectionAdapter= null;
	private boolean showContextMenu = false;      
	private boolean isMulChoice = false; 
	private List<CollectionBean> selItems = null; 
	private Context context = null; 
	private CollectionVo collectionVo = null ;
	private CollectPackVo packVo = null ; 
	private ImageLibVo  imageVo = null ; 
	private String pid ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this ;
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		setContentView(R.layout.local_collection_list_activity);	
		fabMenu = (FloatingActionsMenu) findViewById(R.id.fab);
		fabCreateNote = (FloatingActionButton) findViewById(R.id.create_note);
		fabCreateFolder = (FloatingActionButton) findViewById(R.id.create_folder);
		collectionList = (ListView) findViewById(R.id.colleciton_lv);

		collectionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				CollectionBean item = (CollectionBean) adapterView
						.getAdapter().getItem(position); 
				if(!isMulChoice){
					Intent intent = new Intent(LocalCollectionListActivity.this,
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
		collectionList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adpterView, View view,
					int position, long id) {
				isMulChoice = true;
				showContextMenu = true ; 
				invalidateOptionsMenu();
				fabMenu.setVisibility(View.GONE);
				collectionAdapter = new LocalCollectionAdapter(context,mData,isMulChoice);
				collectionList.setAdapter(collectionAdapter);
				return true;
			}
			
		});
		
		fabCreateNote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocalCollectionListActivity.this,
						CollectionEditorActivity.class);
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID, pid);
				startActivity(intent);
				fabMenu.collapse();
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		fabCreateFolder.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				Toast.makeText(LocalCollectionListActivity.this, "开发中...", Toast.LENGTH_SHORT).show();
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initData();
		showView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mData = collectionVo.queryWithcons("pid = ?", new String[]{String.valueOf(pid)});
		collectionAdapter.updateView(mData);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		collectionVo.closeDB();
		packVo.closeDB();
		imageVo.closeDB();
		super.onDestroy();
	}
	
	private void initData() {
		collectionVo = new CollectionVo(this);
		packVo = new CollectPackVo(this);
		imageVo = new ImageLibVo(this);
		pid = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
		mData = collectionVo.queryWithcons("pid = ?", new String[]{String.valueOf(pid)}); 
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_list_context_menu, menu);
		MenuItem moveMenu = menu.findItem(R.id.context_menu_move);
		MenuItem delMenu = menu.findItem(R.id.context_menu_delete);
		if(showContextMenu){
			moveMenu.setVisible(true);
			delMenu.setVisible(true);
		}else{
			moveMenu.setVisible(false);
			delMenu.setVisible(false);
		}
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
	            case R.id.context_menu_delete:
	            	if(selItems.size()<=0){
	            		Toast.makeText(this, getString(R.string.choose_one), Toast.LENGTH_SHORT).show();
	            		return false; 
	            	}
	            	new  AlertDialog.Builder(this)  
	            	.setTitle(getString(R.string.action_delete))    
	            	.setMessage("确定要删除"+selItems.size()+"条收集?" )  
	            	.setPositiveButton(getString(R.string.btn_confirm) , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {	
		            		RestoreToolBar();
			            	//添加提示框
			            	if(selItems != null){         					            		
			            	//	String [] cids= new String[selItems.size()];
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
			            		
			            		//delete local 
			            		collectionVo.delete(cids);
			            		imageVo.deleteBycids(cids);		
			            		
			            		String count = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT);
			            		if(Integer.parseInt(count) >= delSize){
			            			packVo.update("collect_count = collect_count - " + delSize, 
			            					" pack_id = ? ", new String[]{String.valueOf(pid)});
			            		}else{ //not likely 
			            			return ;
			            		}
			            		showView();
			            		
			            		if(selItems.size()>0)
			            			requestDel(selItems);
			            		}
						}						

					})  
	            	.setNegativeButton(getString(R.string.btn_cancel) , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {}						
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
			collectionAdapter = new LocalCollectionAdapter(this, mData,isMulChoice);
			collectionList.setAdapter(collectionAdapter);
		}else{
			collectionAdapter.updateView(mData);
		}
	}
	private void RestoreToolBar()
	{
		isMulChoice = false ; 
		showContextMenu = false; 
		invalidateOptionsMenu();
		fabMenu.setVisibility(View.VISIBLE);
		collectionAdapter = new LocalCollectionAdapter(this,mData,isMulChoice);
		collectionList.setAdapter(collectionAdapter);
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
	}	
	
	private void requestDel(final List<CollectionBean> items)
	{
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
	            		collectionVo.delete(cids);
	            		imageVo.deleteBycids(cids);
						packVo.update("collect_count = collect_count - " + items.size(), 
            					" pack_id = ? ", new String[]{String.valueOf(pid)});
						showView();
					}else{
						Toast.makeText(LocalCollectionListActivity.this, 
								getString(R.string.delete_fail),Toast.LENGTH_SHORT).show();
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
						VolleyErrorHelper.getMessage(error,getApplicationContext()), 
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
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
}