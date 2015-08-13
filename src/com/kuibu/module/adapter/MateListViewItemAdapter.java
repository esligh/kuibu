package com.kuibu.module.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuibu.common.utils.DataUtils;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.activity.CollectInfoListActivity;
import com.kuibu.module.activity.CollectionDetailActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.activity.UserInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MateListViewItemAdapter extends BaseAdapter {
	private List<MateListItem> datas;
	private Context context;
	private DisplayImageOptions options;
	private int listItemDefaultImageId;
	
	public class HolderViewForText{
		RelativeLayout layout0_rl;
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		TextView content_tv;
		ImageView count_pic_iv; 
		TextView count_tv; 
	}
	public class HolderViewForTextPics{
		RelativeLayout layout1_rl;
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		TextView content_tv;
		ImageView item_pic_iv;
		ImageView count_pic_iv; 
		TextView count_tv; 
	}
	
	public class HolderViewForPics{
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		GridView item_pics_gv;
		ImageView count_pic_iv; 
		TextView count_tv; 
	}

	public MateListViewItemAdapter(Context context, List<MateListItem> datas) {
		this.datas = datas;
		this.context = context ; 
		initStyle();
	}
	
	private void initStyle() {
		Resources.Theme theme = context.getTheme();
		TypedArray typedArray = null;

		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (mPerferences.getBoolean("dark_theme", false)) {
			typedArray = theme.obtainStyledAttributes(R.style.Theme_Kuibu_AppTheme_Dark,
					new int[] {R.attr.listItemDefaultImage });
		} else {
			typedArray = theme.obtainStyledAttributes(R.style.Theme_Kuibu_AppTheme_Light,
					new int[] { R.attr.listItemDefaultImage });
		}

		listItemDefaultImageId = typedArray.getResourceId(0, 0);

		typedArray.recycle();
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(context.getResources().getDrawable(listItemDefaultImageId))
		.showImageForEmptyUri(context.getResources().getDrawable(listItemDefaultImageId))
		.showImageOnFail(context.getResources().getDrawable(listItemDefaultImageId))
		.cacheInMemory(false)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public void updateView(List<MateListItem> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
		
	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		
		return datas.get(position).getType();
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return MateListItem.getTypeCount();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderViewForText holderforText = null;
		HolderViewForTextPics holderfortextPic = null;
		HolderViewForPics holderforpics = null;
		int item_type = getItemViewType(position);
		if (convertView == null) {
			switch (item_type) {
			case MateListItem.ItemType.TEXT_MODE:
				convertView = LayoutInflater.from(context).inflate(R.layout.mate_list_view_item_0,
						parent,false);
				holderforText = new HolderViewForText();
				holderforText.layout0_rl = (RelativeLayout)convertView.findViewById(R.id.mate_0_rela_layout0_rl);			
				holderforText.user_tv = (TextView) convertView
						.findViewById(R.id.mate_0_user_tv);
				holderforText.user_icon_iv = (ImageView) convertView
						.findViewById(R.id.mate_0_user_pic_iv);
				holderforText.title_tv = (TextView) convertView
						.findViewById(R.id.mate_0_item_title_tv);
				holderforText.content_tv = (TextView) convertView
						.findViewById(R.id.mate_0_content_tv);				
				holderforText.count_tv = (TextView) convertView
						.findViewById(R.id.mate_0_count_tv);
				holderforText.count_pic_iv= (ImageView) convertView
						.findViewById(R.id.mate_0_count_pic_iv);
				convertView.setTag(holderforText);
				holderforText.layout0_rl.setTag(position);
				break;
			case MateListItem.ItemType.TEXT_PICS_MODE:
				convertView = LayoutInflater.from(context).inflate(R.layout.mate_list_view_item_1,
						parent,false);
				holderfortextPic = new HolderViewForTextPics();
				holderfortextPic.layout1_rl = (RelativeLayout)convertView.findViewById(R.id.mate_1_rela_layout1_rl);			
				holderfortextPic.user_tv = (TextView) convertView
						.findViewById(R.id.mate_1_user_tv);
				holderfortextPic.user_icon_iv = (ImageView) convertView
						.findViewById(R.id.mate_1_user_pic_iv);
				holderfortextPic.title_tv = (TextView) convertView
						.findViewById(R.id.mate_1_item_title_tv);
				holderfortextPic.content_tv = (TextView) convertView
						.findViewById(R.id.mate_1_content_tv);
				holderfortextPic.item_pic_iv = (ImageView) convertView
						.findViewById(R.id.mate_1_item_pic_iv);
				holderfortextPic.count_tv = (TextView) convertView
						.findViewById(R.id.mate_1_count_tv);
				holderfortextPic.count_pic_iv= (ImageView) convertView
						.findViewById(R.id.mate_1_count_pic_iv);
				convertView.setTag(holderfortextPic);
				break;
			case MateListItem.ItemType.PICS_MODE:
				convertView = LayoutInflater.from(context).inflate(R.layout.mate_list_view_item_2,
						parent,false);
				holderforpics = new HolderViewForPics();
				holderforpics.user_tv = (TextView) convertView
						.findViewById(R.id.mate_2_user_tv);
				holderforpics.user_icon_iv = (ImageView) convertView
						.findViewById(R.id.mate_2_user_pic_iv);
				holderforpics.title_tv = (TextView) convertView
						.findViewById(R.id.mate_2_item_title_tv);
				holderforpics.item_pics_gv = (GridView) convertView
						.findViewById(R.id.mate_2_pics_grid_gv);

				holderforpics.count_tv = (TextView) convertView
						.findViewById(R.id.mate_2_count_tv);
				holderforpics.count_pic_iv= (ImageView) convertView
						.findViewById(R.id.mate_2_count_pic_iv);
				convertView.setTag(holderforpics);
				break;
			}
		} else {
			switch (item_type) {
			case MateListItem.ItemType.TEXT_MODE:
				holderforText = (HolderViewForText) convertView.getTag();
				break;
			case MateListItem.ItemType.TEXT_PICS_MODE:
				holderfortextPic = (HolderViewForTextPics) convertView.getTag();
				break;
			case MateListItem.ItemType.PICS_MODE:
				holderforpics = (HolderViewForPics) convertView.getTag();
				break;
			}
		}
		switch(item_type){
		case MateListItem.ItemType.TEXT_MODE:
			holderforText.layout0_rl.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, UserInfoActivity.class);
					intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
					intent.putExtra(StaticValue.USERINFO.USER_ID,
							datas.get(position).getCreateBy());
					intent.putExtra(StaticValue.USERINFO.USER_SEX,
							datas.get(position).getUserSex());
					intent.putExtra(StaticValue.USERINFO.USER_NAME,
							datas.get(position).getTopText());
					intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE, datas
							.get(position).getUserSignature());
					
					intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
							datas.get(position).getTopUrl());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}				
			});
			
			holderforText.title_tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LayoutInflater.from(context).getContext(),
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					LayoutInflater.from(context).getContext().startActivity(intent);	
				}
			});
			
			holderforText.content_tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					startAction(position);
				}
			});
			holderforText.user_tv.setText(datas.get(position).getTopText());
			
			ImageLoader.getInstance().displayImage(datas.get(position).getTopUrl(),
					holderforText.user_icon_iv,options,null);
			
			holderforText.title_tv.setText(datas.get(position)
					.getTitle());
			holderforText.content_tv.setText(datas.get(position)
					.getSummary());
			holderforText.count_tv.setText(
					DataUtils.formatNumber(datas.get(position).getVoteCount()));
			break;
		case MateListItem.ItemType.TEXT_PICS_MODE:
			holderfortextPic.layout1_rl.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context,
							UserInfoActivity.class);
					intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT,
							true);
					intent.putExtra(StaticValue.USERINFO.USER_ID, datas
							.get(position).getCreateBy());
					intent.putExtra(StaticValue.USERINFO.USER_SEX,
							datas.get(position).getUserSex());
					intent.putExtra(StaticValue.USERINFO.USER_NAME,
							datas.get(position).getTopText());
					intent.putExtra(
							StaticValue.USERINFO.USER_SIGNATURE, datas
									.get(position)
									.getUserSignature());
					intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
							datas.get(position).getTopUrl());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			holderfortextPic.title_tv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub	
					Intent intent = new Intent(LayoutInflater.from(context).getContext(),
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("create_by", datas.get(position).getCreateBy());

					LayoutInflater.from(context).getContext().startActivity(intent);
				}
			});
			holderfortextPic.item_pic_iv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					startAction(position);
				}
			});
			holderfortextPic.content_tv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					startAction(position);
				}
			});
			
			holderfortextPic.user_tv.setText(datas.get(position)
					.getTopText());
			holderfortextPic.content_tv.setText(datas.get(position)
					.getSummary());
			holderfortextPic.count_tv.setText(
					DataUtils.formatNumber(datas.get(position).getVoteCount()));	
			
			ImageLoader.getInstance().displayImage(datas.get(position).getTopUrl(),
					holderfortextPic.user_icon_iv,options,null);
			
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			StringBuffer title = new StringBuffer(datas.get(position).getTitle());
			if(noPic){
				holderfortextPic.item_pic_iv.setVisibility(View.GONE);
				title  = title.append("  (多图)");
			}else{
				ImageLoader.getInstance().displayImage(datas.get(position).getItemPic(),
						holderfortextPic.item_pic_iv);
			}
			holderfortextPic.title_tv.setText(title);
			break;
		case MateListItem.ItemType.PICS_MODE:
			break;
		}
		return convertView;
	}
	
	private void startAction(int position)
	{
		int type =datas.get(position).getType();
		Intent intent = null; 		
		switch(type){
		case MateListItem.ItemType.TEXT_MODE:
		case MateListItem.ItemType.TEXT_PICS_MODE:
			intent = new Intent(LayoutInflater.from(context).getContext(),CollectionDetailActivity.class);
			intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID ,datas.get(position).getId());
//			intent.putExtra("title", datas.get(position).getTitle());
//			intent.putExtra("type", datas.get(position).getType());
//			intent.putExtra("create_by", datas.get(position).getCreateBy());
//			intent.putExtra("name", datas.get(position).getTopText());
//			intent.putExtra("photo", datas.get(position).getTopUrl());
//			intent.putExtra("signature", datas.get(position).getUserSignature());
//			intent.putExtra("sex", datas.get(position).getUserSex());
			break;
		default:
			break;
		}
		if(intent!=null)
			LayoutInflater.from(context).getContext().startActivity(intent);
	}
}
