package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionItemBean;
import com.kuibu.module.adapter.FavoriteDetailsAdapter;

public class FavoriteBoxDetailActivity extends BaseActivity 
	implements OnBorderListener{
	
	private TextView box_name_tv ,box_desc_tv ;
	private TextView focus_count_tv,box_count_tv;
	private ListView box_list ; 
	private View footerView; 
	private BorderScrollView borderScrollView;
	private FavoriteDetailsAdapter adapter ; 
	private List<CollectionItemBean> datas = new ArrayList<CollectionItemBean>();
	private String mboxId ; 
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_box_detail_activity);
		box_name_tv = (TextView)findViewById(R.id.detail_box_name_tv);
		box_desc_tv = (TextView)findViewById(R.id.detail_box_desc_tv);
		box_count_tv =(TextView) findViewById(R.id.detail_box_count_tv);
		focus_count_tv = (TextView)findViewById(R.id.detail_follow_count_tv);
		
		box_list = (ListView)findViewById(R.id.detatils_list);
		footerView = LayoutInflater.from(this).inflate(R.layout.footer,
				null);
		footerView.setVisibility(View.GONE);
		box_list.addFooterView(footerView);
		box_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				Intent intent = new Intent(FavoriteBoxDetailActivity.this,CollectionDetailActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,datas.get(position).getId());	
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		box_list.setOnItemLongClickListener(new OnItemLongClickListener() {			
			@Override
			public boolean onItemLongClick(AdapterView<?> adpterView, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(FavoriteBoxDetailActivity.this);
				builder.setTitle(getString(R.string.operator));
				builder.setItems(getResources().getStringArray(R.array.favorite_box_item_opt),
						new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int pos) {
							switch(pos){
								case 0:
									delFavorite(position);
								break;
							}
						}
				});
				builder.show();
				return false;
			}
		});
		borderScrollView= (BorderScrollView)findViewById(R.id.favorite_box_detail_scroll_view);
		borderScrollView.setOnBorderListener(this);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mboxId = getIntent().getStringExtra(StaticValue.SERMODLE.BOX_ID);
		box_name_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_NAME));
		box_desc_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_DESC));
		box_count_tv.setText("共有"+getIntent().getStringExtra(StaticValue.SERMODLE.BOX_COUNT)+"条收集");
		focus_count_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_FOCUS_COUNT));
		
		loadData();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);	
		MenuItem clear=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
        		StaticValue.MENU_ITEM.CLEAR_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,
        		getString(R.string.clear_all));	        
		clear.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch(item.getItemId()){
		 	case android.R.id.home:
				this.onBackPressed();
				break;
		 	case StaticValue.MENU_ITEM.CLEAR_ID:
		 		new  AlertDialog.Builder(this)  
            	.setTitle(getString(R.string.action_delete))    
            	.setMessage(getString(R.string.sure_clear_favorite))  
            	.setPositiveButton(getString(R.string.btn_confirm) , new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						clearBox();
					}				
				}).setNegativeButton(getString(R.string.btn_cancel) , new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1){}						
				}).show();		 		
		 		break;
		 }
		 return super.onOptionsItemSelected(item);
	 }
	
	private void showView()
	{
		if(adapter == null){
			adapter = new FavoriteDetailsAdapter(this, datas);
			box_list.setAdapter(adapter);
		}else{
			adapter.updateView(datas);
		}		
	}

	@Override
	public void onBottom() {		
		footerView.setVisibility(View.VISIBLE);
		loadData();		
	}

	@Override
	public void onTop() {
		borderScrollView.loadComplete();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
	
	private void loadData()
	{		
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("box_id", mboxId);
		params.put("off", String.valueOf(datas.size()));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/get_favorite").toString();

		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String result = response.getString("result");
						if(result!=null){
							JSONArray arr= new JSONArray(result);
							if(arr.length()>0){
								for(int i=0;i<arr.length();i++){
									CollectionItemBean item = new CollectionItemBean();
								    JSONObject obj = (JSONObject) arr.get(i);  
								    item.setId(obj.getString("id"));
								    item.setTitle(obj.getString("title"));
								    item.setSummary(obj.getString("abstract"));
								    item.setVoteCount(obj.getString("vote_count"));
								    item.setCreateBy(obj.getString("create_by"));
								    datas.add(item);
								}
								showView();								
							}							
							borderScrollView.loadComplete();
							footerView.setVisibility(View.GONE);
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
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);		
	}
	
	private void clearBox()
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("box_id", mboxId);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/clear_favoritebox").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						datas.clear();
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
	
	private void delFavorite(final int position)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("cid", datas.get(position).getId());
		params.put("box_id", mboxId);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION).append("/del_favorite").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						datas.remove(position);
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

}
