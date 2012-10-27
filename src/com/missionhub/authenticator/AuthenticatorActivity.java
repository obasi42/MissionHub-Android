package com.missionhub.authenticator;

import java.net.URLDecoder;
import java.util.concurrent.FutureTask;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockAccountAuthenticatorActivity;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiErrorGson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.SettingsManager;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.exception.WebViewException;
import com.missionhub.model.gson.GAuthTokenDone;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

/**
 * Activity which displays login screen to the user.
 */
@ContentView(R.layout.activity_authenticator)
public class AuthenticatorActivity extends RoboSherlockAccountAuthenticatorActivity {

	/** the logging tag */
	public static final String TAG = AuthenticatorActivity.class.getSimpleName();

	/** the activity result when a duplicate account exists */
	public static final int RESULT_DUPLICATE = 1;

	/** the system account manager */
	@Inject private AccountManager mAccountManager;

	/** the web view used for authentication */
	@InjectView(R.id.webview) private WebView mWebView;

	/** the progress bar */
	@InjectView(R.id.progress) private ProgressBar mProgress;

	/** text to display the current progress action */
	@InjectView(R.id.progress_text) private TextView mProgressText;

	/** holds the task that fetches the access token and adds the system account */
	private FutureTask<GAuthTokenDone> mAuthTask;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);

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

	/** resets the state of the auth activity */
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
		mWebView.loadUrl(getAuthenticationUrl());
	}

	/**
	 * web view client to manage the authentication process
	 */
	private class AuthenticatorWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			// parse the url to a uri for easier checking
			final Uri uri = Uri.parse(url);

			// parse the oauth url to a uri for comparisons to the uri
			final Uri oauthUri = Uri.parse(Configuration.getOauthUrl());

			// make sure we are only working with requests from the oauth server
			if (uri.getHost().equalsIgnoreCase(oauthUri.getHost())) {

				// check for an api error
				if (!U.isNullEmpty(uri.getQueryParameter("error"))) {
					try {
						final Gson gson = new Gson();
						final ApiErrorGson error = gson.fromJson(URLDecoder.decode(uri.getQueryParameter("error_description"), "UTF-8"), ApiErrorGson.class);
						onError(error.getException());
					} catch (final Exception e) {
						onError(new Exception(uri.getQueryParameter("error")));
					}
					return true;
				}

				// Check for the authorization parameter. If it exists, automatically grant app access.
				final String authorization = uri.getQueryParameter("authorization");
				if (!U.isNullEmpty(authorization) && uri.getPath().contains("/authorize")) {
					// check for a mh cookie to ensure the user is logged in
					CookieSyncManager.createInstance(AuthenticatorActivity.this);
					final CookieManager mgr = CookieManager.getInstance();
					final String cookieString = mgr.getCookie(oauthUri.getHost());
					if (cookieString != null && (cookieString.contains("_mh_session="))) {
						mWebView.loadUrl(Configuration.getOauthUrl() + "/grant.json?authorization=" + authorization);
						return true;
					}
				}

				// Check for the authentication code. If exists, use it to obtain the user's access token
				final String code = uri.getQueryParameter("code");
				if (!U.isNullEmpty(code) && uri.getPath().contains("/done")) {
					mWebView.loadData("", "text/plain; charset=UTF-8", null);
					mWebView.setVisibility(View.GONE);

					// fetch the access token and add an account
					addAccountFromCode(code);

					return true;
				}
			}

			return false; // make sure this web view handles all other urls
		}

		@Override
		public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
			AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
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
			mWebView.loadData("", "text/plain; charset=UTF-8", null);
			mWebView.setVisibility(View.GONE);

			displayError(e);
		}
	}

	/**
	 * Builds and returns the url used for authentication
	 * 
	 * @return the url used for authentication
	 */
	private String getAuthenticationUrl() {
		final String url = Configuration.getOauthUrl() + "/authorize";
		final HttpParams params = new HttpParams();
		params.put("android", true);
		params.put("display", "touch");
		params.put("simple", true);
		params.put("response_type", "code");
		params.put("redirect_uri", Configuration.getOauthUrl() + "/done.json");
		params.put("client_id", Configuration.getOauthClientId());
		params.put("scope", Configuration.getOauthScope());

		return url + '?' + params.getParamString();
	}

	/** shows the progress indicator with text */
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

	/** hides the progress indicator */
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

		showProgress("Fetching Account Information...");

		final RoboAsyncTask<GAuthTokenDone> task = new RoboAsyncTask<GAuthTokenDone>(this) {
			@Override
			public GAuthTokenDone call() throws Exception {
				// request the access token from the code
				final GAuthTokenDone done = Api.getAccessToken(code).get();

				// save the person to the local database for access later. call .get to ensure the save process has
				// finished.
				done.person.save().get();

				// return the done object
				return done;
			}

			@Override
			protected void onSuccess(final GAuthTokenDone done) {

				final String accountId = String.valueOf(done.person.name);
				final String token = done.access_token;

				// check for duplicate account
				final Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
				for (final Account account : accounts) {
					final long personId = Long.parseLong(mAccountManager.getUserData(account, Authenticator.KEY_PERSON_ID));
					if (personId == done.person.id) {
						// we have a duplicate, show a toast and cancel the authenticator
						Toast.makeText(getContext(), "Account for " + done.person.name + " already exists.", Toast.LENGTH_LONG).show();
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

				finishActivity(RESULT_OK, account, done.person.id);
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

	/** displays an error dialog */
	private void displayError(final Exception e) {
		final ExceptionHelper eh = new ExceptionHelper(this, e);
		eh.setPositiveButton(new DialogButton() {
			@Override
			public String getTitle() {
				return "Retry";
			}

			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				resetAuthentication();
			}
		});
		eh.setNegativeButton(new DialogButton() {
			@Override
			public String getTitle() {
				return "Cancel";
			}

			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		eh.show();
	}

}