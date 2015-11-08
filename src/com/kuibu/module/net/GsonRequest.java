package com.kuibu.module.net;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * class GsonRequest
 * @see JsonRequest 
 * @author ThinkPad
 * @version 1.0
 * */

public class GsonRequest<T> extends JsonRequest<T> {

	private final Gson mGson;
	private final Class<T> mClassType;
	private final Map<String, String> mHeaders;

	public GsonRequest(String url, JSONObject jsonRequest,
				Class<T> classType, Response.Listener<T> listener,
	            Response.ErrorListener errorListener) {
	        this(jsonRequest == null ? Method.GET : Method.POST, url,jsonRequest,classType,
	                listener, errorListener);
	}
	   
	public GsonRequest(int method, String url, JSONObject jsonRequest,
			Class<T> classType, Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		this(method, url, jsonRequest,null, classType, listener, errorListener);
	}

	public GsonRequest(int method, String url, JSONObject jsonRequest,			
			Map<String, String> headers, Class<T> classType,
			Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest
				.toString(), listener, errorListener);
		mGson = new Gson();
		mClassType = classType;
		mHeaders = headers;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
		try {
			String json = new String(networkResponse.data,
					HttpHeaderParser.parseCharset(networkResponse.headers));
			return Response.success(mGson.fromJson(json, mClassType),
					HttpHeaderParser.parseCacheHeaders(networkResponse));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}