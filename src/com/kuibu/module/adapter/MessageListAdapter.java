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

import com.kuibu.model.entity.MessageItemBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageListAdapter extends BaseAdapter
{
	private List<MessageItemBean> datas  ; 
	private Context context ; 
	
	public MessageListAdapter(Context context,List<MessageItemBean> datas)
	{
		this.context = context ;
		this.datas = datas ; 
	}
	
	public void updateView(List<MessageItemBean> datas)
	{
		this.datas = datas ; 
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(datas ==null)
			return 0;
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
		HolderView holder = null;
		if(convertView == null){
			holder = new HolderView();
			convertView = LayoutInflater.from(context).
					inflate(R.layout.message_list_item, container, false);
			holder.userImg = (ImageView)convertView.findViewById(R.id.user_photo_iv);
			holder.userNameIv  = (TextView)convertView.findViewById(R.id.user_name_tv);
			holder.msgIv = (TextView)convertView.findViewById(R.id.message_tv);
			holder.dateIv = (TextView)convertView.findViewById(R.id.date_tv);
			convertView.setTag(holder);
		}else{
			holder = (HolderView)convertView.getTag();
		}
		String photo = datas.get(position).getCreatorPic();
		if(TextUtils.isEmpty(photo) || photo.equals("null")){
			holder.userImg.setImageResource(R.drawable.default_pic_avata);
		}else{
			ImageLoader.getInstance().displayImage(photo, holder.userImg);
		}		
		holder.userNameIv.setText(datas.get(position).getCreatorName());
		holder.msgIv.setText(datas.get(position).getMessage());
		holder.dateIv.setText(datas.get(position).getCreateTime());
		return convertView;
	}
	
	private class HolderView{
		public ImageView userImg;
		public TextView  userNameIv,msgIv,dateIv;		
	}

}
