package com.missionhub;

import java.util.HashMap;

import com.flurry.android.FlurryAgent;
import com.missionhub.api.Api;
import com.missionhub.auth.User;
import com.missionhub.config.Config;
import com.missionhub.ui.DisplayError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SurveysActivity extends Activity {

	public static final String TAG = SurveysActivity.class.getName();
	
	private WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setTitle(R.string.surveys_title);
		setContentView(R.layout.surveys);

		mWebView = (WebView) findViewById(R.id.webview_surveys);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setAllowFileAccess(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSaveFormData(false);
		mWebView.getSettings().setSavePassword(false);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.setWebChromeClient(new InternalWebViewChrome());
		mWebView.loadUrl(Api.getSurveysUrl());

		clearCookies();

		User.initFlurryUser();
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Surveys");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {
		}
	}

	private class InternalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			AlertDialog ad = DisplayError.display(SurveysActivity.this, errorCode, description, failingUrl);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					mWebView.reload();
				}
			});
			ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			ad.show();
		}
	}

	private class InternalWebViewChrome extends WebChromeClient {
		public void onProgressChanged(WebView view, int progress) {
			SurveysActivity.this.setTitle(R.string.surveys_loading);
			SurveysActivity.this.setProgress(progress * 100);
			if (progress == 100)
				SurveysActivity.this.setTitle(R.string.surveys_title);
		}
	}

	private void clearCookies() {
		CookieSyncManager csm = CookieSyncManager.createInstance(SurveysActivity.this);
		CookieManager mgr = CookieManager.getInstance();
		mgr.removeAllCookie();
		csm.sync();
		csm.startSync();
	}

	public static final int DIALOG_CLOSE = 0;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CLOSE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.surveys_exit)
				.setMessage(R.string.surveys_exit_msg)
				.setCancelable(false)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						SurveysActivity.this.finish();
					}
				}).setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
			AlertDialog alert = builder.create();
			return alert;
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(DIALOG_CLOSE);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onStart() {
		super.onStart();
		User.initFlurryUser();
		FlurryAgent.onStartSession(this, Config.flurryKey);
	}

	@Override
	public void onStop() {
		super.onStop();
		User.initFlurryUser();
		FlurryAgent.onEndSession(this);
	}
}