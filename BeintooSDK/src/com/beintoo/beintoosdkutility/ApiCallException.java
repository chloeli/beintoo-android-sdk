package com.beintoo.beintoosdkutility;

public class ApiCallException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String error;
	Integer ID;
	
	public ApiCallException() {
		super();
		error = "unknown";
	}

	public ApiCallException(String err) {
		super(err);
		error = err;
	}
	
	public ApiCallException(String err, Integer id) {
		super(err);
		error = err;
		ID = id;
	}

	public String getError() {
		return error;
	}
	
	public Integer getId(){
		return ID;
	}
	
	
}
