package com.kuibu.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.interfaces.LoginModel;
import com.kuibu.module.request.listener.OnLoginListener;

/**
 * LoginModelImpl implements the <Interface> LoginModel
 *   
 * @author ThinkPad 
 */
public class LoginModelImpl implements LoginModel{
	
	private OnLoginListener  mListener ; 
	
	public LoginModelImpl(OnLoginListener l)
	{
		this.mListener =  l; 
	}
	
	@Override
	public void doLogin(final String email, final String pwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("pwd", pwd);
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
							.append(Constants.Config.REST_API_VERSION)
							.append("/user_login").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
				new Response.Listener<JSONObject>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					Map<String, Object> params = new HashMap<String,Object>();
					params.put("errCode", state);
					if (StaticValue.RESPONSE_STATUS.LOGIN_SUCCESS // login success
							.equals(state)) {
						
						Session.getSession().setLogin(true); 						
						String token = response.getString("token"); 
						String expirydate = response.getString("expirydate");
						
						Date date = null;
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							date = sdf.parse(expirydate);
						} catch (ParseException e) {
							e.printStackTrace();
						}												
						Session.getSession().setToken(token);
						String sinfo = response.getString("user_info");
						JSONObject info = new JSONObject(sinfo);
						String uid = info.getString("user_id");
						Session.getSession().setuId(uid);
						String uname = info.getString("user_name");
						Session.getSession().setuName(uname);
						String photoUrl = info.getString("user_photo");
						Session.getSession().setuPic(photoUrl);
						String usex = info.getString("user_sex");
						Session.getSession().setuSex(usex);
						String signature = info.getString("signature");
						Session.getSession().setuSignature(signature);	
						String regState = info.getString("reg_state");
						Session.getSession().setRegState(regState);
						
						params.put("uId", uid);
						params.put("uName", uname);
						params.put("uPhoto", photoUrl);
												
						SharedPreferences pref = PreferenceManager.
								getDefaultSharedPreferences(KuibuApplication.getContext());
						Editor editor= pref.edit();
						editor.putString(StaticValue.PrefKey.LOGIN_ACCOUNT, email);
						editor.commit();
						
						// save cookies on local 
						KuibuApplication.getInstance()
								.getPersistentCookieStore()
								.addCookie("token", token, date);
						KuibuApplication
								.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_id",
										Session.getSession().getuId(), date);
						KuibuApplication
								.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_name",
										Session.getSession().getuName(), date);
						KuibuApplication
								.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_signature",signature, date);
						KuibuApplication
								.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_sex",usex, date);
						KuibuApplication
								.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_email",info.getString("user_email"), date);
						KuibuApplication.getInstance()
								.getPersistentCookieStore()
								.addCookie("user_photo", photoUrl, date);						
						KuibuApplication.getInstance()
								.getPersistentCookieStore()
								.addCookie("reg_state", regState, date);							
						mListener.onLoginSuccess(response);
						login_over();
					} else {
						mListener.onLoginError(state);				
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
				mListener.onVolleyError(error);
			}
		});
		// add the request object to the queue to be executed
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	@Override
	public void doLogout() {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/user_logout").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						//logout success
						
					}
				} catch (JSONException e) {
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
		}){
			@Override  
	 		public Map<String, String> getHeaders() throws AuthFailureError {  
	 			return KuibuUtils.prepareReqHeader();
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	@Override
	public boolean doAutoLogin() {
		// TODO Auto-generated method stub
		Cookie cookie = KuibuApplication.getInstance().getPersistentCookieStore().getCookie("token");
		if (cookie == null || cookie.isExpired(new Date())) {
			Session.getSession().clearSession();
			return false;
		} else { 
			//read cookie to global session 
			Session.getSession().setLogin(true);
			String token = cookie.getValue();
			Session.getSession().setToken(token);
			String uEmail = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_email")
					.getValue();
			Session.getSession().setuEmail(uEmail);
			String uSex = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_sex")
					.getValue();
			Session.getSession().setuSex(uSex);
			String uSignature = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_signature")
					.getValue();
			Session.getSession().setuSignature(uSignature);
			
			String regState = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("reg_state")
					.getValue();
			Session.getSession().setRegState(regState);

			String uId = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_id").getValue();

			Session.getSession().setuId(uId);
			String uName = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_name")
					.getValue();
			Session.getSession().setuName(uName);
			String photoUrl = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_photo")
					.getValue();
			Session.getSession().setuPic(photoUrl);
			login_over();
			return true;
		}
	}
	
	/**
	 * login_over to update user's state of server side and request whether 
	 * have some message of user .
	 * this method will be called by do_login and do_autologin when user login success .    
	 */
	private void login_over()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		final String URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/login_over").toString();
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {				
				mListener.onLoginOverSuccess(response);		
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
