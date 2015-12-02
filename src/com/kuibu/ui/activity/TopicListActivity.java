package com.kuibu.ui.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TopicListAdapter;
import com.kuibu.module.presenter.TopicListPresenterImpl;
import com.kuibu.module.presenter.interfaces.TopicListPresenter;
import com.kuibu.ui.view.interfaces.TopicListView;

public class TopicListActivity extends BaseActivity implements TopicListView{
	
	private ListView topicList ; 
	private TopicListAdapter topicAdapter ; 
	private TopicListPresenter mPresenter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_listview);
		topicList = (ListView)findViewById(R.id.simple_listview_lv);
		topicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TopicListActivity.this,TopicInfoActivity.class);
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,
						mPresenter.getListData().get(position).getId());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME, 
						mPresenter.getListData().get(position).getTopic());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA,
						mPresenter.getListData().get(position).getIntroduce());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC,
						mPresenter.getListData().get(position).getTopicPicUrl());
				startActivity(intent);		
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		topicAdapter = new TopicListAdapter(this,null);
		topicList.setAdapter(topicAdapter);
		mPresenter = new TopicListPresenterImpl(this);
		mPresenter.loadTopicList();
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
	public void refreshList(List<TopicItemBean> data) {
		// TODO Auto-generated method stub
		topicAdapter.updateView(data);
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
}
