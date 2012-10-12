package com.missionhub.api;

import com.missionhub.exception.ApiException;

/**
 * PJO representing a JSON api error
 */
public class ApiErrorGson {

	public InnerError error;

	public static class InnerError {
		public String message;
		public String code;
		public String title;
	}

	public ApiException getException() {
		return new ApiException(error.title, error.message, error.code);
	}
}
