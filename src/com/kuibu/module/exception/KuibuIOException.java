package com.kuibu.module.exception;


public class KuibuIOException extends KuibuException {

	private static final long serialVersionUID = 1L;

	public KuibuIOException(String msg) {
		super(msg);

		this.errorCode = CONNECTED_ERORR;
	}

	public KuibuIOException(String msg, Exception cause) {
		super(msg, cause);

		this.errorCode = CONNECTED_ERORR;
	}

}