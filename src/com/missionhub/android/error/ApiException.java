package com.missionhub.android.error;

import com.missionhub.android.api.old.model.GError;

public class ApiException extends MissionHubException {

	public ApiException(final GError error) {
		super(error);
	}

	public ApiException(final Throwable t) {
		super(t);
	}

	public ApiException(final String string) {
		super(string);
	}

	private static final long serialVersionUID = 4807066089587386456L;

}
