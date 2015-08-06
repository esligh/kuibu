package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kuibu.module.activity.R;

public class FavoriteBoxCardListAdapter extends BaseAdapter {

    private List<Map<String,String>> items;
    private final Context context;

    public FavoriteBoxCardListAdapter(Context context, List<Map<String,String>> items) {
        this.context = context;
        this.items = items;
    }

    public void updateView(List<Map<String,String>> items)
    {
    	this.items = items ; 
    	this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
    	if(items!=null)
    		return items.size();
    	return 0 ; 
    }

    @Override
    public Object getItem(int position) {
    	if(items!=null)
    		return items.get(position);
    	return 0 ; 
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;       
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.collect_list_item_card, parent,false);            
            holder = new ViewHolder();
            holder.box_name_tv = (TextView) convertView.findViewById(R.id.listitem_card_box_name);
            holder.item_title_tv_0= (TextView) convertView.findViewById(R.id.list_item_card_title_0);
            holder.item_title_tv_1= (TextView) convertView.findViewById(R.id.list_item_card_title_1);
            holder.item_title_tv_2= (TextView) convertView.findViewById(R.id.list_item_card_title_2);           
            holder.box_count = (TextView) convertView.findViewById(R.id.box_listitem_card_count);           
            holder.dividerLine = convertView.findViewById(R.id.list_item_seperator);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.box_name_tv.setText(items.get(position).get("box_name"));
        String count = items.get(position).get("box_count");
        holder.box_count.setText(count+"个收藏");
        if(Integer.parseInt(count) > 0){        	      
        	String[] titles = items.get(position).get("titles").split(",");
        	
        	if(titles.length >= 1){
        		holder.item_title_tv_0.setVisibility(View.VISIBLE);
        		holder.item_title_tv_0.setText("1."+titles[0]);
        	}
        	if(titles.length >= 2){
        		holder.item_title_tv_1.setVisibility(View.VISIBLE);
        		holder.item_title_tv_1.setText("2."+titles[1]);
        	}
        	if(titles.length >= 3){
        		holder.item_title_tv_2.setVisibility(View.VISIBLE);
        		holder.item_title_tv_2.setText("3."+titles[2]);
        	}        	
        	holder.dividerLine.setVisibility(View.VISIBLE);        	
        }else{        	
        	holder.item_title_tv_0.setVisibility(View.GONE);
        	holder.item_title_tv_1.setVisibility(View.GONE);
        	holder.item_title_tv_2.setVisibility(View.GONE);        	
        	holder.dividerLine.setVisibility(View.GONE);        	
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView box_name_tv;
        private TextView item_title_tv_0;
        private TextView item_title_tv_1;
        private TextView item_title_tv_2;
        private TextView box_count;
        private View dividerLine ; 
    }
}
