package com.kuibu.module.presenter;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.LoginModelImpl;
import com.kuibu.model.interfaces.LoginModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.LoginPresenter;
import com.kuibu.module.request.listener.OnLoginListener;
import com.kuibu.ui.view.interfaces.LoginView;

public class LoginPresenterImpl implements LoginPresenter ,OnLoginListener{
	
	private LoginView loginView; 
	private LoginModel loginModel ; 
	
	public LoginPresenterImpl(LoginView view)
	{
		loginView = view ; 
		loginModel = new LoginModelImpl(this) ; 
	}

	@Override
	public void login(final String email, String pwd) {
		loginModel.doLogin(email, pwd);
	}

	@Override
	public void autoLogin() {
		boolean bSuccess = loginModel.doAutoLogin();
		if(bSuccess){
			loginView.switchDrawerList(true);
		}			
		setupSocketIo();						
	}
	
	
	@Override
	public void logout() {
		if(!Session.getSession().isLogin())
			return ;
		if(KuibuApplication.getSocketIoInstance().
				getSocketIO()!=null){
			if(KuibuApplication.getSocketIoInstance().getSocketIO().isConnected()){
				try {
					JSONObject obj = new JSONObject();
					obj.put("uid", Session.getSession().getuId());
					KuibuApplication.getSocketIoInstance().getSocketIO().
						emit(StaticValue.EVENT.LOGOUT_EVENT, obj);
				} catch (JSONException e) {
					e.printStackTrace();
				} 				
				KuibuApplication.getSocketIoInstance().getSocketIO().disconnect();
			}			
		}						
		loginModel.doLogout();
	}

	@Override
	public void onLoginSuccess(JSONObject response) {
		setupSocketIo();
		loginView.hideLoginView();
		loginView.switchDrawerList(true);
	}

	@Override
	public void onLoginError(String code) {
		// TODO Auto-generated method stub
		if(StaticValue.RESPONSE_STATUS.LOGIN_NFUSER
				.equals(code)){
			KuibuUtils.showText(R.string.no_this_user,Toast.LENGTH_SHORT);
			loginView.setLoginBtnProgress(0);
		}else if(StaticValue.RESPONSE_STATUS.LOGIN_PWDWRONG
				.equals(code)){
			KuibuUtils.showText(R.string.pwd_error, Toast.LENGTH_SHORT);
			loginView.setLoginBtnProgress(0);
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		loginView.setLoginBtnProgress(0);		
	}	
	
	/**
	 * set up persistent connection 
	 * @param  
	 */
	private void setupSocketIo()
	{
		try {
			JSONObject obj = new JSONObject();
			obj.put("uid", Session.getSession().getuId());
			obj.put("name", Session.getSession().getuName());
			KuibuApplication.getSocketIoInstance().SetUp();
			KuibuApplication.getSocketIoInstance().getSocketIO().
			emit(StaticValue.EVENT.LOGIN_EVENT, obj);					
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoginOverSuccess(JSONObject response) {
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				//logout success
				boolean have_message = response.getBoolean("have_message");
				if(have_message){
					loginView.setMsgMenuIcon(true);							
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}	
}