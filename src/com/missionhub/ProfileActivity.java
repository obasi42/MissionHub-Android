package com.missionhub;

import roboguice.inject.InjectView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Profile Activity.
 */
public class ProfileActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = ProfileActivity.class.getSimpleName();

	@InjectView(R.id.version) TextView mVersion;
	
	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		try {
			setTitle(getSession().getUser().getPerson().getName());
			if (getSession().getUser().getPerson().getGender().equalsIgnoreCase("female")) {
				getSupportActionBar().setIcon(R.drawable.ic_female_user_info);
			} else {
				getSupportActionBar().setIcon(R.drawable.ic_male_user_info);
			}
		} catch (Exception e) {}
		
		try {
			mVersion.setText(getString(R.string.profile_version) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {}
	}
	
	@Override public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(R.string.action_refresh).setOnMenuItemClickListener(new RefreshOnMenuItemClickListener()).setIcon(R.drawable.ic_action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}
	
	private class RefreshOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override public boolean onMenuItemClick(MenuItem item) {
			Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_LONG).show();
			//TODO:
			return false;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	Intent intent = new Intent(this, DashboardActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}