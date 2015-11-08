package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.db.CollectPackVo;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TopicListAdapter;

public class OperCollectPackActivity extends BaseActivity {
	
	private static final int MAX_TOPICS  = 5 ; 
	private EditText box_name;
	private EditText box_desc;
	private AutoCompleteTextView box_topic;
	private CheckBox box_cb;
	private List<TopicItemBean> datas = new ArrayList<TopicItemBean>();
	private TopicListAdapter adapter;
	private TagGroup tagGroup;
	private List<TopicItemBean> tags = new LinkedList<TopicItemBean>();
	private CollectPackVo packVo= null ; 
	private String pack_id ; 
	private String oper ; 
	private RadioGroup pack_type_rg ;  
	private ProgressDialog progressDlg ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_collect_pack_activity);
		packVo = new CollectPackVo(this);
		box_name = (EditText) findViewById(R.id.collect_pack_name_et);
		box_desc = (EditText) findViewById(R.id.collect_pack_desc_et);
		box_cb = (CheckBox) findViewById(R.id.collect_pack_dialog_cb);
		box_topic = (AutoCompleteTextView) findViewById(R.id.collect_pack_topic_tv);
		pack_type_rg= (RadioGroup)findViewById(R.id.collect_pack_type);
		tagGroup = (TagGroup) findViewById(R.id.topic_tag_group);
		if(isDarkTheme){
			tagGroup.setTagBackGroundColor(getResources().getColor(R.color.list_view_bg_dark));
		}else{
			tagGroup.setTagBackGroundColor(getResources().getColor(R.color.white));
		}
		
		tagGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final boolean[] arrSelected = new boolean[tags.size()];
				final String [] s = new String[tags.size()];
				for(int i=0;i<tags.size();i++){
					s[i]=tags.get(i).getTopic();
				}
				new AlertDialog.Builder(OperCollectPackActivity.this)
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
												tags.remove(i);
											}
										}
										String [] s = new String[tags.size()];
										for(int i=0;i<tags.size();i++){
											s[i] = tags.get(i).getTopic();
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
		int default_green = getResources().getColor(R.color.default_green);
		tagGroup.setBrightColor(default_green);
		box_topic.addTextChangedListener(new TextChangedListener());
		box_topic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TopicItemBean bean = datas.get(position);	
				if(tags.contains(bean)){
					Toast.makeText(OperCollectPackActivity.this,getString(R.string.have_topic), 
								Toast.LENGTH_SHORT).show();
				}else{
					if(tags.size()>=MAX_TOPICS){
						Toast.makeText(OperCollectPackActivity.this,getString(R.string.max_topic), 
									Toast.LENGTH_SHORT).show();
					}else{
						tags.add(bean);
						String [] s = new String[tags.size()];
						for(int i=0;i<tags.size();i++){
							s[i] = tags.get(i).getTopic();
						}
						tagGroup.setTags(s);
					}
				}
				box_topic.setText("");
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		showView();
		initData();
	}

	void initData()
	{
		oper = getIntent().getStringExtra("OPER");
		if(oper.equals("CREATE")){
			setTitle(getString(R.string.action_create));
		}else if(oper.equals("MODIFY")){
			setTitle(getString(R.string.action_modify));
			pack_id = getIntent().getStringExtra(StaticValue.COLLECTPACK.PACK_ID);			
			Map<String, String> params = new HashMap<String, String>();
			params.put("pack_id", pack_id);
			params.put("uid", Session.getSession().getuId());
			final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/get_collectpack").toString();
			JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
					params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					try {
						String state = response.getString("state");
						if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
								.equals(state)) {
							String data = response.getString("result");
							JSONObject obj = new JSONObject(data);
							box_name.setText(obj.getString("pack_name"));
							if(StaticValue.SERMODLE.PACK_TYPE_WORD.equals(obj.getString("pack_type"))){
								pack_type_rg.check(R.id.collect_pack_type_word);
							}else{
								pack_type_rg.check(R.id.collect_pack_type_pic);
							}
							
							box_desc.setText(obj.getString("pack_desc"));
							box_cb.setChecked(obj.getBoolean("is_private"));
							String[] tids = obj.getString("topic_id").split(",");
							String[] topic_names = obj.getString("topic_names").split(",");
							tagGroup.setTags(topic_names);
							for(int i=0;i<tids.length;i++){
								TopicItemBean item = new TopicItemBean();
								item.setId(tids[i]);
								item.setTopic(topic_names[i]);
								tags.add(item);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
					VolleyLog.e("Error:", error.getCause());
					error.printStackTrace();
				}
			});
			KuibuApplication.getInstance().addToRequestQueue(req);					
		}
	}
	private void showView() {
		if (adapter == null) {
			adapter = new TopicListAdapter(this, datas);
			box_topic.setAdapter(adapter);
		} else {
			adapter.updateView(datas);
		}
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
			if(tags.size()<=0){
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
			bean.pack_id = pack_id ; 
			bean.pack_name = pack_name; 
			bean.pack_type = pack_type_rg.getCheckedRadioButtonId() == R.id.collect_pack_type_word ? 
					StaticValue.SERMODLE.PACK_TYPE_WORD : StaticValue.SERMODLE.PACK_TYPE_PIC ; 
			bean.pack_desc = box_desc.getText().toString().trim();
			bean.create_by = Session.getSession().getuId();
			bean.is_private = box_cb.isChecked() ? 1 : 0 ;
			StringBuffer buffer =new StringBuffer();
			for(int i = 0 ;i<tags.size();i++){
				buffer.append(tags.get(i).getId()).append(",");
			};
			bean.topic_id = buffer.substring(0, buffer.length()-1); 
			if(oper.equals("CREATE")){				
				requestAddpack(bean);
			}else if(oper.equals("MODIFY")){
				packVo.update(bean);
				requestUpdatepack(bean);
			}			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		packVo.closeDB();
		datas.clear();
		tags.clear();
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
			Map<String, String> params = new HashMap<String, String>();
			params.put("slice", query);
			final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/get_topiclist").toString();
			JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
					params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					try {
						String state = response.getString("state");
						if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
								.equals(state)) {
							datas.clear();
							String data = response.getString("result");
							JSONArray arr = new JSONArray(data);
							for (int i = 0; i < arr.length(); i++) {
								JSONObject obj = arr.getJSONObject(i);
								TopicItemBean item = new TopicItemBean();
								item.setId(obj.getString("tid"));
								item.setTopic(obj.getString("topic_name"));
								item.setIntroduce(obj.getString("topic_desc"));
								item.setTopicPicUrl(obj.getString("topic_pic"));
								datas.add(item);
							}
							showView();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
					VolleyLog.e("Error:", error.getCause());
					error.printStackTrace();
				}
			});
			KuibuApplication.getInstance().addToRequestQueue(req);
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
	
	private void requestAddpack(final CollectPackBean newOne)
	{
		if(progressDlg == null){
			progressDlg = new ProgressDialog(this);
			progressDlg.setCanceledOnTouchOutside(false);
			progressDlg.setMessage(getString(R.string.saving));			
		}
		progressDlg.show();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("pack_name", newOne.getPack_name());
		params.put("pack_type", newOne.getPack_type());
		params.put("pack_desc", newOne.getPack_desc());
		params.put("topic_id", newOne.getTopic_id());
		params.put("private", newOne.get_private()==1 ? "1":"");
		params.put("create_by", Session.getSession().getuId());
		String descript = new StringBuffer(Session.getSession().getuId()).append(":") 
				.append(newOne.getPack_name()).toString(); //csn 生成规则		
		params.put("csn", SafeEDcoderUtil.MD5(descript));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/add_collectpack").toString();
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
						CollectPackBean bean = new CollectPackBean();
						bean.setPack_id(response.getString("pack_id"));
						bean.setPack_name(newOne.pack_name);
						bean.setPack_type(newOne.pack_type);
						bean.setPack_desc(newOne.pack_desc);
						bean.setTopic_id(newOne.topic_id);
						bean.setCreate_by(newOne.create_by);
						bean.set_private(newOne.is_private);
						bean.set_sync(1);
						packVo.add(bean);
						Toast.makeText(OperCollectPackActivity.this, getString(R.string.create_success),
								Toast.LENGTH_SHORT).show();			
					}else{
						Toast.makeText(OperCollectPackActivity.this, getString(R.string.create_fail),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				progressDlg.dismiss();
				finish();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				progressDlg.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.oper_fail),
						Toast.LENGTH_SHORT).show();		
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();  
	 		}
		};
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	void requestUpdatepack(CollectPackBean bean)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("pack_id",bean.getPack_id());
		params.put("pack_name", bean.getPack_name());
		params.put("pack_desc", bean.getPack_desc());
		params.put("pack_type", bean.getPack_type());
		params.put("topic_id", bean.getTopic_id());
		params.put("is_private", bean.get_private()==1 ? "1":"");
		params.put("create_by", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION )
				.append("/update_collectpack").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>(){
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
						Toast.makeText(OperCollectPackActivity.this, getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(OperCollectPackActivity.this, getString(R.string.modify_fail),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				finish();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				Toast.makeText(getApplicationContext(), getString(R.string.oper_fail),
						Toast.LENGTH_SHORT).show();	
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();
	 		}
		};
		
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
}
