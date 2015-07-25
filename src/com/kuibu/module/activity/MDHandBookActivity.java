package com.kuibu.module.activity;

import com.kuibu.data.global.Constants;

import us.feras.mdv.MarkdownView;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}	
}
