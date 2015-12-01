package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kuibu.custom.widget.HighlightingEditor;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.db.AlbumVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;

/**
 * @class Markdown编辑主Activity页
 * @author esli
 */

public class WCollectionCreateActivity extends AppCompatActivity {
	
	public static final int REQUEST_IMAGE = 2 ; 
	private Context mContext;
	private EditText mTitle;
	private HighlightingEditor mContent;
	private ViewGroup keyboardBarView;
	private CollectionVo collectionVo = null;
	private CollectionBean collection = null;
	private AlbumVo packVo = null ; 
	private ImageLibVo  imageVo = null ;
	private boolean isDarkTheme ;
	
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
		packVo = new AlbumVo(this);
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
            Intent intent = new Intent(WCollectionCreateActivity.this, MultiImageSelectorActivity.class);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, Constants.Config.MAX_IMAGE_SELECT);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            startActivityForResult(intent, REQUEST_IMAGE);
			return true;
		case R.id.action_preview:
			previewCollection();
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
			case REQUEST_IMAGE:
	            List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
	            if(path != null){
	            	for(String s : path){
	            		StringBuffer markText = new StringBuffer("\n![](");
						markText.append(Constants.URI_PREFIX).append(s).append(")\n");
						mContent.getText().insert(mContent.getSelectionStart(),
								markText);
	            	}
	            }
	            
				break;
			case StaticValue.RequestCode.PREVIEW_OVER:
				if(collection!=null){
					collection.cover = data.getStringExtra("cover_path");
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void previewCollection() {
		String content = mContent.getText().toString();
		if(!TextUtils.isEmpty(content)){
			saveCollection();
			Intent intent = new Intent(this, PreviewWCollectionActivity.class);
			intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ENTITY, collection);
			intent.putExtra(StaticValue.EDITOR_VALUE.FROM_WHO,
					StaticValue.EDITOR_VALUE.EDITOR_TO_PREVIEW);
			startActivityForResult(intent, StaticValue.RequestCode.PREVIEW_OVER);
			overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
		}
	}
	
	private void saveCollection() {
		if (collection == null) {
			if (TextUtils.isEmpty(mTitle.getText().toString())) {
				if (TextUtils.isEmpty(mContent.getText().toString())) {
					return;
				}else{
					String snippet = "";
					if (mContent.getText().toString().length() < Constants.MAX_TITLE_LENGTH) {
						snippet = mContent.getText().toString().substring(0,
								mContent.getText().toString().length())
								.replace("[^\\w\\s]+", " ");
					}else{
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
			collection.isSync= 0 ; 
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
				collection.isSync = 0 ;
			}
			
			if(!(mContent.getText().toString().equals(collection.getContent()))){
				String text = mContent.getText().toString().replace("\n-", "\n\n-");
				collection.setContent(text);
				//update content									
				imageVo.delete(collection._id); //if have
				List<String> imgUris = parserImage(collection.getContent());
				imageVo.add(collection._id,imgUris);								
				collectionVo.update(" content = ?,is_sync = ? ", " _id = ? ", 
						new String[]{text,String.valueOf(0),String.valueOf(collection._id)});
				collection.isSync = 0 ;
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
		saveCollection();
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
	//	unregisterReceiver(pickpicReceiver);
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

	@SuppressWarnings("deprecation")
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
			shortcutButton.setTextColor(getResources().getColor(android.R.color.white));
			shortcutButton.setBackgroundResource(R.drawable.keyboard_shortcut_button_dark);
		}else{
			shortcutButton.setTextColor(getResources().getColor(R.color.text_color_gray_deep));
			shortcutButton.setBackgroundResource(R.drawable.keyboard_shortcut_button_light);
		}
		keyboardBarView.addView(shortcutButton);
	}

	private boolean isSmartShortcutsActivated() {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean("smartShortcuts", true);
	}

}
