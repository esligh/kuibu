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
import android.text.format.DateUtils;
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
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.ui.activity.TopicInfoActivity;

public class FocusTopicFragment extends Fragment {

	private PullToRefreshListView topicList = null;
	private CommonAdapter<TopicItemBean> adapter = null;
	private List<TopicItemBean> mdatas = new ArrayList<TopicItemBean>();
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
		topicList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		topicList.setMode(Mode.PULL_FROM_END);
		topicList.setPullToRefreshOverScrollEnabled(false);
		topicList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData();
				String label = DateUtils.formatDateTime(getActivity(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
			}
		});
		topicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						TopicInfoActivity.class);
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_ID,
						mdatas.get(position-1).getId());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME,
						mdatas.get(position-1).getTopic());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA,
						mdatas.get(position-1).getIntroduce());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC,
						mdatas.get(position-1).getTopicPicUrl());
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
						topicList.onRefreshComplete();
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
			adapter = new CommonAdapter<TopicItemBean>(getActivity(), mdatas,R.layout.focus_topic_list_item){

				@Override
				public void convert(ViewHolder holder, TopicItemBean item) {
					// TODO Auto-generated method stub

					holder.setTvText(R.id.focus_topic_tv,item.getTopic());
					holder.setTvText(R.id.focus_topic_introduce_tv,item.getIntroduce());
					holder.setImageByUrl(R.id.focus_topic_pic_iv, item.getTopicPicUrl());	
				}
				
			};
			topicList.setAdapter(adapter);
		} else {
			adapter.refreshView(mdatas);
		}
	}

}
