package com.kuibu.module.dlg;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;

/**
 * @class  登录窗体
 * @author ThinkPad
 *
 */
public class LoginDialog {
	private Context mContext;
	private EditText mUserEmailEt;
	private EditText mUserPwdEt;
	private ActionProcessButton btnLogIn ; 
	private OnLoginLisener loginListener;
	private static final int PROGRESS_LEN  = 10 ; 
	private AlertDialog   alertLogIn ;
	
	public LoginDialog(Context context) {
		this.mContext = context;
	}

	public void setOnLoginLisener(OnLoginLisener loginLisener) {
		this.loginListener = loginLisener;
	}

	@SuppressLint("InflateParams")
	public void show() {
		LinearLayout loginLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.login_dialog, null);
		mUserEmailEt = (EditText) loginLayout.findViewById(R.id.login_user_email);
		mUserPwdEt = (EditText) loginLayout.findViewById(R.id.login_user_pwd);
		btnLogIn = (ActionProcessButton)loginLayout.findViewById(R.id.btnLogIn);
		btnLogIn.setMode(ActionProcessButton.Mode.ENDLESS);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("登录").setView(loginLayout);
		alertLogIn = builder.show();
		btnLogIn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				String email = mUserEmailEt.getText().toString().trim();
				String pwd = mUserPwdEt.getText().toString().trim();				
				if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)){
					Toast.makeText(mContext, "请输入账号和密码.", Toast.LENGTH_SHORT).show();
				}else{
					btnLogIn.setProgress(PROGRESS_LEN);
					doLogin(email, pwd);
				}
			}
		});
	}

	public interface OnLoginLisener {
		public void onLoginComplete(Map<String, Object> params);
	}

	/**
	 * 登录方法
	 * @param name
	 * @param pwd
	 */
	void doLogin(final String email, final String pwd) {		
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("pwd", pwd);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/user_login";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
				new Response.Listener<JSONObject>() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					Map<String, Object> params = new HashMap<String,Object>();
					params.put("errCode", state);
					if (StaticValue.RESPONSE_STATUS.LOGIN_SUCCESS // 登录成功
							.equals(state)) {
						Session.getSession().setLogin(true); //设置session状态
						String token = response.getString("token"); //获取令牌token
						String expirydate = response.getString("expirydate");//令牌的失效日期
						Date date = null;
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							date = sdf.parse(expirydate);
						} catch (ParseException e) {
							e.printStackTrace();
						}						
						//解析其他用户信息并放到session中
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
						Session.getSession().setRegState("reg_state");
						params.put("uId", uid);
						params.put("uName", uname);
						params.put("uPhoto", photoUrl);
												
						// 保存cookie
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
						//success,establish persistent connection 
						try {							
							JSONObject obj = new JSONObject();
							obj.put("uid", uid);
							obj.put("name", uname);
							KuibuApplication.getSocketIoInstance().SetUp();
							KuibuApplication.getSocketIoInstance().getSocketIO().
									emit(StaticValue.EVENT.LOGIN_EVENT, obj);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						alertLogIn.dismiss();
					} else if (StaticValue.RESPONSE_STATUS.LOGIN_NFUSER
							.equals(state)) {
						Toast.makeText(mContext, "没有此用户,请您先注册!",
								Toast.LENGTH_SHORT).show();
						btnLogIn.setProgress(0);
					} else if (StaticValue.RESPONSE_STATUS.LOGIN_PWDWRONG
							.equals(state)) {
						Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT)
								.show();
						btnLogIn.setProgress(0);
					}
					params.put("isAuto", "false");
					loginListener.onLoginComplete(params); //回调登录接口
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				btnLogIn.setProgress(0);
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		// add the request object to the queue to be executed
		KuibuApplication.getInstance().addToRequestQueue(req);
	}

	/**
	 * 自动登录实现
	 * @return
	 */
	public boolean autoLogin() {
		
		Cookie cookie = KuibuApplication.getInstance()
				.getPersistentCookieStore().getCookie("token");
		if (cookie == null || cookie.isExpired(new Date())) {
			return false;
		} else { 
			//读取cookie到session中
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
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("errCode", StaticValue.RESPONSE_STATUS.LOGIN_SUCCESS);
			String uId = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_id").getValue();
			params.put("uId", uId);
			Session.getSession().setuId(uId);
			String uName = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_name")
					.getValue();
			params.put("uName", uName);
			Session.getSession().setuName(uName);
			String photoUrl = KuibuApplication.getInstance()
					.getPersistentCookieStore().getCookie("user_photo")
					.getValue();
			Session.getSession().setuPic(photoUrl);
			params.put("uPhoto", photoUrl);
			params.put("isAuto", "true");
			loginListener.onLoginComplete(params); //回调接口
			
			try {
				JSONObject obj = new JSONObject();
				obj.put("uid", uId);
				obj.put("name", uName);
				KuibuApplication.getSocketIoInstance().SetUp();
				KuibuApplication.getSocketIoInstance().getSocketIO().
				emit(StaticValue.EVENT.LOGIN_EVENT, obj);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		}
	}
}
