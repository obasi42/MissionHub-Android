package com.missionhub.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;

import com.WazaBe.HoloEverywhere.widget.Toast;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.exception.MissionHubException;
import com.missionhub.model.Organization;
import com.missionhub.model.OrganizationalRole;
import com.missionhub.model.OrganizationalRoleDao;
import com.missionhub.model.Person;
import com.missionhub.util.TreeDataStructure;

import de.greenrobot.dao.QueryBuilder;

public class Session implements OnAccountsUpdateListener {

	/** the logging tag */
	public static final String TAG = Session.class.getSimpleName();

	/** the singleton instance of the session */
	private static Session sSession;

	/** the person id */
	private long mPersonId = -1;

	/** the oragnization id */
	private long mOrganizationId = -1;

	/** the user's primary organization id */
	private long mPrimaryOrganizationId = -1;

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

	/** system labels. */
	public static final String LABEL_ADMIN = "admin";
	public static final String LABEL_CONTACT = "contact";
	public static final String LABEL_INVOLVED = "involved";
	public static final String LABEL_LEADER = "leader";
	public static final String LABEL_ALUMNI = "alumni";

	/** the user's labels */
	private SetMultimap<Long, String> mLabels; // organizationId, label

	/** the cached organization hierarchy */
	private TreeDataStructure<Long> mOrganizationHierarchy;

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
	public synchronized static Session getInstance() {
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
		if (mOrganizationId < 0) return getPrimaryOrganizationId();

		return mOrganizationId;
	}

	/**
	 * Returns the user's primary organization
	 * 
	 * @return
	 */
	public synchronized long getPrimaryOrganizationId() {
		return mPrimaryOrganizationId;
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

					updateLabels();

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
					getOrganizationHierarchy();
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
		Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_resuming)));

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
					// update the person
					Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_updating_person)));

					final Person p = Api.getPersonMe().get();

					// update the account information
					mAccountManager.setUserData(mAccount, Authenticator.KEY_PERSON_ID, String.valueOf(p.getId()));
					mAccountManager.setUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME, p.getName());

					// update the organizations
					Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_updating_orgs)));
					// TODO: uncomment Api.getOrganizations(null).get();

					updateLabels();
					getOrganizationHierarchy();

					Application.postEvent(new SessionResumeSuccessEvent());
				} catch (final Exception e) {
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
		mPrimaryOrganizationId = -1;
		mLabels = null;
		mOrganizationHierarchy = null;

		Application.postEvent(new SessionInvalidatedEvent());
	}

	/**
	 * @return person object associated with the session
	 * @throws NoPersonException
	 */
	public Person getPerson() throws NoPersonException {
		final Person p = Application.getDb().getPersonDao().load(getPersonId());
		if (p == null) {
			throw new NoPersonException(Application.getContext().getString(R.string.no_person_exception));
		}
		return p;
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

	/**
	 * Updates the labels multimap from sql
	 * 
	 * @throws NoPersonException
	 */
	public synchronized void updateLabels() throws NoPersonException {
		final SetMultimap<Long, String> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Long, String> create());
		getPerson().resetOrganizationalRoleList();
		final List<OrganizationalRole> roles = getPerson().getOrganizationalRoleList();
		for (final OrganizationalRole role : roles) {
			role.refresh();
			labelsTemp.put(role.getOrganization_id(), role.getRole());

			if (role.getPrimary() || mPrimaryOrganizationId < 0) {
				mPrimaryOrganizationId = role.getOrganization_id();
			}
		}
		mLabels = labelsTemp;

		if (!isAdminOrLeader(getOrganizationId())) {
			setOrganizationId(mPrimaryOrganizationId);
			Toast.makeText(Application.getContext(), Application.getContext().getString(R.string.session_no_longer_admin), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Returns a tree of the user's organizations
	 * 
	 * @return
	 */
	public synchronized TreeDataStructure<Long> getOrganizationHierarchy() {
		if (mOrganizationHierarchy != null) {
			return mOrganizationHierarchy;
		}

		final QueryBuilder<OrganizationalRole> builder = Application.getDb().getOrganizationalRoleDao().queryBuilder();
		final List<String> adminRoles = new ArrayList<String>();
		adminRoles.add(LABEL_ADMIN);
		adminRoles.add(LABEL_LEADER);
		builder.where(OrganizationalRoleDao.Properties.Person_id.eq(getPersonId()), OrganizationalRoleDao.Properties.Role.in(adminRoles));
		final List<OrganizationalRole> roles = builder.where(OrganizationalRoleDao.Properties.Person_id.eq(getPersonId()), OrganizationalRoleDao.Properties.Role.in(adminRoles)).list();

		// build a tree from organization ancestry
		final TreeDataStructure<Long> tree = new TreeDataStructure<Long>(0l);

		final Iterator<OrganizationalRole> roleItr = roles.iterator();
		while (roleItr.hasNext()) {
			final OrganizationalRole role = roleItr.next();
			role.refresh();
			final Organization org = role.getOrganization();
			org.refresh();
			if (role.getOrganization().getAncestry() != null) {
				TreeDataStructure<Long> parent = tree;
				for (final String ancestor : role.getOrganization().getAncestry().trim().split("/")) {
					final Long a = Long.parseLong(ancestor);
					if (parent.getTree(a) == null) {
						parent = parent.addLeaf(a);
					} else {
						parent = parent.getTree(a);
					}
				}
				if (parent.getTree(org.getId()) == null) {
					parent.addLeaf(org.getId());
				}
			} else {
				tree.addLeaf(org.getId());
			}
		}

		mOrganizationHierarchy = tree;

		return tree;
	}

	/**
	 * Checks if a user has one of the given labels (role)
	 * 
	 * @param label
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(final String... label) {
		return hasLabel(getOrganizationId(), label);
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
			if (mLabels.containsEntry(organizationId, l)) {
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
		return hasLabels(getOrganizationId(), label);
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
			if (!mLabels.containsEntry(organizationId, l)) {
				has = false;
			}
		}
		return has;
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
	 * Returns true if the user is an admin or leader in the given organizationId
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
		return isAdmin(getOrganizationId());
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
		return isLeader(getOrganizationId());
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

	/**
	 * Sets the user's organization id
	 * 
	 * @param organizationId
	 */
	public synchronized void setOrganizationId(final long organizationId) {
		if (isAdminOrLeader(organizationId)) {
			mOrganizationId = organizationId;
			SettingsManager.setSessionOrganizationId(getPersonId(), mOrganizationId);
			Application.postEvent(new SessionOrganizationIdChanged(organizationId));
		} else {
			Toast.makeText(Application.getContext(), Application.getContext().getString(R.string.session_not_admin), Toast.LENGTH_LONG).show();
		}
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

	/* events posted from setOrganizationId() */
	public static class SessionOrganizationIdChanged extends SessionEvent {
		public long organizationId;

		public SessionOrganizationIdChanged(final long organizationId) {
			this.organizationId = organizationId;
		}
	}

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
			super(Application.getContext().getString(R.string.no_account_exception));
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