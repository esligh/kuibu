package com.kuibu.module.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.UserInfoBean;
import com.kuibu.module.iterf.ICamera;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class UserInfoEditActivity extends BaseActivity implements ICamera{
	private ImageView userPhoto ; 
	private TextView userName,userSexM,userSexF;
	private TextView userSignature,userProfession,userResidence,userEducation; 
	private UserInfoBean mInfo =new UserInfoBean();
	private boolean bModifyPhoto  = false ;
	private FinalHttp finalHttp = null;
	private Uri cameraPic = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_edit_activity);
		setTitle(getString(R.string.action_modify));
		userPhoto = (ImageView)findViewById(R.id.user_pic_iv);
		userPhoto.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub				
				showPopup(view);
			}
		});		
		userName = (TextView)findViewById(R.id.user_name_tv);
		userName.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				final EditText input = new EditText(UserInfoEditActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
				builder.setTitle(getString(R.string.input_text))
        		.setView(input)
                .setNegativeButton(getString(R.string.btn_cancel), null)
				.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				String new_name = input.getText().toString().trim();
        				mInfo.setName(new_name);
        				userName.setText(new_name);
        			}
        		}).show();
			}
		});		
		userSexM = (TextView)findViewById(R.id.user_sex_m_tv);
		userSexM.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				mInfo.setSex(StaticValue.SERMODLE.USER_SEX_MALE);
				switchSexBack(StaticValue.SERMODLE.USER_SEX_MALE);				
			}
		});
		userSexF = (TextView)findViewById(R.id.user_sex_f_tv);
		userSexF.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mInfo.setSex(StaticValue.SERMODLE.USER_SEX_FEMALE);
				switchSexBack(StaticValue.SERMODLE.USER_SEX_FEMALE);
			}
		});
		userSignature = (TextView)findViewById(R.id.user_signature_tv);
		userSignature.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText input = new EditText(UserInfoEditActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
				builder.setTitle(getString(R.string.input_text))
        		.setView(input)
                .setNegativeButton(getString(R.string.btn_cancel), null)
				.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				String signature = input.getText().toString().trim();
        				userSignature.setText(signature);
        				mInfo.setSignature(signature);
        			}
        		}).show();
			}
		});
		userProfession = (TextView)findViewById(R.id.user_profession_tv);
		userProfession.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final EditText input = new EditText(UserInfoEditActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
				builder.setTitle(getString(R.string.input_text))
        		.setView(input)
                .setNegativeButton(getString(R.string.btn_cancel), null)
				.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				String profession = input.getText().toString().trim();
        				userProfession.setText(profession);
        				mInfo.setProfession(profession);
        			}
        		}).show();
			}
		});
		userResidence=(TextView)findViewById(R.id.user_residence_tv);
		userResidence.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				final EditText input = new EditText(UserInfoEditActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
				builder.setTitle(getString(R.string.input_text))
        		.setView(input)
                .setNegativeButton(getString(R.string.btn_cancel), null)
				.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				String residence = input.getText().toString().trim();
        				userResidence.setText(residence);
        				mInfo.setResidence(residence);
        			}
        		}).show();
			}
		});
		userEducation=(TextView)findViewById(R.id.user_education_tv);
		userEducation.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub		
				final EditText input = new EditText(UserInfoEditActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoEditActivity.this);
				builder.setTitle(getString(R.string.input_text))
        		.setView(input)
                .setNegativeButton(getString(R.string.btn_cancel), null)
				.setPositiveButton(getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				String education = input.getText().toString().trim();
        				userEducation.setText(education);
        				mInfo.setEducation(education);
        			}
        		}).show();
			}
		});
		finalHttp = new FinalHttp();
		requestDetail();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void switchSexBack(String sex)
	{
		if(isDarkTheme){
			if(sex.equals(StaticValue.SERMODLE.USER_SEX_MALE)){
				userSexM.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				userSexF.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
			}else{
				userSexF.setBackgroundColor(getResources().getColor(R.color.list_item_bg_dark_super_highlight));
				userSexM.setBackgroundColor(getResources().getColor(R.color.list_view_bg_dark));
			}	
		}else{
			if(sex.equals(StaticValue.SERMODLE.USER_SEX_MALE)){
				userSexM.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				userSexF.setBackgroundColor(getResources().getColor(R.color.white));
			}else{
				userSexF.setBackgroundColor(getResources().getColor(R.color.SkyBlue));
				userSexM.setBackgroundColor(getResources().getColor(R.color.white));
			}	
		}					
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem add = menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.SAVE_ID,
				StaticValue.MENU_ORDER.SAVE_ORDER_ID, "保存");

		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()){
	        	case android.R.id.home:
	        		this.onBackPressed();
	        		break;
	        	case StaticValue.MENU_ITEM.SAVE_ID:
	        		if(bModifyPhoto){
	        			uploadUserpic();
	        		}else{
	        			requestUpdate();
	        		}    		
	        		break;
	        }
	        return super.onOptionsItemSelected(item);	 
	 }	 
	 
	 private void uploadUserpic()
	{
			AjaxParams params = new AjaxParams();
			try {
				params.put("user_pic", new File(this.getCacheDir(),Constants.Config.USER_PHOTO_TMP));
				params.put("uid", mInfo.getId());
				params.put("email", mInfo.getEmail());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			final String URL = new StringBuilder(Constants.Config.SERVER_URI)
									.append(Constants.Config.REST_API_VERSION)
									.append("/upload_userpic").toString();
			finalHttp.post(URL, params, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					if (!TextUtils.isEmpty(t)) {
						try {
							JSONObject obj = new JSONObject(t);
							String state = obj.getString("state");
							if(StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS.equals(state)){
								requestUpdate();
							}else if(StaticValue.RESPONSE_STATUS.UPLOAD_FAILD.equals(state)){								
								Toast.makeText(UserInfoEditActivity.this, "修改头像失败", Toast.LENGTH_LONG).show();
							}						
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
	 }
	 
	 private void requestDetail()
	 {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/get_userinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response.getString("result"));
						mInfo.setId(obj.getString("id"));
						mInfo.setEmail(obj.getString("email"));
						mInfo.setPhoto(obj.getString("photo"));
						mInfo.setName(obj.getString("name"));
						userName.setText(mInfo.getName());
						String signature= obj.getString("signature");							
						if(TextUtils.isEmpty(signature) || signature.equals("null")){
							userSignature.setText("请填写");
							mInfo.setSignature("");
						}else{
							userSignature.setText(signature);
							mInfo.setSignature(signature);
						}
						String profession = obj.getString("profession");
						if(TextUtils.isEmpty(profession) || profession.equals("null")){
							userProfession.setText("请填写");
							mInfo.setSignature("");
						}else{
							userProfession.setText(profession);
							mInfo.setSignature(profession);
						}
						
						String residence = obj.getString("residence");
						if(TextUtils.isEmpty(profession) || residence.equals("null")){
							userResidence.setText("请填写");
							mInfo.setSignature("");
						}else{
							userResidence.setText(residence);
							mInfo.setSignature(residence);
						}
						
						String education = obj.getString("education");
						if(TextUtils.isEmpty(education) || education.equals("null")){
							userEducation.setText("请填写");
							mInfo.setSignature("");
						}else{
							userEducation.setText(education);
							mInfo.setSignature(education);
						}
						String url = obj.getString("photo") ; 
						if(TextUtils.isEmpty(url) || url.equals("null")){
							userPhoto.setImageResource(R.drawable.icon_addpic_focused);
						}else{
							ImageLoader.getInstance().displayImage(url,userPhoto);
						}	
						
						String sex = obj.getString("sex");
						mInfo.setSex(sex);
						switchSexBack(sex);	
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
	 
	 private void requestUpdate()
	 {
		 	Map<String, String> params = new HashMap<String, String>();
			params.put("uid", Session.getSession().getuId());
			params.put("name", mInfo.getName());
			params.put("signature", mInfo.getSignature());
			params.put("sex", mInfo.getSex());
			params.put("profession", mInfo.getProfession());
			params.put("residence", mInfo.getResidence());
			params.put("education", mInfo.getEducation());
			final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/update_userinfo").toString();
			JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
					params), new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					// TODO Auto-generated method stub
					try {
						String state = response.getString("state");
						if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {							
		        			Intent intent = new Intent();
		        			intent.putExtra(StaticValue.USERINFO.USERINFOENTITY, mInfo);
		        			intent.setAction(StaticValue.USER_INFO_UPDATE);
		        			sendBroadcast(intent);		        			
		        			
		        			//reset session
		        			Session.getSession().setuName(mInfo.getName());
		        			Session.getSession().setuSex(mInfo.getSex());
		        			Session.getSession().setuSignature(mInfo.getSignature());
	    
		        			//reset cookie 							
		        			Date date = KuibuApplication.getInstance()
		        					.getPersistentCookieStore().getCookie("token").getExpiryDate();
		        			KuibuApplication.getInstance().getPersistentCookieStore()
								.addCookie("user_sex", mInfo.getSex(), date);
		        			KuibuApplication.getInstance().getPersistentCookieStore()
								.addCookie("user_name", mInfo.getName(), date);
		        			KuibuApplication.getInstance().getPersistentCookieStore()
								.addCookie("user_signature", mInfo.getSignature(), date);	
		        			
		        			Toast.makeText(UserInfoEditActivity.this, getString(R.string.modify_success),
		        					Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(UserInfoEditActivity.this, getString(R.string.modify_fail),
		        					Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
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
			}){
				@Override  
		 		public Map<String, String> getHeaders() throws AuthFailureError {  
		 			HashMap<String, String> headers = new HashMap<String, String>();
		 			String credentials = Session.getSession().getToken()+":unused";
		 			headers.put("Authorization","Basic "+
		 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
		 			return headers;  
		 		} 
			};
			KuibuApplication.getInstance().addToRequestQueue(req);
	 }
	 
	 private void showPopup(View v) {
			Context context = v.getContext();
			AlertDialog.Builder builder = new Builder(context);
			
			builder.setTitle(getString(R.string.mod_user_pic))
				   .setItems(context.getResources().getStringArray(R.array.popup_menu_item),
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int position) {
							switch (position) {
							case 0:
								takePhotoByCamera();
								break;
							case 1:
								userPhoto.setImageResource(R.drawable.icon_addpic_focused);
								Crop.pickImage(UserInfoEditActivity.this);
								break;
							}
						}
					});
			builder.show();
		}

		@Override
		public void takePhotoByCamera() {
			cameraPic = Uri.fromFile(new File(getCacheDir(), Constants.Config.CAMERA_TMP));
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPic);
			startActivityForResult(openCameraIntent,
					StaticValue.CommonData.CAMERA_TAKE_PICTRUE_RESULT);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent result) {
			super.onActivityResult(requestCode, resultCode, result);
			switch(requestCode){
			case Crop.REQUEST_PICK:
				if(result != null){
					beginCrop(result.getData());
				}				
				break;
			case Crop.REQUEST_CROP:
				handleCrop(resultCode, result);
				break;
			case StaticValue.CommonData.CAMERA_TAKE_PICTRUE_RESULT:
				if(resultCode == Activity.RESULT_OK){
					Uri destination = Uri.fromFile(new File(getCacheDir(), Constants.Config.USER_PHOTO_TMP));
			        Crop.of(cameraPic, destination).asSquare().start(this);		        
				}
				break;
			}
		}
		
		private void beginCrop(Uri source) {
	        Uri destination = Uri.fromFile(new File(getCacheDir(), Constants.Config.USER_PHOTO_TMP));
	        Crop.of(source, destination).asSquare().start(this);
	    }

	    private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == RESULT_OK) {
	        	userPhoto.setImageURI(Crop.getOutput(result));
	        	bModifyPhoto = true ; 
	        } else if (resultCode == Crop.RESULT_ERROR) {
	            Toast.makeText(this, Crop.getError(result).getMessage(),
	            		Toast.LENGTH_SHORT).show();
	        }
	    }
	    
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
		}
}
