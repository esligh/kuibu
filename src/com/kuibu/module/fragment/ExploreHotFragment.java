package com.kuibu.module.fragment;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.ACache;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.FavoriteBoxInfoActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HotListViewItemAdapter;

public class ExploreHotFragment extends Fragment implements OnLoadListener {

	private PaginationListView hotList = null;
	private HotListViewItemAdapter hotAdapter = null;
	private List<Map<String, String>> mdatas = new ArrayList<Map<String, String>>();
	private ACache mCache;
	private boolean isCache;
	private MultiStateView mMultiStateView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_pagination_hotcollect, container, false);
		mMultiStateView = (MultiStateView) rootView
				.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR)
				.findViewById(R.id.retry)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mMultiStateView
								.setViewState(MultiStateView.ViewState.LOADING);
					}
				});
		mCache = ACache.get(this.getActivity());
		hotList = (PaginationListView) rootView
				.findViewById(R.id.pagination_lv);
		hotList.setOnLoadListener(this);
		hotList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						FavoriteBoxInfoActivity.class);
				intent.putExtra("box_id", mdatas.get(position).get("box_id"));
				intent.putExtra("create_by", mdatas.get(position).get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		JSONArray arr = mCache
				.getAsJSONArray(StaticValue.LOCALCACHE.HOME_HOT_CACHE);
		if (arr != null) {
			loadFromArray(arr);
		} else {
			loadData();
		}
		showView();
		return rootView;
	}

	private void loadFromArray(JSONArray arr) {
		try {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				Map<String, String> item = new HashMap<String, String>();
				item.put("box_id", temp.getString("box_id"));
				item.put("box_name", temp.getString("box_name"));
				item.put("box_desc", temp.getString("box_desc"));
				item.put("box_count", temp.getString("box_count"));
				item.put("uid", temp.getString("uid"));
				item.put("user_name", temp.getString("name"));
				item.put("user_sex", temp.getString("sex"));
				item.put("user_pic", temp.getString("photo"));
				mdatas.add(item);
			}
			showView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mdatas.size() > 0) {
			mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
		} else {
			mMultiStateView.setViewState(MultiStateView.ViewState.EMPTY);
		}
	}

	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("off", String.valueOf(mdatas.size()));
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/get_hotpacks";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						loadFromArray(arr);
						if (!isCache) {
							mCache.put(StaticValue.LOCALCACHE.HOME_HOT_CACHE,arr);
							isCache = true;
						}
						hotList.loadComplete();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
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

	private void showView() {
		if (hotAdapter == null) {
			hotAdapter = new HotListViewItemAdapter(getActivity(), mdatas);
			hotList.setAdapter(hotAdapter);
		} else {
			hotAdapter.updateView(mdatas);
		}
	}

	@Override
	public void onLoad(String tag) {
		// TODO Auto-generated method stub
		loadData();
	}
}
