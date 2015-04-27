package com.sogou.map.logreplay.exception;

@SuppressWarnings("serial")
public class LogReplayException extends RuntimeException {
	
	public static final int UNEXPECTED_ERROR_ID = 999;
	
	private int errorId;
	private String errorMsg;

	public int getErrorId() {
		return errorId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public LogReplayException(int errorId, String errorMsg) {
		this.errorId = errorId;
		this.errorMsg = errorMsg;
	}
	
	public static NotExistException notExistException(String errorMsg) {
		return new NotExistException(errorMsg);
	}
	
	public static OperationFailedException operationFailedException(String errorMsg) {
		return new OperationFailedException(errorMsg);
	}
	
	public static InvalidParameterException invalidParameterException(String errorMsg) {
		return new InvalidParameterException(errorMsg);
	}
	
	public static UnauthorizedException unauthorizedException(String errorMsg) {
		return new UnauthorizedException(errorMsg);
	}
	
	// ----------------------------------- //
	
	public static class NotExistException extends LogReplayException {
		protected NotExistException(String errorMsg) {
			super(10001, errorMsg);
		}
	}
	
	public static class OperationFailedException extends LogReplayException {
		protected OperationFailedException(String errorMsg) {
			super(10002, errorMsg);
		}
	}
	
	public static class InvalidParameterException extends LogReplayException {
		protected InvalidParameterException(String errorMsg) {
			super(10003, errorMsg);
		}
	}
	
	public static class UnauthorizedException extends LogReplayException {
		protected UnauthorizedException(String errorMsg) {
			super(10004, errorMsg);
		}
	}

}
