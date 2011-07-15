package com.missionhub;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.missionhub.api.GError;
import com.missionhub.api.GLoginDone;
import com.missionhub.auth.Auth;
import com.missionhub.config.Config;
import com.missionhub.config.Preferences;
import com.missionhub.error.MHException;
import com.missionhub.helpers.Flurry;
import com.missionhub.ui.DisplayError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

	public static final String TAG = LoginActivity.class.getName();

	private ProgressDialog mProgressDialog;
	private WebView mWebView;
	private Button mCloseBtn;
	private String wvUrl = Config.oauthUrl + "/authorize?display=touch&simple=true&response_type=code&redirect_uri=" + Config.oauthUrl
			+ "/done.json&client_id=" + Config.oauthClientId + "&scope=" + Config.oauthScope;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.login);

		Application.restoreApplicationState(savedInstanceState);
		
		clearCookies();

		mProgressDialog = ProgressDialog.show(LoginActivity.this, "", LoginActivity.this.getString(R.string.alert_loading), true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		mCloseBtn = (Button) findViewById(R.id.close);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.loadUrl(wvUrl);
		
		Flurry.pageView("Login");
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   FlurryAgent.onStartSession(this, Config.flurryKey);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void finish() {
		mWebView.stopLoading();
		mProgressDialog.dismiss();
		clearCookies();
		super.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putAll(Application.saveApplicationState(b));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		Application.restoreApplicationState(b);
	}

	public void clickClose(View view) {
		finish();
	}

	private void returnWithToken() {
		Intent i = new Intent();
		i.putExtra("token", Auth.getAccessToken());
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
			mWebView.setVisibility(View.GONE);
			mCloseBtn.setVisibility(View.GONE);
			AlertDialog ad = DisplayError.display(LoginActivity.this, errorCode, description, failingUrl);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					mWebView.setVisibility(View.VISIBLE);
					mCloseBtn.setVisibility(View.VISIBLE);
					mWebView.reload();
				}
			});
			ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					finish();
				}
			});
			ad.setOnCancelListener(new OnCancelListener(){
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			ad.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Uri uri = Uri.parse(url);
			String authorization = uri.getQueryParameter("authorization");
			if (authorization != null && uri.getPath().equalsIgnoreCase("/oauth/authorize")) {
				CookieSyncManager.createInstance(LoginActivity.this);
				CookieManager mgr = CookieManager.getInstance();
				String cookieString = mgr.getCookie(Config.cookieHost);
				if (cookieString != null && (cookieString.contains("_bonfire_session=") || cookieString.contains("_mh_session=true"))) {
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

			if (mProgressDialog.isShowing()) {
				mProgressDialog.hide();
			}
		}
	}

	private boolean gettingToken = false;
	
	private void getTokenFromCode(String code) {
		if (gettingToken) return;
		
		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
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
		} catch (NameNotFoundException e) {}

		client.post(Config.oauthUrl + "/access_token", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				mProgressDialog.show();
				gettingToken = true;
			}

			@Override
			public void onSuccess(String response) {
				Gson gson = new Gson();
				try{
					GError error = gson.fromJson(response, GError.class);
					onFailure(new MHException(error));
				} catch (Exception out){
					try {
						GLoginDone loginDone = gson.fromJson(response, GLoginDone.class);
						Auth.setAccessToken(loginDone.getAccess_token());
						Preferences.setAccessToken(LoginActivity.this, loginDone.getAccess_token());
						returnWithToken();
					} catch(Exception e) {
						onFailure(e);
					}
				}	
			}
			
			@Override
			public void onFailure(Throwable e) {
				Log.e(TAG, "Login Failed", e);
				AlertDialog ad = DisplayError.display(LoginActivity.this, e);
				ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_close), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						finish();
					}
				});
				ad.setOnCancelListener(new OnCancelListener(){
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});
				ad.show();
				Flurry.error(e, "Login.getTokenFromCode");
			}

			@Override
			public void onFinish() {
				gettingToken = false;
				mProgressDialog.hide();
			}
		});
	}
	
	private void clearCookies() {
		CookieSyncManager csm = CookieSyncManager.createInstance(LoginActivity.this);
		CookieManager mgr = CookieManager.getInstance();
		mgr.removeAllCookie();
		csm.sync();
		csm.startSync();
	}
}
