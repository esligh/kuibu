package com.kuibu.module.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopicListAdapter extends BaseAdapter implements Filterable{

	private List<TopicItemBean> datas ; 
	private Context context ; 
	private ArrayFilter mFilter;  
    private ArrayList<TopicItemBean> mUnfilteredData;  

    private class HolderView{
		TextView topic_tv; 
		TextView introduce_tv;
		ImageView topicPic_iv;
	}
    
	public TopicListAdapter(Context context,List<TopicItemBean> datas)
	{
		this.context = context ; 
		this.datas = datas ; 
	}
	
	public void updateView(List<TopicItemBean> datas)
	{
		this.datas = datas ; 
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
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

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {  
            mFilter = new ArrayFilter();  
        }  
        return mFilter;  
	}
	
	private class ArrayFilter extends Filter {  
		
	        @Override  
	        protected FilterResults performFiltering(CharSequence prefix) {  
	            FilterResults results = new FilterResults();  
	  
	            if (mUnfilteredData == null) {  
	                mUnfilteredData = new ArrayList<TopicItemBean>(datas);  
	            }  
	  
	            if (prefix == null || prefix.length() == 0) {  
	                ArrayList<TopicItemBean> list = mUnfilteredData;  
	                results.values = list;  
	                results.count = list.size();  
	            } else {   
	                ArrayList<TopicItemBean> unfilteredValues = mUnfilteredData;  
	                int count = unfilteredValues.size();              
	                ArrayList<TopicItemBean> newValues = new ArrayList<TopicItemBean>(count);  	  
	                for (int i = 0; i < count; i++) {  
	                	TopicItemBean item = unfilteredValues.get(i);  
	                    if (item != null) {   
	                        if(item.getTopic() !=null){                            
	                            newValues.add(item);  
	                        }
	                    }  
	                }  	  
	                results.values = newValues;  
	                results.count = newValues.size();  
	            }  
	  
	            return results;  
	        }  
	  
	        @SuppressWarnings("unchecked")
			@Override  
	        protected void publishResults(CharSequence constraint,  
	                FilterResults results) {  
	            datas = (List<TopicItemBean>) results.values;  
	            if (results.count > 0) {  
	                notifyDataSetChanged();  
	            } else {  
	                notifyDataSetInvalidated();  
	            }  
	        }  
	 }
}
