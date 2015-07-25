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
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectPackItemBean;
import com.kuibu.module.activity.CollectInfoListActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.FocusCollectItemAdapter;

public class FocusCollectPackFragment extends Fragment implements
		OnLoadListener {

	private PaginationListView packList = null;
	private FocusCollectItemAdapter adapter = null;
	private List<CollectPackItemBean> datas = new ArrayList<CollectPackItemBean>();
	private MultiStateView mMultiStateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_focus_listview,
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
		packList = (PaginationListView) rootView
				.findViewById(R.id.focous_listview);
		packList.setOnLoadListener(this);
		packList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						CollectInfoListActivity.class);
				intent.putExtra("pack_id", datas.get(position).getId());
				intent.putExtra("create_by", datas.get(position).getCreateBy());
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		loadData();
		showView();
		packList.setTag("focus_collect");
		return rootView;
	}

	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("target", StaticValue.SERMODLE.FOCUS_TARGET_COLLECTPACK);
		params.put("off", String.valueOf(datas.size()));
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_focuslist";
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
							JSONObject temp = (JSONObject) arr.get(i);
							CollectPackItemBean bean = new CollectPackItemBean();
							bean.setId(temp.getString("pid"));
							bean.setPackName(temp.getString("pack_name"));
							bean.setPackDesc(temp.getString("pack_desc"));
							bean.setFollowCount(temp.getString("follow_count"));
							bean.setCollectCount(temp
									.getString("collect_count"));
							bean.setCreateBy(temp.getString("create_by"));
							datas.add(bean);
						}
						showView();

						if (datas.size() > 0) {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.CONTENT);
						} else {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.EMPTY);
						}
						packList.loadComplete();
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

	private void showView() {
		if (adapter == null) {
			adapter = new FocusCollectItemAdapter(getActivity(), datas);
			packList.setAdapter(adapter);
		} else {
			adapter.updateView(datas);
		}
	}

	@Override
	public void onLoad(String tag) {
		loadData();
	}
}
