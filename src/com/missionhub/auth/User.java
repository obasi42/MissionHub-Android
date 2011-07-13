package com.missionhub.auth;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.missionhub.api.GContact;
import com.missionhub.api.GOrgGeneric;
import com.missionhub.api.GPerson;
import com.missionhub.error.MHException;

import android.os.Bundle;
import android.util.Log;

public class User {

	/* Logging Tag */
	public static String TAG = User.class.getName();

	/* User's Contact Object */
	private static GContact contact;

	/* User's Organizations */
	private static HashMap<Integer, GOrgGeneric> organizations = new HashMap<Integer, GOrgGeneric>();

	/* User's Current Organization */
	private static int organizationID = -1;

	/* User's Primary Organization */
	private static int primaryOrganizationID = -1;

	/* User's Roles */
	private static HashMultimap<Integer, String> roles = HashMultimap.<Integer, String> create();

	/**
	 * Saves the user state to a bundle
	 * @param b The bundle
	 */
	public static synchronized void saveState(Bundle b) {
		if (b == null) return;
		try {
			Gson gson = new Gson();
			b.putString("_contact", gson.toJson(contact));
		} catch (Exception e) {Log.e(TAG, "User.saveState - Could Not Save Contact", e);};
		b.putInt("_organizationID", organizationID);
		b.putInt("_primaryOrganizationID", primaryOrganizationID);
	}

	/**
	 * Restores the user state from a bundle
	 * @param b The Bundle
	 */
	public static synchronized void restoreState(Bundle b) {
		if (b == null || b.isEmpty()) return;
		try {
			Gson gson = new Gson();
			setContact(gson.fromJson(b.getString("_contact"), GContact.class));
		} catch (Exception e) {Log.e(TAG, "User.restoreState - Could Not Restore Contact", e);};
		setOrganizationID(b.getInt("_organizationID"));
		setPrimaryOrganizationID(b.getInt("_primaryOrganizationID"));
	}

	/**
	 * Destroy the data in User
	 */
	public static synchronized void destroy() {
		contact = null;
		organizations.clear();
		organizationID = -1;
		primaryOrganizationID = -1;
		roles.clear();
	}

	/**
	 * Returns the GContact
	 * @return the GContact
	 */
	public static synchronized GContact getContact() {
		return User.contact;
	}

	/**
	 * Sets the GContact and Inits the User
	 * @param contact
	 * @throws MHException
	 */
	public static synchronized void setContact(GContact contact) throws MHException {
		User.contact = contact;
		initOrganizations(contact.getPerson());
		initRoles(contact.getPerson());
	}
	
	/**
	 * Get All Organizations
	 * @return all organizations
	 */
	public static synchronized HashMap<Integer, GOrgGeneric> getOrganizations() {
		return organizations;
	}

	/**
	 * returns the current organization id
	 * @return current organization id
	 */
	public static synchronized int getOrganizationID() {
		if (organizationID > -1)
			return organizationID;
		else
			return primaryOrganizationID;
	}

	/**
	 * Set the current organization id
	 * @param id
	 */
	public static synchronized void setOrganizationID(int id) {
		if (organizations.containsKey(id))
			organizationID = id;
	}

	/**
	 * Returns the primary organization id
	 * @return primary organization id
	 */
	public static synchronized int getPrimaryOrganizationID() {
		return primaryOrganizationID;
	}

	/**
	 * Sets the primary organizaiton id
	 * @param id
	 */
	public static synchronized void setPrimaryOrganizationID(int id) {
		if (organizations.containsKey(id))
			primaryOrganizationID = id;
	}

	/**
	 * Returns all user's roles
	 * @return all user's roles
	 */
	public static synchronized HashMultimap<Integer, String> getRoles() {
		return roles;
	}
	
	/** 
	 * Returns the user's roles for the current organization
	 * @return user's roles
	 */
	public static synchronized Set<String> getOrganizationRoles() {
		return getOrganizationRoles(getOrganizationID());
	}

	/**
	 * Returns the user's roles for the given organization
	 * @param orgID
	 * @return user's roles
	 */
	public static synchronized Set<String> getOrganizationRoles(int orgID) {
		return null;
	}
	
	/**
	 * Check if the user has an organization membership
	 * @param orgID
	 * @return true if is member
	 */
	public static synchronized boolean hasMembership(int orgID) {
		return organizations.containsKey(orgID);
	}

	/**
	 * Check if the user has a role in the current organization
	 * @param role
	 * @return true if has role
	 */
	public static synchronized boolean hasRole(String role) {
		return hasRole(role, getOrganizationID());
	}

	/**
	 * Check if the user has a role in the given organization
	 * @param role
	 * @param orgID
	 * @return true if has role
	 */
	public static synchronized boolean hasRole(String role, int orgID) {
		try {
			Set<String> orgRoles = roles.get(orgID);
			if (orgRoles.contains(role))
				return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * Initializes the organizations object
	 * @param person
	 * @throws MHException
	 */
	private static synchronized void initOrganizations(GPerson person) throws MHException {
		try {
			final GOrgGeneric[] memberships = person.getOrganizational_memebership();
			organizations.clear();
			for (int i = 0; i < memberships.length; i++) {
				final GOrgGeneric membership = memberships[i];
				organizations.put(membership.getOrg_id(), membership);
				if (membership.getPrimary().equalsIgnoreCase("true") || getPrimaryOrganizationID() < 0) {
					setPrimaryOrganizationID(membership.getOrg_id());
				}
			}
		} catch (Exception e) {
			throw new MHException(e.getMessage(), User.class.getName() + ".initOrganizations", e.getStackTrace());
		}
	}

	/**
	 * Initializes the roles object
	 * @param person
	 * @throws MHException
	 */
	private static synchronized void initRoles(GPerson person) throws MHException {
		try {
			final GOrgGeneric[] orgRoles = person.getOrganizational_roles();
			roles.clear();
			for (int i = 0; i < orgRoles.length; i++) {
				final GOrgGeneric role = orgRoles[i];
				roles.put(role.getOrg_id(), role.getRole());
			}
		} catch (Exception e) {
			throw new MHException(e.getMessage(), User.class.getName() + ".initRoles", e.getStackTrace());
		}
	}
}