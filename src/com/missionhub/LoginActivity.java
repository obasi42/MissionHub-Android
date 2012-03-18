package com.missionhub;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Window;
import com.cr_wd.android.network.HttpClient;
import com.cr_wd.android.network.HttpParams;
import com.google.gson.Gson;
import com.missionhub.api.ApiHandler;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.GError;
import com.missionhub.api.model.GLoginDone;
import com.missionhub.api.model.sql.Person;
import com.missionhub.broadcast.GenericCUDEBroadcast;
import com.missionhub.broadcast.GenericCUDEReceiver;
import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.config.Config;
import com.missionhub.config.Preferences;
import com.missionhub.error.MissionHubException;
import com.missionhub.ui.DisplayError;
import com.missionhub.util.U;

/**
 * The Login Activity
 */
public class LoginActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = LoginActivity.class.getSimpleName();

	@InjectView(R.id.webview)
	WebView mWebView;
	@InjectView(R.id.progressBar)
	ProgressBar mProgressBar;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);

		clearCookies(); // clear cookies to make sure we get a clean session

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
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.setWebChromeClient(new ProgressChromeClient());
		mWebView.loadUrl(url + '?' + params.getParamString());
	}

	/**
	 * Broadcasts a successful login, sets the activity result to OK and
	 * finishes the activity.
	 */
	private void finishOK() {
		SessionBroadcast.broadcastLogin(this.getApplicationContext(), this.getSession().getAccessToken());
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Sets the activity result to canceled and finishes the activity
	 */
	private void finishCanceled() {
		setResult(Activity.RESULT_CANCELED);
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
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(final WebView view, final int errorCode, String description, final String failingUrl) {
			// Hide the webview to not show errors
			mWebView.setVisibility(View.GONE);

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
			ad.setButton(ad.getContext().getString(R.string.action_retry), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.dismiss();
					mWebView.reload();
					mWebView.setVisibility(View.VISIBLE);
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

		@Override
		public void onPageFinished(final WebView view, final String url) {
			final Uri uri = Uri.parse(url);
			final Uri configUri = Uri.parse(Config.oauthUrl);

			// api returned an error
			if (uri.getQueryParameter("error_description") != null) {
				onReceivedError(view, URI_ERROR, URLDecoder.decode(uri.getQueryParameter("error_description")), url);
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
				mWebView.setVisibility(View.GONE);
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
				final ApiHandler handler = this;
				final GLoginDone loginDone = (GLoginDone) gsonObject;

				Preferences.setAccessToken(LoginActivity.this, loginDone.getAccess_token());
				Preferences.setUserID(LoginActivity.this, loginDone.getPerson().getId());

				final GenericCUDEReceiver receiver = new GenericCUDEReceiver(LoginActivity.this, Person.class) {

					@Override
					public void onUpdate(final long[] rowIds) {
						if (!U.contains(rowIds, loginDone.getPerson().getId())) {
							handler.onError(new MissionHubException("Person not found in response."));
							return;
						}

						final MissionHubApplication application = (MissionHubApplication) getApplicationContext();
						application.setSession(Session.resumeSession(application));

						unregister();

						finishOK();
					}

					@Override
					public void onError(final long[] rowIds, final Throwable t) {
						handler.onError(new MissionHubException("Person not found in response."));
					}
				};

				final List<String> cats = new ArrayList<String>();
				cats.add(this.toString());
				receiver.register(cats, GenericCUDEBroadcast.NOTIFY_GENERIC_UPDATE, GenericCUDEBroadcast.NOTIFY_GENERIC_ERROR);

				PersonJsonSql.update(getApplicationContext(), loginDone.getPerson(), this.toString());
			}

			@Override
			public void onError(final Throwable throwable) {
				Log.e(TAG, "Login Failed", throwable);
				final AlertDialog ad = DisplayError.display(LoginActivity.this, throwable);
				ad.setButton(ad.getContext().getString(R.string.action_retry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.dismiss();
						setResult(RESULT_FIRST_USER);
						finish();
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
	public void onSaveInstanceState(final Bundle b) {
		((WebView) findViewById(R.id.webview)).saveState(b);
	}

	@Override
	public void onRestoreInstanceState(final Bundle b) {
		((WebView) findViewById(R.id.webview)).restoreState(b);
	}

	/**
	 * Clears all of the webview cookies to make sure we get a fresh login
	 */
	private void clearCookies() {
		final CookieSyncManager csm = CookieSyncManager.createInstance(this);
		final CookieManager mgr = CookieManager.getInstance();
		mgr.removeAllCookie();
		csm.sync();
		csm.startSync();
	}
}
