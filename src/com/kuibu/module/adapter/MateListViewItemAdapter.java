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
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.AppInfo;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.MateListItem;
import com.kuibu.module.activity.R;
import com.kuibu.ui.activity.AlbumInfoActivity;
import com.kuibu.ui.activity.CollectionDetailActivity;
import com.kuibu.ui.activity.CollectionImageDetailActivity;
import com.kuibu.ui.activity.UserInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MateListViewItemAdapter extends BaseAdapter {
	private List<MateListItem> datas;
	private Context context;
	private DisplayImageOptions options;
	private int listItemDefaultImageId;
	private boolean bwithTop ; 
	
	public class HolderViewForText{
		RelativeLayout layout0_rl;
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		TextView content_tv; 
		TextView vote_count_tv; 
		TextView comment_count_tv;
	}
	
	public class HolderViewForTextPics{
		RelativeLayout layout1_rl;
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		TextView content_tv;
		ImageView item_pic_iv; 
		TextView vote_count_tv; 
		TextView comment_count_tv;
	}
	
	public class HolderViewForPics{
		RelativeLayout layout2_rl;
		TextView user_tv;
		ImageView user_icon_iv;
		TextView title_tv;
		ImageView image_iv;
		TextView desc_tv ; 
		TextView vote_count_tv; 
		TextView comment_count_tv;
	}

	public MateListViewItemAdapter(Context context, List<MateListItem> datas,boolean bwithtop) {
		this.datas = datas;
		this.context = context ;
		this.bwithTop = bwithtop;
		initStyle();
	}
	
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
		.showImageOnLoading(ContextCompat.getDrawable(context,listItemDefaultImageId))
		.showImageForEmptyUri(ContextCompat.getDrawable(context,listItemDefaultImageId))
		.showImageOnFail(ContextCompat.getDrawable(context,listItemDefaultImageId))
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
		return datas == null ? 0 : datas.size();
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
		HolderViewForPics holderforPics = null ; 
		
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
				holderforText.vote_count_tv = (TextView) convertView
						.findViewById(R.id.mate_0_vote_count_tv);
				holderforText.comment_count_tv= (TextView) convertView
						.findViewById(R.id.mate_0_comment_count_tv);
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
				holderfortextPic.vote_count_tv = (TextView) convertView
						.findViewById(R.id.mate_1_vote_count_tv);
				holderfortextPic.comment_count_tv= (TextView) convertView
						.findViewById(R.id.mate_1_comment_count_tv);
				convertView.setTag(holderfortextPic);
				break;
			case MateListItem.ItemType.PICS_MODE:
				convertView = LayoutInflater.from(context).inflate(R.layout.mate_list_view_item_2,
						parent,false);
				holderforPics = new HolderViewForPics();
				holderforPics.layout2_rl = (RelativeLayout)convertView.findViewById(R.id.mate_2_rela_layout2_rl);			
				holderforPics.user_tv = (TextView) convertView
						.findViewById(R.id.mate_2_user_tv);
				holderforPics.user_icon_iv = (ImageView) convertView
						.findViewById(R.id.mate_2_user_pic_iv);
				holderforPics.title_tv = (TextView) convertView
						.findViewById(R.id.mate_2_item_title_tv);
				holderforPics.image_iv = (ImageView) convertView
						.findViewById(R.id.mate_2_item_image_iv);
				holderforPics.image_iv.setAdjustViewBounds(true);
				holderforPics.image_iv.setMaxWidth(AppInfo.getInstance().screenWidth);
				holderforPics.desc_tv = (TextView)convertView.findViewById(R.id.mate_2_item_desc_tv);
				holderforPics.vote_count_tv = (TextView) convertView
						.findViewById(R.id.mate_2_vote_count_tv);
				holderforPics.comment_count_tv= (TextView) convertView
						.findViewById(R.id.mate_2_comment_count_tv);
				convertView.setTag(holderforPics);
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
				holderforPics = (HolderViewForPics)convertView.getTag();
				break;
			}
		}
		switch(item_type){
		case MateListItem.ItemType.TEXT_MODE:
			if(!bwithTop){
				holderforText.layout0_rl.setVisibility(View.GONE);				
			}else{
				holderforText.layout0_rl.setVisibility(View.VISIBLE);
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
				holderforText.user_tv.setText(datas.get(position).getTopText());		
				String url = datas.get(position).getTopUrl();
				if(TextUtils.isEmpty(url) || url.equals("null")){
					holderforText.user_icon_iv.setImageResource(R.drawable.default_pic_avata);
				}else{
					ImageLoader.getInstance().displayImage(url,
							holderforText.user_icon_iv,options,null);
				}
			}
			
			holderforText.title_tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LayoutInflater.from(context).getContext(),
							AlbumInfoActivity.class);
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
			holderforText.title_tv.setText(datas.get(position)
					.getTitle());
			holderforText.content_tv.setText(datas.get(position)
					.getSummary());
			holderforText.vote_count_tv.setText(new StringBuilder(DataUtils
					.formatNumber(datas.get(position).getVoteCount())).append(" 赞"));

			holderforText.comment_count_tv.setText(new StringBuilder(DataUtils
					.formatNumber(datas.get(position).getCommentCount())).append(" 评论"));
			break;
		case MateListItem.ItemType.TEXT_PICS_MODE:
			if(!bwithTop){
				holderfortextPic.layout1_rl.setVisibility(View.GONE);
			}else{
				holderfortextPic.layout1_rl.setVisibility(View.VISIBLE);
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
				holderfortextPic.user_tv.setText(datas.get(position)
						.getTopText());
				
				String url = datas.get(position).getTopUrl();
				if(TextUtils.isEmpty(url) || url.equals("null")){
					holderfortextPic.user_icon_iv.setImageResource(R.drawable.default_pic_avata);
				}else{
					ImageLoader.getInstance().displayImage(url,
							holderfortextPic.user_icon_iv,options,null);
				}				
			}
			
			holderfortextPic.title_tv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub	
					Intent intent = new Intent(LayoutInflater.from(context).getContext(),
							AlbumInfoActivity.class);
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
			
			String summary = datas.get(position).getSummary();
			if(TextUtils.isEmpty(summary) || summary.equals("null")){
				holderfortextPic.content_tv.setText("(多图)");
			}else{
				holderfortextPic.content_tv.setText(summary);
			}			
			holderfortextPic.vote_count_tv.setText(new StringBuilder(
					DataUtils.formatNumber(datas.get(position).getVoteCount())).append(" 赞"));
			holderfortextPic.comment_count_tv.setText(new StringBuilder(DataUtils
					.formatNumber(datas.get(position).getCommentCount())).append(" 评论"));
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			StringBuffer title = new StringBuffer(datas.get(position).getTitle());
			if(noPic){
				holderfortextPic.item_pic_iv.setVisibility(View.GONE);
				title  = title.append("  (图)");
			}else{
				ImageLoader.getInstance().displayImage(datas.get(position).getItemPic(),
						holderfortextPic.item_pic_iv);
			}
			holderfortextPic.title_tv.setText(title);
			break;
		case MateListItem.ItemType.PICS_MODE:
			if(!bwithTop){
				holderforPics.layout2_rl.setVisibility(View.GONE);				
			}else{
				holderforPics.layout2_rl.setVisibility(View.VISIBLE);
				holderforPics.layout2_rl.setOnClickListener(new OnClickListener(){
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
				holderforPics.user_tv.setText(datas.get(position).getTopText());		
				String url = datas.get(position).getTopUrl();
				if(TextUtils.isEmpty(url) || url.equals("null")){
					holderforPics.user_icon_iv.setImageResource(R.drawable.default_pic_avata);
				}else{
					ImageLoader.getInstance().displayImage(url,
							holderforPics.user_icon_iv,options);
				}
			}
			
			holderforPics.title_tv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(LayoutInflater.from(context).getContext(),
							AlbumInfoActivity.class);
					intent.putExtra("pack_id", datas.get(position).getPackId());
					intent.putExtra("type", String.valueOf(datas.get(position).getType()));
					intent.putExtra("create_by", datas.get(position).getCreateBy());
					LayoutInflater.from(context).getContext().startActivity(intent);	
				}
			});
			
			holderforPics.image_iv.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,CollectionImageDetailActivity.class);
					intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_ID,
							datas.get(position).getId()) ;
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left,
							R.anim.anim_slide_out_left);
				}
			});
			boolean bnoPic = PreferencesUtils.getBooleanByDefault(context,
					StaticValue.PrefKey.NO_PICTRUE_KEY, false);
			
			StringBuffer title1 = new StringBuffer(datas.get(position).getTitle());
			if(bnoPic){
				holderforPics.image_iv.setVisibility(View.GONE);
				title1  = title1.append("  (图)");
			}else{
				// ImageLoad
				holderforPics.image_iv.setVisibility(View.VISIBLE);
				String item_url = datas.get(position).getItemPic();
				ImageLoader.getInstance().displayImage(item_url,
						holderforPics.image_iv,options);
			}
			
			holderforPics.title_tv.setText(datas.get(position).getTitle().replace("\n", ""));
			String desc = datas.get(position).getSummary();
			if(TextUtils.isEmpty(desc)){
				holderforPics.desc_tv.setVisibility(View.GONE);
			}else{
				holderforPics.desc_tv.setVisibility(View.VISIBLE);
				holderforPics.desc_tv.setText(desc);
			}			
			holderforPics.vote_count_tv.setText(new StringBuilder(
					DataUtils.formatNumber(datas.get(position).getVoteCount())).append(" 赞"));
			holderforPics.comment_count_tv.setText(new StringBuilder(DataUtils
					.formatNumber(datas.get(position).getCommentCount())).append(" 评论"));
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
			intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN,
					datas.get(position).getCisn());
			break;
		default:
			break;
		}
		if(intent!=null)
			LayoutInflater.from(context).getContext().startActivity(intent);
	}
}
