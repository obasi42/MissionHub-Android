package com.missionhub;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.missionhub.api.ApiHandler;
import com.missionhub.api.MetaApi;
import com.missionhub.api.convert.MetaJsonSql;
import com.missionhub.api.model.GMetaMeta;
import com.missionhub.broadcast.GenericSEBroadcast;
import com.missionhub.broadcast.GenericSEBroadcast.Type;
import com.missionhub.broadcast.GenericSEReceiver;
import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.broadcast.SessionReceiver;
import com.missionhub.config.Config;
import com.missionhub.config.Preferences;
import com.missionhub.util.U;

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
	private User user = null;

	/** state for update() */
	private boolean updating = false;

	/**
	 * Returns a session with previous information
	 * 
	 * @param application
	 * @return
	 */
	public static void resumeSession(final MissionHubApplication application) {
		String accessToken = Preferences.getAccessToken(application);
		long userId = Preferences.getUserID(application);
		long organizationId = Preferences.getOrganizationID(application);

		if (Config.debug && Config.autoLoginToken != null && Config.autoLoginToken.trim().length() > 0) {
			accessToken = Config.autoLoginToken.trim();
			userId = -1;
			organizationId = -1;
		}

		Session session = application.getSession();
		if (application.getSession() == null) {
			session = new Session(application);
		}

		if (!U.isNullEmpty(accessToken)) {
			session.setAccessToken(accessToken);
		}
		if (userId > -1) {
			final User user = new User(application, userId);
			if (user != null && user.getPerson() != null && user.getPerson().getName() != null && user.getPerson().getName().trim().length() > 0) {
				session.setUser(user);
			}
		}
		if (organizationId > -1) {
			session.setOrganizationId(organizationId);
		}
		application.setSession(session);

		if (application.getSession().isValid()) {
			SessionBroadcast.broadcastLogin(application, application.getSession().getAccessToken());
		} else if (!U.isNullEmpty(application.getSession().getAccessToken())) {
			final SessionReceiver sr = new SessionReceiver(application) {
				@Override
				public void onUpdateSuccess() {
					Session.resumeSession(application);
					unregister();
				}

				@Override
				public void onUpdateError(final Throwable t) {
					Log.w(TAG, t.getMessage(), t);
					unregister();
				}
			};
			sr.register(SessionBroadcast.NOTIFY_SESSION_UPDATE_SUCCESS, SessionBroadcast.NOTIFY_SESSION_UPDATE_ERROR);
			application.getSession().update();
		}
	}

	/**
	 * Creates a new session
	 * 
	 * @param application
	 */
	private Session(final MissionHubApplication application) {
		this.application = application;
	}

	public synchronized boolean isValid() {
		if (!U.isNullEmptyNegative(getAccessToken(), getPersonId(), getOrganizationId(), getUser()) && getUser().getPerson() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Logs the user out
	 */
	public synchronized void logout() {
		application.reset();

		SessionBroadcast.broadcastLogout(application);
	}

	/**
	 * updates session information from the missionhub server
	 */
	public synchronized void update() {
		if (updating) {
			return;
		}

		updating = true;

		SessionBroadcast.broadcastUpdateStart(application);

		MetaApi.get(application, new ApiHandler(GMetaMeta.class) {

			@Override
			public void onSuccess(final Object gsonObject) {
				super.onSuccess(gsonObject);

				// if user logged out before verify finished
				if (getAccessToken().equalsIgnoreCase("") || getAccessToken() == null) {
					return;
				}

				final GMetaMeta meta = (GMetaMeta) gsonObject;

				final GenericSEReceiver receiver = new GenericSEReceiver(application, Type.MetaJsonSql) {
					@Override
					public void onUpdateSuccess() {
						if (meta != null && meta.getPerson() != null) {
							setPersonId(meta.getPerson().getId());
						}
						updating = false;
						SessionBroadcast.broadcastUpdateSuccess(application);
						unregister();
					}

					@Override
					public void onUpdateError(final Throwable throwable) {
						onError(throwable);
						unregister();
					}
				};
				final List<String> cats = new ArrayList<String>();
				cats.add(this.toString());
				receiver.register(cats, GenericSEBroadcast.NOTIFY_GENERIC_SUCCESS, GenericSEBroadcast.NOTIFY_GENERIC_ERROR);

				MetaJsonSql.update(application, meta, this.toString());
			}

			@Override
			public void onError(final Throwable t) {
				super.onError(t);
				updating = false;
				SessionBroadcast.broadcastUpdateError(application, t);
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
		Preferences.setAccessToken(application, accessToken);
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
		Preferences.setUserID(application, personId);
		this.personId = personId;
	}

	/**
	 * Gets the session's organization id
	 * 
	 * @return
	 */
	public synchronized long getOrganizationId() {
		if (organizationId < 0 && getUser() != null) {
			setOrganizationId(getUser().getPrimaryOrganization());
		}
		return organizationId;
	}

	/**
	 * Sets the session's organization id
	 * 
	 * @param organizationId
	 */
	public synchronized void setOrganizationId(final long organizationId) {
		Preferences.setOrganizationID(application, organizationId);

		this.organizationId = organizationId;
		Toast.makeText(application, "Org Changed", Toast.LENGTH_LONG).show();
	}

	/**
	 * Gets the session's current user
	 * 
	 * @return
	 */
	public synchronized User getUser() {
		return user;
	}

	/**
	 * Sets the session's current user
	 * 
	 * @param user
	 */
	public synchronized void setUser(final User user) {
		this.user = user;
	}

	/**
	 * Returns true when the session is updating
	 * 
	 * @return true if the session is updating
	 */
	public synchronized boolean isUpdating() {
		return updating;
	}
}