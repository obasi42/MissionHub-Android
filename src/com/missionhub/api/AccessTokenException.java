package com.missionhub.api;

import com.missionhub.model.gson.GErrorsDepreciated;

public class AccessTokenException extends ApiException {

	private static final long serialVersionUID = 1L;

	public AccessTokenException(final Throwable cause) {
		super(cause);
	}

	public AccessTokenException(final GErrorsDepreciated error) {
		super(error);
	}

}