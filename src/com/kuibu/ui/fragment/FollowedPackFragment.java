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
import com.kuibu.common.utils.DataUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.model.entity.CollectPackItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.FollowedPackPresenterImpl;
import com.kuibu.module.presenter.interfaces.FollowedPackPresenter;
import com.kuibu.ui.activity.AlbumInfoActivity;
import com.kuibu.ui.view.interfaces.FollowedPackView;

public class FollowedPackFragment extends BaseFragment implements FollowedPackView{

	private PullToRefreshListView packList = null;
	private CommonAdapter<CollectPackItemBean> adapter = null;
	private MultiStateView mMultiStateView;
	private FollowedPackPresenter mPresenter ; 
	
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
						mPresenter.loadFollowedPackList();
					}
				});
		packList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		packList.setMode(Mode.PULL_FROM_END);
		packList.setPullToRefreshOverScrollEnabled(false);
		packList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadFollowedPackList();
			}
		});
		packList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						AlbumInfoActivity.class);
				intent.putExtra("pack_id", mPresenter.getDataItem(position-1).getId());
				intent.putExtra("type", mPresenter.getDataItem(position-1).getPackType());
				intent.putExtra("create_by", mPresenter.getDataItem(position-1).getCreateBy());
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});
		adapter = new CommonAdapter<CollectPackItemBean>(getActivity(), null,
				R.layout.focus_collect_list_item) {
					@Override
					public void convert(ViewHolder holder,
							CollectPackItemBean item) {
						// TODO Auto-generated method stub
						holder.setTvText(R.id.focus_collect_name_tv,item.getPackName());
						holder.setTvText(R.id.focus_collect_desc_tv,item.getPackDesc());
						holder.setTvText(R.id.collect_follow_count_tv,
								DataUtils.formatNumber(Integer.parseInt(item.getFollowCount()))+"人关注");
						holder.setTvText(R.id.collect__count_tv,
								DataUtils.formatNumber(Integer.parseInt(item.getCollectCount()))+"个收集");
					}			
		};
		packList.setAdapter(adapter);
		mPresenter = new FollowedPackPresenterImpl(this);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mPresenter.loadFollowedPackList();
	}

	@Override
	public void refreshList(List<CollectPackItemBean> data) {
		// TODO Auto-generated method stub
		adapter.refreshView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		packList.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}
	
}
