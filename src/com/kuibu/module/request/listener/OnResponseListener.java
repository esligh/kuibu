package com.kuibu.module.request.listener;

import com.android.volley.Response;

public interface OnResponseListener<T> {
	 /**
     * 成功时回调
     *
     * @param weather
     */
    void onSuccess(Response<T> response);
    /**
     * 失败时回调，简单处理，没做什么
     */
    void onError(); 
}
