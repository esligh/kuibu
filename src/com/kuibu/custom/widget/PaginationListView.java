package com.kuibu.custom.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.kuibu.module.activity.R;

public class PaginationListView extends ListView implements OnScrollListener {
	
	private View footerView;
	int totalItemCount = 0;
	int lastVisibleItem = 0;
	boolean isLoading = false;

	public PaginationListView(Context context) {
		super(context);
		initView(context);
	}

	public PaginationListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

	}

	public PaginationListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	@SuppressLint("InflateParams")
	private void initView(Context context) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		footerView = mInflater.inflate(R.layout.footer,null);
		footerView.setVisibility(View.GONE);
		this.addFooterView(footerView);
		this.setOnScrollListener(this);		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (lastVisibleItem == totalItemCount
				&& scrollState == SCROLL_STATE_IDLE) {
			if (!isLoading && onLoadListener!=null) {
				isLoading = true;
				footerView.setVisibility(View.VISIBLE);				
				onLoadListener.onLoadMore();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	private OnLoadListener onLoadListener;

	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.onLoadListener = onLoadListener;
	}

	public interface OnLoadListener {
		void onLoadMore();
	}

	public void loadComplete() {
		footerView.setVisibility(View.GONE);
		isLoading = false;
		this.invalidate();
	}
}
