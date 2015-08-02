package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.CollectItemBean;

public class CollectItemAdapter extends BaseAdapter {
	private List<CollectItemBean> datas; 
	private LayoutInflater mInfalter;
	
	public class HolderView {
		public TextView topic;
		public CheckBox ckb ; 
	}
	
	public CollectItemAdapter(Context context, List<CollectItemBean> datas){
		this.datas = datas ; 
		this.mInfalter = LayoutInflater.from(context);
	}
	
	public void updateView(List<CollectItemBean> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}
	
	public void addItem(CollectItemBean c)
	{
		datas.add(c);
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView ; 
		if(convertView == null){
			holderView = new HolderView();
			convertView = mInfalter.inflate(R.layout.collect_list_item, parent);
			holderView.topic =(TextView)convertView.findViewById(R.id.collect_topic_tv);
			holderView.ckb = (CheckBox)convertView.findViewById(R.id.ckb_collect_item);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}				
		holderView.topic.setText(datas.get(position).getTopic());
		
		holderView.ckb.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {				
				Toast.makeText(mInfalter.getContext(), "click", Toast.LENGTH_SHORT).show();
			}			
		});
		
		return convertView;
	}

}
