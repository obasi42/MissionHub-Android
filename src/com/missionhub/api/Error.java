package com.missionhub.api;

public class Error {
	public static class InnerError {
		private String message;
		private String code;
		
		public String getMessage() { return message; }
		public String getCode() { return code; }
		
		public void setMessage(String s) { message = s; }
		public void setCode(String s) { code = s; }
	}
	
	private InnerError error;
	
	public InnerError getError() { return error; }
	public void setError(InnerError e) { error = e; }

}
