package com.missionhub.fragment;

import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.app.Activity;
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
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.application.Application;

public class SurveysFragment extends BaseFragment {

	@InjectView(R.id.container) ViewGroup mContainer;

	private WebView mWebView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		getSherlockActivity().getSupportActionBar().setTitle("Surveys");
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		setHasOptionsMenu(true);
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
			goInitialUrl();
		}

		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		}

		mContainer.addView(mWebView);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
	}
	
	public void goInitialUrl() {
		final RoboAsyncTask<String> task = new RoboAsyncTask<String>(Application.getContext()) {
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
				// do this in the UI thread if call() threw an exception
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
					getSherlockActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				}
			}
		}
	}

	private class InternalWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {

		}
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		mWebView.saveState(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
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

}