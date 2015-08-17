package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.model.bean.CollectionItemBean;
import com.kuibu.module.activity.R;

public class FavoriteDetailsAdapter extends BaseAdapter {
	private List<CollectionItemBean> datas ; 
	private final Context context;
	public FavoriteDetailsAdapter(Context context, List<CollectionItemBean> items) {
        this.context = context;
        this.datas = items;
    }
	
	public void updateView(List<CollectionItemBean> items)
	{
		this.datas = items ;
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.detail_list_item_card, parent,false);
			holder = new ViewHolder();
			holder.favorite_title_tv = (TextView) convertView
					.findViewById(R.id.list_item_favorite_title);
			holder.favorite_content_tv = (TextView) convertView
					.findViewById(R.id.list_item_favorite_content);
			holder.favorite_follow_count_tv = (TextView) convertView
					.findViewById(R.id.list_item_favorite_count);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.favorite_title_tv.setText(datas.get(position).getTitle());
		holder.favorite_content_tv.setText(datas.get(position).getSummary());
		holder.favorite_follow_count_tv.setText(datas.get(position).getVoteCount());
		return convertView;
	}
	
	private class ViewHolder
	{
		private TextView favorite_title_tv;
		private TextView favorite_content_tv;
		private TextView favorite_follow_count_tv;
	}

}
