package com.kuibu.module.presenter;

import com.android.volley.VolleyError;
import com.kuibu.model.LoginModelImpl;
import com.kuibu.model.entity.LoginResponse;
import com.kuibu.model.interfaces.LoginModel;
import com.kuibu.module.presenter.interfaces.LoginPresenter;
import com.kuibu.module.request.listener.OnLoginListener;
import com.kuibu.ui.view.interfaces.LoginView;

public class LoginPresenterImpl implements LoginPresenter ,OnLoginListener{
	
	private LoginView loginView; 
	private LoginModel loginModel ; 
	
	public LoginPresenterImpl(LoginView view)
	{
		loginView = view ; 
		loginModel = new LoginModelImpl() ; 
	}

	@Override
	public int login(String name, String pwd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void autoLogin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate(String name, String pwd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int logout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onLoginSuccess(LoginResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoginError(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setUpSocketIoClient() {
		// TODO Auto-generated method stub
		
	}	
}
