package com.missionhub;

/**
 * Represents the currently logged in user
 */
public class User {

	public final MissionHubApplication application;

	public final int id;

	public User(final MissionHubApplication application, final int id) {
		this.application = application;
		this.id = id;
	}

}