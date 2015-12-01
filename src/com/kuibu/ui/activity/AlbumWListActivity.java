package com.kuibu.ui.activity;

import java.util.List;

import android.content.Context;
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

import com.gc.materialdesign.views.ButtonFloat;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.CollectionWAdapter;
import com.kuibu.module.adapter.CollectionWAdapter.HolderView;
import com.kuibu.module.presenter.AlbumWListPresenterImpl;
import com.kuibu.module.presenter.interfaces.AlbumWListPresenter;
import com.kuibu.ui.view.interfaces.AlbumWListView;

public class AlbumWListActivity extends BaseActivity implements AlbumWListView{
	
	private ButtonFloat fbutton;
	private ListView collectionList;
	private CollectionWAdapter collectionAdapter;
	private AlbumWListPresenter mPresenter ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME));
		setContentView(R.layout.local_collection_list_activity);	
		fbutton = (ButtonFloat) findViewById(R.id.buttonFloat);
		collectionList = (ListView) findViewById(R.id.colleciton_lv);
		collectionAdapter = new CollectionWAdapter(this, null,false);
		collectionList.setAdapter(collectionAdapter);
		
		collectionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				CollectionBean item = (CollectionBean) adapterView
						.getAdapter().getItem(position); 
				if(!mPresenter.isMultiChoice()){
					Intent intent = new Intent(AlbumWListActivity.this,
							PreviewWCollectionActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID,
							String.valueOf(mPresenter.getPackId()));
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID, item.get_id()+"");
					intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
							StaticValue.EDITOR_VALUE.LIST_TO_PREVIEW);
					startActivity(intent);					
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else{					
						HolderView  holderView = (HolderView)view.getTag();
						if(holderView ==null)
							return ;
						if(holderView.check.isChecked()){
							holderView.check.setChecked(false);
							mPresenter.unSelectOne(item);
						}else{
							holderView.check.setChecked(true);
							mPresenter.selectOne(item);
						}
						StringBuilder buffer =  new StringBuilder("已选").
								append(mPresenter.getSelectItems().size()).append("项");
						setTitle(buffer.toString());
					}
				}
		});
		
		fbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlbumWListActivity.this,
						WCollectionCreateActivity.class);
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID, mPresenter.getPackId());
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		mPresenter = new AlbumWListPresenterImpl(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		mPresenter.queryCollection();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mPresenter.closeDbConn();
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_list_context_menu, menu);
		MenuItem moveMenu = menu.findItem(R.id.context_menu_move);
		MenuItem delMenu = menu.findItem(R.id.context_menu_delete);
		MenuItem manageItem = menu.findItem(R.id.context_menu_manage);
		if(mPresenter.isMenuVisible()){
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
	            	mPresenter.switchToolBar(true);
	            	return true ; 
	            case R.id.context_menu_delete:
	            	mPresenter.delCollections();	            		            	
	            	return true ; 
	            default:
	                return super.onOptionsItemSelected(item);
	            	
	        }
	 }       

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mPresenter.isMenuVisible()){
			mPresenter.switchToolBar(false);
		}else{
			super.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		}		
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
		collectionAdapter.updateView(data);
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
	public void resetAdapter(List<CollectionBean> data ,boolean isMulChoice) {
		// TODO Auto-generated method stub
		collectionAdapter = new CollectionWAdapter(this,data,isMulChoice);
		collectionList.setAdapter(collectionAdapter);
	}

	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);		
	}
	
	
}