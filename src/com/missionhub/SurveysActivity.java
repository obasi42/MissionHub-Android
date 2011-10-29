package com.missionhub;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.LoaderActionBarItem;
import greendroid.widget.ActionBarItem.Type;

import com.missionhub.api.Survey;
import com.missionhub.helper.Flurry;
import com.missionhub.ui.DisplayError;

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

public class SurveysActivity extends Activity {

	public static final String TAG = SurveysActivity.class.getName();
	
	private WebView mWebView;
	
	private LoaderActionBarItem indicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.surveys_title);
		
		setActionBarContentView(R.layout.surveys);
		getActionBar().setType(ActionBar.Type.Empty);
		indicator = (LoaderActionBarItem) addActionBarItem(Type.Refresh, R.id.action_bar_refresh);
		
		mWebView = (WebView) findViewById(R.id.webview_surveys);
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setAllowFileAccess(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSaveFormData(false);
		mWebView.getSettings().setSavePassword(false);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.setWebViewClient(new InternalWebViewClient());
		mWebView.setWebChromeClient(new InternalWebViewChrome());
		mWebView.loadUrl(Survey.getUrl(this));
		
		clearCookies();

		Flurry.pageView(this, "Surveys");
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
            	mWebView.reload();
                break;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
        return true;
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
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					mWebView.reload();
				}
			});
			ad.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.alert_close), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			ad.show();
		}
	}

	private class InternalWebViewChrome extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int progress) {
			indicator.setLoading(true);
			SurveysActivity.this.setProgress(progress * 100);
			if (progress == 100)
				indicator.setLoading(false);
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
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						SurveysActivity.this.finish();
					}
				}).setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
					@Override
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
	public void onSaveInstanceState(Bundle b) {
		((WebView) findViewById(R.id.webview_surveys)).saveState(b);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		((WebView) findViewById(R.id.webview_surveys)).restoreState(b);
	}
}