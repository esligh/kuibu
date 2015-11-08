package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;

import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;

public class FavoriteBoxCardListAdapter extends CommonAdapter<Map<String,String>> {


    public FavoriteBoxCardListAdapter(Context context, List<Map<String,String>> items,int layoutId) {
        super(context,items,layoutId);
    }

	@Override
	public void convert(com.kuibu.app.model.base.ViewHolder holder,
			Map<String, String> item) {
		// TODO Auto-generated method stub
		holder.setTvText(R.id.listitem_card_box_name,item.get("box_name"));
        String count = item.get("box_count");
        holder.setTvText(R.id.box_listitem_card_count,count+"个收藏");
        if(Integer.parseInt(count) > 0){        	      
        	String[] titles = item.get("titles").split(",");
        	StringBuilder builder = new StringBuilder();
        	for(int i=0;i<titles.length;i++){
        		builder.append(i+1).append(". ").append(titles[i]).append("\n");
        	}        	
        	holder.setVisibility(R.id.list_item_card_title,View.VISIBLE);
        	holder.setTvText(R.id.list_item_card_title,builder.toString());
        	holder.setVisibility(R.id.list_item_seperator,View.VISIBLE);        	
        }else{        	
        	holder.setVisibility(R.id.list_item_card_title,View.GONE);     	
        	holder.setVisibility(R.id.list_item_seperator,View.GONE);        	
        }
        if(StaticValue.SERMODLE.BOX_TYPE_PIC.equals(item.get("box_type"))){
        	holder.setImageResource(R.id.type_icon,R.drawable.pack_type_pic);
        }else{
        	holder.setImageResource(R.id.type_icon,R.drawable.pack_type_word);
        }
	}
}
