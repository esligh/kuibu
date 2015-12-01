package com.kuibu.ui.activity;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TopicListAdapter;
import com.kuibu.module.presenter.CreateAlbumPresenterImpl;
import com.kuibu.module.presenter.interfaces.CreateAlbumPresenter;
import com.kuibu.ui.view.interfaces.CreateAlbumView;

public class CreateAlbumActivity extends BaseActivity implements CreateAlbumView{
	
	private static final int MAX_TOPICS  = 5 ; 
	private EditText box_name;
	private EditText box_desc;
	private AutoCompleteTextView box_topic;
	private CheckBox box_cb;	
	private TopicListAdapter adapter;
	private TagGroup tagGroup;
	private RadioGroup pack_type_rg ;
	private CreateAlbumPresenter mPresenter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_collect_pack_activity);		
		box_name = (EditText) findViewById(R.id.collect_pack_name_et);
		box_desc = (EditText) findViewById(R.id.collect_pack_desc_et);
		box_cb = (CheckBox) findViewById(R.id.collect_pack_dialog_cb);
		box_topic = (AutoCompleteTextView) findViewById(R.id.collect_pack_topic_tv);
		pack_type_rg= (RadioGroup)findViewById(R.id.collect_pack_type);
		tagGroup = (TagGroup) findViewById(R.id.topic_tag_group);
		if(isDarkTheme){
			tagGroup.setTagBackGroundColor(ContextCompat.getColor(this,R.color.list_view_bg_dark));
		}else{
			tagGroup.setTagBackGroundColor(ContextCompat.getColor(this,R.color.white));
		}
		
		tagGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final boolean[] arrSelected = new boolean[mPresenter.getTopicTags().size()];
				final String [] s = new String[mPresenter.getTopicTags().size()];
				for(int i=0;i<mPresenter.getTopicTags().size();i++){
					s[i]=mPresenter.getTopicTags().get(i).getTopic();
				}
				new AlertDialog.Builder(CreateAlbumActivity.this)
						.setTitle(getString(R.string.action_delete))
						.setMultiChoiceItems(
								s,
								arrSelected,
								new DialogInterface.OnMultiChoiceClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										arrSelected[which] = isChecked;
									}
								})
						.setPositiveButton(getString(R.string.btn_confirm),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										for (int i = arrSelected.length-1; i >=0 ; i--) {
											if (arrSelected[i] == true) {
												mPresenter.getTopicTags().remove(i);
											}
										}
										String [] s = new String[mPresenter.getTopicTags().size()];
										for(int i=0;i<mPresenter.getTopicTags().size();i++){
											s[i] = mPresenter.getTopicTags().get(i).getTopic();
										}
										tagGroup.setTags(s);
									}
								})
						.setNegativeButton(getString(R.string.btn_cancel),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}
		});
		int default_green = ContextCompat.getColor(this,R.color.default_green);
		tagGroup.setBrightColor(default_green);
		box_topic.addTextChangedListener(new TextChangedListener());
		box_topic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TopicItemBean bean = (TopicItemBean)viewAdapter.getAdapter().getItem(position);
				if(mPresenter.getTopicTags().contains(bean)){
					Toast.makeText(CreateAlbumActivity.this,getString(R.string.have_topic), 
								Toast.LENGTH_SHORT).show();
				}else{
					if(mPresenter.getTopicTags().size()>=MAX_TOPICS){
						Toast.makeText(CreateAlbumActivity.this,getString(R.string.max_topic), 
									Toast.LENGTH_SHORT).show();
					}else{
						mPresenter.getTopicTags().add(bean);
						String [] s = new String[mPresenter.getTopicTags().size()];
						for(int i=0;i<mPresenter.getTopicTags().size();i++){
							s[i] = mPresenter.getTopicTags().get(i).getTopic();
						}
						tagGroup.setTags(s);
					}
				}
				box_topic.setText("");
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		adapter = new TopicListAdapter(this, null);
		box_topic.setAdapter(adapter);
		mPresenter  = new CreateAlbumPresenterImpl(this);
		mPresenter.loadAlbum();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem add = menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.SAVE_ID,
				StaticValue.MENU_ORDER.SAVE_ORDER_ID, getString(R.string.action_save));
		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			break;
		case StaticValue.MENU_ITEM.SAVE_ID:
			if(mPresenter.getTopicTags().size()<=0){
				Toast.makeText(this, getString(R.string.need_topic), Toast.LENGTH_SHORT).show();
				return true; 
			}
			String pack_name = box_name.getText().toString().trim();
			if(TextUtils.isEmpty(pack_name)){
				Toast.makeText(this, getString(R.string.need_name), Toast.LENGTH_SHORT).show();
				return true; 
			}	
			CollectPackBean bean = new CollectPackBean();
			bean._id = getIntent().getStringExtra("_id");
			bean.pack_id = mPresenter.getPackId() ; 
			bean.pack_name = pack_name; 
			bean.pack_type = pack_type_rg.getCheckedRadioButtonId() == R.id.collect_pack_type_word ? 
					StaticValue.SERMODLE.PACK_TYPE_WORD : StaticValue.SERMODLE.PACK_TYPE_PIC ; 
			bean.pack_desc = box_desc.getText().toString().trim();
			bean.create_by = Session.getSession().getuId();
			bean.is_private = box_cb.isChecked() ? 1 : 0 ;
			StringBuffer buffer =new StringBuffer();
			for(int i = 0 ;i<mPresenter.getTopicTags().size();i++){
				buffer.append(mPresenter.getTopicTags().get(i).getId()).append(",");
			};
			bean.topic_id = buffer.substring(0, buffer.length()-1); 
			if("CREATE".equals(mPresenter.getOperType())){				
				mPresenter.addAlbum(bean);
			}else if("MODIFY".equals(mPresenter.getOperType())){				
				mPresenter.updateAlbum(bean);
			}			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPresenter.closeDbConn();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
	
	class TextChangedListener implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String query = s.toString() ; 
			if (TextUtils.isEmpty(query))
				return;
			mPresenter.getTopicList(query);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void refreshTopicList(List<TopicItemBean> data) {
		// TODO Auto-generated method stub
		adapter.updateView(data);
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
	public void close() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);
	}

	@Override
	public void setAlbumName(String name) {
		// TODO Auto-generated method stub
		box_name.setText(name);
	}

	@Override
	public void setAlbumDesc(String desc) {
		// TODO Auto-generated method stub
		box_desc.setText(desc);
	}

	@Override
	public void setTopicTag(String[] tags) {
		// TODO Auto-generated method stub
		tagGroup.setTags(tags);

	}

	@Override
	public void setAlbumType(int id) {
		// TODO Auto-generated method stub
		pack_type_rg.check(id);
	}

	@Override
	public void setAlbumPrivate(boolean bprivate) {
		// TODO Auto-generated method stub
		box_cb.setChecked(bprivate);
	}

}
