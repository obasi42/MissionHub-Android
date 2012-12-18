package com.missionhub.exception;

public class MissionHubException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissionHubException() {
		super();
	}

	public MissionHubException(final String message) {
		super(message);
	}

	public MissionHubException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MissionHubException(final Throwable cause) {
		super(cause);
	}

}