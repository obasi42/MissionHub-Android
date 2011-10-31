package com.missionhub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.missionhub.api.ApiClient;
import com.missionhub.api.ApiResponseHandler;
import com.missionhub.api.People;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.json.GMetaPerson;
import com.missionhub.api.model.json.GPerson;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.OrganizationalRoleDao;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.api.model.sql.OrganizationalRoleDao.Properties;
import com.missionhub.config.Preferences;
import com.missionhub.helper.U;
import com.missionhub.ui.DisplayError;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

public class ApplicationUser {
	
	/* The Application */
	private Application ctx;
	
	/* Logging Tag */
	public static String TAG = ApplicationUser.class.getName();
	
	/* User's person Id */
	private int id;

	/* User's Contact Object */
	private Person person;
	
	/* User's Access Token */
	private String accessToken;
	
	/* User's Logged In State */
	private boolean loggedIn = false;

	/* User's Organizations */
	private HashMap<Integer, OrganizationalRole> organizations = new HashMap<Integer, OrganizationalRole>();

	/* User's Current Organization */
	private int organizationID = -1;

	/* User's Primary Organization */
	private int primaryOrganizationID = -1;

	/* User's Roles */
	private HashMultimap<Integer, String> roles = HashMultimap.<Integer, String> create();

	/**
	 * Create a new application user
	 * @param ctx Application Context
	 * @param personId person id
	 */
	public ApplicationUser(Application ctx, int personId) {
		this.ctx = ctx;
		this.setId(personId);
		
		PersonDao pd = ctx.getDbSession().getPersonDao();
		person = pd.load(personId);
		update();
	}
	
	public synchronized void update() {
		initOrgsRoles();
		if (!U.nullOrEmpty(person) && !roles.isEmpty() && !U.nullOrEmpty(getAccessToken())) {
			setLoggedIn(true);
		} else {
			setLoggedIn(false);
		}
	}
	
	public synchronized void refresh(Context context) {
		fetchPerson(context, false);
	}
	
	public synchronized void refreshSilent() {
		fetchPerson(ctx, true);
	}
	
	/**
	 * Returns the Person
	 * @return the Person
	 */
	public synchronized Person getPerson() {
		return person;
	}
	
	private synchronized void setPerson(Person person) {
		this.person = person;
	}
	
	/**
	 * Get the User's access token
	 * @return
	 */
	public synchronized String getAccessToken() {
		if (accessToken == null) {
			accessToken = Preferences.getAccessToken(ctx);
		}
		return accessToken;
	}
	
	/**
	 * Set the User's access token
	 * @param accessToken
	 */
	public synchronized void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Set the User's personId
	 * @param personId
	 */
	public synchronized void setId(int personId) {
		this.id = personId;
	}

	/**
	 * Get the User's personId
	 * @return
	 */
	public synchronized int getId() {
		if (id < 0) {
			id = Preferences.getUserID(ctx);
		}
		return id;
	}

	/**
	 * Set the User's logged in status
	 * @param loggedIn
	 */
	public synchronized void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			ctx.postMessage(Application.MESSAGE_USER_LOGGED_IN);
		} else {
			ctx.postMessage(Application.MESSAGE_USER_LOGGED_OUT);
		}
		this.loggedIn = loggedIn;
	}

	/**
	 * Get the User's logged in status
	 * @return
	 */
	public synchronized boolean isLoggedIn() {
		return loggedIn;
	}
	
	/**
	 * Get All Organizations
	 * @return all organizations
	 */
	public synchronized HashMap<Integer, OrganizationalRole> getOrganizations() {
		return organizations;
	}

	/**
	 * returns the current organization id
	 * @return current organization id
	 */
	public synchronized int getOrganizationID() {
		if (organizationID > -1)
			return organizationID;
		else
			return primaryOrganizationID;
	}

	/**
	 * Set the current organization id
	 * @param id
	 */
	public synchronized void setOrganizationID(int id) {
		if (organizations.containsKey(id))
			organizationID = id;
	}

	/**
	 * Returns the primary organization id
	 * @return primary organization id
	 */
	public synchronized int getPrimaryOrganizationID() {
		return primaryOrganizationID;
	}

	/**
	 * Sets the primary organization id
	 * @param id
	 */
	public synchronized void setPrimaryOrganizationID(int id) {
		if (organizations.containsKey(id))
			primaryOrganizationID = id;
	}

	/**
	 * Returns all user's roles
	 * @return all user's roles
	 */
	public synchronized HashMultimap<Integer, String> getRoles() {
		return roles;
	}
	
	/** 
	 * Returns the user's roles for the current organization
	 * @return user's roles
	 */
	public synchronized Set<String> getOrganizationRoles() {
		return getOrganizationRoles(getOrganizationID());
	}

	/**
	 * Returns the user's roles for the given organization
	 * @param orgID
	 * @return user's roles
	 */
	public synchronized Set<String> getOrganizationRoles(int orgID) {
		return roles.get(orgID);
	}
	
	/**
	 * Check if the user has an organization membership
	 * @param orgID
	 * @return true if is member
	 */
	public synchronized boolean hasMembership(int orgID) {
		return organizations.containsKey(orgID);
	}

	/**
	 * Check if the user has a role in the current organization
	 * @param role
	 * @return true if has role
	 */
	public synchronized boolean hasRole(String role) {
		return hasRole(role, getOrganizationID());
	}

	/**
	 * Check if the user has a role in the given organization
	 * @param role
	 * @param orgID
	 * @return true if has role
	 */
	public synchronized boolean hasRole(String role, int orgID) {
		try {
			Set<String> orgRoles = roles.get(orgID);
			if (orgRoles.contains(role))
				return true;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * Initializes a user's organizations and roles
	 * @param person
	 * @throws MHException
	 */
	private synchronized void initOrgsRoles() {
		try {
			organizations.clear();
			roles.clear();
			
			OrganizationalRoleDao ord = ctx.getDbSession().getOrganizationalRoleDao();
			List<OrganizationalRole> orgRoles = ord.queryBuilder().where(Properties.Person_id.eq(getId())).list();
			Iterator<OrganizationalRole> itr = orgRoles.iterator();
			while (itr.hasNext()) {
				OrganizationalRole role = itr.next();
				organizations.put(role.getOrg_id(), role);
				if (role.getPrimary() || getPrimaryOrganizationID() < 0) {
					setPrimaryOrganizationID(role.getOrg_id());
				}
				roles.put(role.getOrg_id(), role.getRole());
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to initOrgRoles", e);
		}
	}
	
	/**
	 * Log User Out
	 * @param ctx Context
	 */
	public synchronized void logout() {
		person = new Person();
		id = -1;
		organizations.clear();
		organizationID = -1;
		primaryOrganizationID = -1;
		roles.clear();
		setAccessToken(null);
		setLoggedIn(false);
		Preferences.removeUserID(ctx);
		Preferences.removeAccessToken(ctx);
		Preferences.removeOrganizationID(ctx);
		ctx.postMessage(Application.MESSAGE_USER_LOGGED_OUT);
	}
	
	private ApiClient client;
	private ProgressDialog mProgressDialog;
	
	
	private synchronized ApiClient getApiClient() {
		return client;
	}
	
	private synchronized void setApiClient(ApiClient client) {
		this.client = client;
	}
	
	private synchronized ProgressDialog getProgressDialog() {
		return mProgressDialog;
	}
	
	private synchronized void setProgressDialog(ProgressDialog pd) {
		mProgressDialog = pd;
	}
	
	
	/**
	 * Check Stored Access Token
	 * @return true if has stored token
	 */
	private synchronized boolean fetchPerson(final Context ctx, boolean silent) {
		if (getAccessToken() != null) {
			setApiClient(new ApiClient(ctx));
			
			if (!silent) {
				setProgressDialog(ProgressDialog.show(ctx, "", ctx.getString(R.string.alert_logging_in), true));
				getProgressDialog().setCancelable(true);
				getProgressDialog().setOnCancelListener(new OnCancelListener(){
					@Override
					public void onCancel(DialogInterface dialog) {
						getApiClient().cancel(true);
						setApiClient(null);
					}
				});
			}
			
			setApiClient(People.getMe(ctx, new MeResponseHandler(ctx, silent)));
			return true;
		}
		return false;
	}
	
	private class MeResponseHandler extends ApiResponseHandler {
		
		Context ctx;
		boolean silent = false;
		
		public MeResponseHandler(Context ctx, boolean silent) {
			super(GMetaPerson.class);
			this.ctx = ctx;
			this.silent = silent;
		}
		
		@Override
		public void onSuccess(Object gMetaPerson) {
			if (getApiClient() == null)
				return;
			
			GMetaPerson personMeta = (GMetaPerson) gMetaPerson;
			GPerson[] people = personMeta.getPeople();
			try {
				if (people.length > 0) {
					PersonJsonSql.update(ctx, people[0]);
					setId(people[0].getId());
					PersonDao pd = ((Application) ctx.getApplicationContext()).getDbSession().getPersonDao();
					setPerson(pd.load(getId()));
					update();
				}
			} catch (Exception e) {
				onFailure(e);
			}
		}
		
		@Override
		public void onFailure(Throwable e) {
			if (getApiClient() == null)
				return;
			
			setPerson(null);
			
			if (!silent) {
				AlertDialog ad = DisplayError.display(ctx, e);
				ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						fetchPerson(ctx, silent);
					}
				});
				ad.show();
			}
			Log.e(TAG, "Auto Login Failed", e);
		}
		
		@Override
		public void onFinish() {
			if (getProgressDialog() != null && getProgressDialog().isShowing())
				getProgressDialog().dismiss();
		}
	};
}