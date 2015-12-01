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
	
	private TextView nameTv ; 
	private TextView signatureTv;
	private ImageView userPhotoIv,userSexIv; 
	private RelativeLayout followLayout; 
	private FButton focusBtn ; 
	private TextView packCountTv,packTextTv;
	private TextView meFollowCountTv,meFollowTextTv;
	private TextView followMeCountTv,followMeTextTv;
	private RelativeLayout packLayout,meFollowLayout,followMeLayout;
	private TextView favoriteBoxTv,topicBoxTv; 
	private TextView profressionTv,emailTv,residenceTv;  
	private UserHomePresenter mPresenter ; 
	
	private BroadcastReceiver userUpdateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			UserInfoBean info = (UserInfoBean)intent.getSerializableExtra(StaticValue.USERINFO.USERINFOENTITY);
			nameTv.setText(info.getName());
			signatureTv.setText(info.getSignature());
			String profession = info.getProfession();
			if(TextUtils.isEmpty(profession)){
				profressionTv.setText("暂无职位描述");
			}else{
				profressionTv.setText(profession);
			}
			String residence = info.getResidence();
			if(TextUtils.isEmpty(residence)){
				residenceTv.setText("暂无");
			}else{
				residenceTv.setText(residence);
			}
			
			ImageLoader.getInstance().displayImage(info.getPhoto(), userPhotoIv);
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
		packCountTv = (TextView)rootView.findViewById(R.id.pack_count_tv);
		packTextTv=(TextView)rootView.findViewById(R.id.pack_text_tv);
		meFollowCountTv=(TextView)rootView.findViewById(R.id.my_follow_count_tv);
		meFollowTextTv=(TextView)rootView.findViewById(R.id.my_follow_text_tv);
		followMeCountTv=(TextView)rootView.findViewById(R.id.follow_me_count_tv);
		followMeTextTv=(TextView)rootView.findViewById(R.id.follow_me_text_tv);
		packLayout = (RelativeLayout)rootView.findViewById(R.id.pack_layout);
		favoriteBoxTv = (TextView)rootView.findViewById(R.id.user_favoritebox);
		topicBoxTv = (TextView)rootView.findViewById(R.id.user_topic);
		profressionTv = (TextView)rootView.findViewById(R.id.user_profession_tv);
		emailTv = (TextView)rootView.findViewById(R.id.user_email_tv);
		residenceTv = (TextView)rootView.findViewById(R.id.user_residence_tv);
		userSexIv = (ImageView)rootView.findViewById(R.id.user_sex_icon);
		packLayout.setOnClickListener(new OnClickListener(){				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CollectPackListActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		meFollowLayout = (RelativeLayout)rootView.findViewById(R.id.my_follow_layout);
		meFollowLayout.setOnClickListener(new OnClickListener() {				
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
		followMeLayout = (RelativeLayout)rootView.findViewById(R.id.follow_me_layout);
		followMeLayout.setOnClickListener(new OnClickListener() {				
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
		
		nameTv = (TextView)rootView.findViewById(R.id.user_nick_name);
		signatureTv = (TextView)rootView.findViewById(R.id.signature);
		userPhotoIv = (ImageView)rootView.findViewById(R.id.user_photo);	
		followLayout = (RelativeLayout)rootView.findViewById(R.id.focus_layout_rl);
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
		favoriteBoxTv.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				Intent intent = new Intent(getActivity(),FavoriteBoxActivity.class);
				intent.putExtra(StaticValue.USERINFO.USER_ID, (String)mPresenter.getUserinfo().get("uid"));
				getActivity().startActivity(intent);
			}
		});
		 
		
		topicBoxTv.setOnClickListener(new OnClickListener() {				
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
			nameTv.setText(Session.getSession().getuName());
			sex = Session.getSession().getuSex();
			String signature = Session.getSession().getuSignature();
			if(TextUtils.isEmpty(signature) ||signature.equals("null")){
				signatureTv.setText("暂无简介");
			}else{
				signatureTv.setText(signature);
			}
			 
			String url = Session.getSession().getuPic();
			if(TextUtils.isEmpty(url) || url.equals("null")){
				userPhotoIv.setImageResource(R.drawable.default_pic_avata);
			}else{
				ImageLoader.getInstance().displayImage(url, userPhotoIv,Constants.defaultAvataOptions);
			}			
		}else{
			String uid = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
			mPresenter.getUserinfo().put("uid", uid);
			String name = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_NAME);
			String signature = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_SIGNATURE);
			String url = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_PHOTO);
			url = url.replace(Constants.Config.USER_PIC_SMALL,Constants.Config.USER_PIC_BIG);
			if(uid!=null && uid.equals(Session.getSession().getuId())){
				followLayout.setVisibility(View.GONE);
			}else{
				followLayout.setVisibility(View.VISIBLE);
			}		
			nameTv.setText(name);
			if(TextUtils.isEmpty(signature) ||signature.equals("null")){
				signatureTv.setText("暂无简介");
			}else{
				signatureTv.setText(signature);
			}
			sex = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_SEX);
			if(uid!=null && uid.equals(Session.getSession().getuId())){
				//...
			}else if(StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)){
				packTextTv.setText("他的收集册");
				meFollowTextTv.setText("他关注的人");
				followMeTextTv.setText("关注他的人");
				favoriteBoxTv.setText("他的收藏");
				topicBoxTv.setText("他的话题");				
			}else{
				packTextTv.setText("她的收集册");
				meFollowTextTv.setText("她关注的人");
				followMeTextTv.setText("关注她的人");
				favoriteBoxTv.setText("她的收藏");
				topicBoxTv.setText("她的话题");		
			}
			if(TextUtils.isEmpty(url) || url.equals("null")){			
				userPhotoIv.setImageResource(R.drawable.default_pic_avata);
			}else{
				ImageLoader.getInstance().displayImage(url, userPhotoIv,Constants.defaultAvataOptions);
			}				
		}		
		if(StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)){
			userSexIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_m_w));
		}else{
			userSexIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_f_w));
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
		followMeCountTv.setText(text);
	}

	@Override
	public void setMeFollowCount(String text) {
		// TODO Auto-generated method stub
		meFollowCountTv.setText(text);
	}

	@Override
	public void setPackCount(String text) {
		// TODO Auto-generated method stub
		packCountTv.setText(text);						
	}

	@Override
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		emailTv.setText(email);
	}

	@Override
	public void setProfession(String profession) {
		// TODO Auto-generated method stub
		profressionTv.setText(profession);

	}

	@Override
	public void setResidence(String residence) {
		// TODO Auto-generated method stub
		residenceTv.setText(residence);	
	}

	@Override
	public String getFollowCount() {
		// TODO Auto-generated method stub
		return followMeCountTv.getText().toString().trim();
	}

	@Override
	public Fragment getFragment() {
		// TODO Auto-generated method stub
		return this;
	}

	
}