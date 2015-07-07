package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.FocusItemBean;

public class FocusListViewItemAdapter extends BaseAdapter {
	private List<FocusItemBean> datas;
	private LayoutInflater mInfalter;
	public class HolderView {
		ImageView iv;
		TextView contentTv, dateTv;
	}
	
	public FocusListViewItemAdapter(Context context, List<FocusItemBean> datas) {
		this.datas = datas;
		this.mInfalter = LayoutInflater.from(context);
	}

	public void updateView(List<FocusItemBean> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView;
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInfalter.inflate(R.layout.focus_list_view, parent,false);
			holderView.contentTv = (TextView) convertView
					.findViewById(R.id.content_tv);
			holderView.dateTv = (TextView) convertView
					.findViewById(R.id.date_tv);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.contentTv.setText(datas.get(position).getContent());
		if (datas.get(position).getDateString() == null) {
			holderView.dateTv.setVisibility(View.GONE);
		} else {
			holderView.dateTv.setVisibility(View.VISIBLE);
			holderView.dateTv.setText(datas.get(position).getDateString());
		}
		return convertView;
	}
}



