package com.kuibu.module.adapter;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kuibu.common.utils.Bimp;
import com.kuibu.module.activity.R;

public class ImageScanGridAdapter extends BaseAdapter
{
	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	private Context context ;
	private int max_select_image = 0 ;
	public boolean isShape()
	{
		return shape;
	}

	public void setShape(boolean shape)
	{
		this.shape = shape;
	}

	public ImageScanGridAdapter(Context context,int max)
	{
		inflater = LayoutInflater.from(context);
		this.context = context ; 
		max_select_image=max; 
	}

	public void update()
	{
		loading();
	}

	public int getCount()
	{
		 return (Bimp.bmp.size() + 1);
	}

	public Object getItem(int arg0)
	{

		return null;
	}

	public long getItemId(int arg0)
	{

		return 0;
	}

	public void setSelectedPosition(int position)
	{
		selectedPosition = position;
	}

	public int getSelectedPosition()
	{
		return selectedPosition;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{

			convertView = inflater.inflate(R.layout.item_published_grida,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.image.setVisibility(View.VISIBLE);

		if (position == Bimp.bmp.size()){
			
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));				
		} else{
			holder.image.setImageBitmap(Bimp.bmp.get(position));
		}			
		if (position == max_select_image){
			holder.image.setVisibility(View.GONE);
		}
		return convertView;
	}

	public class ViewHolder
	{
		public ImageView image;
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 5231:
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void loading()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if (Bimp.max == Bimp.drr.size()){
						Message message = new Message();
						message.what = 5231;
						handler.sendMessage(message);
						break;
					} else{
						try{
							String path = Bimp.drr.get(Bimp.max);
							Bitmap bm = Bimp.revitionImageSize(path);
							Bimp.bmp.add(bm);
							Bimp.max += 1;
							Message message = new Message();
							message.what = 5231;
							handler.sendMessage(message);
						} catch (IOException e){
							e.printStackTrace();
						}
					}						
				}
			}
		}).start();
	}
}
