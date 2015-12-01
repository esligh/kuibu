package com.kuibu.ui.activity;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.UserListAdapter;
import com.kuibu.module.presenter.TopicInfoPresenterImpl;
import com.kuibu.module.presenter.interfaces.TopicInfoPresenter;
import com.kuibu.ui.view.interfaces.TopicInfoView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopicInfoActivity extends BaseActivity implements TopicInfoView{
	
	private ImageView topicPicIv;
	private TextView topicNameTv, topicDescTv;
	private TextView followCountTv;
	private FButton focusBtn;
	private ListView bestAuthorList;	 
	private UserListAdapter authorAdapter ; 	
	private TopicInfoPresenter mPresenter ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_info);
		topicPicIv = (ImageView) findViewById(R.id.topic_pic_iv);
		topicNameTv = (TextView) findViewById(R.id.topic_name_tv);
		topicDescTv = (TextView) findViewById(R.id.topic_desc_tv);
		topicDescTv.setText(getIntent().getStringExtra(StaticValue.TOPICINFO.TOPIC_EXTRA));
		followCountTv = (TextView) findViewById(R.id.follow_count_tv);
		focusBtn = (FButton) findViewById(R.id.focus_collectpack_bt);
		bestAuthorList = (ListView) findViewById(R.id.best_author_list);		
		bestAuthorList.setOnTouchListener(new OnTouchListener() {				
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_MOVE:
						return true; 
					default : break; 
				}
				return false;
			}
		});
		
		bestAuthorList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String,Object> uInfo = (Map<String,Object>)viewAdapter.getAdapter().getItem(position);
				Intent intent = new Intent(TopicInfoActivity.this,UserInfoActivity.class);
				intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);	
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
				intent.putExtra(StaticValue.USERINFO.USER_SEX, (String)uInfo.get("sex"));
				intent.putExtra(StaticValue.USERINFO.USER_NAME, (String)uInfo.get("name"));
				intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE, (String)uInfo.get("signature"));
				intent.putExtra(StaticValue.USERINFO.USER_PHOTO, (String)uInfo.get("photo"));
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
		});
		focusBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(Session.getSession().isLogin()){
					mPresenter.follow();
				}else{
					Toast.makeText(TopicInfoActivity.this, getString(R.string.need_login), 
							Toast.LENGTH_SHORT).show();
				}		
			}
		});
		authorAdapter = new UserListAdapter(this,null); 
		bestAuthorList.setAdapter(authorAdapter);
		mPresenter = new TopicInfoPresenterImpl(this);
		mPresenter.loadTopicInfo();
		mPresenter.loadUserList();
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
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
	
	@Override
	public void refreshList(List<Map<String, Object>> data) {
		// TODO Auto-generated method stub
		authorAdapter.refreshView(data);			
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
	public void setTopicName(String name) {
		// TODO Auto-generated method stub
		topicNameTv.setText(name);
	}


	@Override
	public void setTopicDesc(String desc) {
		// TODO Auto-generated method stub
		topicDescTv.setText(desc);
	}


	@Override
	public void setTopicPic(String url) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(url, topicPicIv);
	}


	@Override
	public void setFollowCount(String count) {
		// TODO Auto-generated method stub
		followCountTv.setText(count);
	}


	@Override
	public void setFollowBtnColor(int color) {
		// TODO Auto-generated method stub
		focusBtn.setButtonColor(color);
	}


	@Override
	public void setFollowBtnText(String text) {
		// TODO Auto-generated method stub
		focusBtn.setText(text);
	}


	@Override
	public String getFollowCount() {
		// TODO Auto-generated method stub
		return followCountTv.getText().toString().trim();
	}


	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);
	}
}
