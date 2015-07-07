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

public class UserInfoListAdapter extends BaseAdapter {
	private List<Map<String, String>> datas = null;
	private LayoutInflater mInflater = null;
	public class ViewHolder {
		TextView text, num;
	}

	public UserInfoListAdapter(Context context, List<Map<String, String>> datas) {
		this.datas = datas;
		this.mInflater = LayoutInflater.from(context);
	}
	
	public void updateView(List<Map<String, String>> datas)
	{
		this.datas = datas ; 
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
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.userinfo_home_listitem, parent,false);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.userinfo_list_text);
			holder.num = (TextView) convertView.findViewById(R.id.userinfo_list_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(datas.get(position).get("UserInfoText").toString());
		holder.num.setText(datas.get(position).get("UserInfoNum").toString());
		return convertView;
	}

}
