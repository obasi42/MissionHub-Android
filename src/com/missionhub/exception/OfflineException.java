package com.missionhub.exception;

import com.missionhub.exception.ExceptionHelper.ExceptionHelperException;

public class OfflineException extends MissionHubException implements ExceptionHelperException {

	private static final long serialVersionUID = 1L;

	public OfflineException() {
		super("MissionHub is currently working offline. Check your internet connection and retry.");
	}

	@Override
	public String getDialogTitle() {
		return "Offline Error";
	}

	@Override
	public String getDialogMessage() {
		return getMessage();
	}

	@Override
	public int getDialogIconId() {
		return 0;
	}

}