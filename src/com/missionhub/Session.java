package com.missionhub;

import com.missionhub.api.ApiHandler;
import com.missionhub.api.PeopleApi;
import com.missionhub.api.model.GMetaPeople;
import com.missionhub.api.model.GPerson;
import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.config.Preferences;
import com.missionhub.error.ApiException;

/**
 * Stores state and user data for the application session
 */
public class Session {

	/** logging tag */
	public static final String TAG = Session.class.getSimpleName();

	/** the application */
	private final MissionHubApplication application;

	/** user's access token */
	private String accessToken = "";

	/** users's person id */
	private int personId = -1;

	/** user's current organization */
	private int organizationId = -1;

	/** current user */
	private User user;

	/**
	 * Creates a new session
	 * 
	 * @param application
	 */
	protected Session(final MissionHubApplication application) {
		this.application = application;
		setUser(new User(application, -1));
	}

	/**
	 * Returns true if the session is active
	 * 
	 * @return
	 */
	public synchronized boolean isActive() {
		return getPersonId() > 0 && getOrganizationId() > 0 && getAccessToken().trim().length() > 0;
	}

	/**
	 * Logs the user out
	 */
	public synchronized void logout() {
		// Kill the current session
		setPersonId(-1);
		setOrganizationId(-1);
		setAccessToken("");
		setUser(new User(application, -1));

		// Remove stored session data
		Preferences.removeAccessToken(application);
		Preferences.removeOrganizationID(application);
		Preferences.removeUserID(application);

		// Tell the application we logged out
		SessionBroadcast.broadcastLogout(application);
	}

	/**
	 * Attempts to resume a previous user session
	 * 
	 * @param verifySession
	 *            if the session should connect to the missionhub server to
	 *            verify the session
	 * 
	 * @return true if resume data is available
	 */
	public synchronized boolean resumeSession(final boolean verifySession) {
		final int userId = Preferences.getUserID(application);
		final int organizationId = Preferences.getOrganizationID(application);
		final String accessToken = Preferences.getAccessToken(application);

		if (accessToken != null && accessToken.trim().length() > 0) {
			setPersonId(userId);
			setOrganizationId(organizationId);
			setAccessToken(accessToken);
			setUser(new User(application, userId));
			if (verifySession) {
				verifySession();
			} else {
				SessionBroadcast.broadcastLogin(application, application.getSession().getAccessToken());
			}
			return true;
		}
		return false;
	}

	public synchronized void verifySession() {
		SessionBroadcast.broadcastVerifyStart(application);

		PeopleApi.getMe(application, new ApiHandler(GMetaPeople.class) {
			@Override public void onSuccess(final Object gsonObject) {
				super.onSuccess(gsonObject);

				final GMetaPeople metaPeople = (GMetaPeople) gsonObject;
				final GPerson[] people = metaPeople.getPeople();

				if (people.length > 0) {
					final GPerson person = people[0];
					if ((getOrganizationId() < 0 || getPersonId() != person.getId()) && metaPeople.getMeta().getRequest_organization() != null) {
						setOrganizationId(Integer.parseInt(metaPeople.getMeta().getRequest_organization()));
					}
					setPersonId(person.getId());
					setUser(new User(application, getPersonId()));
					
					
					
					SessionBroadcast.broadcastVerifyPass(application);
					SessionBroadcast.broadcastLogin(application, application.getSession().getAccessToken());
				} else {
					onError(new ApiException("VerifySession did not return any people"));
				}
			}

			@Override public void onError(final Throwable throwable) {
				super.onError(throwable);
				SessionBroadcast.broadcastVerifyFail(application, throwable);
			}
		});
	}

	/**
	 * Gets the session access token
	 * 
	 * @return
	 */
	public synchronized String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the session access token
	 * 
	 * @param accessToken
	 */
	public synchronized void setAccessToken(String accessToken) {
		if (accessToken == null) {
			accessToken = "";
		}
		this.accessToken = accessToken;
	}

	/**
	 * Gets the session's user id
	 * 
	 * @return
	 */
	public synchronized int getPersonId() {
		return personId;
	}

	/**
	 * Sets the session's user id
	 * 
	 * @param personId
	 */
	public synchronized void setPersonId(final int personId) {
		this.personId = personId;
	}

	/**
	 * Gets the session's organization id
	 * 
	 * @return
	 */
	public synchronized int getOrganizationId() {
		return organizationId;
	}

	/**
	 * Sets the session's organization id
	 * 
	 * @param organizationId
	 */
	public synchronized void setOrganizationId(final int organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * Gets the session's current user
	 * 
	 * @return
	 */
	public synchronized User getUser() {
		if (user == null) {
			setUser(null);
		}
		return user;
	}

	/**
	 * Sets the session's current user
	 * 
	 * @param user
	 */
	public synchronized void setUser(final User user) {
		if (user == null) {
			setUser(new User(application, -1));
		}
		this.user = user;
	}
}