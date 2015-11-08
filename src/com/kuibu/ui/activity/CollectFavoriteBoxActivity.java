package com.kuibu.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.core.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.FavoriteBoxAdapter;

public class CollectFavoriteBoxActivity extends BaseActivity{

	private ListView boxList;
	private List<Map<String, String>> mDatas = new ArrayList<Map<String, String>>();
	private FavoriteBoxAdapter boxAdatper;
	private FButton cancelBtn ,confirmBtn ;
	private List<String> selIds = new LinkedList<String>();;
	private List<String> selectedIds = null ;
	private MultiStateView mMultiStateView;
	private String cid ,ctype; 
	private boolean hasSelected = false;    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_box_activity);
		mMultiStateView = (MultiStateView)findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
        
		boxList = (ListView) findViewById(R.id.favorite_box_list_view);
		boxList.setOnTouchListener(new OnTouchListener() {				
			@SuppressLint("ClickableViewAccessibility")
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
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {				
				Map<String,String> item = (Map<String,String>)viewAdapter.getAdapter().getItem(position);
				ViewHolder  holderView = (ViewHolder)view.getTag();
				if(holderView ==null)
					return ;
				CheckBox cbx = holderView.getView(R.id.favorite_box_cb);
				if(cbx.isChecked()){
					cbx.setChecked(false);
					selIds.remove((String)item.get("box_id"));
				}else{
					if(StaticValue.SERMODLE.BOX_TYPE_PIC.
							equals(item.get("box_type"))){ //收藏类型2只能收藏colletion type为2的收集 
						if(!StaticValue.SERMODLE.BOX_TYPE_PIC.equals(ctype)){
							Toast.makeText(CollectFavoriteBoxActivity.this, 
									getString(R.string.boxtype_not_match), Toast.LENGTH_SHORT).show();
							return ;
						}
					}
					if(StaticValue.SERMODLE.BOX_TYPE_WORD.equals(item.get("box_type"))){//收藏类型1的不能收藏collection type 为2的收集
						if(StaticValue.SERMODLE.BOX_TYPE_PIC.equals(ctype)){
							Toast.makeText(CollectFavoriteBoxActivity.this, 
									getString(R.string.boxtype_not_match), Toast.LENGTH_SHORT).show();
							return ;
						}
					}
					cbx.setChecked(true);
					selIds.add((String)mDatas.get(position).get("box_id"));
				}
				setTitle("已选"+selIds.size()+"项");
			}			
		});
		cancelBtn = (FButton)findViewById(R.id.cancel_choose_box_bt);
		cancelBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
				overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			}
		});
		
		confirmBtn = (FButton)findViewById(R.id.confirm_choose_box_bt);
		confirmBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				operFavorite();
			}
		});		
		cid = getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
		ctype = getIntent().getStringExtra("type");
		loadData();
		hasSelected = getIntent().getBooleanExtra(StaticValue.COLLECTION.IS_COLLECTED, false);
		if(hasSelected){
			getCollctedbox();
		}
		showView();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		super.onCreateOptionsMenu(menu);
		MenuItem edit=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
        		StaticValue.MENU_ITEM.CREATE_ID,StaticValue.MENU_ORDER.FIRST_ID,
        		getString(R.string.create_new_cbox));
        edit.setIcon(getResources().getDrawable(R.drawable.ic_create_favbox));  
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);         
		return true; 
	}

	
	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case StaticValue.MENU_ITEM.CREATE_ID:
			LinearLayout favoriteboxLayout = (LinearLayout) getLayoutInflater()
			.inflate(R.layout.create_favorite_box_dlg, null);
			final EditText box_name = (EditText) favoriteboxLayout
					.findViewById(R.id.favorite_box_name_et);
			final EditText box_desc = (EditText) favoriteboxLayout
					.findViewById(R.id.favorite_box_desc_et);
			final CheckBox box_cb = (CheckBox) favoriteboxLayout
					.findViewById(R.id.favorte_box_dialog_cb);
	        final RadioGroup box_type_rg = (RadioGroup)favoriteboxLayout.findViewById(R.id.favorite_box_type);

			new AlertDialog.Builder(CollectFavoriteBoxActivity.this)
					.setTitle(getString(R.string.create_new_cbox))
					.setView(favoriteboxLayout)
					.setPositiveButton(getString(R.string.btn_confirm),
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
									params.put("box_type", box_type_rg.getCheckedRadioButtonId() == R.id.favorite_box_type_word ? 
					                     StaticValue.SERMODLE.BOX_TYPE_WORD : StaticValue.SERMODLE.BOX_TYPE_PIC );
									requestAddbox(params);
								}
							})
					.setNegativeButton(getString(R.string.btn_cancel),null).show();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDatas.clear();mDatas = null; 
		selIds.clear();selIds = null;
	}

	private void showView() {
		if (boxAdatper == null) {
			boxAdatper = new FavoriteBoxAdapter(this, mDatas,selIds,R.layout.favorite_box_list_item);
			boxList.setAdapter(boxAdatper);
		} else {
			boxAdatper.updateView(mDatas,selIds);
		}
	}
	
	private void loadData() {
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
									item.put("box_type", obj.getString("box_type"));
									mDatas.add(item);
								}
								showView();
							}
							if(mDatas.size()>0){
								mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
							}else{
								mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
							}
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
//				Toast.makeText(getApplicationContext(), 
//						VolleyErrorHelper.getMessage(error,getApplicationContext()), 
//						Toast.LENGTH_SHORT).show();
			}
		});
		
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	private void getCollctedbox()
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
	
	private void requestAddbox(final Map<String,String> params) {
		final String URL = new StringBuilder(Constants.Config.SERVER_URI) 
				.append(Constants.Config.REST_API_VERSION)
				.append("/add_favoritebox").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){						
						mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
						Map<String,String> item = new HashMap<String,String>();
					    item.put("box_id",response.getString("box_id"));
						item.put("box_name", params.get("box_name"));
						item.put("box_type", params.get("box_type"));
						item.put("item_desc", params.get("box_desc"));
						mDatas.add(item);
						showView();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				
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
	
	private void operFavorite()
	{
		if(hasSelected){
			List<String> intersect = new ArrayList<String>();
			intersect.addAll(selectedIds);
			intersect.retainAll(selIds);	
			selectedIds.removeAll(intersect);
			selIds.removeAll(intersect);
			
			if(selectedIds.size()>0){
				delFavorite(selectedIds);
			}
			if(selIds.size()>0){
				addFavorite(selIds);
			}
		}else{
			addFavorite(selIds);
		}		
	}
	
	private void addFavorite(List<String> ids)
	{
		if(ids.size()<=0){
			Toast.makeText(this, getString(R.string.choose_one), Toast.LENGTH_SHORT).show();
			return ; 
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
				Toast.makeText(getApplicationContext(), 
						VolleyErrorHelper.getMessage(error,getApplicationContext()), 
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
	
	private void delFavorite(List<String> ids)
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
	 			return KuibuUtils.prepareReqHeader();  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

}
