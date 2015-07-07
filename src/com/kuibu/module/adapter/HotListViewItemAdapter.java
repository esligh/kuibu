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

public class HotListViewItemAdapter extends BaseAdapter {
	private List<Map<String,String>> datas;
	private Context context;

	public class HolderView {
		TextView  pack_name_tv;
		TextView  pack_desc_tv;
		ImageView owner_pic_iv;
		TextView  owner_name_tv;
		TextView  collect_count_tv;
	}

	public HotListViewItemAdapter(Context context, List<Map<String,String>> datas) {
		this.datas = datas;
		this.context = context;
	}

	public void updateView(List<Map<String,String>> datas) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.hot_collect_list_item,
					parent,false);
			holderView.pack_name_tv = (TextView) convertView
					.findViewById(R.id.hot_collect_folder_name);
			holderView.pack_desc_tv = (TextView) convertView
					.findViewById(R.id.hot_collect_folder_desc);
			holderView.owner_name_tv = (TextView) convertView.findViewById(R.id.hot_collect_folder_owner_name);
			holderView.owner_pic_iv = (ImageView) convertView
					.findViewById(R.id.hot_collect_folder_owner_pic);
			holderView.collect_count_tv =(TextView) convertView.findViewById(R.id.hot_collect_folder_visit_count);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.pack_name_tv.setText(datas.get(position).get("box_name"));
		holderView.pack_desc_tv.setText(datas.get(position).get("box_desc"));
		holderView.owner_name_tv.setText(datas.get(position).get("user_name"));
		holderView.collect_count_tv.setText(datas.get(position).get("box_count"));
		String url = datas.get(position).get("user_pic");
		if(TextUtils.isEmpty(url) || url.equals("null")){
			if(datas.get(position).get("user_sex").equals("M")){
				holderView.owner_pic_iv.setImageResource(R.drawable.default_pic_avatar_male);
			}else{
				holderView.owner_pic_iv.setImageResource(R.drawable.default_pic_avatar_female);
			}
		}else{
			ImageLoader.getInstance().displayImage(url, holderView.owner_pic_iv);
		}
		return convertView;
	}
}