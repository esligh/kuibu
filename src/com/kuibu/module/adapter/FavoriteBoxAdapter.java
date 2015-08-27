package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kuibu.module.activity.R;

public class FavoriteBoxAdapter extends BaseAdapter{
	
	private  List<Map<String,String>> datas ; 
	private  Context context ;  
	private  List<String> selIds ; 
	
	public FavoriteBoxAdapter(Context context, List<Map<String,String>> datas,List<String> selIds)
	{
		this.context = context ; 
		this.datas = datas ; 
		this.selIds = selIds;
	}
	
	public void updateView(List<Map<String,String>> datas,List<String> selIds)
	{
		this.datas =datas ; 
		this.selIds = selIds; 
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(datas != null )
			return datas.size();
		return  0 ;
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
		HolderView holder ; 
		if(convertView == null){
			holder = new HolderView();
			convertView  = LayoutInflater.from(context).inflate(R.layout.favorite_box_list_item, parent,false);
			holder.box_name_tv = (TextView) convertView.findViewById(R.id.favorite_box_name_tv);
			holder.box_cb = (CheckBox)convertView.findViewById(R.id.favorite_box_cb);
			convertView.setTag(holder);
		}else{
			holder = (HolderView)convertView.getTag();
		}		
		holder.box_name_tv.setText((String)datas.get(position).get("box_name"));
		if(selIds!=null && selIds.contains((String)datas.get(position).get("box_id"))){
			holder.box_cb.setChecked(true);
		}else{
			holder.box_cb.setChecked(false);
		}
		return convertView;
	}

	public class HolderView
	{
		public TextView box_name_tv ; 
		public CheckBox box_cb ; 
	}
}
