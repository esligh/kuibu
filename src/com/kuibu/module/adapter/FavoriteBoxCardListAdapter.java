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
            holder.item_title_tv= (TextView) convertView.findViewById(R.id.list_item_card_title);          
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
        	StringBuilder builder = new StringBuilder();
        	for(int i=0;i<titles.length;i++){
        		builder.append(i+1).append(". ").append(titles[i]).append("\n");
        	}        	
        	holder.item_title_tv.setVisibility(View.VISIBLE);
        	holder.item_title_tv.setText(builder.toString());
        	holder.dividerLine.setVisibility(View.VISIBLE);        	
        }else{        	
        	holder.item_title_tv.setVisibility(View.GONE);     	
        	holder.dividerLine.setVisibility(View.GONE);        	
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView box_name_tv;
        private TextView item_title_tv;
        private TextView box_count;
        private View dividerLine ; 
    }
}
