package com.missionhub.android.error;

import com.missionhub.android.api.old.model.GError;

public class MissionHubException extends Exception {
	private static final long serialVersionUID = 1L;

	private String code;
	private String title;

	public MissionHubException(final String message) {
		super(message);
	}

	public MissionHubException(final String title, final String message, final String code) {
		super(message);
		this.setTitle(title);
		this.code = code;
	}

	public MissionHubException(final String message, final String code) {
		super(message);
		this.code = code;
	}

	public MissionHubException(final GError error) {
		super(error.getError().getMessage());
		this.code = error.getError().getCode();
		this.setTitle(error.getError().getTitle());
	}

	public MissionHubException(final String message, final String code, final StackTraceElement[] stackTrace) {
		this(message, code);
		this.setStackTrace(stackTrace);
	}

	public MissionHubException(final Throwable t) {
		super(t);
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}
}