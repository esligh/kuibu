package com.kuibu.module.request.listener;

import org.json.JSONObject;

import com.android.volley.VolleyError;

/**
 * 在Presenter层实现，给Model层回调，更改View层的状态，确保Model层不直接操作View层
 * */
public interface OnLoginListener{
	 /**
     * 登录成功
     * @param response
     */
    void onLoginSuccess(JSONObject response);
    /**
     * 登录失败
     * */
    void onLoginError(String type);
    
    /**
     * 失败时回调
     */
    void onVolleyError(VolleyError error); 
    
    void onLoginOverSuccess(JSONObject response);
}
