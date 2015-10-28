package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kuibu.common.utils.BitmapHelper;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectPackVo;
import com.kuibu.model.vo.CollectionVo;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectionImageActivity extends AppCompatActivity{

	private EditText titleEt; 
	private PhotoView imageIv; 
	private EditText descTv; 
	private boolean isDarkTheme ; 
	private String mImgUri; 
	private CollectionVo collectionVo = null ; 
	private CollectPackVo packVo = null ;
	private CollectionBean collection ; 
	private ImageButton rotateBtn; 
	private String action ; 
	private int degree ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);		
		isDarkTheme= mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false);
		if (isDarkTheme) {
			setTheme(R.style.AppTheme_Dark);			
		}else{
			setTheme(R.style.AppTheme_Light);
		}	
		super.onCreate(savedInstanceState);
		collectionVo = new CollectionVo(this);
		packVo = new CollectPackVo(this);
		
		setContentView(R.layout.collection_image_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		if(toolbar != null){
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		titleEt = (EditText)findViewById(R.id.edit_note_title);		
		imageIv = (PhotoView)findViewById(R.id.collection_image);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageIv.setMaxHeight(dm.heightPixels);
		imageIv.setMaxWidth(dm.widthPixels);
		imageIv.setAdjustViewBounds(true);		
		rotateBtn = (ImageButton)findViewById(R.id.rotate_btn); 
		rotateBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				imageIv.setRotationBy(90);
				degree = (degree+90)%360;
			}
		});
		
		descTv = (EditText)findViewById(R.id.collection_desc);
		if(isDarkTheme){
			descTv.setHintTextColor(getResources().getColor(R.color.hint_foreground_material_dark));
		}else{
			descTv.setHintTextColor(getResources().getColor(R.color.hint_foreground_material_light));
		}
		mImgUri = getIntent().getStringExtra("path");
		if(!TextUtils.isEmpty(mImgUri)){
			ImageLoader.getInstance().displayImage(Constants.URI_PREFIX+mImgUri,imageIv);
		}else{
			collection = (CollectionBean) getIntent().getSerializableExtra(
					StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
			if (collection != null){
				descTv.setText(collection.getContent());
				titleEt.setText(collection.getTitle());
				ImageLoader.getInstance().displayImage(collection.cover,imageIv);
			}
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem add = menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.PREVIEW_ID,
				StaticValue.MENU_ORDER.BASE_ORDER_ID, getString(R.string.preview));
		add.setIcon(getResources().getDrawable(R.drawable.ic_preview_light));
		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
				break;
			case StaticValue.MENU_ITEM.PREVIEW_ID:
				if(TextUtils.isEmpty(titleEt.getText().toString())){
					Toast.makeText(this, getString(R.string.need_title),Toast.LENGTH_SHORT).show();
					titleEt.requestFocus();
				}else{					
					saveCollection();
					Intent intent  = new Intent(CollectionImageActivity.this,ImagePreviewActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
					intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
							StaticValue.EDITOR_VALUE.EDITOR_TO_PREVIEW);
					startActivity(intent);
				} 	
				break;
		}
		return super.onOptionsItemSelected(item);
	}
			
	@Override
	public void onBackPressed() {
		saveCollection();		
		if(!TextUtils.isEmpty(action)){
			Intent intent = new Intent();
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
			setResult(RESULT_OK, intent);
		}			
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		super.onBackPressed();		
	}
	
	private void saveCollection()
	{
		String title = titleEt.getText().toString() ;
		String content = descTv.getText().toString(); 
		if(TextUtils.isEmpty(title)) 
			return ; 
		if(collection == null ){
			action = "A";
			collection = new CollectionBean();			
			collection.title = title;
			collection.content = content;
			if(degree>0){
				collection.cover = BitmapHelper.rotateImage(this, mImgUri, degree, 90);				
			}else{
				collection.cover = mImgUri;
			}			
			collection.pid = getIntent().getStringExtra(
					StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
			collection.type = StaticValue.EDITOR_VALUE.COLLECTION_IMAGE;
			collection.createBy = Session.getSession().getuId();
			collection.isSync= 0 ; 
			collectionVo.add(collection);			
			int key = collectionVo.getlastkey() ;
			collection._id = String.valueOf(key);			
			packVo.update(" collect_count = collect_count+1 ", " pack_id = ?", new String[]{String.valueOf(collection.pid)});	
		}else{ // update don't support
			StringBuffer cons = new StringBuffer(); 
			List<String> args = new ArrayList<String>();
			if(!collection.title.equals(title)){
				cons.append(" title = ? ,");
				collection.title = title; 
				args.add(title);
			}	
			
			if(!collection.content.equals(content)){
				cons.append(" content = ? ") ;
				collection.content = content ; 
				args.add(content);
			}
			
			if(degree>0){
				cons.append(" cover= ? ");
				collection.cover = BitmapHelper.rotateImage(this, mImgUri, degree, 90);			
				args.add(collection.cover);
			}
			
			if(cons.length()>0){
				action = "M";
				args.add(collection._id);
				collectionVo.update(cons.toString()," _id = ? " , args.toArray(new String[args.size()]));
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		collectionVo.closeDB();  
		packVo.closeDB();  
	}	
	
}
