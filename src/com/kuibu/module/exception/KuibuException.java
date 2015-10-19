package com.kuibu.module.exception;

public class KuibuException extends Exception {
	private static final long serialVersionUID = 1L;
	
	// 未知错误
	public static final String UNKONWEN_ERORR = "-1";

	// 连接超时
	public static final String CONNECTED_ERORR = "-1001";

	protected String errorCode = UNKONWEN_ERORR;

	public KuibuException(String msg) {
		super(msg);
	}

	public KuibuException(String msg, Exception cause) {
		super(msg, cause);
	}

	public String getErrorCode() {
		return errorCode;
	}
}