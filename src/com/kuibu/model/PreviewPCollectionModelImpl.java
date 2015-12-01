package com.kuibu.model;

import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.model.interfaces.PreviewPCollectionModel;
import com.kuibu.module.request.listener.OnPreviewPCollectionListener;

public class PreviewPCollectionModelImpl implements PreviewPCollectionModel{

	private OnPreviewPCollectionListener mListener ; 
	private FinalHttp finalHttp ; 
		
	public PreviewPCollectionModelImpl(OnPreviewPCollectionListener l) {
		// TODO Auto-generated constructor stub
		this.mListener = l ;
		finalHttp = new FinalHttp(); 

	}

	@Override
	public void upLoadImgs(AjaxParams params, boolean bfirst) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/simple_upload").toString();
	
		finalHttp.post(URL, params, new AjaxCallBack<String>() {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mListener.OnUploadImgsFailed(errorNo,strMsg);
			}
	
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);				
				mListener.OnUploadImgsSuccess(t);
			}
		});
	}

	@Override
	public void doPublish(final Map<String, Object> params) {
		// TODO Auto-generated method stub
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
		.append(Constants.Config.REST_API_VERSION)
		.append("/add_collection").toString();			
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mListener.OnPubulishResponse(params, response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.OnVolleyError(error);
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return KuibuUtils.prepareReqHeader();
			}
		};
		req.setRetryPolicy(new DefaultRetryPolicy(Constants.Config.TIME_OUT_LONG,
				Constants.Config.RETRY_TIMES, 1.0f));
		KuibuApplication.getInstance().addToRequestQueue(req);
	}	
}
