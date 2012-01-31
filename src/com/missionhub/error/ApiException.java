package com.missionhub.error;

import com.missionhub.api.model.GError;

public class ApiException extends MissionHubException {

	public ApiException(GError error) {
		super(error);
	}
	
	public ApiException(Throwable t) {
		super(t);
	}

	private static final long serialVersionUID = 4807066089587386456L;

}
