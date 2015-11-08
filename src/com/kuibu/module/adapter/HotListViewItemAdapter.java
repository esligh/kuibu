package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.data.global.Constants;
import com.kuibu.module.activity.R;

public class HotListViewItemAdapter extends CommonAdapter<Map<String,String>> {
	
	public HotListViewItemAdapter(Context context, List<Map<String,String>> datas,int layoutId) {
		super(context,datas,layoutId);
	}
	
	@Override
	public void convert(ViewHolder holder, Map<String, String> item) {
		// TODO Auto-generated method stub
		holder.setTvText(R.id.hot_collect_folder_name,item.get("box_name"));
		holder.setTvText(R.id.hot_collect_folder_desc,item.get("box_desc"));
		holder.setTvText(R.id.hot_collect_folder_owner_name,item.get("user_name"));
		holder.setTvText(R.id.hot_collect_folder_visit_count,item.get("box_count"));
		String url = item.get("user_pic");
		if(TextUtils.isEmpty(url) || url.equals("null")){
			holder.setImageResource(R.id.hot_collect_folder_owner_pic,R.drawable.default_pic_avata);
		}else{
			holder.setImageByUrl(R.id.hot_collect_folder_owner_pic, url, Constants.defaultAvataOptions);
		}
	}
	
}