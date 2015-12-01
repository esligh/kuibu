package com.kuibu.ui.activity;

import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.gc.materialdesign.views.ButtonFloat;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.ImageInfo;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.ImageGridAdapter;
import com.kuibu.module.presenter.AlbumPListPresenterImpl;
import com.kuibu.module.presenter.interfaces.AlbumPListPresenter;
import com.kuibu.ui.view.interfaces.AlbumPListView;

public class AlbumPListActivity extends BaseActivity 
	implements IXListViewListener,AlbumPListView{	
	
	private static final int CREATE_IMAGE_REQ_CODE  = 0x1000 ;
	private static final int PREVIEW_REQ_CODE = 0x2000 ;
	
	private XListView mAdapterView = null;
	private ButtonFloat fbutton;
	private ImageGridAdapter mAdapter = null;
	private AlbumPListPresenter mPresenter ; 
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(
				StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		setContentView(R.layout.local_collection_grid_activity);
		
		fbutton = (ButtonFloat) findViewById(R.id.buttonFloat);
		mAdapterView = (XListView) findViewById(R.id.list);
		mAdapterView.setPullLoadEnable(true);
        mAdapterView.setXListViewListener(this);
        
		fbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlbumPListActivity.this,
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
				if(!mPresenter.isMultiChoice()){
					mPresenter.setCurClick(position);
					Intent intent = new Intent(AlbumPListActivity.this,PreviewPCollectionActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, item);
					intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
							StaticValue.EDITOR_VALUE.LIST_TO_PREVIEW);
					startActivityForResult(intent, PREVIEW_REQ_CODE);
					overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out);
				}else{
					ViewHolder  holderView = (ViewHolder)view.getTag();
					if(item.isCheck){
						item.isCheck = false ;
						((ImageView)holderView.getView(R.id.checkmark)).setImageResource(R.drawable.btn_unselected);
						mPresenter.unSelectOne(item);
					}else{
						item.isCheck = true ; 											
						((ImageView)holderView.getView(R.id.checkmark)).setImageResource(R.drawable.btn_selected);
						mPresenter.selectOne(item);
					}

					StringBuilder buffer =  new StringBuilder("已选").
							append(mPresenter.getSelectItems().size()).append("项");
					setTitle(buffer.toString());
				}
								
			}
		});
		mAdapter = new ImageGridAdapter(this,null,R.layout.item_grid_image,false);
		mAdapterView.setAdapter(mAdapter);
		mAdapterView.setPullRefreshEnable(false);
		mPresenter = new AlbumPListPresenterImpl(this);
		mPresenter.queryCollection(); 
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_list_context_menu, menu);
		MenuItem moveMenu = menu.findItem(R.id.context_menu_move);
		MenuItem delMenu = menu.findItem(R.id.context_menu_delete);		
		MenuItem manageMenu = menu.findItem(R.id.context_menu_manage);
		if(mPresenter.isMultiChoice()){
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
			case StaticValue.PICK_PHOTO_CDOE:
				if(result!=null){
					List<String> path = result.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					if(path != null && path.size()>0){
						Intent intent = new Intent(AlbumPListActivity.this,
								PCollectionCreateActivity.class);
						intent.putExtra("path", path.get(0));
						intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID, mPresenter.getPackId());
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
						mPresenter.addCollection(newOne);
					}			
				}
				break;
			case PREVIEW_REQ_CODE:
				if(result != null){
					CollectionBean bean = (CollectionBean)result.getSerializableExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
					if(bean != null){
						updateItem(mPresenter.getCurClick(),bean);
					}
				}
		}
	}


	private void updateItem(int position ,CollectionBean bean)
	{		
		int first = mAdapterView.getFirstVisiblePosition();
		int last = mAdapterView.getLastVisiblePosition() ; 
		if (position >= first && position <= last) {
			CollectionBean cur = (CollectionBean)mAdapter.getItem(position-1);
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
				mPresenter.switchToolBar(true);
				return true;
				
			case R.id.context_menu_delete :
				
				return true ; 
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub		
		if(mPresenter.isMultiChoice()){
			mPresenter.switchToolBar(false);
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
		mPresenter.queryCollection(); 
	}
	
	@Override
	public Intent getDataIntent() {
		// TODO Auto-generated method stub
		return getIntent();
	}


	@Override
	public Context getInstance() {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public void refreshList(List<CollectionBean> data) {
		// TODO Auto-generated method stub
		mAdapter.refreshView(data);
	}


	@Override
	public void refreshOptionMenu() {
		// TODO Auto-generated method stub
		invalidateOptionsMenu();
	}


	@Override
	public void setButtonVisible(int visibility) {
		// TODO Auto-generated method stub
		fbutton.setVisibility(visibility);
	}


	@Override
	public void resetAdapter(List<CollectionBean> data, boolean isMulChoice) {
		// TODO Auto-generated method stub
		mAdapter = new ImageGridAdapter(this,data, R.layout.item_grid_image,isMulChoice);
		mAdapterView.setAdapter(mAdapter);
	}


	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);
	}


	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
        mAdapterView.stopLoadMore();
	}


	@Override
	public ImageGridAdapter getAdapter() {
		// TODO Auto-generated method stub
		return mAdapter;
	}


	@Override
	public void setPullEnable(boolean enable) {
		// TODO Auto-generated method stub
		mAdapterView.setPullLoadEnable(enable);
	}	
}
