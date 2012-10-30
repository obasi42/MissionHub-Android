package com.missionhub.api;

import com.missionhub.exception.MissionHubException;

/**
 * All API errors extend or directly use this exception type.
 */
public class ApiException extends MissionHubException {

	private static final long serialVersionUID = 1L;

	private String mTitle;
	private String mCode;

	public ApiException() {
		super();
	}

	public ApiException(final String message) {
		super(message);
	}

	public ApiException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ApiException(final Throwable cause) {
		super(cause);
	}

	public ApiException(final ApiErrorGson error) {
		this(error.error.title, error.error.message, error.error.code);
	}

	public ApiException(final String title, final String message, final String code) {
		super(message);
		mTitle = title;
		mCode = code;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getCode() {
		return mCode;
	}
}