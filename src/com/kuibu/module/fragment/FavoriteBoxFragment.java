package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.FavoriteBoxDetailActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.FavoriteBoxCardListAdapter;

public class FavoriteBoxFragment extends Fragment {
	
	private List<Map<String,String>> datas= new ArrayList<Map<String,String>>() ; 
    private ListView cardsList;
    private FavoriteBoxCardListAdapter adapter ; 
    private Context context ; 
    private MultiStateView mMultiStateView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collect_fragment_card_layout, container, false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				loadData();
			}   	
        });
        context = this.getActivity();
        cardsList = (ListView) rootView.findViewById(R.id.cards_list);
        cardsList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String count = datas.get(position).get("box_count");
				if(Integer.parseInt(count)>0){
					Intent intent = new Intent(context,FavoriteBoxDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.BOX_ID, datas.get(position).get("box_id"));
					intent.putExtra(StaticValue.SERMODLE.BOX_NAME,datas.get(position).get("box_name"));
					intent.putExtra(StaticValue.SERMODLE.BOX_DESC,datas.get(position).get("box_desc"));
					intent.putExtra(StaticValue.SERMODLE.BOX_COUNT,datas.get(position).get("box_count"));
					intent.putExtra(StaticValue.SERMODLE.BOX_FOCUS_COUNT, datas.get(position).get("focus_count"));
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			}       	
		});
        cardsList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("操作");
				builder.setItems(getResources().getStringArray(R.array.favorite_box_item_opt),
						new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int pos) {
							switch(pos){
								case 0:
									delFavoriteBox(position);
								break;
							}
						}
				});
				builder.show();
				return false;
			}        	
		});
        loadData(); 
        showView();
        return rootView;
    }
    
    
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){ //hiding now 
			
		}else{//showing now 
			loadData();
		}
	}

    private void showView()
    {
    	if(adapter == null){
    		adapter = new FavoriteBoxCardListAdapter(this.getActivity(), datas);
    		cardsList.setAdapter(adapter);
    	}else{
    		adapter.updateView(datas);
    	}
    }
    
	private void loadFromArray(JSONArray arr)
    {
		try{
			for(int i=0;i<arr.length();i++){
				JSONObject obj = arr.getJSONObject(i);
				Map<String,String> item = new HashMap<String,String>();
				item.put("box_id", obj.getString("box_id"));
				item.put("box_name", obj.getString("box_name"));
				item.put("box_desc", obj.getString("box_desc"));
				item.put("box_count", obj.getString("box_count"));
				item.put("focus_count", obj.getString("focus_count"));
				item.put("title", obj.getString("title"));
				item.put("content", obj.getString("content"));
				datas.add(item);
			}
			showView();    	
    	}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(datas.size()>0){
    		mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
    	}else{
    		mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
    	} 	
    }
    private  void loadData()
    {
    	Map<String,Object> params = new HashMap<String,Object>();
    	String uid = getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
    	if(TextUtils.isEmpty(uid)){
    		Bundle args = getArguments() ;
    		uid = args.getString(StaticValue.USERINFO.USER_ID);
    	}
		params.put("uid",uid);
		params.put("off", String.valueOf(datas.size()));
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/get_boxdetail";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						if(!TextUtils.isEmpty(data)){
							JSONArray arr = new JSONArray(data);							
							if(arr.length()>0){
								loadFromArray(arr);
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
   
    private void delFavoriteBox(final int position)
    {
    	Map<String,String> params = new HashMap<String,String>();
		params.put("uid",Session.getSession().getuId());
		params.put("box_id", datas.get(position).get("box_id"));
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/del_favoritebox";
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
