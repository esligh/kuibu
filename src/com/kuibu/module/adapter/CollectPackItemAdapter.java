package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.common.utils.DataUtils;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.module.activity.R;

public class CollectPackItemAdapter extends BaseAdapter {

	private Context  context; 
	private List<CollectPackBean> mData ; 
	public CollectPackItemAdapter(Context context,List<CollectPackBean> data)
	{
		this.context = context;
		mData = data ; 
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public void updateView(List<CollectPackBean> data)
	{
		mData = data ; 
		notifyDataSetChanged(); 
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		// TODO Auto-generated method stub
		HolderView holderView = null ;
		if(convertView == null){
			holderView = new HolderView();
			convertView = LayoutInflater.from(context).inflate(R.layout.collectpack_list_item, container,false);
			holderView.packname_tv = (TextView)convertView.findViewById(R.id.pack_name_tv);
			holderView.packdesc_tv = (TextView)convertView.findViewById(R.id.pack_extra_tv);
			holderView.count_tv = (TextView)convertView.findViewById(R.id.file_count_tv);
			holderView.type_icon = (ImageView)convertView.findViewById(R.id.type_icon);	    
			convertView.setTag(holderView);
		}else{
			holderView = (HolderView)convertView.getTag();
		}
		
		holderView.packname_tv.setText(mData.get(position).getPack_name());
		String desc = mData.get(position).getPack_desc();
		if(TextUtils.isEmpty(desc)){
			holderView.packdesc_tv.setVisibility(View.GONE);
		}else{
			holderView.packdesc_tv.setVisibility(View.VISIBLE);
			holderView.packdesc_tv.setText(desc);
		}
		
		String count = mData.get(position).getCollect_count();
		SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
		int end = 0 ; 
		if(TextUtils.isEmpty(count)){				
			spanBuilder.append("0个");
			end = 1; 
		}
		else{
			spanBuilder.append(DataUtils.formatNumber(Integer.parseInt(count))).append("个");			
			end = count.length(); 
		}
		spanBuilder.setSpan(new TypefaceSpan("sans-serif"), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanBuilder.setSpan(new RelativeSizeSpan(2.0f), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spanBuilder.setSpan(new ForegroundColorSpan(Color.rgb(0xff, 0x66, 0x66)), 
				0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);		
		holderView.count_tv.setText(spanBuilder);
		if(StaticValue.SERMODLE.PACK_TYPE_PIC.equals(
				mData.get(position).getPack_type())){
			holderView.type_icon.setImageResource(R.drawable.pack_type_pic);
		}else{
			holderView.type_icon.setImageResource(R.drawable.pack_type_word);
		}
		return convertView;
	}
	
	private class HolderView
	{
		TextView packname_tv ; 
		TextView count_tv;  
		TextView packdesc_tv ;
		ImageView type_icon ; 
	}
}
