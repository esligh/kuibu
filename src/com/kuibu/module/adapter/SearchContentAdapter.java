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

public class SearchContentAdapter extends BaseAdapter
{
	private List<Map<String,String>> datas ; 
	private Context context ; 
	
	public SearchContentAdapter(Context context,List<Map<String,String>> datas)
	{
		this.datas = datas ; 
		this.context = context ; 
	}
	
	public void updateView(List<Map<String,String>> datas)
	{
		this.datas = datas ; 
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(datas == null)
			return 0 ; 
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
	public View getView(int position, View convertView, ViewGroup container) {
		// TODO Auto-generated method stub
		
		HolderView holderView = null; 
		if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.search_content_list_item, container,false);  
		    holderView = new HolderView();
			holderView.item_title_tv = (TextView)convertView.findViewById(R.id.search_content_item_title);
			holderView.item_sub1_tv = (TextView)convertView.findViewById(R.id.search_content_item_sub1);
			holderView.item_sub2_tv =(TextView)convertView.findViewById(R.id.search_content_item_sub2);
			convertView.setTag(holderView);
		}else{
			holderView = (HolderView)convertView.getTag();
		}		
		if(datas.get(position).get("tag").equals("collectpack")){
			holderView.item_title_tv.setText(datas.get(position).get("item_title"));
			holderView.item_sub1_tv.setText(datas.get(position).get("item_count_1")+"条收集");
			holderView.item_sub2_tv.setText(datas.get(position).get("item_count_2")+"人关注");			
		}else if(datas.get(position).get("tag").equals("collection")){
			holderView.item_title_tv.setText(datas.get(position).get("item_title"));
			holderView.item_sub1_tv.setText(datas.get(position).get("item_count_1")+"人赞");
			holderView.item_sub2_tv.setText("");
		}
		return convertView;
	}
	
	private class HolderView 
	{
		TextView item_title_tv; 
		TextView item_sub1_tv,item_sub2_tv; 
	}
}
