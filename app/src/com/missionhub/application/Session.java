package com.missionhub.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session.StatusCallback;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiException;
import com.missionhub.api.ApiOptions;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.authenticator.AuthenticatorActivity;
import com.missionhub.event.SessionEvent;
import com.missionhub.exception.MissionHubException;
import com.missionhub.model.Person;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.U;

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
     * true while resuming the user's session
     */
    private final AtomicBoolean mResuming = new AtomicBoolean();
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
    private AccountManager mAccountManager;
    /**
     * the account being used
     */
    private Account mAccount;
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
    private StatusCallback mFacebookStatusCallback;
    private SafeAsyncTask<Void> mOpenTask;
    private SafeAsyncTask<Void> mCloseTask;
    private SessionState mState = SessionState.CLOSED;

    /**
     * Creates a new session object and sets up the account manager
     */
    private Session() {
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
     * @return the system account manager
     */
    public AccountManager getAccountManager() {
        if (mAccountManager == null) {
            mAccountManager = AccountManager.get(Application.getContext());
            mAccountManager.addOnAccountsUpdatedListener(this, null, false);
        }
        return mAccountManager;
    }

    /**
     * @return the system account of the current person
     */
    public Account getAccount() {
        if (mAccount == null) {
            mAccount = getAccount(getPersonId());
        }
        return mAccount;
    }

    /**
     * @param personId
     * @return the system account of the given person
     */
    public Account getAccount(long personId) {
        final Account[] accounts = getAllAccounts();
        for (final Account account : accounts) {
            if (getAccountManager().getUserData(account, Authenticator.KEY_PERSON_ID).equalsIgnoreCase(String.valueOf(personId))) {
                return account;
            }
        }
        return null;
    }

    public Account[] getAllAccounts() {
        return getAccountManager().getAccountsByType(Authenticator.ACCOUNT_TYPE);
    }

    /**
     * Opens the session
     */
    public synchronized void open() {
        if (mOpenTask != null) return;

        mOpenTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                setAndPostState(SessionState.OPENING);

                // allow logging in with a hardcoded token
                if (Configuration.getEnvironment() == Configuration.Environment.DEVELOPMENT && !U.isNullEmpty(Configuration.getLoginAs())) {
                    try {
                        Person person = Api.getPersonMe(Configuration.getLoginAs()).get();
                        addSystemAccount(person, Configuration.getLoginAs());
                        SettingsManager.setSessionLastUserId(person.getUser_id());
                    } catch (ApiException exception) {
                        Application.showToast(exception.getMessage(), Toast.LENGTH_LONG);
                    }
                }

                // setup data of last logged in person
                long lastPersonId = SettingsManager.getSessionLastUserId();
                if (lastPersonId > 0) {
                    Account account = getAccount(lastPersonId);
                    if (account != null) {
                        mAccount = account;
                        mPersonId = lastPersonId;
                        mOrganizationId = SettingsManager.getSessionOrganizationId(mPersonId);
                    } else {
                        mPersonId = -1;
                    }
                }

                // send choose or no account message if there is no initialized person
                if (mPersonId <= 0) {
                    if (getAllAccounts().length > 0) {
                        setAndPostState(SessionState.CHOOSE_ACCOUNT);
                    } else {
                        setAndPostState(SessionState.NO_ACCOUNT);
                    }
                    return null;
                }

                // setup ACRA data
                if (Configuration.isACRAEnabled()) {
                    try {
                        ACRA.getErrorReporter().putCustomData("mPersonId", String.valueOf(mPersonId));
                        ACRA.getErrorReporter().putCustomData("mOrganizationId", String.valueOf(mOrganizationId));
                    } catch (Exception e) {
                            /* ignore */
                    }
                }

                Application.trackNewSession();

                // update the person
                Application.postEvent(new SessionEvent(mState, Application.getContext().getString(R.string.init_updating_person)));
                try {
                    updatePerson().get();
                } catch (Exception e) {

                }

                // update the current organization
                Application.postEvent(new SessionEvent(mState, Application.getContext().getString(R.string.init_updating_current_organization)));
                updateCurrentOrganization(false);

                setAndPostState(SessionState.OPEN);
                return null;
            }

            @Override
            public void onSuccess(Void _) {

            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onInterrupted(Exception e) {
            }

            @Override
            public void onFinally() {
                mOpenTask = null;
            }
        };
        mOpenTask.execute();
    }

    private Account addSystemAccount(Person person, String facebookToken) {
        final String accountId = String.valueOf(person.getName());

        // check for duplicate account
        final Account[] accounts = getAllAccounts();
        for (final Account account : accounts) {
            final long personId = Long.parseLong(getAccountManager().getUserData(account, Authenticator.KEY_PERSON_ID));
            if (personId == person.getId()) {
                getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);
                return account;
            }
        }

        // add the new account, for now we will use the access token as the password
        final Account account = new Account(accountId, Authenticator.ACCOUNT_TYPE);
        final Bundle userdata = new Bundle();
        userdata.putString(Authenticator.KEY_PERSON_ID, String.valueOf(person.getId()));
        getAccountManager().addAccountExplicitly(account, facebookToken, userdata);
        getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);

        return account;
    }

    private void setAndPostState(SessionState state) {
        mState = state;
        Application.postEvent(new SessionEvent(mState));
    }

    /**
     * Closes the session
     */
    public synchronized void close() {
        if (mCloseTask != null) return;

        mCloseTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }

            @Override
            public void onFinally() {
                mCloseTask = null;
            }
        };
        mCloseTask.execute();
    }

    /**
     * Closes and destroys the session
     */
    public void destroy() {

    }

    /**
     * @return the facebook status callback that controls the MissionHub session
     */
    public StatusCallback getFacebookStatusCallback() {
        if (mFacebookStatusCallback == null) {
            mFacebookStatusCallback = new StatusCallback() {
                @Override
                public void call(com.facebook.Session session, com.facebook.SessionState state, Exception exception) {

                }
            };
        }
        return mFacebookStatusCallback;
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
                //Application.postEvent(new SessionOrganizationIdChanged(organizationId));

                updateCurrentOrganization(true);
            }
        } else {
            Application.showToast(R.string.session_not_admin, Toast.LENGTH_LONG);
        }
    }

    /**
     * Returns the user's primary organization
     *
     * @return
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
                final long lastUpdated = SettingsManager.getInstance().getUserSetting(getPersonId(), "organization_" + organizationId + "_updated", 0l);
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

//    /**
//     * Attempts to resume the previous user's session
//     */
//    public synchronized void resumeSession() {
//        if (mResuming.get()) return;
//        mResuming.set(true);
//
//        // update from the mh server
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_resuming)));
//
//                    if (Configuration.getEnvironment() == Configuration.Environment.DEVELOPMENT && !U.isNullEmpty(Configuration.getLoginAs())) {
//                        final String facebookToken = Configuration.getLoginAs();
//                        Person person = Api.getPersonMe(facebookToken).get();
//                        addSystemAccount(person, facebookToken);
//                        SettingsManager.setSessionLastUserId(person.getId());
//                    }
//
//                    long personId = SettingsManager.getSessionLastUserId();
//                    if (personId >= 0) {
//                        final Account account = findAccount(personId);
//                        if (account != null) {
//                            mAccount = account;
//                            mPersonId = personId;
//                            mOrganizationId = SettingsManager.getSessionOrganizationId(mPersonId);
//                        } else {
//                            mPersonId = -1;
//                        }
//                    }
//
//                    if (Configuration.isACRAEnabled()) {
//                        try {
//                            ACRA.getErrorReporter().putCustomData("mPersonId", String.valueOf(mPersonId));
//                            ACRA.getErrorReporter().putCustomData("mOrganizationId", String.valueOf(mOrganizationId));
//                        } catch (Exception e) {
//                            /* ignore */
//                        }
//                    }
//
//                    if (mPersonId < 0) {
//                        if (canPickAccount()) {
//                            Application.postEvent(new SessionPickAccountEvent());
//                        } else {
//                            Application.postEvent(new SessionResumeErrorEvent(new NoAccountException()));
//                        }
//                        mResuming.set(false);
//                        return;
//                    }
//
//                    Application.trackNewSession();
//
//                    // resume the session
//                    // update the person
//                    Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_updating_person)));
//                    updatePerson().get();
//
//                    // update the current organization
//                    Application.postEvent(new SessionResumeStatusEvent(Application.getContext().getString(R.string.init_updating_current_organization)));
//                    updateCurrentOrganization(false).get();
//
//                    Application.postEvent(new SessionResumeSuccessEvent());
//                } catch (final Exception e) {
//                    Application.postEvent(new SessionResumeErrorEvent(e));
//                }
//
//                mResuming.set(false);
//            }
//        };
//
//        Application.getExecutor().submit(runnable);
//    }

    /**
     * Resets the session data
     */
    private synchronized void resetSession() {
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

        Application.trackEvent("session", "logout", "logout");
        Application.trackNewSession();

        // clear database cache
        Application.getDb().clear();

        //Application.postEvent(new SessionInvalidatedEvent());
    }

    /**
     * @return person object associated with the session
     * @throws NoPersonException
     */
    public Person getPerson() throws NoPersonException {
        return Application.getDb().getPersonDao().load(getPersonId());
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
                /* ignore */
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
        return accounts.length > 0;
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
        //Application.postEvent(new SessionInvalidTokenEvent());
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

	/*--------------------*\
     * Event Types
	\*--------------------*/

//    /**
//     * base type for all events relating to the user session
//     */
//    public static class SessionEvent {
//    }
//
//    /* events posted from resumeSession() */
//    public static class SessionResumeEvent extends SessionEvent {
//    }
//
//    public static class SessionResumeSuccessEvent extends SessionResumeEvent {
//    }
//
//    public static class SessionResumeErrorEvent extends SessionResumeEvent {
//        public Exception exception;
//
//        public SessionResumeErrorEvent(final Exception exception) {
//            this.exception = exception;
//        }
//    }
//
//    public static class SessionResumeStatusEvent extends SessionResumeEvent {
//        public String status;
//
//        public SessionResumeStatusEvent(final String status) {
//            this.status = status;
//        }
//    }
//
//    /* events posted from setOrganizationId() */
//    public static class SessionOrganizationIdChanged extends SessionEvent {
//        public long organizationId;
//
//        public SessionOrganizationIdChanged(final long organizationId) {
//            this.organizationId = organizationId;
//        }
//    }
//
//	/* account picker events */
//
//    public static class SessionPickAccountEvent extends SessionEvent {
//    }
//
//	/* general events */
//
//    public static class SessionInvalidTokenEvent extends SessionEvent {
//    }
//
//    public static class SessionInvalidatedEvent extends SessionEvent {
//    }

	/*--------------------*\
     * Exceptions
	\*--------------------*/

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

    public void startFacebookSession() {

    }

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
}