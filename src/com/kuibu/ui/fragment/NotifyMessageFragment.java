package com.kuibu.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.UserListAdapter;
import com.kuibu.module.adapter.UserListAdapter.ViewHolder;
//private letter  
import com.kuibu.ui.activity.SendMessageActivity;

public class NotifyMessageFragment extends Fragment {
	
	private PullToRefreshListView userList = null;
	private UserListAdapter listAdapter;
	private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	private MultiStateView mMultiStateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pullrefresh_listview,
				container, false);
		mMultiStateView = (MultiStateView) rootView
				.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR)
				.findViewById(R.id.retry)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mMultiStateView
								.setViewState(MultiStateView.ViewState.LOADING);
						loadData();
					}
				});
		userList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		userList.setMode(Mode.PULL_FROM_END);
		userList.setPullToRefreshOverScrollEnabled(false);
		userList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData();
			}
		});
		userList.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {								
				Map<String, Object> item = (Map<String, Object>) viewAdapter
						.getAdapter().getItem(position);
				
				Intent intent = new Intent(getActivity(),
						SendMessageActivity.class);
				intent.putExtra("sender_id", (String) item.get("sender_id"));
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
				
				ViewHolder  holder = (ViewHolder)view.getTag();
				holder.badge.hide();
				readMsg((String)item.get("sender_id"));
			}
		});
		loadData();
		return rootView;
	}

	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("off", String.valueOf(datas.size()));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_msgusers").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);

						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = (JSONObject) arr.get(i);
							Map<String, Object> item = new HashMap<String, Object>();
							item.put("sender_id", obj.getString("sender_id"));
							item.put("sex", obj.getString("sex"));
							item.put("name", obj.getString("name"));
							item.put("signature", obj.getString("signature"));
							item.put("photo", obj.getString("photo_url"));
							item.put("msg_count", Integer.valueOf(obj.getInt("msg_count")));
							datas.add(item);
						}
						showView();
						if (datas.size() > 0) {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.CONTENT);
						} else {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.EMPTY);
						}
						userList.onRefreshComplete();
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
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	
	private void showView() {
		if (listAdapter == null) {
			listAdapter = new UserListAdapter(getActivity(), datas);
			userList.setAdapter(listAdapter);
		} else {
			listAdapter.updateView(datas);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		datas.clear();
		datas = null ;
	}
	
	
	private void readMsg(String senderId)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("receiver_id", Session.getSession().getuId());
		params.put("sender_id", senderId);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/update_message";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						
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
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
}
