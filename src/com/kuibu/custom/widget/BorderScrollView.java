package com.kuibu.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * class : BorderScrollView 对ScrollView的拓展类，主要是为了实现监听滚动状态完成相应操作
 * @author trinea@trinea.cn 
 * @see github
 */

public class BorderScrollView extends ScrollView {
    private OnBorderListener onBorderListener;
    private View             mContentView;
    private boolean 		 bLoading=false; 
    
    public BorderScrollView(Context context) {
        super(context);
    }
    
    public BorderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BorderScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);       
			doOnBorderListener();       
    }

    public void loadComplete() {
		bLoading = false;
	}
    
    public void setOnBorderListener(final OnBorderListener onBorderListener) {
        this.onBorderListener = onBorderListener;
        if (onBorderListener == null) {
            return;
        }
        if (mContentView == null) {
            mContentView = getChildAt(0);
        }
    }

    /**
     * OnBorderListener, Called when scroll to top or bottom 
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-22
     */
    public static interface OnBorderListener {

        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();
    }

    private void doOnBorderListener() {
        if (mContentView != null && mContentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null && !bLoading) {
         		bLoading = true;         		
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0 && !bLoading) {
            if (onBorderListener != null) {
            	bLoading = true;  
                onBorderListener.onTop();
            }
        }
    }
}