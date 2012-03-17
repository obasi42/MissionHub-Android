package com.missionhub;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
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

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setLogo(R.drawable.logo);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		final SessionReceiver sr = new SessionReceiver(getApplicationContext()) {
			@Override public void onUpdateOrganizationsStart() {
				// showPreparing("org");
			}

			@Override public void onUpdateOrganizationsSuccess() {
				// hidePreparing("org");
			}

			@Override public void onUpdatePersonStart() {
				// showPreparing("person");
			}

			@Override public void onUpdatePersonSuccess() {
				// hidePreparing("person");
				updateBottomBar();
			}

			@Override public void onLogout() {
				final Intent intent = new Intent(getApplicationContext(), MissionHubActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(new Intent(getApplicationContext(), MissionHubActivity.class));
				finish();
			}
		};
		sr.register();

		updateBottomBar();
	}

	//
	// List<String> preparing = new ArrayList<String>();
	// ProgressDialog dialog;
	//
	// public void showPreparing(String id) {
	// if (dialog == null || !dialog.isShowing()) {
	// dialog = ProgressDialog.show(this, "",
	// "Preparing MissionHub for first use...", true);
	// dialog.setCancelable(false);
	// }
	// preparing.add(id);
	// }
	//
	// public void hidePreparing(String id) {
	// preparing.remove(id);
	// if (preparing.isEmpty()) {
	// if (dialog != null && dialog.isShowing()) {
	// dialog.hide();
	// }
	// }
	// }

	@Override public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(R.string.action_profile).setOnMenuItemClickListener(new ProfileOnMenuItemClickListener()).setIcon(R.drawable.ic_action_contact)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(R.string.action_logout).setOnMenuItemClickListener(new LogoutOnMenuItemClickListener()).setIcon(R.drawable.ic_action_logout)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	private class ProfileOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override public boolean onMenuItemClick(final MenuItem item) {
			startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
			return false;
		}
	}

	private class LogoutOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override public boolean onMenuItemClick(final MenuItem item) {
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

	public void clickPeople(final View v) {
		Toast.makeText(this, "People", Toast.LENGTH_SHORT).show();
	}

	public void clickGroups(final View v) {
		Toast.makeText(this, "Groups", Toast.LENGTH_SHORT).show();
	}

	public void clickSurveys(final View v) {
		Toast.makeText(this, "Surveys", Toast.LENGTH_SHORT).show();
	}
}