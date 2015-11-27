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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.AppInfo;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.ui.activity.CollectInfoListActivity;
import com.kuibu.ui.activity.CollectionDetailActivity;
import com.kuibu.ui.activity.CollectionImageDetailActivity;
import com.kuibu.ui.activity.UserInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//与MateListViewItemAdapter 高度相似，主要考虑后续ListView可能的变化以便于拓展，后续可能需要再次重构

public class HomeListViewItemAdapter extends BaseAdapter {
	private List<MateListItem> datas;
	private Context context;
	private DisplayImageOptions options;
	private int listItemDefaultImageId;

	public class HolderViewForText {
		RelativeLayout layout0_rl;
		TextView topic_tv;
		ImageView topic_icon_iv;
		TextView title_tv;
		TextView content_tv;
		TextView time_tv;
	}

	public class HolderViewForTextPics {
		RelativeLayout layout1_rl;
		TextView topic_tv;
		ImageView topic_icon_iv;
		TextView title_tv;
		TextView content_tv;
		ImageView item_pic_iv;
		TextView time_tv;
	}

	public class HolderViewForPics {
		RelativeLayout layout2_rl;
		TextView topic_tv;
		ImageView topic_icon_iv;
		TextView title_tv;
		ImageView item_pic_iv;
		TextView desc_tv ; 
		TextView time_tv;
	}

	public HomeListViewItemAdapter(Context context, List<MateListItem> datas) {
		this.datas = datas;
		this.context = context;
		initStyle();
	}

	@SuppressWarnings("deprecation")
	private void initStyle() {
		Resources.Theme theme = context.getTheme();
		TypedArray typedArray = null;


		boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(context,
				StaticValue.PrefKey.DARK_THEME_KEY , false);
		if (isDarkTheme) {
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

	public void setData(List<MateListItem> datas) {
		this.datas = datas;
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
				convertView = LayoutInflater.from(context).inflate(
						R.layout.home_list_view_item_type0, parent, false);
				holderforText = new HolderViewForText();
				holderforText.layout0_rl = (RelativeLayout) convertView
						.findViewById(R.id.home_0_rela_layout0_rl);
				holderforText.topic_tv = (TextView) convertView
						.findViewById(R.id.home_0_topic_tv);
				holderforText.topic_icon_iv = (ImageView) convertView
						.findViewById(R.id.home_0_topic_pic_iv);
				holderforText.title_tv = (TextView) convertView
						.findViewById(R.id.home_0_item_title_tv);
				holderforText.content_tv = (TextView) convertView
						.findViewById(R.id.home_0_content_tv);
				holderforText.time_tv = (TextView)convertView.findViewById(R.id.home_0_item_time_iv);
				convertView.setTag(holderforText);
				break;
			case MateListItem.ItemType.TEXT_PICS_MODE:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.home_list_view_item_type1, parent, false);
				holderfortextPic = new HolderViewForTextPics();
				holderfortextPic.layout1_rl = (RelativeLayout) convertView
						.findViewById(R.id.home_1_rela_layout0_rl);
				holderfortextPic.topic_tv = (TextView) convertView
						.findViewById(R.id.home_1_topic_tv);
				holderfortextPic.topic_icon_iv = (ImageView) convertView
						.findViewById(R.id.home_1_topic_pic_iv);
				holderfortextPic.title_tv = (TextView) convertView						
						.findViewById(R.id.home_1_item_title_tv);
				holderfortextPic.content_tv = (TextView) convertView
						.findViewById(R.id.home_1_content_tv);
				holderfortextPic.item_pic_iv = (ImageView) convertView
						.findViewById(R.id.home_1_item_pic_iv);
				holderfortextPic.time_tv = (TextView)convertView.findViewById(R.id.home_1_item_time_iv);
				convertView.setTag(holderfortextPic);
				break;
			case MateListItem.ItemType.PICS_MODE:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.home_list_view_item_type2, parent, false);
				holderforpics = new HolderViewForPics();
				holderforpics.layout2_rl = (RelativeLayout) convertView
						.findViewById(R.id.home_2_rela_layout2_rl);
				holderforpics.topic_tv = (TextView) convertView
						.findViewById(R.id.home_2_topic_tv);
				holderforpics.topic_icon_iv = (ImageView) convertView
						.findViewById(R.id.home_2_topic_pic_iv);
				holderforpics.title_tv = (TextView) convertView
						.findViewById(R.id.home_2_item_title_tv);
				holderforpics.item_pic_iv = (ImageView) convertView
						.findViewById(R.id.home_2_image_iv);	
				holderforpics.item_pic_iv.setAdjustViewBounds(true);
				holderforpics.item_pic_iv.setMaxWidth(AppInfo.getInstance().screenWidth);
				holderforpics.desc_tv = (TextView)convertView.findViewById(R.id.home_2_item_desc_tv);
				holderforpics.time_tv = (TextView)convertView.findViewById(R.id.home_2_item_time_iv);
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
				holderfortextPic.item_pic_iv.setImageDrawable(null);
				break;
			case MateListItem.ItemType.PICS_MODE:
				holderforpics = (HolderViewForPics)convertView.getTag();
				break;
			}
		}
		switch (item_type) {
		case MateListItem.ItemType.TEXT_MODE:
			holderforText.layout0_rl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showUserInfo(position);
				}
			});
			holderforText.title_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("type", String.valueOf(datas.get(position).getType()));
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			holderforText.content_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startAction(position);
				}
			});
			holderforText.topic_tv
					.setText(datas.get(position).getTopText());

			String url = datas.get(position).getTopUrl();
			if (TextUtils.isEmpty(url) || url.equals("null")) {
				holderforText.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avata);
				
			} else {
				ImageLoader.getInstance().displayImage(url,
						holderforText.topic_icon_iv,options,null);
			}
			holderforText.title_tv.setText(datas.get(position).getTitle().replace("\n", ""));
			holderforText.content_tv.setText(datas.get(position)
					.getSummary().replace("\n", ""));
			String date = datas.get(position).getLastModify().trim();			
			if(!date.equals("null")){
				holderforText.time_tv.setText(KuibuUtils.formatDateTime(date));
			}									   
			break;
		case MateListItem.ItemType.TEXT_PICS_MODE:
			holderfortextPic.layout1_rl
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							showUserInfo(position);
						}
					});
			holderfortextPic.title_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("type", String.valueOf(datas.get(position).getType()));
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			holderfortextPic.item_pic_iv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					startAction(position);
				}
			});
			holderfortextPic.content_tv
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							startAction(position);
						}
			});
			holderfortextPic.topic_tv.setText(datas.get(position)
					.getTopText());
			
			String url2 = datas.get(position).getTopUrl();
			if (TextUtils.isEmpty(url2) || url2.equals("null") ) {
					holderfortextPic.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avata);				
			} else {
				ImageLoader.getInstance().displayImage(url2,
						holderfortextPic.topic_icon_iv,options,null);
			}
			
			String summary = datas.get(position).getSummary().replace("\n", "");
			if(TextUtils.isEmpty(summary) || summary.equals("null")){
				holderfortextPic.content_tv.setVisibility(View.GONE);
				holderfortextPic.content_tv.setText("(图)");
			}else{
				holderfortextPic.content_tv.setVisibility(View.VISIBLE);
				holderfortextPic.content_tv.setText(summary);
			}
			
			
			String date2 = datas.get(position).getLastModify().trim();
			if(!date2.equals("null")){
				holderfortextPic.time_tv.setText(KuibuUtils.formatDateTime(date2));
			}

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			StringBuffer title = new StringBuffer(datas.get(position).getTitle());
			if(noPic){
				holderfortextPic.item_pic_iv.setVisibility(View.GONE);
				title  = title.append("  (图)");
			}else{
				// ImageLoad
				String item_url = datas.get(position).getItemPic();
				ImageLoader.getInstance().displayImage(item_url,
						holderfortextPic.item_pic_iv,options,null);
			}
			holderfortextPic.title_tv.setText(title.toString());
			
			break;
		case MateListItem.ItemType.PICS_MODE:
			holderforpics.layout2_rl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showUserInfo(position);
				}
			});
			holderforpics.title_tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("type", String.valueOf(datas.get(position).getType()));
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			holderforpics.item_pic_iv.setOnClickListener( new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,CollectionImageDetailActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID,
							datas.get(position).getId()) ; 
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,
							R.anim.anim_slide_out_left);
				}
			});
			
			holderforpics.topic_tv
			.setText(datas.get(position).getTopText());

			String url3 = datas.get(position).getTopUrl();
			if (TextUtils.isEmpty(url3) || url3.equals("null")) {
				holderforText.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avata);
				
			} else {
				ImageLoader.getInstance().displayImage(url3,
						holderforpics.topic_icon_iv,options,null);
			}
			

			boolean bnoPic = PreferencesUtils.getBooleanByDefault(context,
					StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			
			StringBuffer title2 = new StringBuffer(datas.get(position).getTitle());
			if(bnoPic){
				holderforpics.item_pic_iv.setVisibility(View.GONE);
				title2  = title2.append("  (图)");
			}else{
				holderforpics.item_pic_iv.setVisibility(View.VISIBLE);
				// ImageLoad
				String item_url = datas.get(position).getItemPic();
				ImageLoader.getInstance().displayImage(item_url,
						holderforpics.item_pic_iv,options,null);
			}
			
			holderforpics.title_tv.setText(datas.get(position).getTitle().replace("\n", ""));
			String desc = datas.get(position).getSummary(); 
			if(TextUtils.isEmpty(desc)){
				holderforpics.desc_tv.setVisibility(View.GONE);
			}else{
				holderforpics.desc_tv.setVisibility(View.VISIBLE);
				holderforpics.desc_tv.setText(desc);
			}
			
			String date3 = datas.get(position).getLastModify().trim();
			if(!date3.equals("null")){
				holderforpics.time_tv.setText(KuibuUtils.formatDateTime(date3));
			}		
			break;
		}
		return convertView;
	}

	private void startAction(int position) {
		int type = datas.get(position).getType();
		Intent intent = null;
		switch (type) {
			case MateListItem.ItemType.TEXT_MODE:
			case MateListItem.ItemType.TEXT_PICS_MODE:
				intent = new Intent(context, CollectionDetailActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID,
						datas.get(position).getId());
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN,
						datas.get(position).getCisn());
				break;
			default:
				break;
		}
		if (intent != null){
			context.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
		}
	}
	
	
	private void showUserInfo(int position)
	{
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
	
}
