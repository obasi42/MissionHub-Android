package com.missionhub.exception;

public class OfflineException extends MissionHubException {

	private static final long serialVersionUID = 1L;

	public OfflineException() {
		super("MissionHub is currently in offline mode and cannot complete the request.");
	}

}