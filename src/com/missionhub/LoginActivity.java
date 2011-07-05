package com.missionhub;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.missionhub.api.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoginActivity extends Activity {

	public static final String TAG = "LoginActivity";

	public static final String PREFS_NAME = "MissionHubPrivate";

	private ProgressDialog mProgressDialog;
	private WebView mWebView;
	private Button mCloseBtn;
	private String wvUrl = Config.oauthUrl + "/authorize?display=touch&simple=true&response_type=code&redirect_uri=" + Config.oauthUrl
			+ "/done.json&client_id=" + Config.oauthClientId + "&scope=" + Config.oauthScope;

	public String getStoredToken() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("token", null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.login);

		clearCookies();

		mProgressDialog = ProgressDialog.show(LoginActivity.this, "", LoginActivity.this.getString(R.string.alert_loading), true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		mCloseBtn = (Button) findViewById(R.id.btn_logout_close);

		mWebView = (WebView) findViewById(R.id.webview_login);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.loadUrl(wvUrl);
	}

	private void clearCookies() {
		CookieSyncManager csm = CookieSyncManager.createInstance(LoginActivity.this);
		CookieManager mgr = CookieManager.getInstance();
		mgr.removeAllCookie();
		csm.sync();
		csm.startSync();
	}

	@Override
	public void finish() {
		mWebView.stopLoading();
		mProgressDialog.dismiss();
		clearCookies();
		super.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	private void returnWithToken() {
		Intent i = new Intent();
		i.putExtra("token", User.token);
		this.setResult(RESULT_OK, i);
		finish();
	}

	private class InternalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			mProgressDialog.show();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.hide();
			}
			Log.e(TAG, description);
			// TODO Throw Error
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Uri uri = Uri.parse(url);
			String authorization = uri.getQueryParameter("authorization");
			if (authorization != null && uri.getPath().equalsIgnoreCase("/oauth/authorize")) {
				CookieSyncManager.createInstance(LoginActivity.this);
				CookieManager mgr = CookieManager.getInstance();
				String cookieString = mgr.getCookie(Config.cookieHost);
				if (cookieString != null && cookieString.contains("_bonfire_session=")) {
					mWebView.loadUrl(Config.oauthUrl + "/grant.json?authorization=" + authorization);
					return;
				}
			}
			String code = uri.getQueryParameter("code");
			if (code != null && uri.getPath().equalsIgnoreCase("/oauth/done.json")) {
				mWebView.setVisibility(View.GONE);
				mCloseBtn.setVisibility(View.GONE);
				getTokenFromCode(code);
				return;
			}

			Log.e(TAG, "WebView Loaded: " + url);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.hide();
			}
		}
	}

	private void getTokenFromCode(String code) {
		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("client_id", Config.oauthClientId);
		params.put("client_secret", Config.oauthClientSecret);
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		params.put("scope", Config.oauthScope);
		params.put("redirect_uri", Config.oauthUrl + "/done.json");

		client.post(Config.oauthUrl + "/access_token", params, new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				mProgressDialog.show();
			}

			@Override
			public void onSuccess(JSONObject response) {
				try {
					User.token = response.getString("access_token");
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("token", User.token);
					User.isLoggedIn = true;
				} catch (Exception e) {
					onFailure(new Throwable());
					return;
				}
				Log.i(TAG, "Logged In With Token: " + User.token);
				returnWithToken();
			}

			@Override
			public void onFailure(Throwable e) {
				Log.i(TAG, "GET TOKEN FAIL: " + e.toString());
			}

			@Override
			public void onFinish() {
				mProgressDialog.hide();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clickClose(View view) {
		finish();
	}
}