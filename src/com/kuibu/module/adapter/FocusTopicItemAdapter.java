package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.kuibu.common.utils.ImageUtil;
import com.kuibu.model.bean.TopicItemBean;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FocusTopicItemAdapter extends BaseAdapter{

	private List<TopicItemBean> datas;
	private Context  context;
	
	public FocusTopicItemAdapter(Context context,List<TopicItemBean> datas){
		this.context = context ; 
		this.datas = datas ; 
	}

	public void updateView(List<TopicItemBean> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return datas.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView; 
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(context).inflate(R.layout.focus_topic_list_item,
					parent,false);
			holderView.topic_tv = (TextView)convertView.findViewById(R.id.focus_topic_tv);
			holderView.topicPic_iv = (ImageView)convertView.findViewById(R.id.focus_topic_pic_iv);
			holderView.introduce_tv = (TextView)convertView.findViewById(R.id.focus_topic_introduce_tv);
			convertView.setTag(holderView);
		}else{
			holderView=(HolderView)convertView.getTag();
		}
		holderView.topic_tv.setText(datas.get(position).getTopic());
		holderView.introduce_tv.setText(datas.get(position).getIntroduce());
		ImageLoader.getInstance().displayImage(datas.get(position).getTopicPicUrl(), holderView.topicPic_iv);
		return convertView;
	}
	
	private class HolderView{
		TextView topic_tv; 
		TextView introduce_tv;
		ImageView topicPic_iv;
	}
}
