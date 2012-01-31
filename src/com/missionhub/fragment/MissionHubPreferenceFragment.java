package com.missionhub.fragment;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

/**
 * Allows for use of RoboGuice with ActionBarSherlock
 */
public class MissionHubPreferenceFragment extends PreferenceFragment {

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