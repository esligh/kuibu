package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.core.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.adapter.FavoriteBoxAdapter;
import com.kuibu.module.adapter.FavoriteBoxAdapter.HolderView;

public class CollectFavoriteBoxActivity extends BaseActivity{
	private ListView boxList;
	private List<Map<String, String>> mDatas = new ArrayList<Map<String, String>>();
	private FavoriteBoxAdapter boxAdatper;
	private FButton createboxBtn,cancelBtn ,confirmBtn ;
	private List<String> selIds = new LinkedList<String>();;
	private List<String> selectedIds = null ;
	private MultiStateView mMultiStateView;
	private String cid ; 
	private boolean hasSelected =false;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_box_activity);
		mMultiStateView = (MultiStateView)findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				load_data();
			}   	
        });
		boxList = (ListView) findViewById(R.id.favorite_box_list_view);
		boxList.setOnTouchListener(new OnTouchListener() {				
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_MOVE:
						return true; 
					default : break; 
				}
				return false;
			}
		});
		boxList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				HolderView  holderView = (HolderView)view.getTag();
				if(holderView ==null)
					return ;
				if(holderView.box_cb.isChecked()){
					holderView.box_cb.setChecked(false);
					selIds.remove((String)mDatas.get(position).get("box_id"));
				}else{
					holderView.box_cb.setChecked(true);
					selIds.add((String)mDatas.get(position).get("box_id"));
				}
				setTitle("已选"+selIds.size()+"项");
			}			
		});
		cancelBtn = (FButton)findViewById(R.id.cancel_choose_box_bt);
		cancelBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			}
		});
		
		confirmBtn = (FButton)findViewById(R.id.confirm_choose_box_bt);
		confirmBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				oper_favorite();
			}
		});
		
		createboxBtn = (FButton) findViewById(R.id.create_favoritebox_bt);
		createboxBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				LinearLayout favoriteboxLayout = (LinearLayout) getLayoutInflater()
						.inflate(R.layout.create_favorite_box_dlg, null);
				final EditText box_name = (EditText) favoriteboxLayout
						.findViewById(R.id.favorite_box_name_et);
				final EditText box_desc = (EditText) favoriteboxLayout
						.findViewById(R.id.favorite_box_desc_et);
				final CheckBox box_cb = (CheckBox) favoriteboxLayout
						.findViewById(R.id.favorte_box_dialog_cb);
				
				new AlertDialog.Builder(CollectFavoriteBoxActivity.this)
						.setTitle("添加收藏夹")
						.setView(favoriteboxLayout)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) { 
										boolean bcheck = box_cb.isChecked();
										Map<String,String> params = new HashMap<String,String>();
										params.put("box_name", box_name.getText().toString().trim());
										params.put("box_desc", box_desc.getText().toString().trim());
										params.put("is_private",bcheck ? "1":"");//python bool("") is False
										params.put("create_by", Session.getSession().getuId());
										mDatas.add(params);
										request_addbox(params);
										showView();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub
									}
								}).show();
			}
		});
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		cid = getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
		load_data();
		hasSelected = getIntent().getBooleanExtra(StaticValue.COLLECTION.IS_COLLECTED, false);
		if(hasSelected){
			get_collctedbox();
		}
		showView();
	}

	private void showView() {
		if (boxAdatper == null) {
			boxAdatper = new FavoriteBoxAdapter(this, mDatas,selIds);
			boxList.setAdapter(boxAdatper);
		} else {
			boxAdatper.updateView(mDatas,selIds);
		}
	}
	
	private void load_data() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("off", String.valueOf(mDatas.size()));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_favoriteboxes";

		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String result = response.getString("result");
						if(result!=null){
							JSONArray arr= new JSONArray(result);
							if(arr.length()>0){
								for(int i=0;i<arr.length();i++){
									Map<String,String> item = new HashMap<String,String>();
								    JSONObject obj = (JSONObject) arr.get(i);
								    item.put("box_id",obj.getString("box_id"));
									item.put("box_name", obj.getString("box_name"));
									item.put("item_desc", obj.getString("box_desc"));
									mDatas.add(item);
								}
								showView();
								mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
							}else{
								mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
							}
							
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void get_collctedbox()
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("cid",cid);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_collectedboxes";

		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String result = response.getString("result");
						if(!TextUtils.isEmpty(result)){
							selectedIds = new ArrayList<String>(Arrays.asList(result.split(",")));
							selIds.addAll(selectedIds);
							showView();
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
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void request_addbox(Map<String,String> params) {
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/add_favoritebox";

		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
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
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void oper_favorite()
	{
		if(hasSelected){
			List<String> intersect = new ArrayList<String>();
			intersect.addAll(selectedIds);
			intersect.retainAll(selIds);	
			selectedIds.removeAll(intersect);
			selIds.removeAll(intersect);
			
			if(selectedIds.size()>0){
				del_favorite(selectedIds);
			}
			if(selIds.size()>0){
				add_favorite(selIds);
			}
		}else{
			add_favorite(selIds);
		}		
	}
	
	private void add_favorite(List<String> ids)
	{
		if(ids.size()<=0){
			Toast.makeText(this, "请选择至少一个收藏夹", Toast.LENGTH_SHORT).show();
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("create_by", Session.getSession().getuId());
		params.put("bids", ids);
		params.put("cid", getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/add_favorite";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Intent intent = new Intent();
						intent.putExtra("isCollected", true);
						setResult(RESULT_OK , intent);
						finish();
						overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
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
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void del_favorite(List<String> ids)
	{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", Session.getSession().getuId());		
		StringBuffer buffer= new StringBuffer();
		for(int i=0;i<ids.size();i++){
			buffer.append(ids.get(i)).append(",");
		}		
		params.put("box_ids", buffer.toString());
		params.put("cid", getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/del_favorite";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						Intent intent = new Intent();						
						intent.putExtra("isCollected", response.getBoolean("has_collected"));							
						setResult(RESULT_OK , intent);
						finish();
						overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
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
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			HashMap<String, String> headers = new HashMap<String, String>();
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}
}
