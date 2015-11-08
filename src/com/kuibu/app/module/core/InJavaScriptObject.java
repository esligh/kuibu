package com.kuibu.app.module.core;
import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.iterfaces.OnPageLoadFinished;
import com.kuibu.ui.activity.ImageDetailActivity;

public class InJavaScriptObject {
	
	private Activity mInstance ; 
	private OnPageLoadFinished onLoadListener = null; 
	
	public InJavaScriptObject(Activity activity)
	{
		this.mInstance = activity ; 
	}
	
	public void setOnPageLoadFinishedListener(OnPageLoadFinished l)
	{
		this.onLoadListener = l; 
	}
	
	@JavascriptInterface
	public void pageLoadFinished()
	{
		if(onLoadListener != null )
			this.onLoadListener.pageLoadFinished();
	}
	
	@JavascriptInterface
	public void getHtml(String html) {
		if(onLoadListener != null )
			this.onLoadListener.getHtmlSource(html);
    }

	@JavascriptInterface 
	public void openImage(String url) {		//show Image
		if (mInstance != null && !mInstance.isFinishing()) {			
			Intent intent = new Intent(mInstance, ImageDetailActivity.class);
			intent.putExtra(StaticValue.IMG_URL, url);			
			mInstance.startActivity(intent);
		}
	}
	
	
}
