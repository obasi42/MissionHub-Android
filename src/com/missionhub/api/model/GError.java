package com.missionhub.api.model;

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

		public void setMessage(final String s) {
			message = s;
		}

		public void setCode(final String s) {
			code = s;
		}

		public void setTitle(final String t) {
			title = t;
		}
	}

	private InnerError error;

	public InnerError getError() {
		return error;
	}

	public void setError(final InnerError e) {
		error = e;
	}

}
