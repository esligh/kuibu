package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.interfaces.CreateAlbumModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.request.listener.OnCreateAlbumListener;

public class CreateAlbumModelImpl implements CreateAlbumModel{

	private OnCreateAlbumListener mListener ; 
	
	public CreateAlbumModelImpl(OnCreateAlbumListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ;
	}

	@Override
	public void requestAddAlbum(Map<String, String> params,final CollectPackBean item) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/add_collectpack").toString();
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.onAddAlbumResponse(response,item);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onVolleyError(error);
			}
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();  
	 		}
		};
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public void requestUpdateAlbum(Map<String, String> params,final CollectPackBean item) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION )
		.append("/update_collectpack").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>(){
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				mListener.onUpdateAlbumResponse(response,item);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				KuibuUtils.showText(R.string.oper_fail, Toast.LENGTH_SHORT);
			}
		}){
			@Override  
				public Map<String, String> getHeaders() throws AuthFailureError {  
					return KuibuUtils.prepareReqHeader();
				}
		};
		
		KuibuApplication.getInstance().addToRequestQueue(req);	

	}

	@Override
	public void requestAlbum(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_collectpack").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				mListener.onLoadAlbumResponse(response);				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public void requestTopicList(Map<String, String> params) {
		// TODO Auto-generated method stub

		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_topiclist").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub		
				mListener.onTopicListResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
}
