package com.missionhub.application;

import android.accounts.*;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.exception.MissionHubException;
import com.missionhub.model.Person;
import com.missionhub.util.SafeAsyncTask;
import org.acra.ACRA;
import org.holoeverywhere.widget.Toast;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session implements OnAccountsUpdateListener {

    /**
     * the logging tag
     */
    public static final String TAG = Session.class.getSimpleName();

    /**
     * the singleton instance of the session
     */
    private static Session sSession;

    /**
     * the person object
     */
    private Person mPerson;

    /**
     * the person id
     */
    private long mPersonId = -1;

    /**
     * the organization id
     */
    private long mOrganizationId = -1;

    /**
     * the android account manager
     */
    private final AccountManager mAccountManager;

    /**
     * the account being used
     */
    private Account mAccount;

    /**
     * true while resuming the user's session
     */
    private final AtomicBoolean mResuming = new AtomicBoolean();

    /**
     * task used to update the current person data
     */
    private FutureTask<Person> mUpdatePersonTask;

    /**
     * task used to update organizations
     */
    private FutureTask<Void> mUpdateOrganizationsTask;

    /**
     * task used to update current organization
     */
    private SafeAsyncTask<Void> mUpdateOrganizationTask;

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
        if (mOrganizationId <= 0) {
            return getPrimaryOrganizationId();
        }
        return mOrganizationId;
    }

    /**
     * Returns the user's primary organization
     *
     * @return
     * @throws NoPersonException
     */
    public synchronized long getPrimaryOrganizationId() {
        try {
            return getPerson().getPrimaryOrganizationId();
        } catch (final NoPersonException e) {
            /* ignore */
        }
        return -1l;
    }

    /**
     * Returns the access token for the missionhub account in the system. This is a blocking method and should not be
     * called from the ui thread.
     *
     * @return
     */
    public synchronized String getAccessToken() throws NoAccountException, OperationCanceledException, AuthenticatorException, IOException {
        if (mAccount == null) {
            throw new NoAccountException();
        }
        return mAccountManager.blockingGetAuthToken(mAccount, Authenticator.ACCOUNT_TYPE, true);
    }

    /**
     * Updates the person from the MissionHub Server Posts SessionUpdatePersonEvent
     */
    public FutureTask<Person> updatePerson() {
        if (mUpdatePersonTask != null) return mUpdatePersonTask;

        final Callable<Person> callable = new Callable<Person>() {
            @Override
            public Person call() throws Exception {
                Api.getPersonMe(ApiOptions.builder() //
                        .include(Include.all_organization_and_children) //
                        .include(Api.Include.all_organizational_roles) //
                        .include(Api.Include.answer_sheets) //
                        .include(Api.Include.answers) //
                        .include(Api.Include.contact_assignments) //
                        .include(Api.Include.email_addresses) //
                        .include(Api.Include.phone_numbers) //
                        .include(Api.Include.current_address) //
                        .include(Api.Include.comments_on_me) //
                        .include(Api.Include.user) //
                        .build()).get(); //

                // update the account with new data to keep it fresh
                mAccountManager.setUserData(mAccount, Authenticator.KEY_PERSON_ID, String.valueOf(getPerson().getId()));
                mAccountManager.setUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME, getPerson().getName());

                getPerson().refreshAll();
                updateLabels();

                // update the person's organization hierarchy, as it is too expensive to do from the ui thread.
                getPerson().resetOrganizationHierarchy();
                getPerson().getOrganizationHierarchy();

                mUpdatePersonTask = null;

                return getPerson();
            }
        };

        mUpdatePersonTask = new FutureTask<Person>(callable);
        Application.getExecutor().submit(mUpdatePersonTask);
        return mUpdatePersonTask;
    }

    private FutureTask<Void> updateCurrentOrganization(final boolean force) {
        try {
            mUpdateOrganizationTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }

        mUpdateOrganizationTask = new SafeAsyncTask<Void>() {

            private final long mOneWeekMillis = 60 * 60 * 24 * 7 * 1000;

            @Override
            public Void call() throws Exception {
                final long organizationId = getOrganizationId();
                final long lastUpdated = Long.parseLong(SettingsManager.getInstance().getUserSetting(getPersonId(), "organization_" + organizationId + "_updated", "0"));
                final long currentTime = System.currentTimeMillis() - 1000;

                if (lastUpdated < System.currentTimeMillis() - mOneWeekMillis || force) {

                    Api.getOrganization(organizationId, ApiOptions.builder() //
                            .include(Include.all_questions) //
                            .include(Include.groups) //
                            .include(Include.keywords) //
                            .include(Include.leaders) //
                            .include(Include.organizational_roles) //
                            .include(Include.surveys) //
                            .build()).get();

                    SettingsManager.getInstance().setUserSetting(getPersonId(), "organization_" + organizationId + "_updated", currentTime);
                }

                return null;
            }

            @Override
            public void onSuccess(final Void _) {
            }

            @Override
            public void onFinally() {
                mUpdateOrganizationTask = null;
            }

            @Override
            public void onException(final Exception e) {
            }

            @Override
            public void onInterrupted(final Exception e) {
            }
        };

        final FutureTask<Void> future = mUpdateOrganizationTask.future();
        Application.getExecutor().submit(future);
        return future;
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
            } else {
                mPersonId = -1;
            }
        }

        if (Configuration.isACRAEnabled()) {
            try {
                ACRA.getErrorReporter().putCustomData("mPersonId", String.valueOf(mPersonId));
                ACRA.getErrorReporter().putCustomData("mOrganizationId", String.valueOf(mOrganizationId));
            } catch (Exception e) {
                /* ignore */
            }
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
                    updatePerson().get();

                    // update the current organization
                    Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_updating_current_organization)));
                    updateCurrentOrganization(false).get();

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
        mPerson = null;
        mPersonId = -1;
        mOrganizationId = -1;
        mAccount = null;
        mResuming.set(false);
        try {
            mUpdatePersonTask.cancel(true);
            mUpdatePersonTask = null;
        } catch (final Exception e) {
			/* ignore */
        }
        try {
            mUpdateOrganizationsTask.cancel(true);
            mUpdateOrganizationsTask = null;
        } catch (final Exception e) {
			/* ignore */
        }

        if (Configuration.isACRAEnabled()) {
            try {
                ACRA.getErrorReporter().removeCustomData("mPersonId");
                ACRA.getErrorReporter().removeCustomData("mOrganizationId");
            } catch (Exception e) {
                /* ignore */
            }
        }

        // clear database cache
        Application.getDb().clear();

        Application.postEvent(new SessionInvalidatedEvent());
    }

    /**
     * @return person object associated with the session
     * @throws NoPersonException
     */
    public Person getPerson() throws NoPersonException {
        mPerson = Application.getDb().getPersonDao().load(getPersonId());
        if (mPerson == null) {
            throw new NoPersonException(Application.getContext().getString(R.string.no_person_exception));
        }
        return mPerson;
    }

    @Override
    public void onAccountsUpdated(final Account[] accounts) {
        if (getPersonId() > 0 && findAccount(getPersonId()) == null) {
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
     */
    public void deleteAccount() {
        if (mAccount != null) {
            try {
                mAccountManager.removeAccount(mAccount, null, null).getResult();
                SettingsManager.setSessionLastUserId(-1);
                resetSession();
            } catch (final Exception e) {
            }
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
        getPerson().resetLabels();

        if (!getPerson().isAdminOrLeader(getOrganizationId())) {
            setOrganizationId(getPerson().getPrimaryOrganizationId());
            Application.showToast(R.string.session_no_longer_admin, Toast.LENGTH_LONG);
        }
    }

    /**
     * Sets the user's organization id
     *
     * @param organizationId
     * @throws NoPersonException
     */
    public synchronized void setOrganizationId(final long organizationId) throws NoPersonException {
        if (getPerson().isAdminOrLeader(organizationId)) {
            if (organizationId != getOrganizationId()) {
                mOrganizationId = organizationId;
                SettingsManager.setSessionOrganizationId(getPersonId(), mOrganizationId);
                Application.postEvent(new SessionOrganizationIdChanged(organizationId));

                updateCurrentOrganization(true);
            }
        } else {
            Application.showToast(R.string.session_not_admin, Toast.LENGTH_LONG);
        }
    }

	/*--------------------*\
	 * Event Types
	\*--------------------*/

    /**
     * base type for all events relating to the user session
     */
    public static class SessionEvent {
    }

    /* events posted from resumeSession() */
    public static class SessionResumeEvent extends SessionEvent {
    }

    public static class SessionResumeSuccessEvent extends SessionResumeEvent {
    }

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

    public static class SessionPickAccountEvent extends SessionEvent {
    }

	/* general events */

    public static class SessionInvalidTokenEvent extends SessionEvent {
    }

    public static class SessionInvalidatedEvent extends SessionEvent {
    }

	/*--------------------*\
	 * Exceptions
	\*--------------------*/

    /**
     * Exception to be thrown when no MissionHub account exists in the system accounts
     */
    public static class NoAccountException extends MissionHubException {
        private static final long serialVersionUID = 1L;

        public NoAccountException() {
            super(Application.getContext().getString(R.string.no_account_exception));
        }
    }

    /**
     * Exception to be thrown when no person is referenced by the session
     */
    public static class NoPersonException extends MissionHubException {
        private static final long serialVersionUID = 1L;

        public NoPersonException(final String message) {
            super(message);
        }
    }

    /**
     * Returns true of the current person is an admin in the current organization
     *
     * @return
     */
    public boolean isAdmin() {
        try {
            return getPerson().isAdmin(getOrganizationId());
        } catch (final NoPersonException e) {
            return false;
        }
    }

    /**
     * Returns true of the current person is an admin in the current organization
     *
     * @return
     */
    public boolean isLeader() {
        try {
            return getPerson().isLeader(getOrganizationId());
        } catch (final NoPersonException e) {
            return false;
        }
    }

    /**
     * Return true if the current person is an admin or leader in the current organization
     */
    public boolean isAdminOrLeader() {
        try {
            return getPerson().isAdminOrLeader(getOrganizationId());
        } catch (final NoPersonException e) {
            return false;
        }
    }
}