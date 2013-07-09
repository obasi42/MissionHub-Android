package com.missionhub.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Configuration;
import com.missionhub.event.OnHostedListOptionsChangedEvent;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.exception.WebViewException;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.model.Survey;
import com.missionhub.util.FragmentUtils;
import com.missionhub.util.SafeAsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.holoeverywhere.LayoutInflater;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SetJavaScriptEnabled")
public class HostedSurveysFragment extends HostedFragment {

    private ViewGroup mContainer;
    private WebView mWebView;
    private Survey mCurrentSurvey;

    public HostedSurveysFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        FragmentUtils.retainInstance(this);
        setHasOptionsMenu(true);

        Application.registerEventSubscriber(this, OnSidebarItemClickedEvent.class, OnOrganizationChangedEvent.class);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OnSidebarItemClickedEvent event) {
        if (mWebView == null) return;

        Object item = event.getItem();
        if (item instanceof Survey) {
            if (item != mCurrentSurvey) {
                loadSurvey((Survey) item);
            }
        }
    }

    @Override
    public void onPrepareActionBar(ActionBar actionBar) {
        actionBar.setTitle("Surveys");
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, R.id.action_restart, Menu.NONE, getString(R.string.action_restart)).setIcon(R.drawable.ic_action_restart)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_restart) {
            loadSurvey(mCurrentSurvey);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveys, null);

        mContainer = (ViewGroup) view.findViewById(R.id.container);

        if (mWebView == null) {
            mWebView = new WebView(mContainer.getContext());
            mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
            loadSurvey(null);
        }

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        }

        mContainer.addView(mWebView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Application.trackView("Surveys");
    }

    @Override
    public void onResume() {
        super.onResume();

        getHost().setTabletSidebarStatic(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        getHost().setTabletSidebarStatic(true);
    }

    public void loadSurvey(final Survey survey) {
        mCurrentSurvey = survey;
        getHost().getSidebarFragment().update();

        if (mWebView == null) return;
        final SafeAsyncTask<String> task = new SafeAsyncTask<String>() {
            @Override
            public String call() throws Exception {
                return Api.getSurveyUrl(survey);
            }

            @Override
            protected void onSuccess(final String result) {
                mWebView.loadUrl(result);
            }

            @Override
            protected void onException(final Exception e) {
                displayError(e);
            }
        };
        Application.getExecutor().execute(task.future());
    }

    public long getCurrentSurveyId() {
        if (mCurrentSurvey != null) {
            return mCurrentSurvey.getId();
        }
        return -1;
    }

    /**
     * Chrome client that manages indeterminate progress visibility
     */
    private class ProgressChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(final WebView view, final int progress) {
            if (progress < 100) {
                if (getSupportActivity() != null) {
                    getSupportActivity().setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
                }
            }
            if (progress == 100) {
                if (getSupportActivity() != null) {
                    if (mWebView != null) {
                        mWebView.setVisibility(View.VISIBLE);
                    }
                    getSupportActivity().setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
                }
            }
        }
    }

    private class InternalWebViewClient extends WebViewClient {

        private final Pattern mSurveyPathIdPattern = Pattern.compile("(.*?)/(surveys|s)/(\\d+)(.*)");
        private final Pattern mSurveyParameterIdPattern = Pattern.compile("(.*?)survey_id=(\\d+)(.*)");

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.toLowerCase().contains(Configuration.getSurveyUrl().toLowerCase())) {
                long surveyId = -1;
                URI uri = URI.create(url);

                Matcher matcher = mSurveyPathIdPattern.matcher(uri.getPath());
                if (matcher.matches() && NumberUtils.isNumber(matcher.group(3))) {
                    surveyId = Long.parseLong(matcher.group(3));
                }

                if (surveyId == -1) {
                    matcher = mSurveyParameterIdPattern.matcher(uri.getQuery());
                    if (matcher.matches() && NumberUtils.isNumber(matcher.group(2))) {
                        surveyId = Long.parseLong(matcher.group(2));
                    }
                }

                Survey survey;
                if (surveyId >= 0) {
                    survey = Application.getDb().getSurveyDao().load(surveyId);
                } else {
                    survey = null;
                }

                if (mCurrentSurvey != survey) {
                    mCurrentSurvey = survey;
                    getHost().getSidebarFragment().update();
                }
            }
        }

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

    /**
     * displays an error dialog
     */
    private void displayError(final Exception e) {
        final ExceptionHelper eh = new ExceptionHelper(getSupportActivity(), e);
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

    @SuppressWarnings("unused")
    public void onEvent(final OnOrganizationChangedEvent event) {
        loadSurvey(null);
    }

}