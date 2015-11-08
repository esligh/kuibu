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

import com.kuibu.custom.widget.BadgeView;
import com.kuibu.data.global.Constants;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserListAdapter extends BaseAdapter{
	List<Map<String,Object>> datas  ;
	private Context mContext;
	
	public UserListAdapter(Context context, List<Map<String, Object>> datas) {
		this.datas = datas;
		this.mContext = context ; 
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent,false);
			holder = new ViewHolder();

			holder.user_name_tv = (TextView) convertView.findViewById(R.id.user_name_tv);
			holder.user_desc_tv = (TextView) convertView.findViewById(R.id.user_desc_tv);
			holder.user_pic_iv =(ImageView)convertView.findViewById(R.id.user_pic_iv);
			holder.badge = new BadgeView(mContext, (View)(holder.user_pic_iv));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.user_name_tv.setText(datas.get(position).get("name").toString());		
		String signature = (String)datas.get(position).get("signature"); 
		if(TextUtils.isEmpty(signature)){
			holder.user_desc_tv.setText("");
		}else{
			holder.user_desc_tv.setText(signature);
		}
		
		String url = datas.get(position).get("photo").toString();
		if(TextUtils.isEmpty(url) || url.equals("null")){
				holder.user_pic_iv.setImageResource(R.drawable.default_pic_avata);	
		}else{
			ImageLoader.getInstance().displayImage(url, holder.user_pic_iv,Constants.defaultAvataOptions);			
		}
		if(datas.get(position).get("msg_count")!=null && 
				(Integer)datas.get(position).get("msg_count")>0){			 
			holder.badge.setBackgroundResource(R.drawable.design_red_point);			
			holder.badge.setText("...");
			holder.badge.setBadgeMargin(0);
			holder.badge.show();
		}
		return convertView;
	}
	
	public class ViewHolder {
		public TextView user_name_tv, user_desc_tv;
		public ImageView user_pic_iv ;	
		public BadgeView badge ;
	}
}
