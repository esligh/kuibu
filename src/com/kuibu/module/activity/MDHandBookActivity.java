package com.kuibu.module.activity;

import us.feras.mdv.MarkdownView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebSettings;

import com.kuibu.data.global.Constants;
import com.kuibu.model.js.InJavaScriptObject;
import com.kuibu.model.js.WebViewClientExt;

public class MDHandBookActivity extends BaseActivity{
	private MarkdownView bookView ; 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.markdown_hand_book);
		bookView = (MarkdownView)findViewById(R.id.md_hand_book);
		setUpWebViewDefaults();
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
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebViewDefaults()
	{
		bookView.setBackgroundColor(0);
		bookView.getSettings().setJavaScriptEnabled(true);
		bookView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		InJavaScriptObject jsObj = new InJavaScriptObject(this);
		bookView.addJavascriptInterface(jsObj, "injectedObject");
		bookView.setWebViewClient(new WebViewClientExt(this));				
		bookView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);		
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
