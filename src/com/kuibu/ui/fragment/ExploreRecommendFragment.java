package com.kuibu.ui.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.MateListViewItemAdapter;
import com.kuibu.module.presenter.ExploreRecommendPresenterImpl;
import com.kuibu.module.presenter.interfaces.ExploreRecommendPresenter;
import com.kuibu.ui.view.interfaces.ExploreRecommendView;

public class ExploreRecommendFragment extends BaseFragment
		implements ExploreRecommendView{
	
	private PullToRefreshListView recommendList = null;
	private MateListViewItemAdapter recommendAdapter = null;
	private MultiStateView mMultiStateView;
	private ExploreRecommendPresenter mPresenter ; 
	
	@SuppressLint("InflateParams")
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
						mPresenter.loadRecommendList("REQ_NEWDATA");
					}
				});
		recommendList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		recommendList.setVerticalScrollBarEnabled(false);
		recommendList.setMode(Mode.PULL_FROM_END);
		recommendList.setPullToRefreshOverScrollEnabled(false);
		recommendList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadRecommendList("REQ_HISTORY");
				refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.REC_LAST_REFRESH_TIME));
			}
		});
		
		recommendAdapter = new MateListViewItemAdapter(getActivity(),null,true);
		recommendList.setAdapter(recommendAdapter);
		
		mPresenter = new ExploreRecommendPresenterImpl(this);
		
		return rootView;
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		mPresenter.loadFromLocal();
		mPresenter.loadRecommendList("REQ_NEWDATA");		
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){			
			
		}else{
			mPresenter.loadRecommendList("REQ_NEWDATA");
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void refreshList(List<MateListItem> data) {
		// TODO Auto-generated method stub
		recommendAdapter.updateView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		recommendList.onRefreshComplete();	
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}

}
