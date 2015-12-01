package com.kuibu.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.ExploreFavoriteModel;
import com.kuibu.module.request.listener.OnExploreFavoriteListener;

public class ExploreFavoriteModelImpl implements ExploreFavoriteModel{
	private OnExploreFavoriteListener mListener ; 
	
	public ExploreFavoriteModelImpl(OnExploreFavoriteListener l ) {
		// TODO Auto-generated constructor stub
		this.mListener = l  ;
	}

	@Override
	public void requestFavoriteList(final Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_hotpacks").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
			// TODO Auto-generated method stub
				try {
					response.put("action", params.get("action"));
					mListener.onLoadFavoriteListResponse(response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {				
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onVolleyError(error);
			}
		});
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_SHORT, 
		Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
}
