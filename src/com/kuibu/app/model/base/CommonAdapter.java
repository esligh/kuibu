package com.kuibu.app.model.base;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Common ListView Adapter 
 * @author ThinkPad
 * @date 2015/10/26
 * @version 1.0
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
	
	protected Context mContext ; 
	protected List<T> mData;
	protected final int mItemLayoutId;
	
	public CommonAdapter(Context context,List<T> datas ,int itemLayoutId){
		this.mContext = context ;
		this.mData = datas ;
		this.mItemLayoutId = itemLayoutId;
	}
	
	public void refreshView(List<T> data)
	{
		this.mData = data;  
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData==null ? 0 :mData.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public abstract void convert(ViewHolder holder, T item);
	
	@Override  
    public View getView(int position, View convertView, ViewGroup parent)  
    {  
        final ViewHolder viewHolder = ViewHolder.get(mContext, convertView, parent, mItemLayoutId,  
                position);
        convert(viewHolder, getItem(position));  
        return viewHolder.getConvertView();    
    }
	
}
