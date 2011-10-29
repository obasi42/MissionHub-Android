package com.missionhub.error;

import com.missionhub.model.json.GError;

public class MHException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String title;
	
	public MHException(String title, String message, String code) {
		super(message);
		this.setTitle(title);
		this.code = code;
	}
	
	public MHException(String message, String code) {
		super(message);
		this.code = code;
	}
	
	public MHException(GError error) {
		super(error.getError().getMessage());
		this.code = error.getError().getCode();
		this.setTitle(error.getError().getTitle());
	}
	
	public MHException(String message, String code, StackTraceElement[] stackTrace) {
		this(message, code);
		this.setStackTrace(stackTrace);
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}