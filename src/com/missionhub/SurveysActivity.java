package com.missionhub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.missionhub.api.SurveyApi;
import com.missionhub.error.MissionHubException;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.DisplayError.Retry;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.widget.item.NavigationItem;

public class SurveysActivity extends MissionHubMainActivity {

	private WebView mWebView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		
		
		setContentView(R.layout.activity_surveys);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setAllowFileAccess(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSaveFormData(false);
		mWebView.getSettings().setSavePassword(false);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.setWebChromeClient(new InternalWebViewChrome());
		mWebView.loadUrl(SurveyApi.getUrl(this));

		clearCookies();
	}

	/** Global menu items */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuItem refreshItem = menu.add(Menu.NONE, R.id.refresh, 0, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		this.setSupportProgressBarIndeterminateItem(refreshItem);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			mWebView.reload();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateNavigationMenu(final NavigationMenu menu) {
		super.onCreateNavigationMenu(menu);
	}

	@Override
	public boolean onNavigationItemSelected(final NavigationItem item) {
		return super.onNavigationItemSelected(item);
	}

	private class InternalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
			final AlertDialog ad = DisplayError.displayWithRetry(SurveysActivity.this, new MissionHubException(description, String.valueOf(errorCode)), new Retry() {
				@Override
				public void run() {
					mWebView.reload();
				}
			});
			ad.show();
		}
	}

	private class InternalWebViewChrome extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, final int progress) {
			SurveysActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			if (progress == 100) {
				SurveysActivity.this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			}
		}
	}

	private void clearCookies() {
		final CookieSyncManager csm = CookieSyncManager.createInstance(SurveysActivity.this);
		final CookieManager mgr = CookieManager.getInstance();
		mgr.removeAllCookie();
		csm.sync();
		csm.startSync();
	}

	public static final int DIALOG_CLOSE = 0;

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case DIALOG_CLOSE:
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.surveys_exit).setMessage(R.string.surveys_exit_msg).setCancelable(false)
					.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.dismiss();
							SurveysActivity.this.finish();
						}
					}).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.cancel();
						}
					});
			final AlertDialog alert = builder.create();
			return alert;
		}
		return null;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(DIALOG_CLOSE);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSaveInstanceState(final Bundle b) {
		((WebView) findViewById(R.id.webView)).saveState(b);
	}

	@Override
	public void onRestoreInstanceState(final Bundle b) {
		((WebView) findViewById(R.id.webView)).restoreState(b);
	}

}