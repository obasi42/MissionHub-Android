package com.missionhub.activity;

import android.os.Bundle;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.missionhub.util.EasyTracker;

/**
 * The base missionhub activity. Extends RoboSherlockFragmentActivity to provide Roboguice and ActionBarSherlock.
 * Manages the life of the EasyTracker.
 */
public abstract class BaseActivity extends RoboSherlockFragmentActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getTracker().setContext(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getTracker().trackActivityStart(this);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		final Object o = super.onRetainCustomNonConfigurationInstance();
		EasyTracker.getTracker().trackActivityRetainNonConfigurationInstance();
		return o;
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getTracker().trackActivityStop(this);
	}
}