package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserListAdapter extends BaseAdapter{
	List<Map<String,Object>> datas  ;
	private LayoutInflater mInflater = null;
	public UserListAdapter(Context context, List<Map<String, Object>> datas) {
		this.datas = datas;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void updateView( List<Map<String, Object>> datas)
	{
		this.datas = datas;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub		
		if(datas!=null)
			return datas.size();
		return 0 ; 
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_list_item, parent,false);
			holder = new ViewHolder();

			holder.user_name_tv = (TextView) convertView.findViewById(R.id.user_name_tv);
			holder.user_desc_tv = (TextView) convertView.findViewById(R.id.user_desc_tv);
			holder.user_pic_iv =(ImageView)convertView.findViewById(R.id.user_pic_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.user_name_tv.setText(datas.get(position).get("name").toString());
		holder.user_desc_tv.setText(datas.get(position).get("signature").toString());
		String url = datas.get(position).get("photo").toString();
		if(TextUtils.isEmpty(url) || url.equals("null")){
			if(datas.get(position).get("sex").equals("M")){
				holder.user_pic_iv.setImageResource(R.drawable.default_pic_avatar_male);	
			}else{
				holder.user_pic_iv.setImageResource(R.drawable.default_pic_avatar_female);
			}
		}else{
			ImageLoader.getInstance().displayImage(url, holder.user_pic_iv);			
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView user_name_tv, user_desc_tv;
		ImageView user_pic_iv ; 
	}

}
