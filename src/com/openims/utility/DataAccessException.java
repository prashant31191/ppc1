package com.openims.utility;

public class DataAccessException extends RuntimeException {

	private static final long serialVersionUID = 339814065993824993L;
	
	public final static int TYPE_UNKNOWN = 0;
	public final static int TYPE_INSERT = 1;
	public final static int TYPE_DELETE = 2;
	public final static int TYPE_UPDATE = 3;
	public final static int TYPE_QUERY = 4;
	public final static int TYPE_CREATE = 5;
	
	private int errorType = 0;
	
	public DataAccessException(String message){
		super(message);
	}

	public int getErrorType() {
		return errorType;
	}
	
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	
}
