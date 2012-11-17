package com.missionhub.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.MenuItem;

public class LicensesActivity extends BaseActivity {

	private WebView mWebView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mWebView = new WebView(this);
		mWebView.setBackgroundColor(0xFFEEEEEE);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.loadUrl("file:///android_asset/licenses.html");

		setContentView(mWebView);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}