package com.missionhub.api.model.json;

public class GError {
	public static class InnerError {
		private String message;
		private String code;
		private String title;

		public String getMessage() {
			return message;
		}

		public String getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

		public void setMessage(String s) {
			message = s;
		}

		public void setCode(String s) {
			code = s;
		}

		public void setTitle(String t) {
			title = t;
		}
	}

	private InnerError error;

	public InnerError getError() {
		return error;
	}

	public void setError(InnerError e) {
		error = e;
	}

}
