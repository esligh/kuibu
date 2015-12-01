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
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.UserListAdapter;
import com.kuibu.module.adapter.UserListAdapter.ViewHolder;
import com.kuibu.module.presenter.LetterPresenterImpl;
import com.kuibu.module.presenter.interfaces.LetterPresenter;
//private letter  
import com.kuibu.ui.activity.SendMessageActivity;
import com.kuibu.ui.view.interfaces.LetterView;

public class LetterFragment extends BaseFragment implements LetterView{
	
	private PullToRefreshListView userList;
	private UserListAdapter listAdapter;	
	private MultiStateView mMultiStateView;
	private LetterPresenter mPresenter ; 
	
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
						mPresenter.loadSenderList();
					}
				});
		userList = (PullToRefreshListView) rootView
				.findViewById(R.id.pagination_lv);
		userList.setMode(Mode.PULL_FROM_END);
		userList.setPullToRefreshOverScrollEnabled(false);
		userList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadSenderList();
			}
		});
		userList.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view,
					int position, long id) {								
				Map<String, Object> item = (Map<String, Object>) viewAdapter
						.getAdapter().getItem(position);
				
				Intent intent = new Intent(getActivity(),
						SendMessageActivity.class);
				intent.putExtra("sender_id", (String) item.get("sender_id"));
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
				
				ViewHolder  holder = (ViewHolder)view.getTag();
				holder.badge.hide();
				mPresenter.readLetter((String)item.get("sender_id"));
			}
		});

		listAdapter = new UserListAdapter(getActivity(), null);
		userList.setAdapter(listAdapter);
		
		mPresenter = new LetterPresenterImpl(this);
		mPresenter.loadSenderList();
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}
	
	@Override
	public void refreshList(List<Map<String, Object>> data) {
		// TODO Auto-generated method stub
		listAdapter.refreshView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		userList.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}

}
