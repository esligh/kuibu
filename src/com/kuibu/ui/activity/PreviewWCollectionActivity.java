package com.kuibu.ui.activity;

import java.io.File;

import us.feras.mdv.MarkdownView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.module.core.InJavaScriptObject;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.custom.widget.NotifyingScrollView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterfaces.OnPageLoadFinished;
import com.kuibu.module.presenter.PreviewWCollectionPresenterImpl;
import com.kuibu.module.presenter.interfaces.PreviewWCollectionPresenter;
import com.kuibu.ui.view.interfaces.PreviewWCollectionView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class PreviewWCollectionActivity extends BaseActivity 
	implements OnPageLoadFinished,PreviewWCollectionView{
	
	private MarkdownView previewWebView;
	private MenuItem pubMenu;
	private ImageView imageHeader ; 
	private TextView titleTv ;  
	private LinearLayout webViewcontainer;
	private String cssFile ; 	
	private PreviewWCollectionPresenter mPresenter ; 
	private boolean isEditIncoming = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.collection_preview);
        NotifyingScrollView scrollView = (NotifyingScrollView)findViewById(R.id.scroll_view);
		if (isDarkTheme) {
			scrollView.setBackgroundColor(ContextCompat.getColor(this,R.color.webview_dark));
			cssFile = Constants.WEBVIEW_DARK_CSSFILE;
		}else{
			scrollView.setBackgroundColor(ContextCompat.getColor(this,R.color.webview_light));
			cssFile = Constants.WEBVIEW_LIGHT_CSSFILE;
		}
		webViewcontainer = (LinearLayout) findViewById(R.id.content_ll);
		previewWebView = new MarkdownView(getApplicationContext());
		webViewcontainer.addView(previewWebView);
		setUpWebViewDefaults();	
		imageHeader =(ImageView)findViewById(R.id.image_header);
		imageHeader.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				//修改封面
				AlertDialog.Builder builder = new Builder(PreviewWCollectionActivity.this);
				builder.setTitle(getString(R.string.setting_cover));
				builder.setItems(getResources().getStringArray(R.array.popup_menu_item), 
						new android.content.DialogInterface.OnClickListener(){
							@Override
							public void onClick(
									DialogInterface dialog,
									int position) {
								switch(position){
									case 0:
										mPresenter.setCoverPath(takePhotoByCamera());
										break;
									case 1:
										Crop.pickImage(PreviewWCollectionActivity.this);
										break;
								}
						}
				});
				builder.show();
			}
		});			    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		titleTv = (TextView)findViewById(R.id.title);
		setTitle(getString(R.string.preview));
		mPresenter = new PreviewWCollectionPresenterImpl(this);
		mPresenter.loadCollection();	
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebViewDefaults()
	{
		previewWebView.setBackgroundColor(0);
		previewWebView.getSettings().setJavaScriptEnabled(true);
		previewWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		InJavaScriptObject jsObj = new InJavaScriptObject(this);
		jsObj.setOnPageLoadFinishedListener(this);
		previewWebView.addJavascriptInterface(jsObj, "injectedObject");
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			previewWebView.getSettings().setLoadsImagesAutomatically(true);
	    } else {
	    	previewWebView.getSettings().setLoadsImagesAutomatically(false);
	    }
		previewWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {   
				 Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				 startActivity(i);
		         return true;
		    }  
			
			@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d("WebView", "onPageFinished ");
				if(!previewWebView.getSettings().getLoadsImagesAutomatically()) {
					previewWebView.getSettings().setLoadsImagesAutomatically(true);
			    }
				super.onPageFinished(view, url);
				//get html source  
				String javascript = "javascript:window.injectedObject.getHtml('<html>'+" +
		                    "document.getElementsByTagName('html')[0].innerHTML+'</html>');";
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
					view.evaluateJavascript(javascript, new ValueCallback<String>() {
			                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
			                @Override
			                public void onReceiveValue(String s) {
			            		Toast.makeText(PreviewWCollectionActivity.this, s, Toast.LENGTH_SHORT).show();
			                }
					});
				}else{
					view.loadUrl(javascript);
				}
				
			}	
		});
		previewWebView.setWebChromeClient(new WebChromeClient() {			 
		    @Override
		    public boolean onJsAlert(WebView view, String url, String message,
		            final JsResult result) {  
	            result.cancel();  
	            return true; 
		    }		 
		    @Override
		    public boolean onJsConfirm(WebView view, String url,
		            String message, final JsResult result) {
		    	
		        return true;
		    }
		});
		previewWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
	}
	
	@Override
	protected void onPause() {
		if (isEditIncoming) {
			finish();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPresenter.closeDbConn();
		webViewcontainer.removeView(previewWebView);
		previewWebView.destroyDrawingCache();
		previewWebView.removeAllViews();
		previewWebView.destroy();		 
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
		if (mPresenter.needPublish()) {
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
			editNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK){
			switch (requestCode){
				case StaticValue.TAKE_PHOTO_CODE:
	            	ImageLoader.getInstance().displayImage(Constants.URI_PREFIX+mPresenter.getCoverPath(),
	            			imageHeader);
	            	mPresenter.updateCover(mPresenter.getCoverPath());
					break;
					
				case Crop.REQUEST_PICK:
					Uri uri = data.getData();
					imageHeader.setImageURI(uri);
					mPresenter.setCoverPath(StorageUtils.getImageAbsolutePath(this, uri));
					mPresenter.updateCover(mPresenter.getCoverPath());
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("TAKE_PHOTO_URL", mPresenter.getCoverPath());
	}

	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mPresenter.setCoverPath(savedInstanceState.getString("TAKE_PHOTO_URL"));
	}

	public String takePhotoByCamera() {
		File dir = new File(StorageUtils.getFileDirectory(getApplicationContext())
				.getAbsolutePath()+Constants.Config.CAMERA_IMG_DIR);		
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+".jpg");
		Uri imageUri = Uri.fromFile(file);
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, StaticValue.TAKE_PHOTO_CODE);
		return file.getPath();  
	}
	
	private void editNote() {
		isEditIncoming = true;
		Intent intent = new Intent(this, WCollectionCreateActivity.class);
		intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, mPresenter.getCollection());
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,
				R.anim.anim_slide_out_left);
	}
		
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		intent.putExtra("cover_path", mPresenter.getCoverPath());
		setResult(RESULT_OK, intent);
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}
			
	@Override
	public void getHtmlSource(String html) {
		mPresenter.setHtmlSource(html);
	}

	@Override
	public void pageLoadFinished() {
		// TODO Auto-generated method stub
		
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
	public void setPageContent(String content) {
		// TODO Auto-generated method stub
		previewWebView.loadMarkdown(content,cssFile);
	}

	@Override
	public void setCover(String cover) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(cover, imageHeader);
	}
}
