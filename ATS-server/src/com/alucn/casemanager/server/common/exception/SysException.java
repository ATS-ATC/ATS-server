package com.alucn.casemanager.server.common.exception;


/**
 * system exception
 * @author wanghaiqi
 */
public class SysException extends BaseException {
 
	private static final long serialVersionUID = -5876709864402507632L;

	public SysException() {
		super();
	}

	public SysException(String message) {
		super(message);
	}
	
	public SysException(String errorCode, String message) {
		super(errorCode, message);
	}
	
	public SysException(String message, Throwable cause) {
		super(message, cause);
	}
	public SysException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
