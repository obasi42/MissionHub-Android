package com.missionhub.activity;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.WazaBe.HoloEverywhere.widget.ProgressBar;
import com.WazaBe.HoloEverywhere.widget.TextView;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.Session.SessionRefreshEvent;
import com.missionhub.authenticator.AuthenticatorActivity;

/**
 * The initial MissionHub activity.
 * 
 * Restores the user session and starts the HostActivity or prompts the user for login info.
 */
@ContentView(R.layout.activity_init)
public class InitActivity extends BaseActivity {

	@InjectView(R.id.loading) ProgressBar mProgress;
	@InjectView(R.id.logo) ImageView mLogo;
	@InjectView(R.id.status) TextView mStatus;
	@InjectView(R.id.version) TextView mVersion;

	/** request code for authentication */
	private static final int REQUEST_AUTHENTICATION = 0;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Application.registerEventSubscriber(this, SessionRefreshEvent.class);

		if (Session.getInstance().resumeSession()) {
			Session.getInstance().refreshSession();
		} else {
			//final Intent intent = new Intent(this, AuthenticatorActivity.class);
			//startActivityForResult(intent, REQUEST_AUTHENTICATION);
		}
	}

	public void onEventMainThread(final SessionRefreshEvent event) {
		Log.e("EVENT", event.getClass().getSimpleName());
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// TODO: handle result
	}

	@Override
	public void onDestroy() {
		Application.unregisterEventSubscriber(this);
		super.onDestroy();
	}
}
