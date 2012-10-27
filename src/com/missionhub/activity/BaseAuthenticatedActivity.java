package com.missionhub.activity;

import android.content.Intent;
import android.os.Bundle;

import com.WazaBe.HoloEverywhere.widget.Toast;
import com.missionhub.application.Application;
import com.missionhub.application.Session.SessionInvalidTokenEvent;
import com.missionhub.application.Session.SessionInvalidatedEvent;

/** Listens for SesisonEvents and responds accordingly. */
public class BaseAuthenticatedActivity extends BaseActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Application.registerEventSubscriber(this, SessionInvalidatedEvent.class, SessionInvalidTokenEvent.class);
	}

	@Override
	public void onDestroy() {
		Application.unregisterEventSubscriber(this);
		super.onDestroy();
	}

	/**
	 * Responds to Session Invalidated Events
	 * 
	 * @param event
	 */
	public void onEventMainThread(final SessionInvalidatedEvent event) {
		Toast.makeText(this, "You have been logged out of MissionHub.", Toast.LENGTH_LONG).show();
		startInitActivity();
	}

	/**
	 * Responds to Session Invalid Token Events
	 * 
	 * @param event
	 */
	public void onEventMainThread(final SessionInvalidTokenEvent event) {
		Toast.makeText(this, "You MissionHub authentication token is invaild. Please login again.", Toast.LENGTH_LONG).show();
		startInitActivity();
	}

	/**
	 * Finishes the current activity and starts the init activity.
	 */
	private void startInitActivity() {
		final Intent intent = new Intent(getApplicationContext(), InitActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}