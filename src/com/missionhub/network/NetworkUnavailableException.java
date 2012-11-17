package com.missionhub.network;

import java.io.IOException;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.exception.ExceptionHelper.ExceptionHelperException;

public class NetworkUnavailableException extends IOException implements ExceptionHelperException {

	private static final long serialVersionUID = 4513095539506465265L;

	public NetworkUnavailableException() {
		super(Application.getContext().getString(R.string.network_unavailable_exception));
	}

	@Override
	public String getDialogTitle() {
		return Application.getContext().getString(R.string.network_unavailable_exception_title);
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