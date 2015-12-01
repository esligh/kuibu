package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.TopicInfoModel;
import com.kuibu.module.request.listener.OnTopicInfoListener;

public class TopicInfoModelImpl implements TopicInfoModel{

	private OnTopicInfoListener mListener ; 
	
	public TopicInfoModelImpl(OnTopicInfoListener l)
	{
		this.mListener = l ;
	}

	@Override
	public void requestTopicInfo(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_topicinfo").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
			params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.onTopicInfoResponse(response);
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
	public void requestAuthorList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_bestauthor").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
		params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.onUserListResponse(response);
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
	public void doFollow(Map<String, String> params) {
		// TODO Auto-generated method stub
		String URL = params.get("URL");
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.onFollowResponse(response);
			}
		}, new Response.ErrorListener() {
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
