package com.kuibu.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollViewExt extends ScrollView {
	private BottomListener bottomListener;
	private onScrollListener scrollListener;
	
	public ScrollViewExt(Context context) {
		this(context, null);
	}
	public ScrollViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
			if (null != bottomListener) {
				bottomListener.onBottom();
			}
		}
		if (null != scrollListener) {
			scrollListener.onScrollChanged(l, t, oldl, oldt);
		}
	}
	public void setBottomListener(BottomListener bottomListener) {
		this.bottomListener = bottomListener;
	}
	public void setScrollListener(onScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}
	public interface onScrollListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}
	public interface BottomListener {
		public void onBottom();
	}
}
