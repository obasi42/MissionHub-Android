package com.missionhub.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.model.GraphUser;
import com.missionhub.R;
import com.missionhub.activity.BaseActivity;
import com.missionhub.api.ApiException;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.SessionState;
import com.missionhub.application.SettingsManager;
import com.missionhub.event.FacebookEvent;
import com.missionhub.event.SessionEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.ui.widget.PickAccountDialog;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import java.util.logging.Handler;

/**
 * Activity that manages the login process
 */
public class AuthenticatorActivity extends BaseActivity {

    /**
     * the logging tag
     */
    public static final String TAG = AuthenticatorActivity.class.getSimpleName();

    /**
     * The id of the person requesting re-authenticating
     */
    private long mReauthPersonId = -1;

    /**
     * The facebook id of the person re-authenticating
     */
    private long mReauthFacebookId = -1;

    /**
     * The account authenticator response when used as an account authenticator
     */
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;

    /**
     * the progress bar
     */
    private ProgressBar mProgress;

    /**
     * text to display the current progress action
     */
    private TextView mProgressText;

    /**
     * the facebook login button
     */
    private Button mLoginButton;

    /**
     * the missionhub version number
     */
    private TextView mVersion;

    /**
     * the account picker dialog if used
     */
    private PickAccountDialog mAccountDialog;

    /**
     * Holds the authenticator response
     */
    private Intent mResponseIntent;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // set up the authenticator response when used as an authenticator
        mReauthPersonId = getIntent().getLongExtra(Authenticator.KEY_PERSON_ID, -1);
        mReauthFacebookId = getIntent().getLongExtra(Authenticator.KEY_FACEBOOK_ID, -1);
        mAccountAuthenticatorResponse = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        // set up the progress bar and text
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mProgressText = (TextView) findViewById(R.id.progress_text);

        // set up the login button
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin();
            }
        });

        // set the version number
        mVersion = (TextView) findViewById(R.id.version);
        mVersion.setText(Application.getVersionName());

        // receive session events
        Application.registerEventSubscriber(this, SessionEvent.class, FacebookEvent.class);

        Session.getInstance().open();
    }

    /**
     * Callback method that listens for SessionEvents
     *
     * @param event
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(final SessionEvent event) {
        switch (event.getState()) {
            case OPENING:
                if (event.getMessage() != null) {
                    showProgress(event.getMessage());
                } else {
                    showProgress(getString(R.string.init_resuming));
                }
                break;
            case OPEN:
                mResponseIntent = new Intent();
                Account account = Session.getInstance().getAccount();
                final Intent intent = new Intent();
                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                intent.putExtra(Authenticator.KEY_PERSON_ID, Session.getInstance().getAccountPersonId(account));
                intent.putExtra(Authenticator.KEY_FACEBOOK_ID, Session.getInstance().getAccountFacebookId(account));
                finish();
                break;
            case CLOSED_ERROR:
                showLoginButton();
                showException(event.getException());
                break;
            case CHOOSE_ACCOUNT:
                showLoginButton();
                showAccountPicker();
                break;
            case NO_ACCOUNT:
                showLoginButton();
                break;
        }
    }

    /**
     * Callback method that listens for FacebookEvents
     *
     * @param event
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(final FacebookEvent event) {
        switch (event.getState()) {
            case OPENED:
                GraphUser user = Session.getInstance().blockingGetFacebookGraphUser();
                if (user != null) {
                    Session.getInstance().open(Session.getInstance().addSystemAccount(user, event.getSession().getAccessToken()));
                    break;
                } else {
                    showException(new ApiException("Could not login with Facebook"));
                }
                break;
            case CLOSED_LOGIN_FAILED:
                showException(event.getException());
                break;
        }
        findViewById(R.id.test).invalidate();
    }

    @Override
    public void onDestroy() {
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    public void onClickLogin() {
        if (Session.getInstance().getState() == SessionState.NO_ACCOUNT) {
            openFacebookSession();
        } else {
            Session.getInstance().open();
        }
    }

    private void showProgress(final CharSequence action) {
        hideLoginButton();
        mProgress.setVisibility(View.VISIBLE);
        if (StringUtils.isNotEmpty(action)) {
            mProgressText.setText(action);
            mProgressText.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
            mProgressText.setVisibility(View.GONE);
        }
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
        mProgressText.setVisibility(View.GONE);
    }

    private void showLoginButton() {
        hideProgress();
        mLoginButton.setVisibility(View.VISIBLE);
    }

    private void hideLoginButton() {
        mLoginButton.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            if (mResponseIntent != null) {
                setResult(Activity.RESULT_OK, mResponseIntent);
                mAccountAuthenticatorResponse.onResult(mResponseIntent.getExtras());
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
        }
        super.finish();
    }

    /**
     * Passes activity result information to the facebook api client
     *
     * @param requestCode The request code
     * @param resultCode  The activity result
     * @param data        Misc data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        com.facebook.Session session = com.facebook.Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private void showAccountPicker() {
        hideProgress();
        if (mAccountDialog == null) {
            mAccountDialog = new PickAccountDialog();
            mAccountDialog.setCancelable(false);
        }
        mAccountDialog.show(getSupportFragmentManager());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final PickAccountDialog.AccountPickedEvent event) {
        if (event.personId > 0) {
            SettingsManager.setSessionLastPersonId(event.personId);
            Session.getInstance().open();
        } else {
            openFacebookSession();
        }
    }

    private void openFacebookSession() {
        hideLoginButton();
        if (mAccountAuthenticatorResponse == null) {
            Session.getInstance().openFacebookSession(this, true);
        } else {
            if (Session.getInstance().getAllAccounts().length > 0) {
                Session.getInstance().openFacebookSession(this, false);
            } else {
                Session.getInstance().openFacebookSession(this, true);
            }
        }
    }

    private void showException(Exception e) {
        final ExceptionHelper eh = new ExceptionHelper(this, e);
        eh.setPositiveButton(new ExceptionHelper.DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_close);
            }

            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                dialog.dismiss();
            }
        });
        eh.show();
    }
}
