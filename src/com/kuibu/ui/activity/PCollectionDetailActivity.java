package com.kuibu.ui.activity;


import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.PCollectionDetailPresenterImpl;
import com.kuibu.module.presenter.interfaces.PCollectionDetailPresenter;
import com.kuibu.ui.view.interfaces.PCollectionDetailView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PCollectionDetailActivity extends BaseActivity
	implements PCollectionDetailView{	
	
	private PhotoView imageIv; 
	private TextView  titleTv ; 
	private TextView  descTv ; 
	private FButton likeBtn , commentBtn ;
	private MultiStateView mMultiStateView;	 
    private MenuItem mFavActionItem ,mReportItem;
    private Animation imgAnim ;
    private LinearLayout layoutTools ; 
    private PCollectionDetailPresenter mPresenter ; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_image_detail_activity);
		setTitle(null);		
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadContent();
			}   	
        });
		imgAnim = AnimationUtils.loadAnimation(PCollectionDetailActivity.this, 
				R.anim.anim_slide_in_up);
		
		layoutTools = (LinearLayout)findViewById(R.id.layout_tools);		
		imageIv = (PhotoView)findViewById(R.id.image_iv);
		imageIv.setAdjustViewBounds(true);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageIv.setMaxHeight(dm.heightPixels);
		imageIv.setMaxWidth(dm.widthPixels);
		
		titleTv = (TextView)findViewById(R.id.title_tv);
		descTv  = (TextView)findViewById(R.id.desc_tv);
		
		likeBtn = (FButton) findViewById(R.id.like_bt);
		likeBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {				
				if(Session.getSession().isLogin()){
					mPresenter.doVote(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION, 
							mPresenter.isVoted());
				}else{
					Toast.makeText(PCollectionDetailActivity.this, getString(R.string.need_login), Toast.LENGTH_SHORT).show();
				}
			}
		});
		commentBtn = (FButton)findViewById(R.id.comment_bt);
		commentBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PCollectionDetailActivity.this,CommentActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, mPresenter.getCollection().getId());
				intent.putExtra("create_by", mPresenter.getCollection().getCreateBy());
				startActivityForResult(intent, StaticValue.RequestCode.COMMENT_OVER);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
			
		});
		mPresenter = new PCollectionDetailPresenterImpl(this);
		mPresenter.getCollection().setId(getIntent().getStringExtra(
				StaticValue.EDITOR_VALUE.COLLECTION_ID));		
		mPresenter.loadContent();
        if(Session.getSession().isLogin()){
        	mPresenter.loadActions();
        }
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);				
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.content_detail, menu);
		mFavActionItem = menu.findItem(R.id.menu_item_fav_action_bar);
		mReportItem = menu.findItem(R.id.menu_item_report_action_bar);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:				
				this.onBackPressed();
				break;
			case R.id.menu_item_fav_action_bar:
				if(Session.getSession().isLogin()){
					Intent intent = new Intent(PCollectionDetailActivity.this,CollectFavoriteBoxActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, mPresenter.getCollection().getId());
					intent.putExtra("type",mPresenter.getCollection().getType());
					intent.putExtra(StaticValue.COLLECTION.IS_COLLECTED, mPresenter.isInfavorite());
					startActivityForResult(intent,StaticValue.RequestCode.FAVORITE_BOX_REQCODE);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else{
					Toast.makeText(PCollectionDetailActivity.this, getString(R.string.need_login), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.menu_item_report_action_bar:
				if(!mPresenter.isReport()){
					mPresenter.doReport();
				}else{
					Toast.makeText(PCollectionDetailActivity.this, getString(R.string.have_reported),
							Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode){
			case StaticValue.RequestCode.FAVORITE_BOX_REQCODE:
				if(data!=null){
					mPresenter.setInFavorite(data.getBooleanExtra("isCollected", false));
					if (mPresenter.isInfavorite()) {
						Toast.makeText(this, R.string.fav_add_success, Toast.LENGTH_SHORT).show();
						mFavActionItem.setIcon(R.drawable.ab_fav_active);
						mFavActionItem.setTitle(R.string.actionbar_item_fav_cancel);
							
					} else {
						Toast.makeText(this, R.string.fav_cancel_success, Toast.LENGTH_SHORT).show();
						mFavActionItem.setIcon(R.drawable.ab_fav_normal);
						mFavActionItem.setTitle(R.string.actionbar_item_fav_add);
										
					}				
				}
			break;
			case StaticValue.RequestCode.COMMENT_OVER:
				if(data!=null){
					mPresenter.setCommonCount(data.getIntExtra("comment_count", 0));
					if(mPresenter.getCommentCount() > 0){
						StringBuilder buff = new StringBuilder(getString(R.string.comment_text))
			        	.append(" ").append(DataUtils.formatNumber(mPresenter.getCommentCount()));
			        	commentBtn.setText(buff.toString());		        	
					}
				}				
				break;
			case StaticValue.RequestCode.REPORT_COMPLETE:
				if(data!=null){
					mPresenter.setReport(data.getBooleanExtra("is_report", false));
					if(mPresenter.isReport()){
						mReportItem.setIcon(R.drawable.ic_action_report_disabled);
					}					
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	public void setToolsVisible(boolean visible) {
		// TODO Auto-generated method stub
		if(!visible){
			layoutTools.setVisibility(View.GONE);
    		mFavActionItem.setVisible(false);
    		mReportItem.setVisible(false);
		}else{
			layoutTools.setVisibility(View.VISIBLE);
    		mFavActionItem.setVisible(true);
    		mReportItem.setVisible(true);
    		layoutTools.startAnimation(imgAnim);	
		}
	}

	@Override
	public void setImage(String url) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(url, imageIv);
		imageIv.startAnimation(imgAnim);
		imageIv.setVisibility(View.VISIBLE);
	}

	@Override
	public void setCommentCount(String count) {
		// TODO Auto-generated method stub
		commentBtn.setText(count);
	}

	@Override
	public void setVoteCount(String count) {
		// TODO Auto-generated method stub
    	likeBtn.setText(count);
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);	
	}

	@Override
	public void setVoteBtnDrawable(int resId) {
		// TODO Auto-generated method stub
		Drawable drawable= ContextCompat.getDrawable(KuibuApplication.getContext(),resId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		likeBtn.setCompoundDrawables(drawable, null, null, null);
	}

	@Override
	public void setFavMenuDrawable(int resId) {
		// TODO Auto-generated method stub
		mFavActionItem.setIcon(resId);
	}

	@Override
	public void setReportMenuRrawable(int resId) {
		// TODO Auto-generated method stub
		mReportItem.setIcon(resId);
	}

	@Override
	public void setCollectionTitle(String title) {
		// TODO Auto-generated method stub
		titleTv.setText(title);
	}

	@Override
	public void setCollectionDesc(String desc) {
		// TODO Auto-generated method stub
		
		Animation anim = AnimationUtils.loadAnimation(this, 
					R.anim.anim_control_slide_in_left);		
		descTv.setText(desc);
		descTv.startAnimation(anim);
	}

	@Override
	public void setCollectionDescVisible(int visibility) {
		// TODO Auto-generated method stub
		descTv.setVisibility(visibility);
	}	
}
