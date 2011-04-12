package com.beintoo.beintoosdkutility;

public class ApiCallException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String error;

	public ApiCallException() {
		super();
		error = "unknown";
	}

	public ApiCallException(String err) {
		super(err);
		error = err;
	}

	public String getError() {
		return error;
	}
	
	
}
