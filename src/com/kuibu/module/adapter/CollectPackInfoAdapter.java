package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.CollectionItemBean;

public class CollectPackInfoAdapter extends BaseAdapter {
	private List<CollectionItemBean> mData;
	private final Context context;

	public CollectPackInfoAdapter(Context context, List<CollectionItemBean> items) {
		this.context = context;
		this.mData = items;
	}

	public void updateView(List<CollectionItemBean> items) {
		this.mData = items;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.collectpack_info_list_item, parent,false);
			holder = new ViewHolder();
			holder.content = (TextView) convertView
					.findViewById(R.id.pack_info_collection_content);
			holder.title = (TextView) convertView
					.findViewById(R.id.pack_info_collection_title);
			holder.count = (TextView) convertView
					.findViewById(R.id.pack_info_collection_vote_count);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(mData.get(position).getTitle());
		holder.content.setText(mData.get(position).getContent());
		holder.count.setText(mData.get(position).getVoteCount());
		return convertView;
	}

	private static class ViewHolder {
		private TextView content;
		private TextView title;
		private TextView count;
	}
}
