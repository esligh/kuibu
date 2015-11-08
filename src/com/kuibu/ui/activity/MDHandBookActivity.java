package com.kuibu.module.activity;

import us.feras.mdv.MarkdownView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.Constants;

public class MDHandBookActivity extends BaseActivity{
	private MarkdownView bookView ; 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.markdown_hand_book);
		bookView = (MarkdownView)findViewById(R.id.md_hand_book);
		String cssFileUrl;
		if(isDarkTheme){
			cssFileUrl = Constants.WEBVIEW_DARK_CSSFILE;
		}else{
			cssFileUrl = Constants.WEBVIEW_LIGHT_CSSFILE;
		}
		bookView.loadMarkdownFile("file:///android_asset/handbook.md", cssFileUrl);				
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
		
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		bookView.destroyDrawingCache();
		bookView.removeAllViews();
		bookView.destroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.anim_slide_out_right,
					R.anim.anim_slide_in_right);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
