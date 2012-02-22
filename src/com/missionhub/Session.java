package com.missionhub;

import java.util.ArrayList;
import java.util.List;

import com.missionhub.api.ApiHandler;
import com.missionhub.api.OrganizationsApi;
import com.missionhub.api.PeopleApi;
import com.missionhub.api.convert.OrganizationJsonSql;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.GMetaOrganizations;
import com.missionhub.api.model.GMetaPeople;
import com.missionhub.api.model.GPerson;
import com.missionhub.broadcast.OrganizationBroadcast;
import com.missionhub.broadcast.OrganizationReceiver;
import com.missionhub.broadcast.PersonBroadcast;
import com.missionhub.broadcast.PersonReceiver;
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
	private long personId = -1;

	/** user's current organization */
	private long organizationId = -1;

	/** current user */
	private User user;
	
	/** state for updatePerson() */
	private boolean updatingPerson = false;
	
	/** state for updateOrganizations() */
	private boolean updatingOrganizations = false;

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

		application.deleteDatabase();

		// Tell the application we logged out
		SessionBroadcast.broadcastLogout(application);
	}

	/**
	 * Attempts to resume a previous user session
	 * 
	 * @return true if resume data is available
	 */
	public synchronized boolean resumeSession() {
		final long userId = Preferences.getUserID(application);
		final long organizationId = Preferences.getOrganizationID(application);
		final String accessToken = Preferences.getAccessToken(application);

		if (accessToken != null && accessToken.trim().length() > 0) {
			setPersonId(userId);
			setOrganizationId(organizationId);
			setAccessToken(accessToken);
			setUser(new User(application, userId));
			SessionBroadcast.broadcastLogin(application, application.getSession().getAccessToken());
			if (userId < 0) {
				updatePerson();
				updateOrganizations();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the current user's information
	 */
	public synchronized void updatePerson() {
		if (updatingPerson) return;
		updatingPerson = true;
		
		SessionBroadcast.broadcastUpdatePersonStart(application);

		PeopleApi.getMe(application, new ApiHandler(GMetaPeople.class) {
			@Override public void onSuccess(final Object gsonObject) {
				super.onSuccess(gsonObject);

				// if user logged out before verify finished
				if (getAccessToken().equalsIgnoreCase("") || getAccessToken() == null) {
					return;
				}

				final GMetaPeople metaPeople = (GMetaPeople) gsonObject;
				final GPerson[] people = metaPeople.getPeople();

				if (people.length > 0) {
					final GPerson person = people[0];

					final PersonReceiver pr = new PersonReceiver(application) {
						@Override public void onUpdate(final long personId) {
							if (personId != person.getId()) {
								return;
							}

							if ((getOrganizationId() < 0 || getPersonId() != person.getId()) && metaPeople.getMeta().getRequest_organization() != null) {
								setOrganizationId(Integer.parseInt(metaPeople.getMeta().getRequest_organization()));
								Preferences.setOrganizationID(application, getOrganizationId());
							}
							setPersonId(person.getId());

							Preferences.setAccessToken(application, getAccessToken());
							Preferences.setUserID(application, getPersonId());

							setUser(new User(application, getPersonId()));

							SessionBroadcast.broadcastUpdatePersonSuccess(application);

							updatingPerson = false;
							
							unregister();
						}
					};

					final List<String> cats = new ArrayList<String>();
					cats.add("updatePerson");

					pr.register(PersonBroadcast.NOTIFY_PERSON_UPDATE, cats);

					PersonJsonSql.update(application, person, "updatePerson");
				} else {
					onError(new ApiException("Session.updatePerson did not return any people"));
				}
			}

			@Override public void onError(final Throwable throwable) {
				super.onError(throwable);
				updatingPerson = false;
				SessionBroadcast.broadcastUpdatePersonError(application, throwable);
			}
		});
	}

	/**
	 * Update the current user's organizations
	 */
	public synchronized void updateOrganizations() {
		updateOrganizations(null);
	}

	/**
	 * Update the current user's organizations
	 * @param organizations the orgs to update
	 */
	public synchronized void updateOrganizations(final long... organizations) {
		if (updatingOrganizations) return;
		updatingOrganizations = true;
		
		SessionBroadcast.broadcastUpdateOrganizationsStart(application);

		final ApiHandler hander = new ApiHandler(GMetaOrganizations.class) {
			@Override public void onSuccess(final Object gsonObject) {
				super.onSuccess(gsonObject);

				// if user logged out during fetch
				if (getAccessToken().equalsIgnoreCase("") || getAccessToken() == null) {
					return;
				}
				
				final OrganizationReceiver or = new OrganizationReceiver(application) {
					@Override public void onComplete(final long[] organizationIds) {
						SessionBroadcast.broadcastUpdateOrganizationsSuccess(application);
						updatingOrganizations = false;
						unregister();
					}

					@Override public void onError(final long[] organizationIds, final Throwable throwable) {
						SessionBroadcast.broadcastUpdateOrganizationsError(application, throwable);
						updatingOrganizations = false;
						unregister();
					}
				};
				final List<String> cats = new ArrayList<String>();
				cats.add("updateOrganizations");
				or.register(cats, OrganizationBroadcast.NOTIFY_ORGANIZATIONS_COMPLETE, OrganizationBroadcast.NOTIFY_ORGANIZATIONS_ERROR);

				final GMetaOrganizations metaOrganizations = (GMetaOrganizations) gsonObject;
				OrganizationJsonSql.update(application, metaOrganizations.getOrganizations(), true, true, "updateOrganizations");
			}

			@Override public void onError(final Throwable throwable) {
				super.onError(throwable);
				SessionBroadcast.broadcastUpdateOrganizationsError(application, throwable);
			}
		};

		if (organizations != null) {
			final List<Long> orgs = new ArrayList<Long>();
			for (final long org : organizations) {
				orgs.add(org);
			}
			OrganizationsApi.get(application, orgs, hander);
		} else {
			OrganizationsApi.get(application, hander);
		}
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
	public synchronized long getPersonId() {
		return personId;
	}

	/**
	 * Sets the session's user id
	 * 
	 * @param personId
	 */
	public synchronized void setPersonId(final long personId) {
		this.personId = personId;
	}

	/**
	 * Gets the session's organization id
	 * 
	 * @return
	 */
	public synchronized long getOrganizationId() {
		return organizationId;
	}

	/**
	 * Sets the session's organization id
	 * 
	 * @param organizationId
	 */
	public synchronized void setOrganizationId(final long organizationId) {
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