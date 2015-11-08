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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.module.activity.R;

public class CollectPackItemAdapter extends CommonAdapter<CollectPackBean> {

	public CollectPackItemAdapter(Context context,List<CollectPackBean> data,int layoutId)
	{
		super(context,data,layoutId);
	}
	
	@Override
	public void convert(ViewHolder holder, CollectPackBean item) {
		// TODO Auto-generated method stub
		holder.setTvText(R.id.pack_name_tv,item.getPack_name());
		String desc = item.getPack_desc();
		if(TextUtils.isEmpty(desc)){
			holder.setVisibility(R.id.pack_extra_tv,View.GONE);
		}else{
			holder.setVisibility(R.id.pack_extra_tv,View.VISIBLE);
			holder.setTvText(R.id.pack_extra_tv,desc);
		}
		
		String count = item.getCollect_count();
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
		TextView count_tv = holder.getView(R.id.file_count_tv); 
		count_tv.setText(spanBuilder);
		if(StaticValue.SERMODLE.PACK_TYPE_PIC.equals(
				item.getPack_type())){
			holder.setImageResource(R.id.type_icon,R.drawable.pack_type_pic);
		}else{
			holder.setImageResource(R.id.type_icon,R.drawable.pack_type_word);
		}
	}
	
}
