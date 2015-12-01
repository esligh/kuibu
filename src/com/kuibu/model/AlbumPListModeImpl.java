package com.kuibu.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.AlbumPListModel;
import com.kuibu.module.request.listener.OnAlbumPListListener;

public class AlbumPListModeImpl implements AlbumPListModel{
	
	private OnAlbumPListListener mListener ; 
	
	public AlbumPListModeImpl(OnAlbumPListListener l)
	{
		this.mListener = l ; 
		
	}

	@Override
	public void requestDelCollection(final Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/del_collection").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					response.put("size", Integer.parseInt(params.get("size")));
					mListener.OnDelCollectionResponse(response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.e("Error: ", error.getMessage());
					VolleyLog.e("Error:", error.getCause());
					error.printStackTrace();
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
