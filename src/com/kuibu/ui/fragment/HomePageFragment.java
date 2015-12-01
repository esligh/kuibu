package com.kuibu.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.HomeListViewAdapter;
import com.kuibu.module.presenter.HomePagePresenterImpl;
import com.kuibu.module.presenter.interfaces.HomePagePresenter;
import com.kuibu.ui.view.interfaces.HomePageView;

/**
 * homepage 
 * @author ThinkPad
 */
public class HomePageFragment extends Fragment implements HomePageView{	
	private HomeListViewAdapter mAdapter;  
    private PullToRefreshListView mPullRefreshListView;
    private MultiStateView mMultiStateView;
    private HomePagePresenter mPresenter ; 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_homepage, container,false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadNetData("INIT",true);				
			}
        });
        mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pullToRefreshListView);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setPullToRefreshOverScrollEnabled(false);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub			
				mPresenter.loadNetData("DOWN",true);
				mPullRefreshListView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.HOME_LAST_REFRESH_TIME));			
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadNetData("UP",false);
				refreshView.getLoadingLayoutProxy()
				.setLastUpdatedLabel(KuibuUtils.getRefreshLabel(getActivity(),
						StaticValue.PrefKey.HOME_LAST_REFRESH_TIME));
			}			
		});
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				mPresenter.loadNetData("UP",false);
				mPullRefreshListView.setRefreshingFooter();
			}
		});				
		return rootView;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mPresenter = new HomePagePresenterImpl(this);
		if(!mPresenter.loadLocalData()){
			mPresenter.loadNetData("INIT", true);
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		KuibuApplication.getInstance().cancelPendingRequests(this);
	}

	@Override
	public void refreshListView(List<MateListItem> data) {
		// TODO Auto-generated method stub
		if (mAdapter == null) {
			mAdapter = new HomeListViewAdapter(getActivity(),data);
			mPullRefreshListView.setAdapter(mAdapter);
		} else {
			mAdapter.updateView(data);
		}
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		mPullRefreshListView.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}
				
}
