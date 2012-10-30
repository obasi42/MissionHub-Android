package com.missionhub.api;

public class AccessTokenException extends ApiException {

	private static final long serialVersionUID = 1L;

	public AccessTokenException(final Throwable cause) {
		super(cause);
	}

	public AccessTokenException(final ApiErrorGson error) {
		super(error);
	}

}