package com.missionhub;

import java.util.Vector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.missionhub.broadcast.SessionBroadcast;

/**
 * Represents the currently logged in user
 */
public class User {
	
	/** logging tag */
	public static final String TAG = User.class.getSimpleName();
	
	/* system labels */
	public static final String LABEL_ADMIN = "admin";
	public static final String LABEL_CONTACT = "contact";
	public static final String LABEL_INVOLVED = "involved";
	public static final String LABEL_LEADER = "leader";
	public static final String LABEL_ALUMNI = "alumni";

	/** the application */
	public final MissionHubApplication application;

	/** user's id */
	public final int id;

	/** user's labels */
	private SetMultimap<Integer, String> labels; // organizationId, label
	
	/** user's primary */
	private int primaryOrganization = -1;
	
	/**
	 * Creates a new user object
	 * @param application
	 * @param id
	 */
	public User(final MissionHubApplication application, final int id) {
		this.application = application;
		this.id = id;
		
		updateLabels();
	}
	
	private void updateLabels() {
		SetMultimap<Integer, String> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Integer, String> create()); // organizationId, label
		
		
		labels = labelsTemp;
	}
}