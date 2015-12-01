package com.kuibu.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.UserInfoBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterfaces.IConstructFragment;
import com.kuibu.module.presenter.UserHomePresenterImpl;
import com.kuibu.module.presenter.interfaces.UserHomePresenter;
import com.kuibu.ui.activity.CollectPackListActivity;
import com.kuibu.ui.activity.FavoriteBoxActivity;
import com.kuibu.ui.activity.UserListActivity;
import com.kuibu.ui.activity.UserTopicListActivity;
import com.kuibu.ui.view.interfaces.UserHomeView;
import com.nostra13.universalimageloader.core.ImageLoader;

public final class UserHomeFragment extends BaseFragment implements
		IConstructFragment,UserHomeView {		
	
	private TextView user_name_tv ; 
	private TextView signature_tv  ;
	private ImageView user_photo_iv,user_sex_iv; 
	private RelativeLayout focus_layout_rl; 
	private FButton focusBtn ; 
	private TextView pack_count_tv,pack_text_tv;
	private TextView my_follow_count_tv,my_follow_text_tv;
	private TextView follow_me_count_tv,follow_me_text_tv;
	private RelativeLayout pack_layout,my_follow_layout,follow_me_layout;
	private TextView favorite_box_tv,topic_box_tv; 
	private TextView user_profession_tv,user_email_tv,user_residence_tv;  
	private UserHomePresenter mPresenter ; 
	
	private BroadcastReceiver userUpdateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			UserInfoBean info = (UserInfoBean)intent.getSerializableExtra(StaticValue.USERINFO.USERINFOENTITY);
			user_name_tv.setText(info.getName());
			signature_tv.setText(info.getSignature());
			String profession = info.getProfession();
			if(TextUtils.isEmpty(profession)){
				user_profession_tv.setText("暂无职位描述");
			}else{
				user_profession_tv.setText(profession);
			}
			String residence = info.getResidence();
			if(TextUtils.isEmpty(residence)){
				user_residence_tv.setText("暂无");
			}else{
				user_residence_tv.setText(residence);
			}
			
			ImageLoader.getInstance().displayImage(info.getPhoto(), user_photo_iv);
		}		
	};
	
	@Override
	public BaseFragment newInstance(String tag) {
		UserHomeFragment fragment = new UserHomeFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.user_info_home,container,false);
		pack_count_tv = (TextView)rootView.findViewById(R.id.pack_count_tv);
		pack_text_tv=(TextView)rootView.findViewById(R.id.pack_text_tv);
		my_follow_count_tv=(TextView)rootView.findViewById(R.id.my_follow_count_tv);
		my_follow_text_tv=(TextView)rootView.findViewById(R.id.my_follow_text_tv);
		follow_me_count_tv=(TextView)rootView.findViewById(R.id.follow_me_count_tv);
		follow_me_text_tv=(TextView)rootView.findViewById(R.id.follow_me_text_tv);
		pack_layout = (RelativeLayout)rootView.findViewById(R.id.pack_layout);
		favorite_box_tv = (TextView)rootView.findViewById(R.id.user_favoritebox);
		topic_box_tv = (TextView)rootView.findViewById(R.id.user_topic);
		user_profession_tv = (TextView)rootView.findViewById(R.id.user_profession_tv);
		user_email_tv = (TextView)rootView.findViewById(R.id.user_email_tv);
		user_residence_tv = (TextView)rootView.findViewById(R.id.user_residence_tv);
		user_sex_iv = (ImageView)rootView.findViewById(R.id.user_sex_icon);
		pack_layout.setOnClickListener(new OnClickListener(){				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CollectPackListActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		my_follow_layout = (RelativeLayout)rootView.findViewById(R.id.my_follow_layout);
		my_follow_layout.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),UserListActivity.class);
				intent.putExtra("follow_who", "OTHERS");
				intent.putExtra(StaticValue.USERINFO.USER_ID,(String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);	
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		follow_me_layout = (RelativeLayout)rootView.findViewById(R.id.follow_me_layout);
		follow_me_layout.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),UserListActivity.class);
				intent.putExtra("follow_who", "ME");
				intent.putExtra(StaticValue.USERINFO.USER_ID,(String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		user_name_tv = (TextView)rootView.findViewById(R.id.user_nick_name);
		signature_tv = (TextView)rootView.findViewById(R.id.signature);
		user_photo_iv = (ImageView)rootView.findViewById(R.id.user_photo);	
		focus_layout_rl = (RelativeLayout)rootView.findViewById(R.id.focus_layout_rl);
		focusBtn = (FButton)rootView.findViewById(R.id.focus_collectpack_bt);
		focusBtn.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Session.getSession().isLogin()){
					mPresenter.followCollector();
				}else{
					Toast.makeText(getActivity(), getActivity().getString(R.string.need_login), 
							Toast.LENGTH_SHORT).show();
				}
			}
		});			
		favorite_box_tv.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				Intent intent = new Intent(getActivity(),FavoriteBoxActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
			}
		});
		 
		
		topic_box_tv.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),UserTopicListActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID,(String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});			
		IntentFilter ifilterPickpic = new IntentFilter();
		ifilterPickpic.addAction(StaticValue.USER_INFO_UPDATE);
		getActivity().registerReceiver(userUpdateReceiver, ifilterPickpic);
		
		mPresenter = new UserHomePresenterImpl(this);
		return rootView;
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initData(); //solve problem : Fragment（XXFragment） not attached to Activity
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}
	
	@SuppressWarnings("deprecation")
	void initData()
	{
		boolean flag = this.getActivity().getIntent().getBooleanExtra(
				StaticValue.USERINFO.SHOWLAYOUT, false);
		String sex = null;
		if(!flag){
			mPresenter.getUserinfo().put("uid", Session.getSession().getuId());
			user_name_tv.setText(Session.getSession().getuName());
			sex = Session.getSession().getuSex();
			String signature = Session.getSession().getuSignature();
			if(TextUtils.isEmpty(signature) ||signature.equals("null")){
				signature_tv.setText("暂无简介");
			}else{
				signature_tv.setText(signature);
			}
			 
			String url = Session.getSession().getuPic();
			if(TextUtils.isEmpty(url) || url.equals("null")){
				user_photo_iv.setImageResource(R.drawable.default_pic_avata);
			}else{
				ImageLoader.getInstance().displayImage(url, user_photo_iv,Constants.defaultAvataOptions);
			}			
		}else{
			String uid = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
			mPresenter.getUserinfo().put("uid", uid);
			String name = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_NAME);
			String signature = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_SIGNATURE);
			String url = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_PHOTO);
			url = url.replace(Constants.Config.USER_PIC_SMALL,Constants.Config.USER_PIC_BIG);
			if(uid!=null && uid.equals(Session.getSession().getuId())){
				focus_layout_rl.setVisibility(View.GONE);
			}else{
				focus_layout_rl.setVisibility(View.VISIBLE);
			}		
			user_name_tv.setText(name);
			if(TextUtils.isEmpty(signature) ||signature.equals("null")){
				signature_tv.setText("暂无简介");
			}else{
				signature_tv.setText(signature);
			}
			sex = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_SEX);
			if(uid!=null && uid.equals(Session.getSession().getuId())){
				//...
			}else if(StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)){
				pack_text_tv.setText("他的收集册");
				my_follow_text_tv.setText("他关注的人");
				follow_me_text_tv.setText("关注他的人");
				favorite_box_tv.setText("他的收藏");
				topic_box_tv.setText("他的话题");				
			}else{
				pack_text_tv.setText("她的收集册");
				my_follow_text_tv.setText("她关注的人");
				follow_me_text_tv.setText("关注她的人");
				favorite_box_tv.setText("她的收藏");
				topic_box_tv.setText("她的话题");		
			}
			if(TextUtils.isEmpty(url) || url.equals("null")){			
				user_photo_iv.setImageResource(R.drawable.default_pic_avata);
			}else{
				ImageLoader.getInstance().displayImage(url, user_photo_iv,Constants.defaultAvataOptions);
			}				
		}		
		if(StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)){
			user_sex_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_m_w));
		}else{
			user_sex_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_f_w));
		}
		mPresenter.loadUserinfo();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(userUpdateReceiver);
	}

	@Override
	public void setFollowBtnColor(int color) {
		// TODO Auto-generated method stub
		focusBtn.setButtonColor(color);						
	}

	@Override
	public void setFollowBtnText(String text) {
		// TODO Auto-generated method stub
		focusBtn.setText(text);
	}

	@Override
	public void setFollowMeCount(String text) {
		// TODO Auto-generated method stub
		follow_me_count_tv.setText(text);
	}

	@Override
	public void setMeFollowCount(String text) {
		// TODO Auto-generated method stub
		my_follow_count_tv.setText(text);
	}

	@Override
	public void setPackCount(String text) {
		// TODO Auto-generated method stub
		pack_count_tv.setText(text);						
	}

	@Override
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		user_email_tv.setText(email);
	}

	@Override
	public void setProfession(String profession) {
		// TODO Auto-generated method stub
		user_profession_tv.setText(profession);

	}

	@Override
	public void setResidence(String residence) {
		// TODO Auto-generated method stub
		user_residence_tv.setText(residence);	
	}

	@Override
	public String getFollowCount() {
		// TODO Auto-generated method stub
		return follow_me_count_tv.getText().toString().trim();
	}

	@Override
	public Fragment getFragment() {
		// TODO Auto-generated method stub
		return this;
	}

	
}