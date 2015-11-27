package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.ButtonFloat;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.db.CollectPackVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.ImageInfo;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.ImageGridAdapter;

public class LocalCollectionPListActivity extends BaseActivity implements IXListViewListener{
	
	private static final int CREATE_IMAGE_REQ_CODE  = 0x1000 ;
	private static final int PREVIEW_REQ_CODE = 0x2000 ; 
	
	private static final int OPER_TYPE_REFRESH = 1 ; 
	private static final int OPER_TYPE_LOADMORE = 2; 
	private static final int PAGE_SIZE = 6 ; 
    private XListView mAdapterView = null;
	private ButtonFloat fbutton;
	private ImageGridAdapter mViewAdapter = null;
	private String pid;
	private CollectionVo collectionVo ; 
	private CollectPackVo packVo = null ; 
	private ImageLibVo  imageVo = null ; 
	private ContentTask mTask = new ContentTask(this,OPER_TYPE_LOADMORE); 
	private boolean bMultiChoice = false ;
	private LinkedList<CollectionBean> mData = new LinkedList<CollectionBean>();
	private List<CollectionBean> selItems = null; 
	private int curPosition  ; 
	private int packCount ; 
	private ProgressDialog progressDlg ; 

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(
				StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		setContentView(R.layout.local_collection_grid_activity);
		String count = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT);
		packCount = Integer.parseInt(count);
		collectionVo = new CollectionVo(this);
		packVo = new CollectPackVo(this);
		imageVo = new ImageLibVo(this);		
		fbutton = (ButtonFloat) findViewById(R.id.buttonFloat);
		mAdapterView = (XListView) findViewById(R.id.list);
		mAdapterView.setPullLoadEnable(true);
        mAdapterView.setXListViewListener(this);
        
		fbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocalCollectionPListActivity.this,
						MultiImageSelectorActivity.class);
				intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
	            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, Constants.Config.MAX_IMAGE_SELECT);
	            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);         
				startActivityForResult(intent, StaticValue.PICK_PHOTO_CDOE);
			}
		});
		mAdapterView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				CollectionBean item = (CollectionBean)parent.getAdapter().getItem(position);
				if(!bMultiChoice){
					curPosition = position ; 
					Intent intent = new Intent(LocalCollectionPListActivity.this,ImagePreviewActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, item);
					intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
							StaticValue.EDITOR_VALUE.LIST_TO_PREVIEW);
					startActivityForResult(intent, PREVIEW_REQ_CODE);
					overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out);
				}else{
					ViewHolder  holderView = (ViewHolder)view.getTag();
					if(selItems == null){
						selItems = new LinkedList<CollectionBean>() ; 
					}
					if(item.isCheck){
						item.isCheck = false ;
						((ImageView)holderView.getView(R.id.checkmark)).setImageResource(R.drawable.btn_unselected);
						selItems.remove(item);
					}else{
						item.isCheck = true ; 											
						((ImageView)holderView.getView(R.id.checkmark)).setImageResource(R.drawable.btn_selected);
						selItems.add(item);
					}

					StringBuilder buffer =  new StringBuilder("已选").append(selItems.size()).append("项");
					setTitle(buffer.toString());
				}
								
			}
		});
		
		pid = getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);		
		mViewAdapter = new ImageGridAdapter(this,mData,R.layout.item_grid_image,bMultiChoice);
		mAdapterView.setAdapter(mViewAdapter);
		mAdapterView.setPullRefreshEnable(false);
		loadData(OPER_TYPE_LOADMORE);
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_list_context_menu, menu);
		MenuItem moveMenu = menu.findItem(R.id.context_menu_move);
		MenuItem delMenu = menu.findItem(R.id.context_menu_delete);		
		MenuItem manageMenu = menu.findItem(R.id.context_menu_manage);
		if(bMultiChoice){
			moveMenu.setVisible(true);
			delMenu.setVisible(true);
			manageMenu.setVisible(false);
		}else{
			manageMenu.setVisible(true);
			moveMenu.setVisible(false);
			delMenu.setVisible(false);
		}
		moveMenu.setVisible(false); // no support now . 
        return true;
    }
	
	private void RestoreToolBar()
	{
		bMultiChoice = false ;  
		invalidateOptionsMenu();
		if(selItems!=null)
			selItems.clear();
		fbutton.setVisibility(View.VISIBLE);
		mViewAdapter = new ImageGridAdapter(this,mData, R.layout.item_grid_image,bMultiChoice);
		mAdapterView.setAdapter(mViewAdapter);
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
			case StaticValue.PICK_PHOTO_CDOE:
				if(result!=null){
					List<String> path = result.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					if(path != null && path.size()>0){
						Intent intent = new Intent(LocalCollectionPListActivity.this,
								CollectionImageActivity.class);
						intent.putExtra("path", path.get(0));
						intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID, pid);
						startActivityForResult(intent, CREATE_IMAGE_REQ_CODE);
					}					
				}				
				break;
			case CREATE_IMAGE_REQ_CODE:
				if(result !=null ){
					CollectionBean newOne = (CollectionBean)result.getSerializableExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
					if(newOne != null){
						ImageInfo info = BitmapHelper.getImageInfo(newOne.cover);
						newOne.height = info.height ; 
						newOne.width = info.width ; 
						newOne.cover  = Constants.URI_PREFIX + newOne.cover ;
						mData.addFirst(newOne);
						mViewAdapter.refreshView(mData);
					}			
				}
				break;
			case PREVIEW_REQ_CODE:
				if(result != null){
					CollectionBean bean = (CollectionBean)result.getSerializableExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
					if(bean != null){
						updateItem(curPosition,bean);
					}
				}
		}
	}


	private void updateItem(int position ,CollectionBean bean)
	{		
		int first = mAdapterView.getFirstVisiblePosition();
		int last = mAdapterView.getLastVisiblePosition() ; 
		if (position >= first && position <= last) {
			CollectionBean cur = (CollectionBean)mViewAdapter.getItem(position-1);
            View view = mAdapterView.getChildAt(position - first);  
            ViewHolder holder = (ViewHolder) view.getTag();
            if(!cur.title.equals(bean.title)){
            	holder.setTvText(R.id.item_title,bean.title);
            	cur.setTitle(bean.title);
            }
            if(!TextUtils.isEmpty(bean.content) && !bean.content.equals(cur.content)){
            	holder.setTvText(R.id.item_desc,bean.content);
            	cur.setContent(bean.content);
            }
            if(bean.isPublish == 1){
            	holder.setVisibility(R.id.published_icon,View.GONE);
            	cur.setIsPublish(1);
            }
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				overridePendingTransition(R.anim.anim_slide_out_right,
						R.anim.anim_slide_in_right);
				return true;
			case R.id.context_menu_manage:
				bMultiChoice = true; 
				invalidateOptionsMenu();
				fbutton.setVisibility(View.GONE);				
				mViewAdapter = new ImageGridAdapter(LocalCollectionPListActivity.this,
						mData,R.layout.item_grid_image,bMultiChoice);
				mAdapterView.setAdapter(mViewAdapter);
				return true;
				
			case R.id.context_menu_delete :
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
		            		
		            		if(cids.size()>0){
		            			collectionVo.delete(cids);
		            			imageVo.deleteBycids(cids);		
		            			mViewAdapter.refreshView(mData);
		            		}
		            		
		            		if(packCount >= delSize && delSize>0){
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
		if(bMultiChoice){
			RestoreToolBar();
		}else{
			super.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_right);
		}
		
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadData(OPER_TYPE_LOADMORE); 
	}
	
	private void loadData(int type)
	{
		if(mTask.getStatus() != Status.RUNNING){
			mTask = new ContentTask(this,type);
			mTask.execute("");
		}
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
	            		collectionVo.delete(cids);
	            		imageVo.deleteBycids(cids);
						packVo.update("collect_count = collect_count - " + items.size(), 
            					" pack_id = ? ", new String[]{String.valueOf(pid)});
						mViewAdapter.refreshView(mData);
					}else{
						Toast.makeText(LocalCollectionPListActivity.this, 
								getString(R.string.delete_fail),Toast.LENGTH_SHORT).show();
					}
					progressDlg.dismiss();
					RestoreToolBar();
            		
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
						getString(R.string.net_error),Toast.LENGTH_SHORT).show();
				progressDlg.dismiss();
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
	
	private class ContentTask extends AsyncTask<String,Integer,List<CollectionBean>>{
		
		private Context context ; 
		private int oType ; 
		
		public ContentTask(Context context , int type)
		{
			this.context = context ; 
			this.oType = type ; 
		}
		
		@Override
		protected List<CollectionBean> doInBackground(String... arg0) {
			List<CollectionBean> result = new ArrayList<CollectionBean>();
			if(oType == OPER_TYPE_REFRESH){
				
			}else if(oType == OPER_TYPE_LOADMORE){
				//分页取数据
				result = collectionVo.queryWithByPage(" _id < ? and pid = ? ",
						new String[]{String.valueOf(mViewAdapter.getMinId()),pid},PAGE_SIZE,0);
				
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
						mAdapterView.setPullLoadEnable(false);
					}
					mData.addAll(result);
					mViewAdapter.refreshView(mData);
	                mAdapterView.stopLoadMore();
				}	
			}		
		}
	}	
}
