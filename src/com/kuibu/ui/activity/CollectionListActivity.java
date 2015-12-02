package com.kuibu.ui.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.MateListViewItemAdapter;
import com.kuibu.module.presenter.CollectionListPresenterImpl;
import com.kuibu.module.presenter.interfaces.CollectionListPresenter;
import com.kuibu.ui.view.interfaces.CollectionListView;

public class CollectionListActivity extends BaseActivity implements CollectionListView{	
	
	private PullToRefreshListView listView ; 
	private MateListViewItemAdapter mAdapter ; 	
	private MultiStateView mMultiStateView;
	private CollectionListPresenter mPresenter ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pullrefresh_listview);
		mMultiStateView = (MultiStateView)findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadCollectionList();
			}   	
        });
		listView = (PullToRefreshListView)findViewById(R.id.pagination_lv);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setPullToRefreshOverScrollEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadCollectionList();
			}			
		});
		mAdapter = new MateListViewItemAdapter(this, null,false);
		listView.setAdapter(mAdapter);		
		mPresenter = new CollectionListPresenterImpl(this);
		mPresenter.loadCollectionList();
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
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
	public void refreshList(List<MateListItem> datas) {
		// TODO Auto-generated method stub
		mAdapter.updateView(datas);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		listView.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}

	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);
	}	
}
