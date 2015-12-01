package com.kuibu.ui.fragment;

import java.util.List;
import java.util.Map;

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
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HotListViewItemAdapter;
import com.kuibu.module.presenter.ExploreFavoritePresenterImpl;
import com.kuibu.module.presenter.interfaces.ExploreFavoritePresenter;
import com.kuibu.ui.activity.FavoriteBoxInfoActivity;
import com.kuibu.ui.view.interfaces.ExploreFavoriteView;

public class ExploreFavoriteFragment extends BaseFragment implements ExploreFavoriteView{
	
	private PullToRefreshListView hotList;
	private HotListViewItemAdapter hotAdapter;
	private MultiStateView mMultiStateView;
	private ExploreFavoritePresenter mPresenter ;
	
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
						mPresenter.loadFavoriteList("REQ_NEWDATA");
					}
				});

		hotList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		hotList.setMode(Mode.PULL_FROM_END);
		hotList.setPullToRefreshOverScrollEnabled(false);
		hotList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadFavoriteList("REQ_HISTORY");
				refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.FAV_LAST_REFRESH_TIME));
			}
		});
		
		hotList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						FavoriteBoxInfoActivity.class);
				intent.putExtra("box_id", mPresenter.getDataItem(position-1).get("box_id"));
				intent.putExtra("box_type", mPresenter.getDataItem(position-1).get("box_type"));
				intent.putExtra("create_by", mPresenter.getDataItem(position-1).get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
			}
		});

		hotAdapter = new HotListViewItemAdapter(getActivity(), null,
				R.layout.hot_collect_list_item);
		hotList.setAdapter(hotAdapter);
		mPresenter = new ExploreFavoritePresenterImpl(this);		
		mPresenter.loadFavoriteList("REQ_NEWDATA");	
		return rootView;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){			
			
		}else{
			mPresenter.loadFavoriteList("REQ_NEWDATA");
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
		
	@Override
	public void refreshList(List<Map<String, String>> data) {
		// TODO Auto-generated method stub
		hotAdapter.refreshView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		hotList.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}
}
