package com.kuibu.ui.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TopicListAdapter;
import com.kuibu.module.adapter.UserListAdapter;
import com.kuibu.module.presenter.SearchViewPresenterImpl;
import com.kuibu.module.presenter.interfaces.SearchViewPresenter;
import com.kuibu.ui.view.interfaces.SearchView;

public class SearchViewActivity extends AppCompatActivity implements SearchView{
	
	private PullToRefreshListView thingList ;	
	private TextView collectionTv,collectorTv ,topicTv; 
	private String  target ;
	private TopicListAdapter topicAdapter;	
	private UserListAdapter userAdapter;
	private CommonAdapter<Map<String,String>> contentAdapter ; 	 
	private boolean isDarkTheme ; 
	private EditText searchView ; 
	private SearchViewPresenter mPresenter ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		isDarkTheme= PreferencesUtils.getBooleanByDefault(this,StaticValue.PrefKey.DARK_THEME_KEY, false);
		if (isDarkTheme) {
			setTheme(R.style.AppTheme_Dark);			
		}else{
			setTheme(R.style.AppTheme_Light);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		searchView = (EditText)findViewById(R.id.search_content);
		thingList = (PullToRefreshListView)findViewById(R.id.search_view_list);
		thingList.setMode(Mode.PULL_FROM_END);
		thingList.setPullToRefreshOverScrollEnabled(false);
		thingList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				LoadMore();
			}
		});
		collectionTv = (TextView)findViewById(R.id.collection_tv);
		collectorTv = (TextView)findViewById(R.id.collector_tv);
		topicTv = (TextView)findViewById(R.id.topic_tv);
		collectionTv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "CONTENT";
				switchBackground(target);
				mPresenter.clearContent();
				if(contentAdapter==null)
					contentAdapter = new CommonAdapter<Map<String,String>>(SearchViewActivity.this, null,R.layout.search_content_list_item){
						@Override
						public void convert(ViewHolder holder,
								Map<String, String> item) {
							// TODO Auto-generated method stub
							content_convert(holder,item);
						}					
				};				
				thingList.setAdapter(contentAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					mPresenter.requestContent(query);
				}				
			}
		});
		collectorTv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "COLLECTOR";
				switchBackground(target);
				mPresenter.clearUsers();
				if(userAdapter==null)
					userAdapter = new UserListAdapter(SearchViewActivity.this, null);				
				thingList.setAdapter(userAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					mPresenter.requestUsers(query);
				}				
			}
		});
		topicTv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				target = "TOPIC";
				switchBackground(target);				
				mPresenter.clearTopics();
				if(topicAdapter==null)
					topicAdapter = new TopicListAdapter(SearchViewActivity.this, null);				
				thingList.setAdapter(topicAdapter);
				String query = searchView.getText().toString();
				if(!TextUtils.isEmpty(query)){
					mPresenter.requestTopics(query);
				}
			}
		});
		thingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub				
				Object obj = viewAdapter.getAdapter().getItem(position);
				if(obj == null)  return ; 
				if(target.equals("TOPIC")){
					TopicItemBean item = (TopicItemBean)obj;
					Intent intent = new Intent(SearchViewActivity.this,
							TopicInfoActivity.class);
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,
							item.getId());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME, 
							item.getTopic());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC, 
							item.getTopicPicUrl());
					intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA, 
							item.getIntroduce());
					startActivity(intent);		
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else if(target.equals("COLLECTOR")){
					@SuppressWarnings("unchecked")
					Map<String,Object> uInfo = (Map<String,Object>)obj;
					Intent intent = new Intent(SearchViewActivity.this,UserInfoActivity.class);
					intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);	
					intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
					intent.putExtra(StaticValue.USERINFO.USER_SEX, (String)uInfo.get("sex"));
					intent.putExtra(StaticValue.USERINFO.USER_NAME, (String)uInfo.get("name"));
					intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE, (String)uInfo.get("signature"));
					intent.putExtra(StaticValue.USERINFO.USER_PHOTO, (String)uInfo.get("photo"));
					startActivity(intent);					
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else if(target.equals("CONTENT")){
					
					@SuppressWarnings("unchecked")
					Map<String,String> item =(Map<String,String>) obj ;
					String tag = item.get("tag");
					if(tag.equals("collectpack")){
						Intent intent = new Intent(SearchViewActivity.this,
								AlbumInfoActivity.class);
						intent.putExtra("pack_id", item.get("item_id"));
						intent.putExtra("create_by", item.get("create_by"));
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
					}else if(tag.equals("collection")){
						Intent intent = new Intent(SearchViewActivity.this,CollectionDetailActivity.class);
						intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,item.get("item_id"));
						intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN,
								item.get("cisn"));
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
					}
				}
			}			
		});
		searchView.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				boolean flag = true; 
				if(TextUtils.isEmpty(s.toString())){
					flag = false;
				}								
				if(target.equals("TOPIC")){
					mPresenter.clearTopics();
					if(flag)
						mPresenter.requestTopics(s.toString());
				}else if(target.equals("COLLECTOR")){
					mPresenter.clearUsers();;
					if(flag)
						mPresenter.requestUsers(s.toString());
				}else if(target.equals("CONTENT")){
					mPresenter.clearContent();
					if(flag)
						mPresenter.requestContent(s.toString());
				//	else
				//		showContentView();
				}
			}
		});
		target = "CONTENT";
		switchBackground(target);
		mPresenter = new SearchViewPresenterImpl(this);
	}
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPresenter.cancelRequest();
	}

	@SuppressWarnings("deprecation")
	private void switchBackground(String target){
		if(TextUtils.isEmpty(target)) 
			return ; 
		if(isDarkTheme){
			if(target.equals("TOPIC")){
				topicTv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collectionTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				collectorTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));				
			}else if(target.equals("COLLECTOR")){
				collectorTv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collectionTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				topicTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));										
			}else if(target.equals("CONTENT")){
				collectionTv.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				collectorTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
				topicTv.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));			
			}
		}else{
			if(target.equals("TOPIC")){
				topicTv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collectionTv.setBackgroundColor(getResources().getColor(R.color.white));
				collectorTv.setBackgroundColor(getResources().getColor(R.color.white));
				
			}else if(target.equals("COLLECTOR")){
				collectorTv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collectionTv.setBackgroundColor(getResources().getColor(R.color.white));
				topicTv.setBackgroundColor(getResources().getColor(R.color.white));										
			}else if(target.equals("CONTENT")){
				collectionTv.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				collectorTv.setBackgroundColor(getResources().getColor(R.color.white));
				topicTv.setBackgroundColor(getResources().getColor(R.color.white));			
			}
		}		
	}

	private void content_convert(ViewHolder holder,
			Map<String, String> item)
	{
		if(item.get("tag").equals("collectpack")){
			holder.setTvText(R.id.search_content_item_title,item.get("item_title"));
			holder.setTvText(R.id.search_content_item_sub1,item.get("item_count_1")+"条收集");
			holder.setTvText(R.id.search_content_item_sub2,item.get("item_count_2")+"人关注");			
		}else if(item.get("tag").equals("collection")){
			holder.setTvText(R.id.search_content_item_title,item.get("item_title"));
			holder.setTvText(R.id.search_content_item_sub1,item.get("item_count_1")+" 赞");
			holder.setTvText(R.id.search_content_item_sub2,item.get("item_count_2")+" 评论");
		}
	}
				
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
			case android.R.id.home:
				this.onBackPressed();
				finish();
				overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);				
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);	
	}

	private void LoadMore() {
		String query  = searchView.getText().toString();
		if(TextUtils.isEmpty(query))
			return  ;
		if(target.equals("TOPIC")){
			mPresenter.requestTopics(query);
		}else if(target.equals("COLLECTOR")){
			mPresenter.requestUsers(query);
		}else if(target.equals("CONTENT")){
			mPresenter.requestContent(query);
		}
	}
	
	@Override
	public void refreshTopicList(List<TopicItemBean> data) {
		// TODO Auto-generated method stub
		if(topicAdapter==null){
			topicAdapter = new TopicListAdapter(this,data);
			thingList.setAdapter(topicAdapter);
		}else{
			topicAdapter.updateView(data);
		}		
	}
	
	@Override
	public void refreshUserList(List<Map<String, Object>> data) {
		if(userAdapter==null){
			userAdapter = new UserListAdapter(this, data);
			thingList.setAdapter(userAdapter);
		}else{
			userAdapter.refreshView(data);
		}		
	}
	
	@Override
	public void refreshContentList(List<Map<String, String>> data) {
		if(contentAdapter==null){
			contentAdapter = new CommonAdapter<Map<String,String>>(this, data,
					R.layout.search_content_list_item){

						@Override
						public void convert(ViewHolder holder,
								Map<String, String> item) {
							// TODO Auto-generated method stub
							content_convert(holder,item);
						}
				
			};
			thingList.setAdapter(contentAdapter);
		}else{
			contentAdapter.refreshView(data);
		}		
	}
	
	@Override
	public void loadComplete() {
		// TODO Auto-generated method stub
		thingList.onRefreshComplete();
	}	
}
