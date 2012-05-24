package com.missionhub.android;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Window;
import com.cr_wd.android.network.HttpClient;
import com.cr_wd.android.network.HttpParams;
import com.google.gson.Gson;
import com.missionhub.R;
import com.missionhub.android.api.old.ApiHandler;
import com.missionhub.android.api.old.model.GError;
import com.missionhub.android.api.old.model.GLoginDone;
import com.missionhub.android.app.MissionHubBaseActivity;
import com.missionhub.android.config.Config;
import com.missionhub.android.config.Preferences;
import com.missionhub.android.error.MissionHubException;
import com.missionhub.android.ui.DisplayError;

/**
 * The activity used for login
 */
public class LoginActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = LoginActivity.class.getSimpleName();

	/** result retry */
	public static final int RESULT_RETRY = 1;

	/** the webview container */
	private FrameLayout container;

	/** the webview */
	private WebView mWebView;

	/** the progress bar */
	private ProgressBar mProgressBar;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		clearCookies(); // clear cookies to make sure we get a clean session

		initWebView();
	}

	@SuppressWarnings("deprecation")
	private void initWebView() {
		container = (FrameLayout) findViewById(R.id.container);

		if (mWebView == null) {
			// build the initial request
			final String url = Config.oauthUrl + "/authorize";
			final HttpParams params = new HttpParams();
			params.put("android", true);
			params.put("display", "touch");
			params.put("simple", true);
			params.put("response_type", "code");
			params.put("redirect_uri", Config.oauthUrl + "/done.json");
			params.put("client_id", Config.oauthClientId);
			params.put("scope", Config.oauthScope);

			// setup the webview
			mWebView = new WebView(this);
			mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setAppCacheEnabled(true);
			mWebView.getSettings().setSupportZoom(false);
			mWebView.getSettings().setSavePassword(false);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			mWebView.setWebViewClient(new InternalWebViewClient());
			mWebView.setWebChromeClient(new ProgressChromeClient());
			mWebView.loadUrl(url + '?' + params.getParamString());
		}

		container.addView(mWebView);
	}

	/**
	 * Sets the activity result to OK and finishes the activity.
	 */
	private void finishOK() {
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Sets the activity result to canceled and finishes the activity
	 */
	private void finishCanceled() {
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Sets the activity result to retry and finished the activity
	 */
	private void finishRetry() {
		setResult(RESULT_RETRY);
		finish();
	}

	/**
	 * Chrome client that manages indeterminate progress visibility
	 */
	private class ProgressChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, final int progress) {
			if (progress < 100) {
				showProgress();
			}
			if (progress == 100) {
				if (!gettingToken) {
					hideProgress();
				}
			}
		}
	}

	/**
	 * WebViewClient that captures and manipulates requests and responses from
	 * the webview
	 */
	private class InternalWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(final WebView view, final int errorCode, String description, final String failingUrl) {
			// Hide the webview to not show errors
			container.setVisibility(View.GONE);

			Throwable exception = new MissionHubException(description);

			if (errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
				// bad network connection
				exception = new java.net.ConnectException();
			} else if (errorCode == URI_ERROR) {
				// got an error from the server
				try {
					final Gson gson = new Gson();
					final GError error = gson.fromJson(description, GError.class);
					description = error.getError().getMessage();
				} catch (final Exception e) {
					description = getString(R.string.error_msg);
				}
				exception = new MissionHubException(description);
			}

			final AlertDialog ad = DisplayError.display(LoginActivity.this, exception);
			ad.setButton(DialogInterface.BUTTON_POSITIVE, ad.getContext().getString(R.string.action_retry), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					if (errorCode == FATAL_ERROR) {
						finishRetry();
					} else {
						mWebView.reload();
						container.setVisibility(View.VISIBLE);
					}
				}
			});
			ad.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.action_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					finishCanceled();
				}
			});
			ad.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(final DialogInterface dialog) {
					finishCanceled();
				}
			});
			ad.show();
		}

		public static final int URI_ERROR = 1; // error code for api error
												// response
		public static final int FATAL_ERROR = 2;

		@Override
		public void onPageFinished(final WebView view, final String url) {
			final Uri uri = Uri.parse(url);
			final Uri configUri = Uri.parse(Config.oauthUrl);

			// api returned an error
			if (uri.getQueryParameter("error_description") != null) {
				try {
					onReceivedError(view, URI_ERROR, URLDecoder.decode(uri.getQueryParameter("error_description"), "UTF-8"), url);
				} catch (final UnsupportedEncodingException e) {
					onReceivedError(view, URI_ERROR, e.getLocalizedMessage(), url);
				}
				return;
			}

			// somehow we have loaded the contacts page
			if (uri.getPath().contains("contacts")) {
				mWebView.stopLoading();
				onReceivedError(view, FATAL_ERROR, getString(R.string.error_msg), url);
				return;
			}

			final String authorization = uri.getQueryParameter("authorization");

			if (authorization != null && uri.getHost().equalsIgnoreCase(configUri.getHost()) && uri.getPath().contains("/authorize")) {
				CookieSyncManager.createInstance(LoginActivity.this);
				final CookieManager mgr = CookieManager.getInstance();
				final String cookieString = mgr.getCookie(Config.baseUrl);

				if (cookieString != null && (cookieString.contains("_mh_session=") && cookieString.contains("logged_in=true"))) {
					mWebView.loadUrl(Config.oauthUrl + "/grant.json?authorization=" + authorization);
					return;
				}
			}
			final String code = uri.getQueryParameter("code");
			if (code != null && uri.getHost().equalsIgnoreCase(configUri.getHost()) && uri.getPath().contains("/done")) {
				container.setVisibility(View.GONE);
				getTokenFromCode(code);
				return;
			}
		}
	}

	private boolean gettingToken = false;

	/**
	 * Fetches the access token from a grant code Starts a user session if fetch
	 * completes successfully
	 */
	private void getTokenFromCode(final String code) {
		if (gettingToken) {
			return;
		}

		gettingToken = true;
		showProgress();
		mProgressBar.setVisibility(View.VISIBLE);

		final HttpClient client = new HttpClient();

		final HttpParams params = new HttpParams();
		params.put("client_id", Config.oauthClientId);
		params.put("client_secret", Config.oauthClientSecret);
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		params.put("scope", Config.oauthScope);
		params.put("redirect_uri", Config.oauthUrl + "/done.json");
		params.put("platform", "android");
		params.put("platform_product", Build.PRODUCT);
		params.put("platform_release", android.os.Build.VERSION.RELEASE);
		try {
			params.put("app", String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
		} catch (final NameNotFoundException e) {}

		client.post(Config.oauthUrl + "/access_token", params, new ApiHandler(GLoginDone.class) {

			@Override
			public void onSuccess(final Object gsonObject) {
				final GLoginDone loginDone = (GLoginDone) gsonObject;

				Preferences.setAccessToken(LoginActivity.this, loginDone.getAccess_token());

				if (loginDone.getPerson() != null) {
					Preferences.setUserID(LoginActivity.this, loginDone.getPerson().getId());
				}

				finishOK();
			}

			@Override
			public void onError(final Throwable throwable) {
				Log.e(TAG, "Login Failed", throwable);
				final AlertDialog ad = DisplayError.display(LoginActivity.this, throwable);
				ad.setButton(DialogInterface.BUTTON_POSITIVE, ad.getContext().getString(R.string.action_retry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.dismiss();
						finishRetry();
					}
				});
				ad.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.action_close), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.dismiss();
						finishCanceled();
					}
				});
				ad.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(final DialogInterface dialog) {
						finishCanceled();
					}
				});
				ad.show();

				hideProgress();
				mProgressBar.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		if (mWebView != null) {
			container.removeView(mWebView);
		}

		super.onConfigurationChanged(newConfig);

		setContentView(R.layout.activity_login);

		initWebView();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		mWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mWebView.restoreState(savedInstanceState);
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
