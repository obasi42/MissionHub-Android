package com.missionhub;

import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.Person;

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
	private final MissionHubApplication application;

	/** user's id */
	public final int id;

	/** user's labels */
	private SetMultimap<Integer, String> labels; // organizationId, label

	/** user's primary */
	private int primaryOrganization = -1;
	
	/** user's contact */
	private Person person;
	
	/**
	 * Creates a new user object
	 * 
	 * @param application
	 * @param id
	 */
	public User(final MissionHubApplication application, final int id) {
		this.application = application;
		this.id = id;
		
		person = application.getDbSession().getPersonDao().load(id);
		person.refresh();

		updateLabels();
		verifySessionOrganization();
	}

	/**
	 * Updates the labels multimap from sql
	 */
	private synchronized void updateLabels() {
		final SetMultimap<Integer, String> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Integer, String> create()); // organizationId,
		List<OrganizationalRole> roles = person.getOrganizationalRoleList();
		for (OrganizationalRole role : roles) {
			labels.put(role.getOrg_id(), role.getName());
			if (role.getPrimary() || primaryOrganization < 0) {
				primaryOrganization = role.getOrg_id();
			}
		}
		labels = labelsTemp;
	}
	
	/**
	 * Makes sure the organization set in the session is valid
	 */
	private synchronized void verifySessionOrganization() {
		if (application.getSession().getOrganizationId() < 0 || !labels.containsKey(application.getSession().getOrganizationId())) {
			application.getSession().setOrganizationId(primaryOrganization);
			Toast.makeText(application, R.string.user_toast_invalid_organization, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Checks if a user has a label (role)
	 * @param label
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(String label) {
		return hasLabel(application.getSession().getOrganizationId(), label);
	}
	
	/**
	 * Checks if a user has a label (role)
	 * @param label
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(int organizationId, String label) {
		return labels.containsEntry(organizationId, label);
	}
	
	/**
	 * Returns a multimap of all of the user's roles
	 * @return
	 */
	public synchronized SetMultimap<Integer, String> getLabels() {
		return  Multimaps.unmodifiableSetMultimap(labels);
	}
	
	/**
	 * Gets the user's person from sql
	 * @return
	 */
	public synchronized Person getPerson() {
		return person;
	}
	
	/**
	 * Returns the user's primary organization
	 * @return
	 */
	public synchronized int getPrimaryOrganization() {
		return primaryOrganization;
	}
}