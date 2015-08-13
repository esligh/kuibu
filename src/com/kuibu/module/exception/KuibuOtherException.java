package com.kuibu.module.exception;


public class KuibuOtherException extends KuibuException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KuibuOtherException(String msg) {
		super(msg);
	}

	public KuibuOtherException(String msg, Exception cause) {
		super(msg, cause);
	}

}