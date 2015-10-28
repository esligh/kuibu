package com.kuibu.app.model.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

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
	
	private ViewHolder(Context context,ViewGroup parent,int layoutId,int position)
	{
		this.mPosition = position ; 
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,false);
		this.mConvertView.setTag(this);
	}
	
	public static ViewHolder get(Context context, View convertView,  
            ViewGroup parent, int layoutId, int position) 
	{
		if(convertView == null){
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder)convertView.getTag();
	}
	
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
	
    public int getPosition()  
    {  
        return mPosition;  
    }
	
	/**ΪTextView �����ı�
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setTVText(int viewId,String text){
		TextView view = getView(viewId);
		view.setText(text);
		return this; 
	}
	
	/**ΪEditText�����ı�
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setETText(int viewId,String text)
	{
		EditText view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	
	/** 
     * ΪImageView����ͼƬ 
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
     * ΪImageView����ͼƬ 
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
     * ΪImageView����ͼƬ 
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
     
    //....
}
