package com.missionhub.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.missionhub.activity.HostActivity;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.util.FragmentUtils;

public abstract class HostedFragment extends BaseFragment {

    private ChangeHostFragmentEvent.OnFragmentChangedCallback mCallback;

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
}