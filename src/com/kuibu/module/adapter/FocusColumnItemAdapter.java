package com.kuibu.module.adapter;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.model.bean.FocusColumnItemBean;
import com.kuibu.module.activity.R;

public class FocusColumnItemAdapter extends BaseAdapter{

	private List<FocusColumnItemBean> datas;
	private LayoutInflater mInfalter;
	
	public FocusColumnItemAdapter(Context context,List<FocusColumnItemBean> datas){
		mInfalter = LayoutInflater.from(context);
		this.datas = datas ; 
	}

	public void updateView(List<FocusColumnItemBean> datas) {
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
			convertView = mInfalter.inflate(R.layout.focus_column_list_item,
					parent,false);
			holderView.column_tv = (TextView)convertView.findViewById(R.id.focus_column_name);
			holderView.introduce_tv = (TextView)convertView.findViewById(R.id.focus_column_introduce);
			holderView.count_tv =(TextView)convertView.findViewById(R.id.focus_column_ccount);
			convertView.setTag(holderView);
		}else{
			holderView=(HolderView)convertView.getTag();
		}
		holderView.column_tv.setText(datas.get(position).getColumn_name());
		holderView.introduce_tv.setText(datas.get(position).getColumn_introduce());
		holderView.count_tv.setText(datas.get(position).getCollect_count());
		return convertView;
	}
	
	private class HolderView{
		TextView column_tv; 
		TextView introduce_tv;
		TextView count_tv;
	}

}
