package com.kuibu.model.webview;
import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.ImageDetailActivity;
import com.kuibu.module.iterf.OnPageLoadFinished;

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