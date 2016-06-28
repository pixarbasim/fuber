package com.fuber.errorhandling;

public class FuberException extends Exception {
	private static final long serialVersionUID = 1L;
	private int status;
	private int code;
	private String message;
	private String developerMessage;

	public FuberException(int pStatus, int pCode, String pMessage, String pDeveloperMessage) {
		super(pMessage);
		status = pStatus;
		code = pCode;
		message = pMessage;
		developerMessage = pMessage;

	}

	public FuberException() {

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
