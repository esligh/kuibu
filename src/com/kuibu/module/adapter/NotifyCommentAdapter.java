package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.module.activity.R;

public class NotifyCommentAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String,String>> datas;
	
	public NotifyCommentAdapter(Context context,List<Map<String,String>> datas){
		this.context = context  ;
		this.datas = datas ; 
	}
	
	public void updateView(List<Map<String,String>> datas)
	{
		this.datas = datas ; 
		this.notifyDataSetChanged();
	}
		
	@Override
	public int getCount() {
		if(datas == null)		
			return 0;
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		// TODO Auto-generated method stub
		HolderView holder = null;
		if(convertView == null){
			holder = new HolderView();
			convertView  = LayoutInflater.from(context).inflate(R.layout.notifycomment_list_item, 
					container,false);
			holder.top_left_tv = (TextView)convertView.findViewById(R.id.top_text_left_tv);
			holder.top_right_tv = (TextView)convertView.findViewById(R.id.top_text_Right_tv);
			holder.title_tv = (TextView)convertView.findViewById(R.id.title_tv);
			holder.content_tv = (TextView)convertView.findViewById(R.id.content_tv);
			convertView.setTag(holder);
		}else{
			holder = (HolderView)convertView.getTag();
		}
		holder.top_left_tv.setText(datas.get(position).get("name"));
		holder.top_right_tv.setText(datas.get(position).get("desc"));
		holder.title_tv.setText(datas.get(position).get("title"));
		holder.content_tv.setText(datas.get(position).get("content"));
		return convertView;
	}
	
	private class HolderView
	{
		TextView top_left_tv,top_right_tv;
		TextView title_tv,content_tv;
	}
}
