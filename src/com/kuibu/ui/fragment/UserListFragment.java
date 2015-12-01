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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.UserListAdapter;
import com.kuibu.ui.activity.UserInfoActivity;

public class UserListFragment extends Fragment {
	
	private PullToRefreshListView userList;
	private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	private UserListAdapter authorAdapter;
	private Map<String, String> mParams;
	private MultiStateView mMultiStateView;

	public UserListFragment(Map<String, String> p) {
		this.mParams = p;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.user_listview_activity,
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
				.findViewById(R.id.user_list_view);
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
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String, Object> item = datas.get(position-1);
				Intent intent = new Intent(getActivity(),
						UserInfoActivity.class);
				intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
				intent.putExtra(StaticValue.USERINFO.USER_ID,
						(String) item.get("uid"));
				intent.putExtra(StaticValue.USERINFO.USER_SEX,
						(String) item.get("sex"));
				intent.putExtra(StaticValue.USERINFO.USER_NAME,
						(String) item.get("name"));
				intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
						(String) item.get("signature"));
				intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
						(String) item.get("photo"));
				startActivity(intent);
			}
		});
		loadData();
		return rootView;
	}

	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", mParams.get("uid"));
		params.put("off", String.valueOf(datas.size()));
		params.put("target", StaticValue.SERMODLE.FOCUS_TARGET_COLLECTOR);
		params.put("follow_who", mParams.get("follow_who"));
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_focuslist").toString();

		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONArray arr = new JSONArray(response
								.getString("result"));

						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = (JSONObject) arr.get(i);
							Map<String, Object> item = new HashMap<String, Object>();
							item.put("uid", obj.getString("id"));
							item.put("sex", obj.getString("sex"));
							item.put("name", obj.getString("name"));
							item.put("signature", obj.getString("signature"));
							item.put("photo", obj.getString("photo"));
							datas.add(item);
						}
						showView();
						if (arr.length() > 0) {
							mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
						} else {
							Toast.makeText(getActivity(),getActivity().getString(R.string.nomore_data),
									Toast.LENGTH_SHORT).show();
						}
					}
					userList.onRefreshComplete();
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

	private void showView() {
		if (authorAdapter == null) {
			authorAdapter = new UserListAdapter(this.getActivity(), datas);
			userList.setAdapter(authorAdapter);
		} else {
			authorAdapter.refreshView(datas);
		}
	}
}
