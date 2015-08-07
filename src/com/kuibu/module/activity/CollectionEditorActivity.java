package com.kuibu.module.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kuibu.common.utils.Bimp;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectPackVo;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;
import com.kuibu.module.markdown.HighlightingEditor;

/**
 * @class Markdown编辑主Activity页
 * @author esli
 */

public class CollectionEditorActivity extends ActionBarActivity {
	
	private Context mContext;
	private EditText mTitle;
	private HighlightingEditor mContent;
	private ViewGroup keyboardBarView;
	private CollectionVo collectionVo = null;
	private CollectionBean collection = null;
	private CollectPackVo packVo = null ; 
	private ImageLibVo  imageVo = null ; 
	private String mPhotoPath = null; 
	private boolean isDarkTheme ; 
	private int ALERTDLG_THEME ; 

	//选择图片接收器
	private BroadcastReceiver pickpicReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Bimp.drr.size() > 0) {
				for (int i = 0; i < Bimp.drr.size(); i++) {
					String path = Bimp.drr.get(i);
					StringBuffer markText = new StringBuffer("\n![](");
					markText.append(Constants.URI_PREFIX).append(path).append(")\n");
					mContent.getText().insert(mContent.getSelectionStart(),
							markText);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);		
		isDarkTheme= mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false);
		if (isDarkTheme) {
			setTheme(R.style.AppTheme_Dark);
			ALERTDLG_THEME = AlertDialog.THEME_HOLO_DARK;
		}else{
			setTheme(R.style.AppTheme_Light);
			ALERTDLG_THEME = AlertDialog.THEME_HOLO_LIGHT;
		}	
		super.onCreate(savedInstanceState);
		collectionVo = new CollectionVo(this);
		packVo = new CollectPackVo(this);
		imageVo = new ImageLibVo(this);
		setContentView(R.layout.collection_editor_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null){
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		mContext = this;
		mContent = (HighlightingEditor) findViewById(R.id.note_content);
		mTitle = (EditText) findViewById(R.id.edit_note_title);
		keyboardBarView = (ViewGroup) findViewById(R.id.keyboard_bar);
		if(isDarkTheme){
			keyboardBarView.setBackgroundResource(R.drawable.abc_ab_solid_dark_holo);
		}else{
			keyboardBarView.setBackgroundResource(R.color.lighter_grey);
		}
		collection = (CollectionBean) getIntent().getSerializableExtra(
				StaticValue.EDITOR_VALUE.COLLECTION_ENTITY);
		if (collection != null){
			mContent.setText(collection.getContent());
			mTitle.setText(collection.getTitle());			
		}
		IntentFilter ifilterPickpic = new IntentFilter();
		ifilterPickpic.addAction(StaticValue.CHOOSE_PIC_OVER);
		registerReceiver(pickpicReceiver, ifilterPickpic);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editor_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			return true;
		case R.id.action_insert_img:
			Bimp.clear();
			
			AlertDialog.Builder builder = new Builder(mContext,ALERTDLG_THEME);
			builder.setTitle("插入图片");
			builder.setItems(
					getResources().getStringArray(R.array.popup_menu_item),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int pos) {
							// TODO Auto-generated method stub
							switch (pos) {
							case 0:
								mPhotoPath = takePhotoByCamera();
								break;
							case 1:
								Intent mIntent = new Intent(mContext,
										ImageScanActivity.class);
								mIntent.putExtra("max", 10);
								mContext.startActivity(mIntent);
								break;
							}
						}
					});
			builder.show();
			return true;
		case R.id.action_preview:
			previewNote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		startActivityForResult(openCameraIntent, StaticValue.TAKE_PHOTO_OK);
		return file.getPath();  
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == RESULT_OK){
			switch (requestCode){
			case StaticValue.TAKE_PHOTO_OK:
				String markText = "\n![](file://" + mPhotoPath + ")\n";
				mContent.getText().insert(mContent.getSelectionStart(),
						markText);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void previewNote() {
		saveNote();
		Intent intent = new Intent(this, PreviewActivity.class);
		intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
		intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
				StaticValue.EDITOR_VALUE.EDITOR_TO_PREVIEW);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}
	
	private void saveNote() {
		if (collection == null) {
			if (mTitle == null || mTitle.getText().length() == 0) {
				if (mContent.getText().toString().length() == 0) {
					return;
				} else {
					String snippet = "";
					if (mContent.getText().toString().length() < Constants.MAX_TITLE_LENGTH) {
						snippet = mContent.getText().toString().substring(0,
								mContent.getText().toString().length())
								.replace("[^\\w\\s]+", " ");
					} else {
						snippet = mContent.getText().toString()
								.substring(0, Constants.MAX_TITLE_LENGTH)
								.replace("[^\\w\\s]+", " ");
					}
					mTitle.setText(snippet.replaceAll("\\n", " ").trim());
				}
			}
			collection = new CollectionBean();
			collection.title = mTitle.getText().toString();
			collection.content = mContent.getText().toString().replace("\n-", "\n\n-");
			collection.pid = getIntent().getStringExtra(
					StaticValue.EDITOR_VALUE.COLLECT_PACK_ID);
			collection.type = isHasImage(collection.content) ? StaticValue.EDITOR_VALUE.COLLECTION_TEXTIMAGE
					: StaticValue.EDITOR_VALUE.COLLECTION_IMAGE;
			collection.createBy = Session.getSession().getuId();
			collection.isSync=0 ; 
			collectionVo.add(collection);
			int key = collectionVo.getlastkey() ;
			collection._id = String.valueOf(key);			
			packVo.update(" collect_count = collect_count+1 ", " pack_id = ?", new String[]{String.valueOf(collection.pid)});
			if(collection.type.equals("1")){
				List<String> imgUris = parserImage(collection.content);
				imageVo.add(collection._id,imgUris);
			}
		}else{ //update some field 
			String title = mTitle.getText().toString();
			if (!(title).equals(collection.getTitle())) {
				// update title 
				collection.setTitle(mTitle.getText().toString());
				collectionVo.update(" title = ?,is_sync= ? ", " _id = ? ", 
						new String[]{title,String.valueOf(0),String.valueOf(collection._id)});				
			}
			if(!(mContent.getText().toString().equals(collection.getContent()))){
				String text = mContent.getText().toString().replace("\n-", "\n\n-");
				collection.setContent(text);
				//update content				
				if(collection.type.equals("1")){					
					imageVo.delete(collection._id); //先删除
					List<String> imgUris = parserImage(collection.getContent());
					imageVo.add(collection._id,imgUris);					
				}				
				collectionVo.update(" content = ?,is_sync = ? ", " _id = ? ", 
						new String[]{text,String.valueOf(0),String.valueOf(collection._id)});
			}			
		}		
	}
	
	//从content 中解析出imageUris
	private List<String> parserImage(String s)
	{
		List<String> imgUris = new ArrayList<String>();
		String pattern = "!\\[.*\\]\\(\\s*(file:.*)\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);		
		while (m.find()){
			String url  = m.group(1);
			imgUris.add(url);
		}
		return imgUris;
	}
	
	private boolean isHasImage(String s)
	{
		String pattern = "!\\[.*\\]\\(\\s*(file:.*)\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);		
		while(m.find()){
			return true; 
		}
		return false;
	}
	
	private class KeyboardBarListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			CharSequence shortcut = ((Button) v).getText();
			mContent.getText().insert(mContent.getSelectionStart(), shortcut);
		}
	}

	private class KeyboardBarSmartShortCutListener implements
			View.OnClickListener {
		@Override
		public void onClick(View v) {
			CharSequence shortcut = ((Button) v).getText();
			if (mContent.hasSelection()) {
				CharSequence selected = mContent.getText().subSequence(
						mContent.getSelectionStart(), mContent.getSelectionEnd());
				mContent.getText().replace(
						mContent.getSelectionStart(),
						mContent.getSelectionEnd(),
						Character.toString(shortcut.charAt(0)) + selected
								+ shortcut.charAt(1));
			} else {
				mContent.getText().insert(mContent.getSelectionStart(), shortcut);
				mContent.setSelection(mContent.getSelectionStart() - 2);
			}
		}
	}
	@Override
	public void onBackPressed() {
		saveNote();
		super.onBackPressed(); // remember to put this after setResult(),or you will get null value of intent in the onActivityResult method.
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// Set up the font and background activity_preferences
		setupKeyboardBar();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(pickpicReceiver);
		collectionVo.closeDB();
		packVo.closeDB();
		imageVo.closeDB();
		super.onDestroy();
	}

	private void setupKeyboardBar() {
		boolean showShortcuts = PreferenceManager.getDefaultSharedPreferences(
				this).getBoolean("showShortcuts", true);

		if (showShortcuts && keyboardBarView.getChildCount() == 0) {
			appendRegularShortcuts();
			if (isSmartShortcutsActivated()) {
				appendSmartBracketShortcuts();
			} else {
				appendRegularBracketShortcuts();
			}
		} else if (!showShortcuts) {
			findViewById(R.id.keyboard_bar_scroll).setVisibility(View.GONE);
		}
	}

	private void appendRegularShortcuts() {
		for (String shortcut : Constants.KEYBOARD_SHORTCUTS) {
			appendButton(shortcut, new KeyboardBarListener());
		}
	}

	private void appendRegularBracketShortcuts() {
		for (String shortcut : Constants.KEYBOARD_SHORTCUTS_BRACKETS) {
			appendButton(shortcut, new KeyboardBarListener());
		}

	}

	private void appendSmartBracketShortcuts() {
		for (String shortcut : Constants.KEYBOARD_SMART_SHORTCUTS) {
			appendButton(shortcut, new KeyboardBarSmartShortCutListener());
		}
	}

	private void appendButton(String shortcut, View.OnClickListener l) {
		Button shortcutButton = new Button(this);
		shortcutButton.setText(shortcut);
		shortcutButton.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		shortcutButton.setTextSize(Constants.KEYBOARD_DEFAULT_SIZE);
		shortcutButton.setTypeface(null, Typeface.BOLD);
		shortcutButton.setOnClickListener(l);
		if(isDarkTheme){
			shortcutButton.setTextColor(getResources().getColor(
					android.R.color.white));
			shortcutButton.setBackgroundResource(R.drawable.keyboard_shortcut_button_dark);
		}else{
			shortcutButton.setTextColor(getResources().getColor(R.color.text_color_gray_deep));
			shortcutButton.setBackgroundResource(R.drawable.keyboard_shortcut_button_light);
		}
		keyboardBarView.addView(shortcutButton);
	}

	private boolean isSmartShortcutsActivated() {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean("", false);
	}

}
