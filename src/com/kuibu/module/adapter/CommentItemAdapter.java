package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.CommentItemBean;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentItemAdapter extends BaseAdapter {
	private List<CommentItemBean> datas; 
	private Context mContext; 
	
	private class HolderView {
		ImageView user_photo_iv;
		TextView user_name_tv;
		TextView content_tv;
		TextView date_tv ;
		TextView voteCount_tv ; 
	}
	
	public CommentItemAdapter(Context context, List<CommentItemBean> datas){
		this.datas = datas ; 
		this.mContext = context;
	}
	
	public void updateView(List<CommentItemBean> datas) {
		this.datas = datas;
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
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView ; 
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_list_item, parent,false);
			holderView.user_photo_iv =(ImageView)convertView.findViewById(R.id.user_photo_iv);
			holderView.user_name_tv  = (TextView)convertView.findViewById(R.id.user_name_tv);
			holderView.content_tv = (TextView)convertView.findViewById(R.id.content_tv);
			holderView.date_tv = (TextView)convertView.findViewById(R.id.date_tv);
			holderView.voteCount_tv = (TextView)convertView.findViewById(R.id.vote_count_tv);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		holderView.user_name_tv.setText(datas.get(position).getUserName());
		holderView.content_tv.setText(datas.get(position).getContent());
		holderView.date_tv.setText(datas.get(position).getGenDate());
		holderView.voteCount_tv.setText(datas.get(position).getVoteCount());
		
		String url = datas.get(position).getUserPicUrl();
		if(TextUtils.isEmpty(url) || url.equals("null")){
			if(datas.get(position).getUserSex().equals("M")){
				holderView.user_photo_iv.setImageResource(R.drawable.default_pic_avatar_male);
			}else{
				holderView.user_photo_iv.setImageResource(R.drawable.default_pic_avatar_female);
			}				
		}else{
			ImageLoader.getInstance().displayImage(url, holderView.user_photo_iv);
		}		
		return convertView;
	}
}
