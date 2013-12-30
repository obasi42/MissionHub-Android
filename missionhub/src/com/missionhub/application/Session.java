package com.missionhub.application;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.model.GraphUser;
import com.missionhub.BuildConfig;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiException;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.ApiRequest;
import com.missionhub.api.InvalidFacebookTokenException;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.authenticator.AuthenticatorActivity;
import com.missionhub.event.FacebookEvent;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.event.SessionEvent;
import com.missionhub.model.Organization;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.model.UserDao;
import com.missionhub.util.ErrbitReportSender;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TaskUtils;

import org.acra.ACRA;
import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.FutureTask;

public class Session implements OnAccountsUpdateListener {

    /**
     * the logging tag
     */
    public static final String TAG = Session.class.getSimpleName();
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
    private SafeAsyncTask<Person> mUpdatePersonTask;
    /**
     * task used to update current organization
     */
    private SafeAsyncTask<Void> mUpdateOrganizationTask;
    /**
     * task used to update permissions
     */
    private SafeAsyncTask<Void> mUpdatePermissionsTask;

    private StatusCallback mFacebookStatusCallback;
    private SafeAsyncTask<Void> mOpenTask;
    private SafeAsyncTask<Void> mCloseTask;
    private SessionState mState = SessionState.CLOSED;

    /**
     * Creates a new session object and sets up the account manager
     */
    protected Session() {
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

    public Account[] getAllAccounts() {
        return getAccountManager().getAccountsByType(Authenticator.ACCOUNT_TYPE);
    }

    public Account getAccount() {
        if (mAccount != null) {
            return mAccount;
        }
        return findAccount(mPersonId, 0);
    }

    public void open() {
        long lastPersonId = SettingsManager.getSessionLastPersonId();
        if (lastPersonId > 0) {
            open(findAccount(lastPersonId, 0));
        } else {
            open(null);
        }
    }

    public long getAccountPersonId(Account account) {
        if (account != null) {
            String id = getAccountManager().getUserData(account, Authenticator.KEY_PERSON_ID);
            if (id != null) {
                return Long.parseLong(id);
            }
        }
        return -1;
    }

    public long getAccountFacebookId(Account account) {
        if (account != null) {
            String id = getAccountManager().getUserData(account, Authenticator.KEY_FACEBOOK_ID);
            if (id != null) {
                return Long.parseLong(id);
            }
        }
        return -1;
    }

    public void open(final Account account) {
        if (mOpenTask != null) return;

        mOpenTask = new SafeAsyncTask<Void>() {
            public FutureTask<Void> organziationTask;
            public FutureTask<Person> personTask;
            public FutureTask<Void> permissionTask;

            @Override
            public Void call() throws Exception {
                setAndPostState(SessionState.OPENING);

                // allow logging in with a hardcoded token
                if (Application.isDebug() && StringUtils.isNotEmpty(Configuration.getLoginAs())) {
                    try {
                        Person person = Api.getPersonMe(Configuration.getLoginAs(), ApiOptions.builder().include(Include.user).build()).get();
                        addSystemAccount(person, Configuration.getLoginAs());
                    } catch (ApiException exception) {
                        setAndPostException(exception);
                        return null;
                    }
                }

                // if we have a facebook id but no person id, grab the person's data
                long personId = getAccountPersonId(account);
                long facebookId = getAccountFacebookId(account);
                if (facebookId > 0 && personId <= 0) {
                    final String authToken = getAccountManager().peekAuthToken(account, Authenticator.ACCOUNT_TYPE);
                    try {
                        Person person = Api.getPersonMe(authToken, ApiOptions.builder().include(Include.user).build()).get();
                        addSystemAccount(person, authToken);
                        personId = person.getId();
                    } catch (InvalidFacebookTokenException exception) {
                        personId = -1;
                    }
                }

                // setup data for the account
                if (account != null) {
                    mAccount = account;
                    mPersonId = personId;
                    mOrganizationId = SettingsManager.getSessionOrganizationId(personId);
                    if (mOrganizationId <= 0 && personId > 0) {
                        Person person = Application.getDb().getPersonDao().load(personId);
                        if (person != null) {
                            mOrganizationId = person.getPrimaryOrganizationId();
                        }
                    }
                } else {
                    mAccount = null;
                    mPersonId = -1;
                    mOrganizationId = -1;
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
                        ErrbitReportSender.putErrbitData(ErrbitReportSender.ErrbitReportField.USER_ID, String.valueOf(mPersonId));
                        ACRA.getErrorReporter().putCustomData("MISSIONHUB_ORGANIZATION_ID", String.valueOf(mOrganizationId));
                    } catch (Exception e) { /* ignore */ }
                }

                Application.trackNewSession();

                SettingsManager.setSessionLastPersonId(mPersonId);

                // update the permissions
                Application.postEvent(new SessionEvent(mState, Application.getContext().getString(R.string.session_updating_permissions)));
                permissionTask = updatePermissions(false);
                permissionTask.get();

                // update the person
                Application.postEvent(new SessionEvent(mState, Application.getContext().getString(R.string.session_updating_person)));
                personTask = updatePerson(false);
                personTask.get();

                // update the current organization
                Application.postEvent(new SessionEvent(mState, Application.getContext().getString(R.string.session_updating_current_organization)));
                organziationTask = updateCurrentOrganization(false);
                organziationTask.get();

                setAndPostState(SessionState.OPEN);
                return null;
            }

            @Override
            public void onException(Exception e) {
                setAndPostException(e);
            }

            @Override
            public void onFinally() {
                TaskUtils.cancel(permissionTask, personTask, organziationTask);
                mOpenTask = null;
            }
        };
        mOpenTask.execute();
    }

    /**
     * Closes the session
     */
    public void close() {
        if (mCloseTask != null) return;

        mCloseTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                SettingsManager.setSessionLastPersonId(-1);
                mPersonId = -1;
                mOrganizationId = -1;
                mAccount = null;
                TaskUtils.cancel(mOpenTask);
                TaskUtils.cancel(mUpdatePersonTask);
                TaskUtils.cancel(mUpdatePermissionsTask);
                TaskUtils.cancel(mUpdateOrganizationTask);

                if (Configuration.isACRAEnabled()) {
                    try {
                        ErrbitReportSender.removeErrbitData(ErrbitReportSender.ErrbitReportField.USER_ID);
                        ACRA.getErrorReporter().removeCustomData("MISSIONHUB_ORGANIZATION_ID");
                    } catch (Exception e) { /* ignore */ }
                }

                Application.trackEvent("session", "logout", "logout");
                Application.trackNewSession();

                // clear database cache
                Application.getDb().clear();

                setAndPostState(SessionState.CLOSED);
                return null;
            }

            @Override
            public void onException(Exception e) {
                setAndPostException(e);
            }

            @Override
            public void onInterrupted(Exception e) {

            }

            @Override
            public void onFinally() {
                mCloseTask = null;
            }
        };
        mCloseTask.execute();
    }

    public Account addSystemAccount(Person person, String facebookToken) {
        Account account = findAccount(person.getId(), person.getFb_uid());
        if (account != null) {
            getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);
        } else {
            account = new Account(person.getName(), Authenticator.ACCOUNT_TYPE);
            final Bundle userdata = new Bundle();
            userdata.putString(Authenticator.KEY_PERSON_ID, String.valueOf(person.getId()));
            userdata.putString(Authenticator.KEY_FACEBOOK_ID, String.valueOf(person.getFb_uid()));
            getAccountManager().addAccountExplicitly(account, facebookToken, userdata);
            getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);
        }
        return account;
    }

    public Account addSystemAccount(GraphUser user, String facebookToken) {
        Account account = findAccount(0, Long.parseLong(user.getId()));
        if (account != null) {
            getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);
        } else {
            account = new Account(user.getName(), Authenticator.ACCOUNT_TYPE);
            final Bundle userdata = new Bundle();
            userdata.putString(Authenticator.KEY_FACEBOOK_ID, user.getId());
            getAccountManager().addAccountExplicitly(account, facebookToken, userdata);
            getAccountManager().setAuthToken(account, Authenticator.ACCOUNT_TYPE, facebookToken);
        }
        return account;
    }

    private void setAndPostState(SessionState state) {
        mState = state;
        Application.postEvent(new SessionEvent(mState));
    }

    private void setAndPostException(Exception exception) {
        mState = SessionState.CLOSED_ERROR;
        Application.postEvent(new SessionEvent(exception));
    }

    /**
     * Returns the personid for this session
     *
     * @return
     */
    public Long getPersonId() {
        return mPersonId;
    }

    /**
     * Returns the organization the person is currently working under.
     *
     * @return
     */
    public long getOrganizationId() {
        if (mOrganizationId <= 0) {
            return getPrimaryOrganizationId();
        }
        return mOrganizationId;
    }

    public Organization getOrganization() {
        return Application.getDb().getOrganizationDao().load(getOrganizationId());
    }


    /**
     * Sets the user's organization id
     *
     * @param organizationId
     */
    public void setOrganizationId(final long organizationId, boolean updateOrganization, boolean async) throws Exception {
        if (organizationId != getOrganizationId()) {
            if (getPerson().isAdminOrUser(organizationId)) {
                mOrganizationId = organizationId;
            } else {
                Application.showToast(R.string.session_no_longer_admin_or_user, Toast.LENGTH_LONG);
                mOrganizationId = getPrimaryOrganizationId();
            }
            if (updateOrganization) {
                FutureTask<Void> future = updateCurrentOrganization(true);
                if (!async) {
                    future.get();
                }
            }
            SettingsManager.setSessionOrganizationId(getPersonId(), mOrganizationId);
            Application.postEvent(new OnOrganizationChangedEvent(mOrganizationId));
        }
    }

    /**
     * Returns the user's primary organization
     *
     * @return
     */
    public long getPrimaryOrganizationId() {
        try {
            return Application.getDb().getUserDao().queryBuilder().where(UserDao.Properties.Person_id.eq(mPersonId)).unique().getPrimary_organization_id();
        } catch (final Exception e) {
            Log.e(TAG, e.getMessage(), e);
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
    public String getAccessToken() throws AuthenticatorException, OperationCanceledException, IOException {
        return getAccountManager().blockingGetAuthToken(mAccount, Authenticator.ACCOUNT_TYPE, true);
    }

    /**
     * Updates the person from the MissionHub Server Posts SessionUpdatePersonEvent
     */
    public FutureTask<Person> updatePerson(final boolean force) {
        if (!force && mUpdatePersonTask != null) return mUpdatePersonTask.future();

        TaskUtils.cancel(mUpdatePersonTask);

        mUpdatePersonTask = new SafeAsyncTask<Person>() {
            private final long mOneDayMillis = 60 * 60 * 24 * 1000;
            public ApiRequest<Person> mApiRequest;

            @Override
            public Person call() throws Exception {
                final long lastUpdated = SettingsManager.getInstance().getUserSetting(getPersonId(), "person_" + getPersonId() + "_updated", 0l);
                final long currentTime = System.currentTimeMillis() - 1000;

                if (!Configuration.isSkipSessionUpdates() && (lastUpdated < System.currentTimeMillis() - mOneDayMillis || force)) {
                    mApiRequest = Api.getPersonMe(ApiOptions.builder() //
                            .include(Include.all_organization_and_children) //
                            .include(Include.all_organizational_permissions) //
                            .include(Include.answer_sheets) //
                            .include(Include.answers) //
                            .include(Include.contact_assignments) //
                            .include(Include.email_addresses) //
                            .include(Include.phone_numbers) //
                            .include(Include.current_address) //
                            .include(Include.interactions) //
                            .include(Include.user) //
                            .include(Include.organizational_labels)
                            .build());
                    mApiRequest.get();

                    SettingsManager.getInstance().setUserSetting(getPersonId(), "person_" + getPersonId() + "_updated", currentTime);
                }

                // update the account with new data to keep it fresh
                getAccountManager().setUserData(mAccount, Authenticator.KEY_PERSON_ID, String.valueOf(getPerson().getId()));
                getAccountManager().setUserData(mAccount, Authenticator.KEY_FACEBOOK_ID, String.valueOf(getPerson().getFb_uid()));
                getAccountManager().setUserData(mAccount, AccountManager.KEY_ACCOUNT_NAME, getPerson().getName());

                return getPerson();
            }

            @Override
            public void onSuccess(final Person person) {
            }

            @Override
            public void onFinally() {
                if (mApiRequest != null) {
                    mApiRequest.disconnect();
                }
                mUpdatePersonTask = null;
            }

            @Override
            public void onException(final Exception e) {
            }
        };
        Application.getExecutor().execute(mUpdatePersonTask.future());
        return mUpdatePersonTask.future();
    }

    public FutureTask<Void> updateCurrentOrganization(final boolean force) {
        return updateOrganization(getOrganizationId(), force);
    }

    public FutureTask<Void> updateOrganization(final long organizationId, final boolean force) {
        TaskUtils.cancel(mUpdateOrganizationTask);

        mUpdateOrganizationTask = new SafeAsyncTask<Void>() {

            private final long mOneWeekMillis = 60 * 60 * 24 * 7 * 1000;
            public ApiRequest<Organization> mApiRequest;

            @Override
            public Void call() throws Exception {
                final long lastUpdated = SettingsManager.getInstance().getUserSetting(getPersonId(), "organization_" + organizationId + "_updated", 0l);
                final long currentTime = System.currentTimeMillis() - 1000;

                if (!Configuration.isSkipSessionUpdates() && (lastUpdated < System.currentTimeMillis() - mOneWeekMillis || force)) {

                    mApiRequest = Api.getOrganization(organizationId, ApiOptions.builder()
                            .include(Include.all_questions)
                            .include(Include.keywords)
                            .include(Include.users)
                            .include(Include.labels)
                            .include(Include.surveys)
                            .include(Include.organizational_permission)
                            .include(Include.interaction_types)
                            .build());

                    mApiRequest.get();

                    getPerson().refreshAll();
                    getPerson().getOrganizationHierarchy();

                    SettingsManager.getInstance().setUserSetting(getPersonId(), "organization_" + organizationId + "_updated", currentTime);
                }

                return null;
            }

            @Override
            public void onFinally() {
                if (mApiRequest != null) {
                    mApiRequest.disconnect();
                }
                mUpdateOrganizationTask = null;
            }

            @Override
            public void onException(final Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        };

        Application.getExecutor().execute(mUpdateOrganizationTask.future());
        return mUpdateOrganizationTask.future();
    }

    private FutureTask<Void> updatePermissions(final boolean force) {
        TaskUtils.cancel(mUpdatePermissionsTask);

        mUpdatePermissionsTask = new SafeAsyncTask<Void>() {

            private final long mOneWeekMillis = 60 * 60 * 24 * 7 * 1000;
            public ApiRequest<List<Permission>> mApiRequest;

            @Override
            public Void call() throws Exception {
                final long lastUpdated = SettingsManager.getInstance().getSetting("permissions_updated", 0l);
                final long currentTime = System.currentTimeMillis() - 1000;

                if (!Configuration.isSkipSessionUpdates() && (lastUpdated < System.currentTimeMillis() - mOneWeekMillis || force)) {
                    mApiRequest = Api.listPermissions();
                    mApiRequest.get();

                    SettingsManager.getInstance().setSetting("permissions_updated", currentTime);
                }

                return null;
            }

            @Override
            public void onSuccess(final Void _) {
            }

            @Override
            public void onFinally() {
                if (mApiRequest != null) {
                    mApiRequest.disconnect();
                }
                mUpdatePermissionsTask = null;
            }

            @Override
            public void onException(final Exception e) {
            }

            @Override
            public void onInterrupted(final Exception e) {
            }
        };
        final FutureTask<Void> future = mUpdatePermissionsTask.future();
        Application.getExecutor().submit(future);
        return future;
    }


    /**
     * @return person object associated with the session
     */
    public Person getPerson() {
        return Application.getDb().getPersonDao().load(getPersonId());
    }

    @Override
    public void onAccountsUpdated(final Account[] accounts) {
        if (getPersonId() > 0 && findAccount(getPersonId(), 0) == null) {
            close();
        }
    }

    /**
     * Find the system account by a personId
     *
     * @param personId
     * @param facebookId
     * @return
     */
    private Account findAccount(final long personId, final long facebookId) {
        final Account[] accounts = getAllAccounts();
        for (final Account account : accounts) {
            final String strPersonId = getAccountManager().getUserData(account, Authenticator.KEY_PERSON_ID);
            final String strFacebookId = getAccountManager().getUserData(account, Authenticator.KEY_FACEBOOK_ID);

            if (personId > 0 && strPersonId != null && strPersonId.equalsIgnoreCase(String.valueOf(personId))) {
                return account;
            }
            if (facebookId > 0 && strFacebookId != null && strFacebookId.equalsIgnoreCase(String.valueOf(facebookId))) {
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
                getAccountManager().removeAccount(mAccount, null, null).getResult();
                SettingsManager.setSessionLastPersonId(-1);
                close();
            } catch (final Exception e) {
                /* ignore */
            }
        }
    }

    /**
     * Reports the Session as having a bad access token
     */
    public void invalidateAuthToken() {
        getAccountManager().invalidateAuthToken(Authenticator.ACCOUNT_TYPE, null);
        setAndPostState(SessionState.INVALID_TOKEN);
    }

    /*--------------------------------*\
     * Permission Convenience Methods
	\*--------------------------------*/

    /**
     * Returns true if the current person is an admin in the current organization
     *
     * @return
     */
    public boolean isAdmin() {
        Person p = getPerson();
        return p != null && p.isAdmin();
    }

    /**
     * Returns true if the current person is a user in the current organization
     *
     * @return
     */
    public boolean isUser() {
        Person p = getPerson();
        return p != null && p.isUser();
    }

    /**
     * Return true if the current person is an admin or user in the current organization
     */
    public boolean isAdminOrUser() {
        Person p = getPerson();
        return p != null && p.isAdminOrUser();
    }

    public com.facebook.Session openFacebookSession(AuthenticatorActivity activity, boolean sso) {
        closeFacebookSession();

        com.facebook.Session.Builder builder = new com.facebook.Session.Builder(Application.getContext());
        builder.setApplicationId(Configuration.getFacebookAppId());
        com.facebook.Session session = builder.build();
        com.facebook.Session.setActiveSession(session);

        com.facebook.Session.OpenRequest request = new com.facebook.Session.OpenRequest(activity);
        // suppress sso behavior when an account already exists and the request is not a re-auth
        if (!sso) {
            request.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        }
        request.setCallback(getFacebookStatusCallback());
        session.openForRead(request);
        return session;
    }

    public StatusCallback getFacebookStatusCallback() {
        if (mFacebookStatusCallback == null) {
            mFacebookStatusCallback = new StatusCallback() {
                @Override
                public void call(com.facebook.Session session, com.facebook.SessionState state, Exception exception) {
                    Application.postEvent(new FacebookEvent(session, state, exception));
                }
            };
        }
        return mFacebookStatusCallback;
    }

    public GraphUser blockingGetFacebookGraphUser() {
        try {
            Response response = Request.newMeRequest(com.facebook.Session.getActiveSession(), null).executeAndWait();
            if (response.getError() != null) {
                throw new RuntimeException(response.getError().getErrorMessage());
            }
            return response.getGraphObjectAs(GraphUser.class);
        } catch (Exception e) { /* ignore */ }
        return null;
    }


    public SessionState getState() {
        return mState;
    }

    public void closeFacebookSession() {
        com.facebook.Session session = com.facebook.Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }

    public boolean isOpen() {
        return mState == SessionState.OPEN;
    }
}