package com.missionhub;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.TextView;

import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.broadcast.SessionReceiver;

public class DashboardActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = DashboardActivity.class.getSimpleName();

	@InjectView(R.id.name) TextView mName;
	@InjectView(R.id.organization) TextView mOrganization;

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);

		final SessionReceiver sr = new SessionReceiver(getApplicationContext()) {
			@Override
			public void onVerifyPass() {
				updateBottomBar();
			}
		};
		sr.register(SessionBroadcast.NOTIFY_VERIFY_PASS);
		
		updateBottomBar();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateBottomBar();
	}

	public void updateBottomBar() {
		try {
			mName.setText(getSession().getUser().getPerson().getName());
		} catch (final Exception e) {}
		
		try {
			mOrganization.setText(getDbSession().getOrganizationDao().load(getSession().getOrganizationId()).getName());
		} catch (final Exception e) {}
	}
}