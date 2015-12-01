package com.kuibu.model;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.interfaces.HomePageModel;
import com.kuibu.module.request.listener.OnHomePageListener;

public class HomePageModelImpl implements HomePageModel{

	private OnHomePageListener mListener ; 
	public HomePageModelImpl(OnHomePageListener l)
	{
		this.mListener = l ;
	}
	
	@Override
	public void requestData(final Map<String, String> params,final boolean bcache) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/get_collections").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
			params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String data = response.getString("result");
						JSONArray arr = new JSONArray(data);
						String action = params.get("action");
						mListener.parseFromJson(arr,action);						
						if(arr.length()>0){
							if(action.equals("INIT") && bcache){
							    	KuibuApplication.getCacheInstance()
							    	.put(StaticValue.LOCALCACHE.HOME_LIST_CACHE, arr);
							}else if(action.equals("DOWN")&& bcache){
							    	JSONArray oldarr = KuibuApplication.getCacheInstance()
							    			.getAsJSONArray(StaticValue.LOCALCACHE.HOME_LIST_CACHE);
							    	JSONArray newarr = DataUtils.joinJSONArray(oldarr, arr, 
							    			StaticValue.LOCALCACHE.DEFAULT_CACHE_SIZE);
							    	KuibuApplication.getCacheInstance()
							    	.put(StaticValue.LOCALCACHE.HOME_LIST_CACHE, newarr);
							}
						}						
					}					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mListener.onLoadDataSuccess(response);
			}}, new Response.ErrorListener() {
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
		KuibuApplication.getInstance().addToRequestQueue(req,this);
	}

	@Override
	public void cancelRequest() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
	}
	
}
