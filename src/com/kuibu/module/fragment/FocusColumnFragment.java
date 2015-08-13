package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuibu.custom.widget.PaginationListView;
import com.kuibu.custom.widget.PaginationListView.OnLoadListener;
import com.kuibu.model.bean.FocusColumnItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.FocusColumnItemAdapter;

public class FocusColumnFragment extends Fragment implements OnLoadListener {
	private PaginationListView paginationLv = null;
	private FocusColumnItemAdapter fuAdapter = null;
	private List<FocusColumnItemBean> fu_datas = new ArrayList<FocusColumnItemBean>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_focus_listview,
				container, false);
		paginationLv = (PaginationListView) rootView
				.findViewById(R.id.focous_listview);
		paginationLv.setOnLoadListener(this);
		initFocusUData();
		showFoucsUView();
		paginationLv.setTag("focus_column");
		return rootView;
	}

	private void initFocusUData() {
		
	}

	private void showFoucsUView() {
		if (fuAdapter == null) {
			fuAdapter = new FocusColumnItemAdapter(getActivity(), fu_datas);
			paginationLv.setAdapter(fuAdapter);
		} else {
			fuAdapter.updateView(fu_datas);
		}
	}

	@Override
	public void onLoad(String tag) {
		// TODO Auto-generated method stub

	}

}
