package com.kuibu.module.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.data.global.Constants;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.module.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageGridAdapter extends CommonAdapter<CollectionBean> {

	private boolean bMultiChoice ; 
	
	public ImageGridAdapter(Context context,List<CollectionBean> datas,int layoutId,
			boolean bMultiChoice) {
		super(context,datas,layoutId);
		this.bMultiChoice = bMultiChoice;
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
	public void convert(final ViewHolder holder, CollectionBean item) {
		// TODO Auto-generated method stub
		if(bMultiChoice){
			holder.setVisibility(R.id.checkmark,View.VISIBLE);
		}else{
			holder.setVisibility(R.id.checkmark,View.GONE);
		}		
		((ImageView)holder.getView(R.id.item_pic)).setAdjustViewBounds(true);
		((ImageView)holder.getView(R.id.item_pic)).setScaleType(ScaleType.CENTER_CROP);		
		((ImageView)holder.getView(R.id.item_pic)).setMaxHeight(200);		
		((ImageView)holder.getView(R.id.item_pic)).setMaxWidth(200);
        holder.setTvText(R.id.item_title,item.getTitle());
        holder.setTvText(R.id.date_time, item.getCreateDate());
        String desc = item.getContent() ;
        if(TextUtils.isEmpty(desc)){
        	holder.setVisibility(R.id.item_desc,View.GONE);
        }else{
        	holder.setVisibility(R.id.item_desc,View.VISIBLE);
        	holder.setTvText(R.id.item_desc,desc);
        } 
        if(item.isPublish == 1){
        	holder.setVisibility(R.id.published_icon,View.GONE);
        }else{
        	holder.setVisibility(R.id.published_icon,View.VISIBLE);
        }

		ImageLoader.getInstance()
		.displayImage(item.getCover(), (ImageView)holder.getView(R.id.item_pic), Constants.defaultOptions,
			new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				((ProgressBar)holder.getView(R.id.progress)).setProgress(0);
				((ProgressBar)holder.getView(R.id.progress)).setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				((ProgressBar)holder.getView(R.id.progress)).setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				((ProgressBar)holder.getView(R.id.progress)).setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				((ProgressBar)holder.getView(R.id.progress)).setProgress(Math.round(100.0f * current / total));
			}
		});		
	}	
}
