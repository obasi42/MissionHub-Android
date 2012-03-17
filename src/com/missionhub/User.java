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
	public final long id;

	/** user's labels */
	private SetMultimap<Long, String> labels; // organizationId, label

	/** user's primary */
	private long primaryOrganization = -1l;

	/** user's contact */
	private Person person;

	/**
	 * Creates a new user object
	 * 
	 * @param application
	 * @param id
	 */
	public User(final MissionHubApplication application, final long id) {
		this.application = application;
		this.id = id;

		Log.e("NEW USER", "" + id);

		person = application.getDbSession().getPersonDao().load(id);
		if (person == null) {
			return;
		}

		person.refresh();

		updateLabels();
		verifySessionOrganization();
	}

	/**
	 * Updates the labels multimap from sql
	 */
	private synchronized void updateLabels() {
		final SetMultimap<Long, String> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Long, String> create()); // organizationId,
		final List<OrganizationalRole> roles = person.getOrganizationalRoleList();
		for (final OrganizationalRole role : roles) {
			labelsTemp.put(role.getOrganization_id(), role.getRole());
			if (role.getPrimary() || primaryOrganization < 0) {
				primaryOrganization = role.getOrganization_id();
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
	 * Checks if a user has one of the given labels (role)
	 * 
	 * @param label
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(final String... label) {
		return hasLabel(application.getSession().getOrganizationId(), label);
	}

	/**
	 * Checks if a user has one of the given labels (role)
	 * 
	 * @param label
	 * @param organizationId
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(final long organizationId, final String... label) {
		boolean has = false;
		for (final String l : label) {
			if (labels.containsEntry(organizationId, l)) {
				has = true;
			}
		}
		return has;
	}

	/**
	 * Checks if a user has all of the given labels (role)
	 * 
	 * @param label
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabels(final String... label) {
		return hasLabels(application.getSession().getOrganizationId(), label);
	}

	/**
	 * Checks if a user has all of the given labels (role)
	 * 
	 * @param label
	 * @param organizationId
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabels(final long organizationId, final String... label) {
		boolean has = true;
		for (final String l : label) {
			if (!labels.containsEntry(organizationId, l)) {
				has = false;
			}
		}
		return has;
	}

	/**
	 * Returns a multimap of all of the user's labels
	 * 
	 * @return
	 */
	public synchronized SetMultimap<Long, String> getLabels() {
		return Multimaps.unmodifiableSetMultimap(labels);
	}

	/**
	 * Gets the user's person from sql
	 * 
	 * @return
	 */
	public synchronized Person getPerson() {
		return person;
	}

	/**
	 * Returns the user's primary organization
	 * 
	 * @return
	 */
	public synchronized long getPrimaryOrganization() {
		return primaryOrganization;
	}

	/**
	 * Returns true if the user is an admin or leader in current organization
	 * 
	 * @return
	 */
	public synchronized boolean isAdminOrLeader() {
		return isAdmin() || isLeader();
	}

	/**
	 * Returns true if the user is an admin or leader in the given
	 * organizationId
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isAdminOrLeader(final long organizationId) {
		return isAdmin(organizationId) || isLeader(organizationId);
	}

	/**
	 * Returns true if the user is an admin in the current organization
	 * 
	 * @return
	 */
	public synchronized boolean isAdmin() {
		return isAdmin(application.getSession().getOrganizationId());
	}

	/**
	 * Returns true if the user is an admin in the given organizationId
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isAdmin(final long organizationId) {
		return hasLabel(organizationId, LABEL_ADMIN);
	}

	/**
	 * Returns true if the user is a leader in the current organization
	 * 
	 * @return
	 */
	public synchronized boolean isLeader() {
		return isLeader(application.getSession().getOrganizationId());
	}

	/**
	 * Returns true if the user is a leader in the given organization
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isLeader(final long organizationId) {
		return hasLabel(organizationId, LABEL_LEADER);
	}
}