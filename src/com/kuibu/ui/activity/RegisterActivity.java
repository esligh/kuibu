package com.kuibu.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.common.utils.PhoneUtils;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;
import com.kuibu.module.iterfaces.ICamera;
import com.soundcloud.android.crop.Crop;

public class RegisterActivity extends BaseActivity implements ICamera {
	private static final int PROGRESS_LEN  = 10 ; 
	private static final int PROGRESS_MAX = 100 ; 
	private ActionProcessButton progressBtn;
	private ImageView user_photo_iv;
	private EditText user_name_et;
	private EditText user_signature_et ; 
	private EditText user_pwd_et;
	private EditText user_pwd_confirm_et;
	private EditText user_email_et;
	private RadioGroup sex_rg; 
	private boolean has_photo = false; 
	private FinalHttp finalHttp = null;
	private Uri cameraPic = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		progressBtn = (ActionProcessButton) findViewById(R.id.finishi_reg);
		progressBtn.setMode(ActionProcessButton.Mode.ENDLESS);
		user_photo_iv = (ImageView) findViewById(R.id.user_photo);
		user_photo_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showPopup(view);
			}
		});
		user_name_et = (EditText) findViewById(R.id.user_name);
		user_signature_et =(EditText) findViewById(R.id.user_signature);
		
		user_pwd_et = (EditText) findViewById(R.id.user_pwd);
		user_email_et = (EditText) findViewById(R.id.user_email);
		user_pwd_confirm_et = (EditText) findViewById(R.id.user_pwd_confirm);
		sex_rg = (RadioGroup) findViewById(R.id.user_sex_rg);
		progressBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {	
				if(progressBtn.getProgress() == PROGRESS_MAX){
					Toast.makeText(RegisterActivity.this, getString(R.string.note_active), 
							Toast.LENGTH_SHORT).show();
					finish();
				} else{
					progressBtn.setProgress(PROGRESS_LEN);
					requestRegister();
				}
			}
		});
		finalHttp = new FinalHttp();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void requestRegister() {
		// Post params to be sent to the server
		HashMap<String, String> params = new HashMap<String, String>();
		String user_name = user_name_et.getText().toString();
		String user_pwd = user_pwd_et.getText().toString();
		String user_email = user_email_et.getText().toString();
		String user_signature= user_signature_et.getText().toString();
		String user_pwd_confirm = user_pwd_confirm_et.getText().toString();
		if (TextUtils.isEmpty(user_name)) {
			Toast.makeText(RegisterActivity.this, getString(R.string.input_name), 
					Toast.LENGTH_SHORT).show();
			progressBtn.setProgress(0);
			return;
		}
		
		if (TextUtils.isEmpty(user_email)) {
			Toast.makeText(RegisterActivity.this, getString(R.string.input_email), 
					Toast.LENGTH_LONG).show();
			progressBtn.setProgress(0);
			return;
		}
		if(TextUtils.isEmpty(user_signature)){
			Toast.makeText(RegisterActivity.this, getString(R.string.input_signature), 
					Toast.LENGTH_LONG).show();
			progressBtn.setProgress(0);
		}
		if (TextUtils.isEmpty(user_pwd) || TextUtils.isEmpty(user_pwd_confirm)) {
			Toast.makeText(RegisterActivity.this, getString(R.string.input_password), 
					Toast.LENGTH_LONG).show();
			progressBtn.setProgress(0);
			return;
		}
		
		if (!user_pwd.equals(user_pwd_confirm)) {
			Toast.makeText(RegisterActivity.this, getString(R.string.password_not_equal),
					Toast.LENGTH_LONG).show();
			progressBtn.setProgress(0);
			return;
		}
		params.put("name", user_name);
		params.put("pwd", user_pwd);
		params.put("email", user_email);
		params.put("signature", user_signature);
		params.put("dev_id", PhoneUtils.getDeviceId(this));
		String sex = sex_rg.getCheckedRadioButtonId() == R.id.male_rb ? 
				StaticValue.SERMODLE.USER_SEX_MALE:StaticValue.SERMODLE.USER_SEX_FEMALE;
		params.put("sex", sex);		
		
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
								.append(Constants.Config.REST_API_VERSION)
								.append("/user_register").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("reg_state");
					if(StaticValue.RESPONSE_STATUS.REG_SENDEMAIL.equals(state)){
						if(has_photo){
							uploadUserpic(response.getString("uid"));
						}else{
							progressBtn.setProgress(PROGRESS_MAX);
						}						
					}else if(StaticValue.RESPONSE_STATUS.REG_SAMENAME.equals(state)){
						Toast.makeText(RegisterActivity.this, getString(R.string.register_samename),
								Toast.LENGTH_LONG).show();
						progressBtn.setProgress(0);

					}else if(StaticValue.RESPONSE_STATUS.REG_SAMEEMAIL.equals(state)){
						Toast.makeText(RegisterActivity.this, getString(R.string.register_sameemail),
								Toast.LENGTH_LONG).show();
						progressBtn.setProgress(0);
					}else{
						Toast.makeText(RegisterActivity.this, getString(R.string.register_fail),
								Toast.LENGTH_LONG).show();
						progressBtn.setProgress(0);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					progressBtn.setProgress(0);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				progressBtn.setProgress(0);
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
			Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}

	private void uploadUserpic(String uid)
	{
		AjaxParams params = new AjaxParams();
		try {
			params.put("user_pic", new File(this.getCacheDir(),Constants.Config.USER_PHOTO_TMP));
			params.put("uid", uid);
			params.put("email", user_email_et.getText().toString().trim());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/upload_userpic";
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if (!TextUtils.isEmpty(t)) {
					try {
						JSONObject obj = new JSONObject(t);
						String state = obj.getString("state");
						if(StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS.equals(state)){
							progressBtn.setProgress(PROGRESS_MAX);
						}else if(StaticValue.RESPONSE_STATUS.UPLOAD_FAILD.equals(state)){
							progressBtn.setProgress(0);
							Toast.makeText(RegisterActivity.this, getString(R.string.upload_pic_fail),
									Toast.LENGTH_LONG).show();
						}						
					} catch (JSONException e) {
						e.printStackTrace();
						progressBtn.setProgress(0);
					}
				}
			}
		});
	}
	
	private void showPopup(View v) {
		Context context = v.getContext();
		AlertDialog.Builder builder = new Builder(context);
		
		builder.setTitle(getString(R.string.add_user_pic))
			   .setItems(context.getResources().getStringArray(R.array.popup_menu_item),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: // take photo
							takePhotoByCamera();
							break;
						case 1: // choose from library
				            user_photo_iv.setImageResource(R.drawable.icon_addpic_focused);
							Crop.pickImage(RegisterActivity.this);
							break;
						}
					}
				});
		builder.show();
	}

	@Override
	public void takePhotoByCamera() {
		File dir = new File(StorageUtils.getFileDirectory(getApplicationContext())
				.getAbsolutePath()+Constants.Config.CAMERA_IMG_DIR); 
		if(!dir.exists())
			dir.mkdirs();
		File file = new File(dir, String.valueOf(System.currentTimeMillis())+".jpg");
		cameraPic = Uri.fromFile(file);
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
			if(resultCode==RESULT_OK){
		        Uri destination = Uri.fromFile(new File(getCacheDir(), Constants.Config.USER_PHOTO_TMP));
		        Crop.of(result.getData(), destination).asSquare().start(this);
			}
			break;
		case Crop.REQUEST_CROP:
			if (resultCode == RESULT_OK) {
	        	user_photo_iv.setImageURI(Crop.getOutput(result));
	        	has_photo = true ; 
	        }else if (resultCode == Crop.RESULT_ERROR) {
	            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
	        }
			break;
		case StaticValue.CommonData.CAMERA_TAKE_PICTRUE_RESULT:
			if(resultCode == Activity.RESULT_OK){
				Uri destination = Uri.fromFile(new File(getCacheDir(), Constants.Config.USER_PHOTO_TMP));
		        Crop.of(cameraPic, destination).asSquare().start(this);		        
			}
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	
	
}
