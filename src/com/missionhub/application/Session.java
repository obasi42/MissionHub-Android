package com.missionhub.application;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.content.Context;

import com.missionhub.api.Api;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.exception.MissionHubException;
import com.missionhub.model.Person;
import com.missionhub.network.NetworkUnavailableException;
import com.missionhub.ui.widget.PickAccountDialog;

public class Session implements OnAccountsUpdateListener {

	/** the logging tag */
	public static final String TAG = Session.class.getSimpleName();

	/** the singleton instance of the session */
	private static Session sSession;

	/** the person id */
	private long mPersonId = -1;

	/** the group id */
	private long mOrganizationId = -1;

	/** the android account manager */
	private final AccountManager mAccountManager;

	/** the account being used */
	private Account mAccount;

	/** true if updating the user's organizations */
	private final AtomicBoolean mUpdatingPerson = new AtomicBoolean(false);

	/** true if updating the user's organizations */
	private final AtomicBoolean mUpdatingOrganizations = new AtomicBoolean(false);

	/** true if the session is being refreshed */
	private final AtomicBoolean mResuming = new AtomicBoolean(false);

	/**
	 * Creates a new session object and sets up the account manager
	 */
	private Session() {
		mAccountManager = AccountManager.get(Application.getContext());
		mAccountManager.addOnAccountsUpdatedListener(this, null, false);
	}

	/**
	 * @return the singleton instance of the session
	 */
	public static Session getInstance() {
		if (sSession == null) {
			sSession = new Session();
		}
		return sSession;
	}

	/**
	 * Returns the personid for this session
	 * 
	 * @return
	 */
	public synchronized Long getPersonId() {
		return mPersonId;
	}

	/**
	 * Returns the organization the person is currently working under.
	 * 
	 * @return
	 */
	public synchronized long getOrganizationId() {
		return mOrganizationId;
	}

	/**
	 * Returns the access token for the missionhub account in the system. This is a blocking method and should not be
	 * called from the ui thread.
	 * 
	 * @return
	 * @throws NoAccountException
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 * @throws BadAccessTokenException
	 */
	public synchronized String getAccessToken() throws NoAccountException, OperationCanceledException, AuthenticatorException, IOException {
		if (mAccount == null) {
			throw new NoAccountException();
		}
		return mAccountManager.blockingGetAuthToken(mAccount, Authenticator.ACCOUNT_TYPE, true);
	}

	/**
	 * Updates the person from the MissionHub Server Posts SessionUpdatePersonEvent SSEF events
	 */
	public void updatePerson() {
		if (mUpdatingPerson.get()) return;
		mUpdatingPerson.set(true);

		Application.postEvent(new SessionUpdatePersonStartedEvent());

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					final Person p = Api.getPersonMe().get();

					// update the account with new data to keep it fresh
					mAccountManager.setUserData(mAccount, Authenticator.KEY_PERSON_ID, String.valueOf(p.getId()));
					mAccountManager.setUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME, p.getName());

					Application.postEvent(new SessionUpdatePersonSuccessEvent());
				} catch (final Exception e) {
					Application.postEvent(new SessionUpdatePersonErrorEvent(e));
				}
				mUpdatingPerson.set(false);
				Application.postEvent(new SessionUpdatePersonFinishedEvent());
			}
		};
		Application.getExecutor().submit(runnable);
	}

	/**
	 * Updates the user's organization data from the MissionHub Server Posts SessionUpdatePersonEvent SSEF events
	 */
	public void updateOrganizations() {
		if (mUpdatingOrganizations.get()) return;
		mUpdatingOrganizations.set(true);

		Application.postEvent(new SessionUpdateOrganizationsStartedEvent());

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Api.getOrganizations(null).get();
					Application.postEvent(new SessionUpdateOrganizationsSuccessEvent());
				} catch (final Exception e) {
					Application.postEvent(new SessionUpdateOrganizationsErrorEvent(e));
				}
				mUpdatingOrganizations.set(false);
				Application.postEvent(new SessionUpdateOrganizationsFinishedEvent());
			}
		};
		Application.getExecutor().submit(runnable);
	}

	/**
	 * Attempts to resume the previous user's session
	 */
	public synchronized void resumeSession() {
		Application.postEvent(new SessionResumeStatusEvent("Resuming session..."));

		long personId = SettingsManager.getSessionLastUserId();
		if (personId >= 0) {
			final Account account = findAccount(personId);
			if (account != null) {
				mAccount = account;
				mPersonId = personId;
				mOrganizationId = SettingsManager.getSessionOrganizationId(mPersonId);
			}
			personId = -1;
		}

		if (mPersonId < 0) {
			if (canPickAccount()) {
				Application.postEvent(new SessionPickAccountEvent());
			} else {
				Application.postEvent(new SessionResumeErrorEvent(new NoAccountException()));
			}
			return;
		}

		if (mResuming.get()) return;
		mResuming.set(true);

		// update from the mh server
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// TODO: uncomment

					// update the person
					// Application.postEvent(new SessionResumeStatusEvent("Updating Person..."));

					// final Person p = Api.getPersonMe().get();

					// update the account information
					// mAccountManager.setUserData(mAccount, Authenticator.KEY_PERSON_ID, String.valueOf(p.getId()));
					// mAccountManager.setUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME, p.getName());

					// update the organizations
					// Application.postEvent(new SessionResumeStatusEvent("Updating Organizations..."));
					// Api.getOrganizations(null).get();

					Application.postEvent(new SessionResumeSuccessEvent());
				} catch (final Exception e) {
					if (e instanceof NetworkUnavailableException) {
						Application.postEvent(new SessionResumeOfflineEvent());
						return;
					}
					Application.postEvent(new SessionResumeErrorEvent(e));
				}

				mResuming.set(false);
			}
		};
		Application.getExecutor().submit(runnable);
	}

	/**
	 * Resets the session data
	 */
	private synchronized void resetSession() {
		mPersonId = -1;
		mOrganizationId = -1;
		mAccount = null;

		Application.postEvent(new SessionInvalidatedEvent());
	}

	/**
	 * @return person object associated with the session
	 * @throws NoPersonException
	 */
	public Person getPerson() throws NoPersonException {
		final Person p = Application.getDb().getPersonDao().load(getPersonId());
		if (p == null) {
			throw new NoPersonException("no person in sqlite db");
		}
		return p;
	}

	/**
	 * Shows a dialog for the user to pick a missionhub account or add a new one
	 * 
	 * @param activity
	 *            the activity context to display the dialog in
	 */
	public void pickAccount(final Context context) {
		final PickAccountDialog dialog = new PickAccountDialog(context);
		dialog.show();
	}

	@Override
	public void onAccountsUpdated(final Account[] accounts) {
		if (mPersonId > 0 && findAccount(mPersonId) == null) {
			resetSession();
		}
	}

	/**
	 * Find the system account by a personId
	 * 
	 * @param personId
	 * @return
	 */
	private Account findAccount(final long personId) {
		final Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
		for (final Account account : accounts) {
			if (mAccountManager.getUserData(account, Authenticator.KEY_PERSON_ID).equalsIgnoreCase(String.valueOf(personId))) {
				return account;
			}
		}
		return null;
	}

	/**
	 * Deletes an account for the specified person. This should not be called from the main thread.
	 * 
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public void deleteAccount() {
		if (mAccount != null) {
			try {
				mAccountManager.removeAccount(mAccount, null, null).getResult();
				SettingsManager.setSessionLastUserId(-1);
				resetSession();
			} catch (final Exception e) {}
		}
	}

	/**
	 * Checks if there are multiple missionhub accounts on the system
	 * 
	 * @return
	 */
	public boolean canPickAccount() {
		final Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
		if (accounts.length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Logs the current session user out
	 */
	public void logout() {
		SettingsManager.setSessionLastUserId(-1);
		resetSession();
	}

	/**
	 * Reports the Session as having a bad access token
	 */
	public void reportInvalidAccessToken() {
		deleteAccount();
		Application.postEvent(new SessionInvalidTokenEvent());
	}

	/*--------------------*\
	 * Event Types
	\*--------------------*/

	/** base type for all events relating to the user session */
	public static class SessionEvent {}

	/* events posted from updatePerson() */
	public static class SessionUpdatePersonEvent extends SessionEvent {}

	public static class SessionUpdatePersonStartedEvent extends SessionUpdatePersonEvent {}

	public static class SessionUpdatePersonFinishedEvent extends SessionUpdatePersonEvent {}

	public static class SessionUpdatePersonSuccessEvent extends SessionUpdatePersonEvent {}

	public static class SessionUpdatePersonErrorEvent extends SessionUpdatePersonEvent {
		public Exception exception;

		public SessionUpdatePersonErrorEvent(final Exception e) {
			exception = e;
		}
	}

	/* events posted from updateOrganizations() */
	public static class SessionUpdateOrganizationsEvent extends SessionEvent {}

	public static class SessionUpdateOrganizationsStartedEvent extends SessionUpdateOrganizationsEvent {}

	public static class SessionUpdateOrganizationsFinishedEvent extends SessionUpdateOrganizationsEvent {}

	public static class SessionUpdateOrganizationsSuccessEvent extends SessionUpdateOrganizationsEvent {}

	public static class SessionUpdateOrganizationsErrorEvent extends SessionUpdateOrganizationsEvent {
		public Exception exception;

		public SessionUpdateOrganizationsErrorEvent(final Exception e) {
			exception = e;
		}
	}

	/* events posted from resumeSession() */
	public static class SessionResumeEvent extends SessionEvent {}

	public static class SessionResumeSuccessEvent extends SessionResumeEvent {}

	public static class SessionResumeErrorEvent extends SessionResumeEvent {
		public Exception exception;

		public SessionResumeErrorEvent(final Exception exception) {
			this.exception = exception;
		}
	}

	public static class SessionResumeStatusEvent extends SessionResumeEvent {
		public String status;

		public SessionResumeStatusEvent(final String status) {
			this.status = status;
		}
	}

	public static class SessionResumeOfflineEvent extends SessionResumeEvent {}

	/* account picker events */

	public static class SessionPickAccountEvent extends SessionEvent {}

	/* general events */

	public static class SessionInvalidTokenEvent extends SessionEvent {}

	public static class SessionInvalidatedEvent extends SessionEvent {}

	/*--------------------*\
	 * Exceptions
	\*--------------------*/

	/** Exception to be thrown when no MissionHub account exists in the system accounts */
	public static class NoAccountException extends MissionHubException {
		private static final long serialVersionUID = 1L;

		public NoAccountException() {
			super("No MissionHub Account Found");
		}
	}

	/** Exception to be thrown when no person is referenced by the session */
	public static class NoPersonException extends MissionHubException {
		private static final long serialVersionUID = 1L;

		public NoPersonException(final String message) {
			super(message);
		}
	}

}