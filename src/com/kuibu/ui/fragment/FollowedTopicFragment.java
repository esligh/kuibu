package com.kuibu.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.FollowedTopicPresenterImpl;
import com.kuibu.module.presenter.interfaces.FollowedTopicPresenter;
import com.kuibu.ui.activity.TopicInfoActivity;
import com.kuibu.ui.view.interfaces.FollowedTopicView;

public class FollowedTopicFragment extends BaseFragment
	implements FollowedTopicView{

	private PullToRefreshListView topicList;
	private CommonAdapter<TopicItemBean> adapter;
	private MultiStateView mMultiStateView;
	private FollowedTopicPresenter mPresenter ;
	
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
						mPresenter.loadFollowedTopicList();
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
				mPresenter.loadFollowedTopicList();
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
						mPresenter.getDataItem(position-1).getId());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_NAME,
						mPresenter.getDataItem(position-1).getTopic());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_EXTRA,
						mPresenter.getDataItem(position-1).getIntroduce());
				intent.putExtra(StaticValue.TOPICINFO.TOPIC_PIC,
						mPresenter.getDataItem(position-1).getTopicPicUrl());
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		adapter = new CommonAdapter<TopicItemBean>(getActivity(), null,R.layout.focus_topic_list_item){
			@Override
			public void convert(ViewHolder holder, TopicItemBean item) {
				// TODO Auto-generated method stub
				holder.setTvText(R.id.focus_topic_tv,item.getTopic());
				holder.setTvText(R.id.focus_topic_introduce_tv,item.getIntroduce());
				holder.setImageByUrl(R.id.focus_topic_pic_iv, item.getTopicPicUrl());	
			}				
		};
		topicList.setAdapter(adapter);
		mPresenter = new FollowedTopicPresenterImpl(this);
		mPresenter.loadFollowedTopicList();
		return rootView;
	}
		


	@Override
	public void refreshList(List<TopicItemBean> data) {
		// TODO Auto-generated method stub
		adapter.refreshView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		topicList.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}

	@Override
	public Bundle getAuguments() {
		// TODO Auto-generated method stub
		return getArguments();
	}
}
