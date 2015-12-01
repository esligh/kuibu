package com.kuibu.ui.fragment;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.FavoriteBoxCardListAdapter;
import com.kuibu.module.presenter.FavoriteBoxPresenterImpl;
import com.kuibu.module.presenter.interfaces.FavoriteBoxPresenter;
import com.kuibu.ui.activity.FavoriteBoxInfoActivity;
import com.kuibu.ui.view.interfaces.FavoriteBoxView;

public class FavoriteBoxFragment extends Fragment implements FavoriteBoxView{
	
    private ListView boxList;
    private FavoriteBoxCardListAdapter adapter ;  
    private MultiStateView mMultiStateView;
    private FavoriteBoxPresenter mPresenter ; 
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collect_fragment_card_layout, container, false);
		mMultiStateView = (MultiStateView) rootView.findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadBoxList();
			}   	
        });
        boxList = (ListView) rootView.findViewById(R.id.cards_list);
        boxList.setOnItemClickListener(new OnItemClickListener() {        	
			@Override
			public void onItemClick(AdapterView<?> viewAdapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String count = mPresenter.getDataItem(position).get("box_count");
				if(Integer.parseInt(count)>0){
					Intent intent = new Intent(getActivity(),
							FavoriteBoxInfoActivity.class);
					intent.putExtra("box_id", mPresenter.getDataItem(position).get("box_id"));
					intent.putExtra("box_type", mPresenter.getDataItem(position).get("box_type"));
					intent.putExtra("create_by", Session.getSession().getuId());
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			}       	
		});
        boxList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle(getActivity().getString(R.string.operator));
				builder.setItems(getResources().getStringArray(R.array.favorite_box_item_opt),
						new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int pos) {
							switch(pos){
								case 0:
									mPresenter.delBox(position);
								break;
							}
						}
				});
				builder.show();
				return false;
			}        	
		});
		adapter = new FavoriteBoxCardListAdapter(this.getActivity(), null,R.layout.collect_list_item_card);
		boxList.setAdapter(adapter);
		mPresenter = new FavoriteBoxPresenterImpl(this);
		mPresenter.loadBoxList(); 
        return rootView;
    }
    
    
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){ 			 
			mPresenter.loadBoxList();
		}
	}

	
    @Override
	public void onDetach() {
		// TODO Auto-generated method stub
    	KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}

	@Override
	public Intent getDataIntent() {
		// TODO Auto-generated method stub
		return getActivity().getIntent();
	}

	@Override
	public void refreshList(List<Map<String,String>> data) {
		// TODO Auto-generated method stub
		adapter.refreshView(data);
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
	}

	@Override
	public Bundle getDataArguments() {
		// TODO Auto-generated method stub
		return getArguments();
	}
}
