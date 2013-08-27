package com.missionhub.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.missionhub.activity.HostActivity;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.util.FragmentUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class HostedFragment extends BaseFragment {

    private ChangeHostFragmentEvent.OnFragmentChangedCallback mCallback;
    private Set<String> mProgress = Collections.synchronizedSet(new HashSet<String>());

    public HostedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.retainInstance(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getHost()._setCurrentFragment(this);
        if (mCallback != null) {
            mCallback.onFragmentChanged(this);
            mCallback = null;
        }
        getHost().setProgress(this, mProgress);
    }

    @Override
    public void onPause() {
        getHost().removeProgress(this);
        super.onPause();
    }

    public void setOnFragmentChangedCallback(ChangeHostFragmentEvent.OnFragmentChangedCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getSupportActionBar() != null) {
            onPrepareActionBar(getSupportActionBar());
        }
    }

    public void onPrepareActionBar(ActionBar actionBar) {
    }

    public void onPrepareMenuOptionsMenu(Menu menu) {
    }

    public void onPrepareHostOptionsMenu(Menu menu) {
    }

    @Override
    final public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!getHost().isMenuOpen()) {
            onPrepareMenuOptionsMenu(menu);
        } else {
            onPrepareHostOptionsMenu(menu);
        }
    }

    public HostActivity getHost() {
        return (HostActivity) getSupportActivity();
    }

    public void showProgress(String task) {
        mProgress.add(task);
        if (getHost() != null) {
            getHost().setProgress(this, mProgress);
        }
    }

    public void hideProgress(String task) {
        mProgress.remove(task);
        if (getHost() != null) {
            getHost().setProgress(this, mProgress);
        }
    }

    boolean hasProgress() {
        return !mProgress.isEmpty();
    }

    public void onProgressStateChanged(boolean progress) {
    }

    public void closeMenu() {
        if (getHost() != null) {
            getHost().closeMenu();
        }
    }
}