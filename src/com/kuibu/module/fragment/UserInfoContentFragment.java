package com.kuibu.module.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.VolleyErrorHelper;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.UserInfoBean;
import com.kuibu.module.activity.CollectPackListActivity;
import com.kuibu.module.activity.FavoriteBoxActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.activity.UserListActivity;
import com.kuibu.module.activity.UserTopicListActivity;
import com.kuibu.module.iterfaces.IConstructFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

public final class UserInfoContentFragment extends Fragment implements
		IConstructFragment {		
	
	private TextView user_name_tv ; 
	private TextView signature_tv  ;
	private ImageView user_photo_iv,user_sex_iv; 
	private RelativeLayout focus_layout_rl; 
	private FButton focusBtn ; 
	private Map<String,Object> uInfo = new HashMap<String,Object>() ; 
	private boolean bUserIsFollow;  	
	private TextView pack_count_tv,pack_text_tv;
	private TextView my_follow_count_tv,my_follow_text_tv;
	private TextView follow_me_count_tv,follow_me_text_tv;
	private RelativeLayout pack_layout,my_follow_layout,follow_me_layout;
	private TextView favorite_box_tv,topic_box_tv; 
	private TextView user_profession_tv,user_email_tv,user_residence_tv;  

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
	public Fragment newInstance(String tag) {
		UserInfoContentFragment fragment = new UserInfoContentFragment();
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
					intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
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
					intent.putExtra(StaticValue.USERINFO.USER_ID,(String)uInfo.get("uid"));
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
					intent.putExtra(StaticValue.USERINFO.USER_ID,(String)uInfo.get("uid"));
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
						doFocus();
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
					intent.putExtra(StaticValue.USERINFO.USER_ID, (String)uInfo.get("uid"));
					getActivity().startActivity(intent);
				}
			});
			topic_box_tv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),UserTopicListActivity.class);
					intent.putExtra(StaticValue.USERINFO.USER_ID,(String)uInfo.get("uid"));
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});			
			IntentFilter ifilterPickpic = new IntentFilter();
			ifilterPickpic.addAction(StaticValue.USER_INFO_UPDATE);
			getActivity().registerReceiver(userUpdateReceiver, ifilterPickpic);			
		return rootView;
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initData(); //solve problem : Fragment（XXFragment） not attached to Activity
	}

	@SuppressWarnings("deprecation")
	void initData()
	{
		boolean flag = this.getActivity().getIntent().getBooleanExtra(
				StaticValue.USERINFO.SHOWLAYOUT, false);
		String sex = null;
		if(!flag){
			uInfo.put("uid", Session.getSession().getuId());
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
				ImageLoader.getInstance().displayImage(url, user_photo_iv);
			}			
		}else{
			String uid = this.getActivity().getIntent().getStringExtra(StaticValue.USERINFO.USER_ID);
			uInfo.put("uid", uid);
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
				ImageLoader.getInstance().displayImage(url, user_photo_iv);
			}				
		}		
		if(StaticValue.SERMODLE.USER_SEX_MALE.equals(sex)){
			user_sex_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_m_w));
		}else{
			user_sex_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_gender_f_w));
		}
		requestDetail();
	}
	
	private void requestDetail()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", (String)uInfo.get("uid"));
		params.put("requestor_id", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_userdetail").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response.getString("result"));
						pack_count_tv.setText(DataUtils.formatNumber(obj.getInt("pack_count")));						
						my_follow_count_tv.setText(DataUtils.formatNumber(obj.getInt("my_follow_count")));
						follow_me_count_tv.setText(DataUtils.formatNumber(obj.getInt("follow_me_count")));
						user_email_tv.setText(obj.getString("email"));
						String profession = obj.getString("profession");
						if(TextUtils.isEmpty(profession) || profession.equals("null")){
							user_profession_tv.setText("暂无职业描述");
						}else{
							user_profession_tv.setText(profession);
						}
						String residence = obj.getString("residence");
						if(TextUtils.isEmpty(residence) || residence.equals("null")){
							user_residence_tv.setText("暂无");
						}else{
							user_residence_tv.setText(residence);
						}
						bUserIsFollow = obj.getBoolean("is_focus");
						if(bUserIsFollow && Session.getSession().isLogin()){
							if(!isDetached()){
								@SuppressWarnings("deprecation")
								int btnColor= getActivity().getResources().getColor(R.color.fbutton_color_concrete);
								focusBtn.setButtonColor(btnColor);						
								focusBtn.setText(getActivity().getString(R.string.btn_cancel_focus));
							}											
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	private void doFocus()
	{		
		Map<String, String> params = new HashMap<String, String>();
		params.put("follower_id", Session.getSession().getuId());
		params.put("type", StaticValue.SERMODLE.COLLECTOR_TYPE);
		params.put("obj_id", (String)uInfo.get("uid"));
		final String URL;
		if(bUserIsFollow){ 
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_follows").toString();
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
				  .append(Constants.Config.REST_API_VERSION)
				  .append("/add_follows").toString();
		}
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state) && !isDetached()) {
						int count = Integer.parseInt(follow_me_count_tv.getText().toString().trim());						
						if(bUserIsFollow){
							follow_me_count_tv.setText(DataUtils.formatNumber(count-1));
							int btnColor= getResources().getColor(R.color.fbutton_color_green_sea);
							focusBtn.setButtonColor(btnColor);						
							focusBtn.setText(getActivity().getString(R.string.btn_focus));
							bUserIsFollow = false; 
						}else{
							follow_me_count_tv.setText(DataUtils.formatNumber(count+1));
							int btnColor= getResources().getColor(R.color.fbutton_color_concrete);
							focusBtn.setButtonColor(btnColor);						
							focusBtn.setText(getActivity().getString(R.string.btn_cancel_focus));
							bUserIsFollow = true; 
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				Toast.makeText(getActivity().getApplicationContext(), 
						VolleyErrorHelper.getMessage(error, getActivity().getApplicationContext()), 
						Toast.LENGTH_SHORT).show();
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {   
	 			return KuibuUtils.prepareReqHeader();  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(userUpdateReceiver);
	}
	
}