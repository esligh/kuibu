package com.kuibu.module.presenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.UserInfoEditModelImpl;
import com.kuibu.model.entity.UserInfoBean;
import com.kuibu.model.interfaces.UserInfoEditModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.request.listener.OnUserInfoEditListener;
import com.kuibu.module.request.listener.UserInfoEditPresenter;
import com.kuibu.ui.view.interfaces.UserInfoEditView;

public class UserInfoEditPresenterImpl implements UserInfoEditPresenter,OnUserInfoEditListener{
	
	private UserInfoEditModel mModel ; 
	private UserInfoEditView mView ; 
	private UserInfoBean mInfo =new UserInfoBean();
	private ProgressDialog progressDlg ;
	
	public UserInfoEditPresenterImpl(UserInfoEditView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new UserInfoEditModelImpl(this);
	}

	@Override
	public void uploadUserPic() {
		// TODO Auto-generated method stub
			AjaxParams params = new AjaxParams();
			try {
				params.put("user_pic", new File(mView.getInstance().getCacheDir(),
						Constants.Config.USER_PHOTO_TMP));
				params.put("uid", mInfo.getId());
				params.put("email", mInfo.getEmail());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			mModel.uploadUserPic(params);
	 }
	 

	@Override
	public void loadUserInfo() {
			// TODO Auto-generated method stub			
		if(progressDlg == null ){
			progressDlg = new ProgressDialog(mView.getInstance());
			progressDlg.setCanceledOnTouchOutside(false);	
			progressDlg.setMessage(mView.getInstance().getString(R.string.please_wait));
		}
		progressDlg.show();
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", Session.getSession().getuId());
		mModel.requestUserInfo(params);	
	}
	 

	@Override
	public void updateUserInfo() {
		// TODO Auto-generated method stub
		progressDlg.setMessage(KuibuUtils.getString(R.string.saving));
	 	progressDlg.show();
	 	Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("name", mInfo.getName());
		params.put("signature", mInfo.getSignature());
		params.put("sex", mInfo.getSex());
		params.put("profession", mInfo.getProfession());
		params.put("residence", mInfo.getResidence());
		params.put("education", mInfo.getEducation());
		mModel.requestUpdateInfo(params);
	}
	
	@Override
	public void onUserInfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				JSONObject obj = new JSONObject(response.getString("result"));
				mInfo.setId(obj.getString("id"));
				mInfo.setEmail(obj.getString("email"));
				mInfo.setPhoto(obj.getString("photo"));
				mInfo.setName(obj.getString("name"));
				mView.setUserName(mInfo.getName());
				String signature= obj.getString("signature");							
				if(TextUtils.isEmpty(signature) || signature.equals("null")){
					mView.setUserSignature(KuibuUtils.getString(R.string.please_input));					
					mInfo.setSignature("");
				}else{
					mView.setUserSignature(signature);
					mInfo.setSignature(signature);
				}
				String profession = obj.getString("profession");
				if(TextUtils.isEmpty(profession) || profession.equals("null")){
					mView.setProfession(KuibuUtils.getString(R.string.please_input));
					mInfo.setSignature("");
				}else{
					mView.setProfession(profession);
					mInfo.setSignature(profession);
				}
				
				String residence = obj.getString("residence");
				if(TextUtils.isEmpty(profession) || residence.equals("null")){
					mView.setResidence(KuibuUtils.getString(R.string.please_input));
					mInfo.setSignature("");
				}else{
					mView.setResidence(residence);
					mInfo.setSignature(residence);
				}
				
				String education = obj.getString("education");
				if(TextUtils.isEmpty(education) || education.equals("null")){
					mView.setEducation(KuibuUtils.getString(R.string.please_input));
					mInfo.setSignature("");
				}else{
					mView.setEducation(education);
					mInfo.setSignature(education);
				}
				String url = obj.getString("photo") ; 
				if(TextUtils.isEmpty(url) || url.equals("null")){				
					mView.setUserPhoto(R.drawable.icon_addpic_focused);
				}else{
					mView.setUserPhoto(url);
				}					
				String sex = obj.getString("sex");
				mInfo.setSex(sex);
				mView.switchSex(sex);	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressDlg.dismiss();

	}

	@Override
	public void onUploadPicSuccess(String t) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(t)) {
			try {
				JSONObject obj = new JSONObject(t);
				String state = obj.getString("state");
				if(StaticValue.RESPONSE_STATUS.UPLOAD_SUCCESS.equals(state)){
					updateUserInfo();
				}else if(StaticValue.RESPONSE_STATUS.UPLOAD_FAILD.equals(state)){
					KuibuUtils.showText("修改头像失败", Toast.LENGTH_LONG);
				}						
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public void onUploadPicFailed() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUpdateInfoResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {							
    			Intent intent = new Intent();
    			intent.putExtra(StaticValue.USERINFO.USERINFOENTITY, mInfo);
    			intent.setAction(StaticValue.USER_INFO_UPDATE);
    			mView.getInstance().sendBroadcast(intent);
    			
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
    			
    			KuibuUtils.showText(R.string.modify_success,Toast.LENGTH_SHORT);
			}else{
				KuibuUtils.showText(R.string.modify_fail,Toast.LENGTH_SHORT);
			}
			progressDlg.dismiss();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
	}

	@Override
	public UserInfoBean getUserInfo() {
		// TODO Auto-generated method stub
		return mInfo;
	}

}
