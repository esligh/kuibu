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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.MateListItem;
import com.kuibu.module.activity.CollectInfoListActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.activity.CollectionDetailActivity;
import com.kuibu.module.activity.UserInfoActivity;
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
		GridView item_pics_gv;
	}

	public HomeListViewItemAdapter(Context context, List<MateListItem> datas) {
		this.datas = datas;
		this.context = context;
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
						.findViewById(R.id.home_2_rela_layout0_rl);
				holderforpics.topic_tv = (TextView) convertView
						.findViewById(R.id.home_2_topic_tv);
				holderforpics.topic_icon_iv = (ImageView) convertView
						.findViewById(R.id.home_2_topic_pic_iv);
				holderforpics.title_tv = (TextView) convertView
						.findViewById(R.id.home_2_item_title_tv);
				holderforpics.item_pics_gv = (GridView) convertView
						.findViewById(R.id.home_2_pics_grid_gv);
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
				holderforpics = (HolderViewForPics) convertView.getTag();
				break;
			}
		}
		switch (item_type) {
		case MateListItem.ItemType.TEXT_MODE:
			holderforText.layout0_rl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
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
			holderforText.title_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
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
				if (Session.getSession().getuSex().equals("M")) {
					holderforText.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avatar_male);
				} else {
					holderforText.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avatar_female);
				}
			} else {
				ImageLoader.getInstance().displayImage(url,
						holderforText.topic_icon_iv,options,null);
			}
			holderforText.title_tv.setText(datas.get(position).getTitle());
			holderforText.content_tv.setText(datas.get(position)
					.getSummary());
			String date = datas.get(position).getLastModify().trim();
			if(!date.equals("null")){
				holderforText.time_tv.setText(date);
			}									   
			break;
		case MateListItem.ItemType.TEXT_PICS_MODE:
			holderfortextPic.layout1_rl
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
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
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
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
				if (Session.getSession().getuSex().equals("M")) {
					holderfortextPic.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avatar_male);
				} else {
					holderfortextPic.topic_icon_iv
							.setImageResource(R.drawable.default_pic_avatar_female);
				}
			} else {
				ImageLoader.getInstance().displayImage(url2,
						holderfortextPic.topic_icon_iv,options,null);
			}
			
			holderfortextPic.content_tv.setText(datas.get(position)
					.getSummary());
			
			String date2 = datas.get(position).getLastModify().trim();
			if(!date2.equals("null")){
				holderfortextPic.time_tv.setText(date2);
			}

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			StringBuffer title = new StringBuffer(datas.get(position).getTitle());
			if(noPic){
				holderfortextPic.item_pic_iv.setVisibility(View.GONE);
				title  = title.append("  (多图)");
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

				}
			});
			holderforpics.title_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							CollectInfoListActivity.class);
					context.startActivity(intent);
				}
			});

			holderforpics.topic_tv
					.setText(datas.get(position).getTopText());
			holderforpics.title_tv.setText(datas.get(position).getTitle());
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
				intent.putExtra("title", datas.get(position).getTitle());
				intent.putExtra("type", String.valueOf(datas.get(position).getType()));
				intent.putExtra("content", datas.get(position).getSummary());
				intent.putExtra("create_by", datas.get(position).getCreateBy());
				intent.putExtra("name", datas.get(position).getTopText());
				intent.putExtra("photo", datas.get(position).getTopUrl());
				intent.putExtra("signature", datas.get(position).getUserSignature());
				intent.putExtra("sex", datas.get(position).getUserSex());
				intent.putExtra("vote_count", datas.get(position).getVoteCount());
				intent.putExtra("comment_count", datas.get(position).getCommentCount());
				break;
			default:
				break;
		}
		if (intent != null){
			context.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
		}
	}
	
	
}
