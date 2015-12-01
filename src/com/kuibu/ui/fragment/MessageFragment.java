package com.kuibu.ui.fragment;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.MessagePresenterImpl;
import com.kuibu.module.presenter.interfaces.MessagePresenter;
import com.kuibu.ui.activity.CollectionDetailActivity;
import com.kuibu.ui.activity.CollectionImageDetailActivity;
import com.kuibu.ui.view.interfaces.MessageView;

public class MessageFragment extends BaseFragment implements MessageView{
	
	private PullToRefreshListView listView;
	private CommonAdapter<Map<String,String>> adapter; 
	private MultiStateView mMultiStateView;	
	private MessagePresenter mPresenter ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_pullrefresh_listview,container, false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadMessageList();
			}   	
        });
		listView = (PullToRefreshListView) rootView.findViewById(R.id.pagination_lv);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setPullToRefreshOverScrollEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.loadMessageList();
				String label = DateUtils.formatDateTime(getActivity(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpaterView, View view, int position,
					long id) {
				@SuppressWarnings("unchecked")
				Map<String,String> item = (Map<String,String>)adpaterView.getAdapter().getItem(position);
				Intent intent = new Intent();
				String type = item.get("type");
				if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
					intent.setClass(getActivity(), CollectionImageDetailActivity.class);
				}else{
					intent.setClass(getActivity(), CollectionDetailActivity.class);
				}
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,item.get("cid"));
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		adapter =new CommonAdapter<Map<String,String>>(this.getActivity(),null,R.layout.notifycomment_list_item){

			@Override
			public void convert(ViewHolder holder, Map<String,String> item) {
				// TODO Auto-generated method stub
				holder.setTvText(R.id.top_text_left_tv,item.get("name"));
				holder.setTvText(R.id.top_text_Right_tv,item.get("desc"));
				holder.setTvText(R.id.title_tv,item.get("title"));
				holder.setTvText(R.id.content_tv,item.get("abstract"));
			}				
		};
		listView.setAdapter(adapter);
		mPresenter = new MessagePresenterImpl(this);
		mPresenter.loadMessageList();
		return rootView; 
	}
		
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void refreshList(List<Map<String, String>> data) {
		// TODO Auto-generated method stub
		adapter.refreshView(data);
	}

	@Override
	public void stopRefresh() {
		// TODO Auto-generated method stub
		listView.onRefreshComplete();
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}
	
}
