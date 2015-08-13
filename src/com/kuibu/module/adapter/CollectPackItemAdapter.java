package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.module.activity.R;

public class CollectPackItemAdapter extends BaseAdapter {

	private Context  context; 
	private List<CollectPackBean> mData ; 
	public CollectPackItemAdapter(Context context,List<CollectPackBean> data)
	{
		this.context = context;
		mData = data ; 
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public void updateView(List<CollectPackBean> data)
	{
		mData = data ; 
		notifyDataSetChanged(); 
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		// TODO Auto-generated method stub
		HolderView holderView = null ;
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(context).inflate(R.layout.collectpack_list_item, container,false);
			holderView.packname_tv = (TextView)convertView.findViewById(R.id.pack_name_tv);
			holderView.packdesc_tv = (TextView)convertView.findViewById(R.id.pack_extra_tv);
			holderView.count_tv = (TextView)convertView.findViewById(R.id.file_count_tv);
			convertView.setTag(holderView);
		}else{
			holderView = (HolderView)convertView.getTag();
		}
		holderView.packname_tv.setText(mData.get(position).getPack_name());
		holderView.packdesc_tv.setText(mData.get(position).getPack_desc());
		String count = mData.get(position).getCollect_count();
		if(TextUtils.isEmpty(count))
			holderView.count_tv.setText("0条收集");
		else
			holderView.count_tv.setText(count+"条收集");
		return convertView;
	}
	
	private class HolderView
	{
		TextView packname_tv ; 
		TextView count_tv;  
		TextView packdesc_tv ;
	}
}
