package com.missionhub.network;

import java.io.IOException;

import com.missionhub.exception.ExceptionHelper.ExceptionHelperException;

public class NetworkUnavailableException extends IOException implements ExceptionHelperException {

	private static final long serialVersionUID = 4513095539506465265L;

	public NetworkUnavailableException() {
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