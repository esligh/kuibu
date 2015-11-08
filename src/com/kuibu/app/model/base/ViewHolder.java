package com.kuibu.app.model.base;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author ThinkPad
 * @date 2015/10/26
 * @version 1.0
 */

public class ViewHolder {
	
	private final SparseArray<View> mViews; 
	private View mConvertView;
	private int mPosition ; 
	private ViewGroup mParent;
	
	/**锟斤拷为一锟斤拷锟斤拷锟绞癸拷锟�Open 
	 * @param rootView
	 */
	public ViewHolder(View rootView)
	{		
		this.mConvertView = rootView; 
		this.mViews = new SparseArray<View>();
	}
	
	/**  
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,  
            ViewGroup parent, int layoutId, int position) 
	{
		if(convertView == null){
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder)convertView.getTag();
	}
		
	/**
	 * 锟斤拷为List锟斤拷Adapter 锟斤拷Holder Close
	 * @param context
	 * @param parent
	 * @param layoutId
	 * @param position
	 */
	private ViewHolder(Context context,ViewGroup parent,int layoutId,int position)
	{
		
		this.mPosition = position ; 
		this.mParent = parent ; 
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);
		this.mConvertView.setTag(this);
	}
	
	@SuppressWarnings("unchecked")
	public  <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		if(view == null){
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view ; 
	}
	
	public View getConvertView()
	{
		return mConvertView; 
	}
	
	public ViewGroup getParent()
	{
		return mParent ;
	}
	
    public int getPosition()  
    {  
        return mPosition;  
    }
    	
	/**if listview
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ListView> T getListView(int viewId)
	{
		return (T)getView(viewId);
	}
	
	/**
	 * 锟斤拷锟斤拷view锟缴硷拷锟斤拷
	 * */
	public void setVisibility(int viewId,int visibility)
	{
		getView(viewId).setVisibility(visibility);
	}
	
	public void setChecked(int viewId,boolean flag)
	{
		((CheckBox)getView(viewId)).setChecked(flag);
	}
	/**为TextView 锟斤拷锟斤拷锟侥憋拷
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setTvText(int viewId,String text){
		//Log.d("setTvText_viewId",viewId+"");
		TextView view = getView(viewId);
		view.setText(text);
		return this; 
	}
	
	/**为EditText锟斤拷锟斤拷锟侥憋拷
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setEtText(int viewId,String text)
	{
		EditText view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	
	/** 
     * 为ImageView锟斤拷锟斤拷图片 
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageResource(int viewId, int drawableId)  
    {  
        ImageView view = getView(viewId);  
        view.setImageResource(drawableId);    
        return this;  
    } 
    
    /** 
     * 为ImageView锟斤拷锟斤拷图片 
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageBitmap(int viewId, Bitmap bm)  
    {  
        ImageView view = getView(viewId);  
        view.setImageBitmap(bm);  
        return this;  
    }  
    
    /** 
     * 为ImageView锟斤拷锟斤拷图片 need ImageLoader library 
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageByUrl(int viewId, String url)  
    {  
        ImageLoader.getInstance().displayImage(url, (ImageView)getView(viewId));  
        return this;  
    }  
    /**
     * with options 
     * */    
    public ViewHolder setImageByUrl(int viewId,String url ,DisplayImageOptions options)
    {
    	ImageLoader.getInstance().displayImage(url, (ImageView)getView(viewId),options);  
        return this;
    }
  
    /**
     * with listener
     * @param viewId
     * @param url
     * @param options
     * @param listener
     * @return
     */
    public ViewHolder setImageByUrl(int viewId,String url ,
    		DisplayImageOptions options,ImageLoadingListener listener)
    {
    	ImageLoader.getInstance().displayImage(url, (ImageView)getView(viewId),options,listener);
        return this;
    }
    //....
}
