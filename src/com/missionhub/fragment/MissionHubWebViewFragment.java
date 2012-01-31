package com.missionhub.fragment;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebViewFragment;

/**
 * Allows for use of RoboGuice with ActionBarSherlock
 */
public class MissionHubWebViewFragment extends WebViewFragment {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembers(this);
	}

}