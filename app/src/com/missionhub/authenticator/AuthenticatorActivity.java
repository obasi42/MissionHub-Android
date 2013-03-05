package com.missionhub.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.*;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import com.actionbarsherlock.view.Window;
import com.google.gson.Gson;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.SettingsManager;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.exception.WebViewException;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GAccessToken;
import com.missionhub.model.gson.GErrorsDepreciated;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.U;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.concurrent.FutureTask;

/**
 * Activity which displays login screen to the user.
 */
@SuppressLint("SetJavaScriptEnabled")
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    /**
     * the logging tag
     */
    public static final String TAG = AuthenticatorActivity.class.getSimpleName();

    /**
     * the activity result when a duplicate account exists
     */
    public static final int RESULT_DUPLICATE = 1;

    /**
     * the system account manager
     */
    private AccountManager mAccountManager;

    /**
     * the web view used for authentication
     */
    private WebView mWebView;

    /**
     * the progress bar
     */
    private ProgressBar mProgress;

    /**
     * text to display the current progress action
     */
    private TextView mProgressText;

    /**
     * holds the task that fetches the access token and adds the system account
     */
    private FutureTask<GAccessToken> mAuthTask;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_authenticator);

        mAccountManager = AccountManager.get(this);

        mWebView = (WebView) findViewById(R.id.webview);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mProgressText = (TextView) findViewById(R.id.progress_text);

        // web view settings
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccess(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setSupportMultipleWindows(false);
        mWebView.getSettings().setSupportZoom(false);

        // web view display settings
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);

        // the web view client
        mWebView.setWebViewClient(new AuthenticatorWebViewClient());

        // begin authentication
        resetAuthentication();
    }

    /**
     * resets the state of the auth activity
     */
    private void resetAuthentication() {
        // cancel the current auth task
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }

        // clear the web view cookies
        clearCookies();

        // show the webview and go to the initial auth page
        mWebView.setVisibility(View.VISIBLE);
        try {
            mWebView.loadUrl(getAuthenticationUrl());
        } catch (final Exception e) {
            displayError(e);
        }
    }

    /**
     * web view client to manage the authentication process
     */
    private class AuthenticatorWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            return false; // make sure this web view handles all other urls
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);

            // parse the url to a uri for easier checking
            final Uri uri = Uri.parse(url);

            // parse the oauth url to a uri for comparisons to the uri
            final Uri oauthUri = Uri.parse(Configuration.getOauthUrl());

            // make sure we are only working with requests from the oauth server
            if (uri.getHost() != null && uri.getHost().equalsIgnoreCase(oauthUri.getHost())) {

                // prevent the mission hub server from being dumb
                if (!uri.getPath().contains("/users") && !uri.getPath().contains(oauthUri.getPath())) {
                    onError(new Exception(getString(R.string.unexpected_data)));
                    return;
                }

                // check for an api error
                if (!U.isNullEmpty(uri.getQueryParameter("error"))) {
                    try {
                        final Gson gson = new Gson();
                        final GErrorsDepreciated error = gson.fromJson(URLDecoder.decode(uri.getQueryParameter("error_description"), "UTF-8"), GErrorsDepreciated.class);
                        onError(error.getException());
                    } catch (final Exception e) {
                        onError(new Exception(uri.getQueryParameter("error")));
                    }
                    return;
                }

                // Check for the authorization parameter. If it exists, automatically grant app access.
                final String authorization = uri.getQueryParameter("authorization");
                if (!U.isNullEmpty(authorization) && uri.getPath().contains("/authorize")) {
                    // check for a mh cookie to ensure the user is logged in
                    CookieSyncManager.createInstance(AuthenticatorActivity.this);
                    final CookieManager mgr = CookieManager.getInstance();
                    final String cookieString = mgr.getCookie(oauthUri.getHost());
                    if (cookieString != null && (cookieString.contains("_mh_session="))) {
                        mWebView.stopLoading();
                        mWebView.loadUrl(Configuration.getOauthUrl() + "/grant.json?authorization=" + authorization);
                        return;
                    }
                }

                // Check for the authentication code. If exists, use it to obtain the user's access token
                final String code = uri.getQueryParameter("code");
                if (!U.isNullEmpty(code) && uri.getPath().contains("/done")) {
                    mWebView.stopLoading();
                    mWebView.loadData("", "text/plain; charset=UTF-8", null);
                    mWebView.setVisibility(View.GONE);

                    // fetch the access token and add an account
                    addAccountFromCode(code);
                }
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        }

        @Override
        public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
            onError(new WebViewException(errorCode, description, failingUrl));
        }

        public void onError(final Exception e) {
            mWebView.stopLoading();
            mWebView.loadData("", "text/plain; charset=UTF-8", null);
            mWebView.setVisibility(View.GONE);

            displayError(e);
        }
    }

    /**
     * Builds and returns the url used for authentication
     *
     * @return the url used for authentication
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    private String getAuthenticationUrl() throws URISyntaxException, MalformedURLException {
        final URIBuilder builder = new URIBuilder(Configuration.getOauthUrl() + "/authorize");
        builder.addParameter("android", "true");
        builder.addParameter("display", "touch");
        builder.addParameter("simple", "true");
        builder.addParameter("response_type", "code");
        builder.addParameter("redirect_uri", Configuration.getOauthUrl() + "/done.json");
        builder.addParameter("client_id", Configuration.getOauthClientId());
        builder.addParameter("scope", Configuration.getOauthScope());

        return builder.build().toURL().toString();
    }

    /**
     * shows the progress indicator with text
     */
    private void showProgress(final String action) {
        AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
        mProgress.setVisibility(View.VISIBLE);

        if (!U.isNullEmpty(action)) {
            mProgressText.setText(action);
            mProgressText.setVisibility(View.VISIBLE);
        } else {
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

    /**
     * Fetches an access token and adds a system account from the oauth code
     *
     * @param code
     */
    private void addAccountFromCode(final String code) {
        if (mAuthTask != null) return;

        showProgress(getString(R.string.auth_fetching_account_info));

        final SafeAsyncTask<GAccessToken> task = new SafeAsyncTask<GAccessToken>() {
            @Override
            public GAccessToken call() throws Exception {
                // request the access token from the code
                final GAccessToken done = Api.getAccessToken(code).get();

                // save the person stub to the local database for access later.
                done.person.save();

                // return the done object
                return done;
            }

            @Override
            protected void onSuccess(final GAccessToken done) {

                final Person person = Application.getDb().getPersonDao().load(done.person.id);

                final String accountId = String.valueOf(person.getName());
                final String token = done.access_token;

                // check for duplicate account
                final Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
                for (final Account account : accounts) {
                    final long personId = Long.parseLong(mAccountManager.getUserData(account, Authenticator.KEY_PERSON_ID));
                    if (personId == done.person.id) {
                        // we have a duplicate, show a toast and cancel the authenticator
                        Toast.makeText(Application.getContext(), String.format(getString(R.string.auth_duplicate_account), person.getName()), Toast.LENGTH_LONG).show();
                        finishActivity(RESULT_DUPLICATE, account, personId);
                        return;
                    }
                }

                // if there are no other accounts, set this as the last used so it is resumed on next launch
                // if there are are other accounts, clear the session id to allow the user to pick an account on next
                // launch
                if (accounts.length == 0) {
                    SettingsManager.setSessionLastUserId(done.person.id);
                } else {
                    SettingsManager.setSessionLastUserId(-1);
                }

                // add the new account, for now we will use the access token as the password
                final Account account = new Account(accountId, Authenticator.ACCOUNT_TYPE);
                final Bundle userdata = new Bundle();
                userdata.putString(Authenticator.KEY_PERSON_ID, String.valueOf(done.person.id));
                mAccountManager.addAccountExplicitly(account, token, userdata);
                mAccountManager.setPassword(account, token);

                finishActivity(Activity.RESULT_OK, account, done.person.id);
            }

            @Override
            protected void onException(final Exception e) {
                displayError(e);
            }

            @Override
            protected void onFinally() {
                mAuthTask = null;
                hideProgress();
            }

            private void finishActivity(final int result, final Account account, final long personId) {
                final Intent intent = new Intent();
                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                intent.putExtra(Authenticator.KEY_PERSON_ID, personId);
                setAccountAuthenticatorResult(intent.getExtras());
                setResult(result, intent);
                finish();
            }
        };
        Application.getExecutor().execute(task.future());
    }

    /**
     * Clears all of the web view cookies to make sure we get a fresh login
     */
    private void clearCookies() {
        final WebViewDatabase db = WebViewDatabase.getInstance(this);
        db.clearFormData();
        db.clearHttpAuthUsernamePassword();
        db.clearUsernamePassword();
        final CookieSyncManager csm = CookieSyncManager.createInstance(this);
        final CookieManager mgr = CookieManager.getInstance();
        mgr.removeAllCookie();
        csm.sync();
        csm.startSync();
    }

    /**
     * displays an error dialog
     */
    private void displayError(final Exception e) {
        final ExceptionHelper eh = new ExceptionHelper(this, e);
        eh.setPositiveButton(new DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_retry);
            }

            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                resetAuthentication();
            }
        });
        eh.setNegativeButton(new DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_cancel);
            }

            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                dialog.dismiss();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        eh.show();
    }

}