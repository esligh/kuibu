package com.kuibu.ui.activity;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.PreviewPCollectionPresenterImpl;
import com.kuibu.module.presenter.interfaces.PreviewPCollectionPresenter;
import com.kuibu.ui.view.interfaces.PreviewPCollectionView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PreviewPCollectionActivity extends BaseActivity 
	implements PreviewPCollectionView{
	
	public static final int EDIT_REQ_CODE = 0x2001 ; 
	private PhotoView imageIv; 
	private TextView  titleTv ; 
	private TextView  descTv ; 	
	private MenuItem pubMenu ; 
	private ImageView actionBtn ;
	private PreviewPCollectionPresenter mPresenter ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.image_preview_activity);	
		imageIv = (PhotoView)findViewById(R.id.image_iv);
		imageIv.setAdjustViewBounds(true);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageIv.setMaxHeight(dm.heightPixels);
		imageIv.setMaxWidth(dm.widthPixels);		
		titleTv = (TextView)findViewById(R.id.title_tv);
		descTv  = (TextView)findViewById(R.id.desc_tv);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_up);			
		actionBtn = (ImageView)findViewById(R.id.action_btn);
		actionBtn.startAnimation(anim);
		actionBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				mPresenter.trigger();
			}
		});
		setTitle(null);
		mPresenter = new PreviewPCollectionPresenterImpl(this);
		mPresenter.loadCollection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.preview_menu, menu);
		pubMenu = menu.findItem(R.id.action_publish);
		MenuItem edit = menu.findItem(R.id.action_edit);
		int from_who = getIntent().getIntExtra(
				StaticValue.EDITOR_VALUE.FROM_WHO, 0);
		if (from_who == StaticValue.EDITOR_VALUE.EDITOR_TO_PREVIEW) {
			edit.setVisible(false);
		} else {
			edit.setVisible(true);
		}

		if (!mPresenter.needPublish()) {
			pubMenu.setVisible(false);
		} else {
			pubMenu.setVisible(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_right);
			return true;
		case R.id.action_publish:
			mPresenter.publish();			
			return true;
		case R.id.action_edit:
			mPresenter.setChanged(false);
			Intent intent = new Intent(PreviewPCollectionActivity.this, PCollectionCreateActivity.class);
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, mPresenter.getCollection());
			startActivityForResult(intent,EDIT_REQ_CODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
			case EDIT_REQ_CODE:
				if(result != null){
					CollectionBean bean = (CollectionBean)result.getSerializableExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
					if(bean !=null){
						mPresenter.setChanged(true); 
						mPresenter.setContentTitle( bean.title);
						mPresenter.setContentDesc(bean.content);
						titleTv.setText(bean.title);
						descTv.setText(bean.content);
					}
				}
				break;
		}
	}
	@Override
	public void onBackPressed() {
		if(mPresenter.hasChanged()){
			Intent intent = new Intent();
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, mPresenter.getCollection());
			setResult(RESULT_OK, intent);
		}
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		super.onBackPressed();						
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPresenter.removeCallback();
		mPresenter.closeDbConn();
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
	public void setPubMenuVisible(boolean visible) {
		// TODO Auto-generated method stub
		pubMenu.setVisible(visible);
	}

	@Override
	public void setCollectionTitle(String title) {
		// TODO Auto-generated method stub
		titleTv.setText(title);
	}

	@Override
	public void setCollectionDesc(String desc) {
		// TODO Auto-generated method stub
		descTv.setText(desc);
		Animation anim = AnimationUtils.loadAnimation(this, 
				R.anim.anim_control_slide_in_left);		
		descTv.startAnimation(anim);
	}

	@Override
	public void setCollectionDescVisible(int visibility) {
		// TODO Auto-generated method stub
		descTv.setVisibility(visibility);
	}

	@Override
	public void setImage(String url) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(url, imageIv);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_up);
		imageIv.startAnimation(anim);
	}

	@Override
	public void setImageRotationBy(int degree) {
		// TODO Auto-generated method stub
        imageIv.setRotationBy(degree);
	}

	@Override
	public void setRotateBtnIcon(int resId) {
		// TODO Auto-generated method stub
		actionBtn.setImageResource(resId);
	}

	@Override
	public void setImageRotationTo(int degree) {
		// TODO Auto-generated method stub
		imageIv.setRotationTo(degree);
	}
}
