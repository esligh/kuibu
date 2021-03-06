package com.kuibu.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.CommentModel;
import com.kuibu.module.request.listener.OnCommentListener;

public class CommentModelImpl implements CommentModel{
	private OnCommentListener mListener ; 
	
	public CommentModelImpl(OnCommentListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ; 
	}

	@Override
	public void requestDel(final Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/del_comment";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					response.put("postion", params.get("position"));
					mListener.onDelCommentResponse(response);
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
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public void requestCommentList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_comments").toString();		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
			params), new Response.Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject response) {
			// TODO Auto-generated method stub
			mListener.onCommentListResponse(response);
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
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public void requestAdd(final Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/add_comment";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					response.put("content", params.get("content"));
					mListener.onAddCommentResponse(response);
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
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
}
