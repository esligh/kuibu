package com.kuibu.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.PackInfoModel;
import com.kuibu.module.request.listener.OnPackInfoListener;

public class PackInfoModelImpl implements PackInfoModel{

	private OnPackInfoListener mListener ; 
	public PackInfoModelImpl(OnPackInfoListener l) {
		this.mListener = l ;
	}
	@Override
	public void requestPackInfo(Map<String,String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_collectpack").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				mListener.onLoadPackInfoSuccess(response);
				
			}}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onLoadError(error);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	@Override
	public void requestPackList(final Map<String,String> params) {
		// TODO Auto-generated method stub
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_packlist";
		JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							response.put("type", params.get("type"));
							mListener.onLoadPackListSuccess(response);
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
						mListener.onLoadError(error);
					}
				});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	@Override
	public void doFollow(Map<String, String> params,boolean bFollow) {
		// TODO Auto-generated method stub
		final String URL;
		if(bFollow){ 
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_follows").toString();
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
				  .append(Constants.Config.REST_API_VERSION)
				  .append("/add_follows").toString();
		}
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				mListener.onFollowSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onLoadError(error);
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();
	 		} 
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
}
