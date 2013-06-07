package com.missionhub.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.missionhub.activity.HostActivity;
import com.missionhub.util.U;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

public abstract class HostedFragment extends BaseFragment {

    public HostedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
        setHasOptionsMenu(true);
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