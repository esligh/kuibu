package com.kuibu.model;

import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.CollectionListModel;
import com.kuibu.module.request.listener.OnCollectionListListener;

public class CollectionListModelImpl implements CollectionListModel{

	private OnCollectionListListener mListener ;
	
	public CollectionListModelImpl(OnCollectionListListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ;
	}

	@Override
	public void requestCollectionList(Map<String, String> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/get_collections").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				mListener.onCollectionListResponse(response);
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

}
