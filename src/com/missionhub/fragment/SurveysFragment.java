package com.missionhub.fragment;

import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.application.Application;
import com.missionhub.application.Session.SessionOrganizationIdChanged;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.exception.WebViewException;
import com.missionhub.util.U;

@SuppressLint("SetJavaScriptEnabled")
public class SurveysFragment extends MainFragment {

	@InjectView(R.id.container) ViewGroup mContainer;

	private WebView mWebView;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		menu.add(Menu.NONE, R.id.action_restart, Menu.NONE, getString(R.string.action_restart)).setIcon(R.drawable.ic_action_restart)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == R.id.action_restart) {
			goInitialUrl();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_surveys, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mWebView == null) {
			mWebView = new WebView(mContainer.getContext());
			mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setAppCacheEnabled(true);
			mWebView.getSettings().setLoadsImagesAutomatically(true);
			mWebView.getSettings().setSupportZoom(false);
			mWebView.getSettings().setSavePassword(false);
			mWebView.getSettings().setSaveFormData(false);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			mWebView.setScrollbarFadingEnabled(true);
			mWebView.setWebChromeClient(new ProgressChromeClient());
			mWebView.setWebViewClient(new InternalWebViewClient());
			mWebView.setVisibility(View.GONE);
			goInitialUrl();
		}

		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		}

		mContainer.addView(mWebView);

		Application.registerEventSubscriber(this, SessionOrganizationIdChanged.class);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		U.resetActionBar(getSherlockActivity());
		getSherlockActivity().getSupportActionBar().setTitle("Surveys");
	}

	public void goInitialUrl() {
		final SafeAsyncTask<String> task = new SafeAsyncTask<String>() {
			@Override
			public String call() throws Exception {
				return Api.getSurveyUrl().get();
			}

			@Override
			protected void onSuccess(final String result) {
				Log.e("URL", result);
				mWebView.loadUrl(result);
			}

			@Override
			protected void onException(final Exception e) {
				displayError(e);
			}
		};
		Application.getExecutor().execute(task.future());
	}

	/**
	 * Chrome client that manages indeterminate progress visibility
	 */
	private class ProgressChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(final WebView view, final int progress) {
			if (progress < 100) {
				if (getSherlockActivity() != null) {
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
				}
			}
			if (progress == 100) {
				if (getSherlockActivity() != null) {
					mWebView.setVisibility(View.VISIBLE);
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
			}
		}
	}

	private class InternalWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
			onError(new WebViewException(errorCode, description, failingUrl));
		}

		public void onError(final Exception e) {
			mWebView.stopLoading();
			mWebView.loadData("", "text/plain; charset=UTF-8", null);
			mWebView.setVisibility(View.GONE);

			displayError(e);
		}
	}

	/** displays an error dialog */
	private void displayError(final Exception e) {
		final ExceptionHelper eh = new ExceptionHelper(getActivity(), e);
		eh.setPositiveButton(new DialogButton() {
			@Override
			public String getTitle() {
				return "Retry";
			}

			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				mWebView.setVisibility(View.VISIBLE);
				mWebView.reload();
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
			}
		});
		eh.show();
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		mWebView.saveState(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		Application.unregisterEventSubscriber(this);
		// remove the webview from the container
		// purposely leak the context by storing mWebView
		// webviews are evil.
		mContainer.removeView(mWebView);

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// make sure we get rid of this reference
		// we are don't purposely leaking views
		mWebView = null;

		super.onDestroy();
	}

	public void onEventMainThread(final SessionOrganizationIdChanged event) {
		goInitialUrl();
	}

}