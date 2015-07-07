package com.kuibu.module.menu;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kuibu.module.activity.ImageScanActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterf.ICamera;

public class PopupWindowsMenu extends PopupWindow implements OnClickListener
{
	private Context mContext ; 
	private int max  = 0 ;
	private ICamera camera;
	
	public PopupWindowsMenu(Context mContext, View parent,int max)
	{
		super(mContext);
		this.mContext = mContext ; 
		this.max  = max;  
		this.camera = (ICamera)mContext;
		View view = View
				.inflate(mContext, R.layout.item_popupwindows, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_ins));
		LinearLayout ll_popup = (LinearLayout) view
				.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in_2));

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
	//	setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		Button camera_btn = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button photo_btn = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button cancel_btn = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		camera_btn.setOnClickListener(this);
		photo_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
			int id = view.getId();
			if(R.id.item_popupwindows_Photo == id){
				Intent mIntent = new Intent(mContext,
						ImageScanActivity.class);
				mIntent.putExtra("max", max);
				mContext.startActivity(mIntent);			
				dismiss();
			}else if(R.id.item_popupwindows_cancel == id){
				dismiss();
			}else if(R.id.item_popupwindows_camera == id){
				camera.takePhotoByCamera();
				dismiss();
			}			
	}	
}
