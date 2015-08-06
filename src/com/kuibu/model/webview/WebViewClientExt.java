package com.kuibu.model.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class WebViewClientExt extends WebViewClient {
	
	private Context mContext ; 
	public WebViewClientExt(Context context) {
		this.mContext = context ; 		
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {   
		 Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
		 mContext.startActivity(i);
         return true;
    }  
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		Log.d("WebView", "onPageStarted");
		super.onPageStarted(view, url, favicon);
		view.getSettings().setJavaScriptEnabled(true);
	}
	
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	public void onPageFinished(WebView view, String url) {
		Log.d("WebView", "onPageFinished ");
		super.onPageFinished(view, url);
		//get html source  
		String javascript = "javascript:window.injectedObject.getHtml('<html>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</html>');";
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			view.evaluateJavascript(javascript, new ValueCallback<String>() {
	                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	                @Override
	                public void onReceiveValue(String s) {
	            		Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
	                }
			});
		}else{
			view.loadUrl(javascript);
		}
		
	}	
}
