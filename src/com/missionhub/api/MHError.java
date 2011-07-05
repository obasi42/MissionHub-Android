package com.missionhub.api;

public class MHError extends Exception{
	private static final long serialVersionUID = 1L;
	
	public MHError(String message, String code) {
		super(message);
		this.code = code;
	}
	
	public MHError(GError error) {
		super(error.getError().getMessage());
		this.code = error.getError().getCode();
	}
	
	private String code;
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}