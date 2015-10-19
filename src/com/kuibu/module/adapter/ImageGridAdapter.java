package com.kuibu.module.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kuibu.model.bean.CollectionBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageGridAdapter extends BaseAdapter {

	private Context mContext ; 
	private List<CollectionBean> mData = new ArrayList<CollectionBean>(); 
	private DisplayImageOptions options;
	private boolean bMultiChoice ; 
	
	public class HolderView {
		public ImageView imageView;
		public TextView titleView ;
		public TextView descView ; 
		public ImageView labelView ;
		public ImageView checkView ; 
		public ProgressBar progressBar;
	}
	
	public ImageGridAdapter(Context context,List<CollectionBean> datas,boolean bMultiChoice) {
		this.mContext = context ; 
		this.mData = datas ; 
		this.bMultiChoice = bMultiChoice;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.image_small_default)
				.showImageForEmptyUri(R.drawable.image_small_default)
				.showImageOnFail(R.drawable.image_small_default)
				.cacheInMemory(false)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	public void updateView(List<CollectionBean> datas)
	{
		this.mData = datas ; 
		notifyDataSetChanged();
	}
	
	
	public String getMinId()
	{
		int size = mData.size(); 
		if( size == 0 ){
			return String.valueOf(Integer.MAX_VALUE) ; 
		}
		return mData.get(size-1)._id ; 
	}
	
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final HolderView holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_image, parent, false);
			holder = new HolderView();
			holder.imageView = (ImageView) convertView.findViewById(R.id.item_pic);
			holder.titleView = (TextView)convertView.findViewById(R.id.item_title); 
			holder.descView = (TextView)convertView.findViewById(R.id.item_desc);
			holder.labelView = (ImageView)convertView.findViewById(R.id.published_icon);
			holder.checkView = (ImageView)convertView.findViewById(R.id.checkmark);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}
		if(bMultiChoice){
			holder.checkView.setVisibility(View.VISIBLE);
		}else{
			holder.checkView.setVisibility(View.GONE);
		}
		holder.imageView.setMaxWidth(200);
		holder.imageView.setAdjustViewBounds(true);
		if(mData.get(position).getWidth() == 0){
			holder.imageView.setMaxHeight(200);
		}
        holder.titleView.setText(mData.get(position).getTitle());
        String desc = mData.get(position).getContent() ;
        if(TextUtils.isEmpty(desc)){
        	holder.descView.setVisibility(View.GONE);
        }else{
        	holder.descView.setVisibility(View.VISIBLE);
        	holder.descView.setText(desc);
        } 
        if(mData.get(position).isPublish == 1){
        	holder.labelView.setVisibility(View.GONE);
        }else{
        	holder.labelView.setVisibility(View.VISIBLE);
        }

		ImageLoader.getInstance()
		.displayImage(mData.get(position).getCover(), holder.imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				holder.progressBar.setProgress(0);
				holder.progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				holder.progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				holder.progressBar.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				holder.progressBar.setProgress(Math.round(100.0f * current / total));
			}
		});
		
		return convertView;
	}
}
