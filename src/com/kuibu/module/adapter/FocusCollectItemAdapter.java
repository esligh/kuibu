package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.model.entity.CollectPackItemBean;
import com.kuibu.module.activity.R;

public class FocusCollectItemAdapter extends BaseAdapter{
	private List<CollectPackItemBean> datas;
	private Context context; 
	
	public FocusCollectItemAdapter(Context context,List<CollectPackItemBean> datas){
		this.context = context ; 
		this.datas = datas ; 
	}

	public void updateView(List<CollectPackItemBean> datas) {
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
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(context).inflate(R.layout.focus_collect_list_item,parent,false);
			holderView.collect_pack_name_tv = (TextView)convertView.findViewById(R.id.focus_collect_name_tv);
			holderView.collect_pack_desc_tv = (TextView)convertView.findViewById(R.id.focus_collect_desc_tv);
			holderView.follow_count_tv = (TextView)convertView.findViewById(R.id.collect_follow_count_tv);
			holderView.collect_count_tv = (TextView)convertView.findViewById(R.id.collect__count_tv);
			convertView.setTag(holderView);
		}else{
			holderView=(HolderView)convertView.getTag();
		}
		holderView.collect_pack_name_tv.setText(datas.get(position).getPackName());
		holderView.collect_pack_desc_tv.setText(datas.get(position).getPackDesc());
		holderView.follow_count_tv.setText(datas.get(position).getFollowCount()+"人关注");
		holderView.collect_count_tv.setText(datas.get(position).getCollectCount()+"个收集");
		return convertView;
	}	
	private class HolderView{
		TextView collect_pack_name_tv; 
		TextView collect_pack_desc_tv;
		TextView follow_count_tv ;
		TextView collect_count_tv; 
	}
}
