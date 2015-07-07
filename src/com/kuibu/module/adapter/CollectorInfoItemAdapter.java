package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.CollectorInfoItemBean;

public class CollectorInfoItemAdapter extends BaseAdapter {

	private List<CollectorInfoItemBean> datas;
	private Context mContext;
	
	public CollectorInfoItemAdapter(Context context,List<CollectorInfoItemBean> datas){
		this.mContext = context;
		this.datas = datas ; 
	}

	public void updateView(List<CollectorInfoItemBean> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return datas.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView; 
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.collector_info_list_item,
					parent,false);
			holderView.name_tv = (TextView)convertView.findViewById(R.id.collector_name_tv);
			holderView.introduce_tv = (TextView)convertView.findViewById(R.id.collector_introduce_tv);
			convertView.setTag(holderView);
		}else{
			holderView=(HolderView)convertView.getTag();
		}
		holderView.name_tv.setText(datas.get(position).getName());
		holderView.introduce_tv.setText(datas.get(position).getIntroduce());
		return convertView;
	}
	
	private class HolderView{
		TextView name_tv; 
		TextView introduce_tv;
	}
}
