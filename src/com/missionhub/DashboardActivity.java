package com.missionhub;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
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
			@Override public void onVerifyPass() {
				updateBottomBar();
			}
			
			@Override public void onLogout() {
				startActivity(new Intent(getApplicationContext(), MissionHubActivity.class));
				finish();
			}
		};
		sr.register(SessionBroadcast.NOTIFY_VERIFY_PASS, SessionBroadcast.NOTIFY_LOGOUT);

		updateBottomBar();
	}

	@Override public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(R.string.action_profile).setOnMenuItemClickListener(new ProfileOnMenuItemClickListener()).setIcon(R.drawable.ic_action_user).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(R.string.action_logout).setOnMenuItemClickListener(new LogoutOnMenuItemClickListener()).setIcon(R.drawable.ic_action_power).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}
	
	private class ProfileOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override public boolean onMenuItemClick(MenuItem item) {
			Toast.makeText(getBaseContext(), "Click Profile", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	private class LogoutOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override public boolean onMenuItemClick(MenuItem item) {
			getSession().logout();
			return false;
		}
	}

	@Override public void onResume() {
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