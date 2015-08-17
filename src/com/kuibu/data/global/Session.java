
package com.kuibu.data.global;

import java.io.Serializable;

/**
 * @class :伪会话类，保存登录后用户信息
 * @author ThinkPad
 */

public class Session implements Serializable{

	private static final long serialVersionUID = 1L;
	private String uId;
	private String uName;
	private String uSex ; 
	private String uSignature; 
	private String uEmail;
	private String token;
	private String uPic;  
	private String regState ; 
	private boolean login = false; 	
	private static Session session;

	public static Session getSession() {
		if (session == null) {
			session = new Session();
		}
		return session;
	}

	public static void setSession(Session s)
	{
		if (session == null) {
			session = new Session();
		}
		session.uId = s.uId;
		session.uName=s.uName;
		session.uSex=s.uSex;
		session.uSignature=s.uSignature;
		session.uEmail = s.uEmail;
		session.token = s.token ; 
		session.uPic = s.uPic  ;
		session.regState = s.regState ; 
		session.login = s.login ;
	}
	
	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	public void clearSession() {
		uId=uName=uSex=uSignature=uEmail=token=uPic=regState=null;
		login=false;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	public String getuPic() {
		return uPic;
	}

	public void setuPic(String uPic) {
		this.uPic = uPic;
	}

	public String getuSex() {
		return uSex;
	}

	public void setuSex(String uSex) {
		this.uSex = uSex;
	}

	public String getuSignature() {
		return uSignature;
	}

	public void setuSignature(String uSignature) {
		this.uSignature = uSignature;
	}

	public String getuEmail() {
		return uEmail;
	}

	public void setuEmail(String uEmail) {
		this.uEmail = uEmail;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean loginState) {
		this.login = loginState;
	}

	public String getRegState() {
		return regState;
	}

	public void setRegState(String regState) {
		this.regState = regState;
	}		
}
