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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.activity.TopicInfoActivity;
import com.kuibu.module.adapter.FocusTopicItemAdapter;

public class FocusTopicFragment extends Fragment implements OnLoadListener {

	private PaginationListView topicList = null;
	private FocusTopicItemAdapter adapter = null;
	private List<TopicItemBean> mdatas = new ArrayList<TopicItemBean>();
	private MultiStateView mMultiStateView;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pagination_listview,
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
		topicList = (PaginationListView) rootView
				.findViewById(R.id.pagination_lv);
		topicList.setOnLoadListener(this);
		topicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						TopicInfoActivity.class);
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,
						mdatas.get(position).getId());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME,
						mdatas.get(position).getTopic());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA,
						mdatas.get(position).getIntroduce());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC,
						mdatas.get(position).getTopicPicUrl());
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		loadData();
		return rootView;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){			
			
		}else{
			mdatas.clear();
			loadData();
		}
	}
	
	private void loadData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", getArguments().getString("uid"));
		params.put("target", StaticValue.SERMODLE.FOCUS_TARGET_TOPIC);
		params.put("off", String.valueOf(mdatas.size()));
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
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						if(arr.length()>0){
							for (int i = 0; i < arr.length(); i++) {
								JSONObject temp = (JSONObject) arr.get(i);
								TopicItemBean item = new TopicItemBean();
								item.setId(temp.getString("tid"));
								item.setTopic(temp.getString("topic_name"));
								item.setIntroduce(temp.getString("topic_desc"));
								item.setFocusCount(temp.getString("focus_count"));

								item.setTopicPicUrl(temp.getString("topic_pic"));
								mdatas.add(item);
							}
							showView();
						}
						if (mdatas.size() > 0) {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.CONTENT);
						} else {
							mMultiStateView
									.setViewState(MultiStateView.ViewState.EMPTY);
						}
						topicList.loadComplete();
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
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	private void showView() {
		if (adapter == null) {
			adapter = new FocusTopicItemAdapter(getActivity(), mdatas);
			topicList.setAdapter(adapter);
		} else {
			adapter.updateView(mdatas);
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		loadData();
	}
}
