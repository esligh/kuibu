package com.kuibu.model.js;
import com.kuibu.module.iterf.OnPageLoadFinished;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class InJavaScriptObject {
	private Activity mInstance ; 
	private OnPageLoadFinished onLoadListener ; 
	
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
		Log.d("HTML", html);
		this.onLoadListener.getHtmlSource(html);
    }

	@JavascriptInterface 
	public void openImage(String url) {		//show Image
		if (mInstance != null && !mInstance.isFinishing()) {			
	//		Intent intent = new Intent(mInstance, NewsDetailImageActivity.class);
	//		intent.putExtra("imageUrl", url);			
	//		mInstance.startActivity(intent);
		}
	}
}
