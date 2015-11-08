package com.kuibu.module.request.listener;

import com.android.volley.VolleyError;
import com.kuibu.model.entity.LoginResponse;

/**
 * 在Presenter层实现，给Model层回调，更改View层的状态，确保Model层不直接操作View层
 * */
public interface OnLoginListener{
	 /**
     * 登录成功
     * @param response
     */
    void onLoginSuccess(LoginResponse response);
    /**
     * 登录失败
     * */
    void onLoginError(String type);
    
    /**
     * 失败时回调
     */
    void onVolleyError(VolleyError error); 
}
