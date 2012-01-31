package com.missionhub.fragment;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Allows for use of RoboGuice with ActionBarSherlock
 */
public class MissionHubDialogFragment extends DialogFragment {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectViewMembers(this);
	}

}