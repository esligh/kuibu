package com.kuibu.module.net;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Session;

public class GsonRequest extends JsonObjectRequest{
	
	private boolean bwithAuth = false; 
	private Listener<JSONObject> mResponseListener;
	private ErrorListener mErrorListener ; 
	
	public GsonRequest(int method, String url, JSONObject jsonRequest,boolean bwithAuth,
			Listener<JSONObject> listener, ErrorListener errorListener) {		 
		super(method, url, jsonRequest, listener, errorListener);
		this.bwithAuth = bwithAuth ;
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		// TODO Auto-generated method stub
		if(bwithAuth){
			HashMap<String, String> headers = new HashMap<String, String>();
			String credentials = Session.getSession().getToken()
					+ ":unused";
			headers.put(
					"Authorization",
					"Basic "
							+ SafeEDcoderUtil.encryptBASE64(
									credentials.getBytes()).replaceAll(
									"\\s+", ""));
			return headers;
		}else{
			return super.getHeaders();
		}		
	}
	

}
