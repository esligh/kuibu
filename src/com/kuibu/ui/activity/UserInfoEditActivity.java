package com.kuibu.ui.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterfaces.ICamera;
import com.kuibu.module.presenter.UserInfoEditPresenterImpl;
import com.kuibu.module.request.listener.UserInfoEditPresenter;
import com.kuibu.ui.view.interfaces.UserInfoEditView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class UserInfoEditActivity extends BaseActivity 
	implements ICamera,UserInfoEditView{

	private ImageView userPhoto ; 
	private TextView userName,userSexM,userSexF;
	private TextView userSignature,userProfession,userResidence,userEducation;  
	private Uri cameraPic;
	private boolean bModifyPhoto  = false ;		 
	private UserInfoEditPresenter mPresenter ; 
	
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
        				mPresenter.getUserInfo().setName(new_name);
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
				mPresenter.getUserInfo().setSex(StaticValue.SERMODLE.USER_SEX_MALE);
				switchSexBack(StaticValue.SERMODLE.USER_SEX_MALE);				
			}
		});
		userSexF = (TextView)findViewById(R.id.user_sex_f_tv);
		userSexF.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPresenter.getUserInfo().setSex(StaticValue.SERMODLE.USER_SEX_FEMALE);
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
        				mPresenter.getUserInfo().setSignature(signature);
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
        				mPresenter.getUserInfo().setProfession(profession);
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
        				mPresenter.getUserInfo().setResidence(residence);
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
        				mPresenter.getUserInfo().setEducation(education);
        			}
        		}).show();
			}
		});
		mPresenter = new UserInfoEditPresenterImpl(this);
		mPresenter.loadUserInfo();
	}
	
	@SuppressWarnings("deprecation")
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
				StaticValue.MENU_ORDER.SAVE_ORDER_ID, getString(R.string.action_save));

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
	        			mPresenter.uploadUserPic();
	        		}else{
	        			mPresenter.updateUserInfo();
	        		}    		
	        		break;
	        }
	        return super.onOptionsItemSelected(item);	 
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

		@Override
		public Intent getDataIntent() {
			// TODO Auto-generated method stub
			return getIntent();
		}

		@Override
		public Context getInstance() {
			// TODO Auto-generated method stub
			return this;
		}

		@Override
		public void setUserName(String name) {
			// TODO Auto-generated method stub
			userName.setText(name);
		}

		@Override
		public void setUserSignature(String signature) {
			// TODO Auto-generated method stub
			userSignature.setText(signature);
		}

		@Override
		public void setProfession(String profession) {
			// TODO Auto-generated method stub
			userProfession.setText(profession);
		}

		@Override
		public void setResidence(String residence) {
			// TODO Auto-generated method stub
			userResidence.setText(residence);
		}

		@Override
		public void setUserPhoto(String url) {
			// TODO Auto-generated method stub
			ImageLoader.getInstance().displayImage(url,userPhoto,Constants.defaultAvataOptions);
		}

		@Override
		public void setEducation(String edu) {
			// TODO Auto-generated method stub
			userEducation.setText(edu);
		}

		@Override
		public void switchSex(String sex) {
			// TODO Auto-generated method stub
			switchSexBack(sex);
		}

		@Override
		public void setUserPhoto(int resId) {
			// TODO Auto-generated method stub
			userPhoto.setImageResource(resId);
		}
}
