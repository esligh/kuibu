package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;

public class CollectionWAdapter extends BaseAdapter{
	
	private List<CollectionBean> mData; 
	private Context context;	 
	boolean isMulChoice ; 
	
	public CollectionWAdapter(Context context,List<CollectionBean> data,boolean isMulChoice)
	{
		this.context = context ;   
        mData = data ;
        this.isMulChoice = isMulChoice ; 
	}
	
	public void updateView(List<CollectionBean> data)
	{
		mData = data ; 
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData == null ? 0 : mData.size();
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
	public View getView(final int position, View convertView, ViewGroup container) {
		// TODO Auto-generated method stub	
		HolderView holderView = null; 
		if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.local_collection_w_list_item, container,false);  
		    holderView = new HolderView();
			holderView.collecitonTitle = (TextView)convertView.findViewById(R.id.collection_title_tv);
			holderView.collectionDesc = (TextView)convertView.findViewById(R.id.collection_desc_tv);
			holderView.collectionDate =(TextView)convertView.findViewById(R.id.collection_date_tv);
			holderView.check = (CheckBox)convertView.findViewById(R.id.collection_check);
			holderView.label = (ImageView)convertView.findViewById(R.id.published_icon);
			convertView.setTag(holderView);
		}else{
			holderView = (HolderView)convertView.getTag();
		}		
		StringBuilder buffer = new StringBuilder(String.valueOf(1+position)).append(". ")
				.append(mData.get(position).getTitle());
		holderView.collecitonTitle.setText(buffer.toString());
		holderView.collectionDesc.setText(do_parse(mData.get(position).getContent()));
		holderView.collectionDate.setText("创建于 "+mData.get(position).getCreateDate());
		if(mData.get(position).getIsPublish() == 1){
			holderView.label.setVisibility(View.GONE);
		}else{
			holderView.label.setVisibility(View.VISIBLE);
		}		
		if(isMulChoice){
			holderView.check.setVisibility(View.VISIBLE);
		}else{
			holderView.check.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private String do_parse(String html) {
		StringBuffer result = new StringBuffer(html);
		return result.toString();
	}
	
	public class HolderView{
		public TextView collecitonTitle;  
		public TextView collectionDesc ; 
		public TextView collectionDate ;
		public CheckBox check ;  
		public ImageView label ;
	}
}
