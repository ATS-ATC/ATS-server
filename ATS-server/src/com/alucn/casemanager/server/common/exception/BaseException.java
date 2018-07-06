package com.alucn.casemanager.server.common.exception;

public class BaseException extends Exception {

	private static final long serialVersionUID = 8547261488299166908L;

	private String errorCode = "20000";

	private String showMessage;

	private Throwable cause;

	public BaseException() {

	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BaseException(String message, Throwable cause) {
		super(message, cause);
		this.cause = cause;

	}

	/**
	 * @param message
	 * @param cause
	 */
	public BaseException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.cause = cause;

	}

	public String getMessage() {
		String msg = super.getMessage();

		String causeMsg = null;
		if (cause != null)
			causeMsg = cause.getMessage();

		if (msg != null) {
			if (causeMsg != null)
				return msg + " caused by: " + causeMsg;
			else
				return msg;
		} else {
			return causeMsg;
		}
	}

	/**
	 * @param cause
	 */
	public BaseException(Throwable cause) {
		super(cause);
		this.cause = cause;
	}

	/**
	 * @return java.lang.String
	 */
	public String toString() {
		if (cause == null)
			return super.toString();
		else
			return super.toString() + " cause: " + cause.toString();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getShowMessage() {
		if (showMessage == null)
			return super.getMessage();

		return showMessage;
	}

	public void setShowMessage(String showMessage) {
		this.showMessage = showMessage;
	}
}
