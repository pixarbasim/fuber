package com.fuber.errorhandling;

import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.beanutils.BeanUtils;

import com.sun.jersey.api.NotFoundException;

@XmlRootElement
public class ErrorMessage {

	@XmlElement(name = "status")
	private int status;
	@XmlElement(name = "code")
	private int code;
	@XmlElement(name = "message")
	private String message;
	@XmlElement(name = "developerMessage")
	private String developerMessage;

	public ErrorMessage() {

	}

	public ErrorMessage(FuberException pFuberException) {
		try {
			BeanUtils.copyProperties(this, pFuberException);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public ErrorMessage(NotFoundException pNotFoundException){
		this.status = Response.Status.NOT_FOUND.getStatusCode();
		this.message = pNotFoundException.getMessage();
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
