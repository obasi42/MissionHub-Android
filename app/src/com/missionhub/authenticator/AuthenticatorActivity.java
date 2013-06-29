package com.missionhub.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Window;
import com.facebook.widget.LoginButton;
import com.missionhub.R;
import com.missionhub.activity.BaseActivity;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.Session;
import com.missionhub.application.SettingsManager;
import com.missionhub.event.SessionEvent;
import com.missionhub.model.Person;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

/**
 * Activity that manages the login process
 */
public class AuthenticatorActivity extends BaseActivity {

    /**
     * the logging tag
     */
    public static final String TAG = AuthenticatorActivity.class.getSimpleName();

    /**
     * The id of the person requesting authentication
     */
    private long mRequestPersonId = -1;

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

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_authenticator);

        // set up the authenticator response when used as an authenticator
        mRequestPersonId = getIntent().getLongExtra(Authenticator.KEY_PERSON_ID, -1);
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
        Application.registerEventSubscriber(this, SessionEvent.class);

        // open a missionhub session
        Session.getInstance().open();
    }


    /**
     * Called by session open
     *
     * @param event
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(final SessionEvent event) {

    }

    @Override
    public void onDestroy() {
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
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
        com.facebook.Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    public void onClickLogin() {

    }

    /**
     * shows the progress indicator
     */
    private void showProgress(final String action) {
        mProgress.setVisibility(View.VISIBLE);
        if (StringUtils.isNotEmpty(action)) {
            mProgressText.setText(action);
            mProgressText.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.INVISIBLE);
            mProgressText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * hides the progress indicator
     */
    private void hideProgress() {
        AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        mProgress.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void finish() {
        // TODO: finish this

//        final Intent intent = new Intent();
//        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//        intent.putExtra(Authenticator.KEY_PERSON_ID, personId);
//        setResult(result, intent);
//        finish();

//
//        if (mAccountAuthenticatorResponse != null) {
//            // send the result bundle back if set, otherwise send an error.
//            if (mResultBundle != null) {
//                mAccountAuthenticatorResponse.onResult(mResultBundle);
//            } else {
//                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
//            }
//            mAccountAuthenticatorResponse = null;
//        }
        super.finish();
    }


//    public static Account addAccountForPerson(Person person, String accessToken, AuthenticatorActivity activity) {
//        final AccountManager accountManager = AccountManager.get(Application.getContext());
//        final String accountId = String.valueOf(person.getName());
//
//        // check for duplicate account
//        final Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
//        for (final Account account : accounts) {
//            final long personId = Long.parseLong(accountManager.getUserData(account, Authenticator.KEY_PERSON_ID));
//            if (personId == person.getId()) {
//                // account already exists
//                if (activity != null) {
//                    Toast.makeText(activity, String.format(activity.getString(R.string.auth_duplicate_account), person.getName()), Toast.LENGTH_LONG).show();
//                    activity.finishActivity(RESULT_DUPLICATE, account, personId);
//                }
//                return account;
//            }
//        }
//
//        // if there are no other accounts, set this as the last used so it is resumed on next launch
//        // if there are are other accounts, clear the session id to allow the user to pick an account on next
//        // launch
//        if (accounts.length == 0) {
//            SettingsManager.setSessionLastUserId(person.getId());
//        } else {
//            SettingsManager.setSessionLastUserId(-1);
//        }
//
//        // add the new account, for now we will use the access token as the password
//        final Account account = new Account(accountId, Authenticator.ACCOUNT_TYPE);
//        final Bundle userdata = new Bundle();
//        userdata.putString(Authenticator.KEY_PERSON_ID, String.valueOf(person.getId()));
//        accountManager.addAccountExplicitly(account, accessToken, userdata);
//        accountManager.setPassword(account, accessToken);
//
//        return account;
//    }
}