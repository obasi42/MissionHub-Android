package com.missionhub.authenticator;

import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockAccountAuthenticatorActivity;
import com.google.gson.Gson;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiErrorGson;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.application.SettingsManager;
import com.missionhub.exception.ApiException;
import com.missionhub.model.gson.GAuthTokenDone;
import com.missionhub.network.HttpParams;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.U;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends RoboSherlockAccountAuthenticatorActivity {

	/** the logging tag */
	public static final String TAG = AuthenticatorActivity.class.getSimpleName();

	/** the system account manager */
	private AccountManager mAccountManager;

	@InjectView(R.id.placeholder) FrameLayout mPlaceholder;
	@InjectView(R.id.content) LinearLayout mContent;
	@InjectView(R.id.status) TextView mStatus;
	@InjectView(R.id.error) TextView mError;
	@InjectView(R.id.btn_resources) TextView mResources;
	@InjectView(R.id.loading) ProgressBar mLoading;
	@InjectView(R.id.btn_retry) Button mRetry;

	/** the authentication webview */
	private WebView mWebView;

	/** true when fetching the token from a code */
	private final AtomicBoolean mGettingToken = new AtomicBoolean();

	/** true if the webview has an error */
	private final AtomicBoolean mWebViewError = new AtomicBoolean();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_authenticator);

		mResources.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				IntentHelper.openUrl("http://blog.missionhub.com/");
			}
		});

		if (savedInstanceState == null) {
			clearCookies();
		} else {
			mGettingToken.set(savedInstanceState.getBoolean("mGettingToken"));
			if (mGettingToken.get()) {
				showLoading("Getting authentication token...");
			}
		}

		mAccountManager = AccountManager.get(this);

		initWebView();
	}

	/**
	 * Creates the web view used to authenticate the user
	 */
	@SuppressWarnings("deprecation")
	protected void initWebView() {
		if (mWebView == null) {

			final String url = Configuration.getOauthUrl() + "/authorize";
			final HttpParams params = new HttpParams();
			params.put("android", true);
			params.put("display", "touch");
			params.put("simple", true);
			params.put("response_type", "code");
			params.put("redirect_uri", Configuration.getOauthUrl() + "/done.json");
			params.put("client_id", Configuration.getOauthClientId());
			params.put("scope", Configuration.getOauthScope());

			mWebView = new WebView(this);
			mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mWebView.getSettings().setAppCacheEnabled(true);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setLoadsImagesAutomatically(true);
			mWebView.getSettings().setSupportZoom(false);
			mWebView.getSettings().setSavePassword(false);
			mWebView.getSettings().setSaveFormData(false);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			mWebView.setScrollbarFadingEnabled(true);
			mWebView.setWebViewClient(new InternalWebViewClient());
			mWebView.setWebChromeClient(new ProgressChromeClient());
			mWebView.loadUrl(url + '?' + params.getParamString());

			mRetry.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					hideError();
					mWebView.reload();
				}
			});
		}

		mPlaceholder.addView(mWebView);
	}

	/**
	 * Chrome client that manages indeterminate progress visibility
	 */
	private class ProgressChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, final int progress) {
			if (progress < 100) {
				AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			}
			if (progress == 100) {
				AuthenticatorActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		}
	}

	/**
	 * Web view client that directs the authentication process
	 */
	private class InternalWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
			mWebViewError.set(true);
			showError(getString(R.string.network_error), description);
		}

		@Override
		public void onPageFinished(final WebView view, final String url) {
			if (!mWebViewError.get()) {
				hideError();
			}

			final Uri uri = Uri.parse(url);
			final Uri oauthUri = Uri.parse(Configuration.getOauthUrl());

			// api returned an error, show an error and return
			if (uri.getQueryParameter("error") != null) {
				try {
					final Gson gson = new Gson();
					final ApiErrorGson error = gson.fromJson(URLDecoder.decode(uri.getQueryParameter("error_description"), "UTF-8"), ApiErrorGson.class);
					showError(error.error.title, error.error.message);
				} catch (final Exception e) {
					showError(getString(R.string.network_error), uri.getQueryParameter("error"));
				}
				return;
			}

			// Somehow we have loaded the contacts page, show an error and return;
			if (uri.getPath().contains("contacts")) {
				showError(getString(R.string.network_error), null);
				return;
			}

			// Check for the authorization parameter. If it exists, automatically grant the app access to the user's mh
			// account.
			final String authorization = uri.getQueryParameter("authorization");
			if (authorization != null && uri.getHost().equalsIgnoreCase(oauthUri.getHost()) && uri.getPath().contains("/authorize")) {
				CookieSyncManager.createInstance(AuthenticatorActivity.this);
				final CookieManager mgr = CookieManager.getInstance();
				final String cookieString = mgr.getCookie(oauthUri.getHost());

				if (cookieString != null && (cookieString.contains("_mh_session=") && cookieString.contains("logged_in=true"))) {
					mWebView.loadUrl(Configuration.getOauthUrl() + "/grant.json?authorization=" + authorization);
					return;
				}
			}

			// Check for the authentication code. If exists, use it to obtain the user's access token
			final String code = uri.getQueryParameter("code");
			if (code != null && uri.getHost().equalsIgnoreCase(oauthUri.getHost()) && uri.getPath().contains("/done")) {
				getTokenFromCode(code);
				return;
			}
		}
	}

	/**
	 * Gets the user's access token from a grant code
	 * 
	 * @param code
	 */
	private void getTokenFromCode(final String code) {
		if (mGettingToken.get()) return;
		mGettingToken.set(true);

		showLoading("Getting authentication token...");

		final RoboAsyncTask<GAuthTokenDone> task = new RoboAsyncTask<GAuthTokenDone>(this) {
			@Override
			public GAuthTokenDone call() throws Exception {
				final GAuthTokenDone done = Api.getAccessToken(code).get();
				done.person.save().get();
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
						finishActivity(RESULT_CANCELED, account, personId);
						return;
					}
				}

				// if there are no other accounts, set this at the last used so it is resumed immediately
				// otherwise remove the last session user id so a prompt is displayed the next time the session is
				// resumed.
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

			private void finishActivity(final int result, final Account account, final long personId) {
				final Intent intent = new Intent();
				intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
				intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
				intent.putExtra(Authenticator.KEY_PERSON_ID, personId);
				setAccountAuthenticatorResult(intent.getExtras());
				setResult(result, intent);
				finish();
			}

			@Override
			protected void onException(final Exception e) {
				mRetry.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						getTokenFromCode(code);
					}
				});
				showError(e);
			}

			@Override
			protected void onFinally() {
				mGettingToken.set(false);
			}
		};
		Application.getExecutor().execute(task.future());
	}

	private void showError(String error, final String description) {
		mContent.setVisibility(View.VISIBLE);
		mWebView.setVisibility(View.GONE);

		if (!U.isNullEmpty(description)) {
			error += "\n" + description;
		}
		mError.setText(error);
		mError.setVisibility(View.VISIBLE);

		if (!U.isNullEmpty(error)) {
			mError.setText(error);
			mError.setVisibility(View.VISIBLE);
		} else {
			mError.setVisibility(View.GONE);
		}

		mStatus.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
		mRetry.setVisibility(View.VISIBLE);

		setProgressBarIndeterminateVisibility(false);
	}

	private void showError(final Exception e) {
		Log.e(TAG, e.getMessage(), e);

		if (e instanceof ApiException) {
			final ApiException e2 = (ApiException) e;

			String error = e2.getTitle();
			if (U.isNull(error)) {
				error += e2.getMessage();
			} else {
				error += "\n" + e2.getMessage();
			}

			if (!U.isNullEmpty(e2.getCode())) {
				error += "\nCode " + e2.getCode() + ".";
			}

			showError(error, null);
		} else {
			final Throwable cause = e.getCause();
			if (cause != null) {
				showError(cause.getMessage(), null);
			} else {
				showError(e.getMessage(), null);
			}
		}
	}

	private void hideError() {
		mWebView.setVisibility(View.VISIBLE);
		mContent.setVisibility(View.GONE);
	}

	private void showLoading(final String status) {
		mContent.setVisibility(View.VISIBLE);
		mWebView.setVisibility(View.GONE);

		if (!U.isNullEmpty(status)) {
			mStatus.setText(status);
			mStatus.setVisibility(View.VISIBLE);
		} else {
			mStatus.setVisibility(View.GONE);
		}

		mError.setVisibility(View.GONE);
		mLoading.setVisibility(View.VISIBLE);
		mRetry.setVisibility(View.GONE);

		setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onConfigurationChanged(final android.content.res.Configuration newConfig) {
		if (mWebView != null) {
			mPlaceholder.removeView(mWebView);
		}
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_authenticator);
		initWebView();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("mGettingToken", mGettingToken.get());
		mWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mGettingToken.set(savedInstanceState.getBoolean("mGettingToken"));
		mWebView.restoreState(savedInstanceState);
		if (mGettingToken.get()) {
			showLoading("Getting authentication token...");
		}
	}

	/**
	 * Clears all of the webview cookies to make sure we get a fresh login
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
}