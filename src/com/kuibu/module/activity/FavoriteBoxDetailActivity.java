package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectionItemBean;
import com.kuibu.module.adapter.FavoriteDetailsAdapter;

public class FavoriteBoxDetailActivity extends BaseActivity implements OnBorderListener{
	private TextView box_name_tv ,box_desc_tv ;
	private TextView focus_count_tv,box_count_tv;
	private ListView box_list ; 
	private View footerView; 
	private BorderScrollView borderScrollView;
	private FavoriteDetailsAdapter adapter ; 
	private List<CollectionItemBean> datas = new ArrayList<CollectionItemBean>();
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				Intent intent = new Intent(FavoriteBoxDetailActivity.this,ShowCollectionActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,datas.get(position).getId());
				intent.putExtra("title", datas.get(position).getTitle());
				intent.putExtra("content",datas.get(position).getContent());
				intent.putExtra("create_by", datas.get(position).getCreateBy());
				intent.putExtra("vote_count", datas.get(position).getVoteCount());
				intent.putExtra("name", datas.get(position).getCreatorName());
				intent.putExtra("photo",datas.get(position).getCreatorPic() );
				intent.putExtra("signature", datas.get(position).getCreatorSignature());
				intent.putExtra("sex", datas.get(position).getCreatorSex());	
				intent.putExtra("comment_count", datas.get(position).getCommentCount());	
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		borderScrollView= (BorderScrollView)findViewById(R.id.favorite_box_detail_scroll_view);
		borderScrollView.setOnBorderListener(this);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		load_data();
	}	
	
	void load_data()
	{
		box_name_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_NAME));
		box_desc_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_DESC));
		box_count_tv.setText("共有"+getIntent().getStringExtra(StaticValue.SERMODLE.BOX_COUNT)+"条收集");
		focus_count_tv.setText(getIntent().getStringExtra(StaticValue.SERMODLE.BOX_FOCUS_COUNT));
		
		String box_id = getIntent().getStringExtra(StaticValue.SERMODLE.BOX_ID);
		Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("box_id", box_id);
		params.put("off", String.valueOf(datas.size()));
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/get_favorite";

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
									CollectionItemBean item = new CollectionItemBean();
								    JSONObject obj = (JSONObject) arr.get(i);
								    item.setTitle(obj.getString("title"));
								    item.setContent(obj.getString("content"));
								    item.setVoteCount(obj.getString("vote_count"));
								    item.setCreateBy(obj.getString("create_by"));
								    item.setCreatorName(obj.getString("name"));
									item.setCreatorPic(obj.getString("photo"));
									item.setCreatorSex(obj.getString("sex"));
									item.setCreatorSignature(obj.getString("signature"));
									item.setCommentCount(obj.getString("comment_count"));
								    datas.add(item);
								}
								showView();								
							}							
							borderScrollView.loadComplete();
							footerView.setVisibility(View.GONE);
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
		// TODO Auto-generated method stub		
		footerView.setVisibility(View.VISIBLE);
		load_data();		
	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();
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
