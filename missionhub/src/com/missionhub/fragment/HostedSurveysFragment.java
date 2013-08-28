package com.missionhub.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.application.Application;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.exception.WebViewException;
import com.missionhub.model.Survey;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.FragmentUtils;
import com.missionhub.util.ResourceUtils;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TaskUtils;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.List;
import java.util.concurrent.FutureTask;

@SuppressLint("SetJavaScriptEnabled")
public class HostedSurveysFragment extends HostedFragment implements AdapterView.OnItemClickListener {

    private ViewGroup mContainer;
    private ListView mIndex;
    private SurveyItemAdapter mIndexAdapter;
    private WebView mWebView;
    private Survey mCurrentSurvey;
    private SafeAsyncTask<Void> mUpdateTask;
    private SafeAsyncTask<List<Survey>> mBuildTask;
    private ViewGroup mIndexContainer;

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
                closeMenu();
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(final OnOrganizationChangedEvent event) {
        loadSurvey(null);
        buildIndex();
    }

    @Override
    public void onPrepareActionBar(ActionBar actionBar) {
        actionBar.setTitle("Surveys");
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        if (mCurrentSurvey == null) {
            MenuItem refresh = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, getString(R.string.action_refresh)).setIcon(R.drawable.ic_action_refresh_dark)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            if (hasProgress()) {
                refresh.setEnabled(false);
            } else {
                refresh.setEnabled(true);
            }

        } else {
            menu.add(Menu.NONE, R.id.action_index, Menu.NONE, getString(R.string.action_index)).setIcon(R.drawable.ic_action_list)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            menu.add(Menu.NONE, R.id.action_restart, Menu.NONE, getString(R.string.action_restart)).setIcon(R.drawable.ic_action_restart)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateIndex();
                return true;
            case R.id.action_restart:
                loadSurvey(mCurrentSurvey);
                return true;
            case R.id.action_index:
                loadSurvey(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveys, null);

        mContainer = (ViewGroup) view.findViewById(R.id.container);
        mIndexContainer = (ViewGroup) view.findViewById(R.id.index_container);
        mIndex = (ListView) view.findViewById(android.R.id.list);
        if (mIndexAdapter == null) {
            mIndexAdapter = new SurveyItemAdapter(inflater.getContext());
            buildIndex();
        } else {
            mIndexAdapter.setContext(inflater.getContext());
        }
        mIndex.setAdapter(mIndexAdapter);
        mIndex.setOnItemClickListener(this);

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

        if (survey == null) {
            showIndex();
            return;
        }

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
        task.execute();
    }

    public long getCurrentSurveyId() {
        if (mCurrentSurvey != null) {
            return mCurrentSurvey.getId();
        }
        return -1;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Survey survey = (Survey) adapterView.getItemAtPosition(position);
        loadSurvey(survey);
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
                showSurvey();
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

    public void updateIndex() {
        TaskUtils.cancel(mUpdateTask);

        mUpdateTask = new SafeAsyncTask<Void>() {
            public FutureTask<Void> task;

            @Override
            public Void call() throws Exception {
                task = Application.getSession().updateCurrentOrganization(true);
                task.get();
                return null;
            }

            @Override
            protected void onSuccess(Void _) throws Exception {
                buildIndex();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                TaskUtils.cancel(task);
                mUpdateTask = null;
                hideProgress("update");
            }
        };
        showProgress("update");
        mUpdateTask.execute();
    }

    public void buildIndex() {
        TaskUtils.cancel(mUpdateTask);

        mBuildTask = new SafeAsyncTask<List<Survey>>() {

            @Override
            public List<Survey> call() throws Exception {
                return Application.getSession().getOrganization().getSortedSurveys();
            }

            @Override
            protected void onSuccess(List<Survey> surveys) throws Exception {
                if (surveys.isEmpty()) {
                    // no surveys, go to contact list
                    Application.showToast(ResourceUtils.getString(R.string.surveys_no_surveys), Toast.LENGTH_LONG);
                    Application.postEvent(new ChangeHostFragmentEvent(HostedPeopleListFragment.class));
                } else {
                    if (mIndexAdapter == null) return;
                    synchronized (mIndexAdapter.getLock()) {
                        mIndexAdapter.setNotifyOnChange(false);
                        mIndexAdapter.clear();
                        mIndexAdapter.addAll(surveys);
                        mIndexAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mBuildTask = null;
                hideProgress("build");
            }
        };
        showProgress("build");
        mBuildTask.execute();
    }

    private void showIndex() {
        mIndexContainer.setVisibility(View.VISIBLE);
        mWebView.stopLoading();
        mWebView.loadData("", "text/plain; charset=utf-8", null);
        mWebView.setVisibility(View.GONE);
    }

    private void showSurvey() {
        mIndexContainer.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    private static class SurveyItemAdapter extends ObjectArrayAdapter<Survey> {
        public SurveyItemAdapter(Context context) {
            super(context, 1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Survey survey = getItem(position);
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_survey, parent, false);
                holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (StringUtils.isNotEmpty(survey.getTitle())) {
                holder.text1.setText(survey.getTitle());
            } else {
                holder.text1.setText("Survey: " + survey.getId());
            }
            return view;
        }

        private static class ViewHolder {
            TextView text1;
        }
    }

    /**
     * Chrome client that manages indeterminate progress visibility
     */
    private class ProgressChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(final WebView view, final int progress) {
            if (progress < 100) {
                if (getSupportActivity() != null) {
                    showProgress("webview");
                }
            }
            if (progress == 100) {
                if (getSupportActivity() != null) {
                    if (mCurrentSurvey != null && mWebView != null) {
                        showSurvey();
                    }
                    hideProgress("webview");
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
            showIndex();
            displayError(e);
        }
    }
}