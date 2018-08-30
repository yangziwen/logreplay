package io.github.yangziwen.logreplay.exception;

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
	
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
	public static NotExistException notExistException(String errorMsg, Object... args) {
		return new NotExistException(String.format(errorMsg, args));
	}
	
	public static OperationFailedException operationFailedException(String errorMsg, Object... args) {
		return new OperationFailedException(String.format(errorMsg, args));
	}
	
	public static InvalidParameterException invalidParameterException(String errorMsg, Object... args) {
		return new InvalidParameterException(String.format(errorMsg, args));
	}
	
	public static UnauthorizedException unauthorizedException(String errorMsg, Object... args) {
		return new UnauthorizedException(String.format(errorMsg, args));
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
