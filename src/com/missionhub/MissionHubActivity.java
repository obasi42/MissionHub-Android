package com.missionhub;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.missionhub.config.Preferences;
import com.missionhub.helper.Flurry;

public class MissionHubActivity extends Activity {

	/* Logging Tag */
	public static final String TAG = MissionHubActivity.class.getName();

	/* Views */
	private RelativeLayout mLoggedOut;
	private RelativeLayout mLoggedIn;
	private TextView mOrganization;
	private TextView mName;

	/* Activity Result Constants */
	public final int RESULT_LOGIN_ACTIVITY = 0;
	public final int RESULT_PROFILE_ACTIVITY = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActionBarContentView(R.layout.main);
		getActionBar().setType(ActionBar.Type.Dashboard);
		getActionBar().setVisibility(View.GONE);

		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(R.drawable.action_bar_user)
				.setContentDescription(R.string.action_bar_profile), R.id.action_bar_profile);

		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(R.drawable.action_bar_logout)
				.setContentDescription(R.string.action_bar_logout), R.id.action_bar_logout);

		
		Preferences.setLastRunVersion(this, ((Application) getApplicationContext()).getVersion());

		mLoggedIn = (RelativeLayout) findViewById(R.id.loggedin);
		mLoggedOut = (RelativeLayout) findViewById(R.id.loggedout);
		mName = (TextView) findViewById(R.id.name);
		mOrganization = (TextView) findViewById(R.id.organization);
				
		getApp().registerHandler(appHandler);
		
		if (!getUser().isLoggedIn()) {
			getUser().refresh(this);
		}
		
		Flurry.pageView(this, "Main");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_OK) {
			getUser();
		}
		if (requestCode == RESULT_PROFILE_ACTIVITY && resultCode == RESULT_OK && data.hasExtra("logout")) {
			logout();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}

	public void clickAbout(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_learn_more).setIcon(R.drawable.ic_dialog_info).setMessage(R.string.alert_learn_more_msg)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Flurry.event(MissionHubActivity.this, "Main.AboutLink");
						Uri uri = Uri.parse("http://missionhub.com?mobile=0");
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				}).setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void clickContacts(View view) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}

	public void clickLogin(View view) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, RESULT_LOGIN_ACTIVITY);
	}

	public void clickSurveys(View view) {
		Intent i = new Intent(this, SurveysActivity.class);
		startActivity(i);
	}

	public void logout() {
		getUser().logout();
		Flurry.event(this, "Main.Logout");
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_profile:
			Intent i = new Intent(this, ProfileActivity.class);
			startActivityForResult(i, RESULT_PROFILE_ACTIVITY);
			break;
		case R.id.action_bar_logout:
			logout();
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	/* Handles messages from Application */
	private Handler appHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			Log.d(TAG, "GOT MESSAGE: " + msg);
			
			switch (msg.what) {
			case Application.MESSAGE_USER_LOGGED_IN:
			case Application.MESSAGE_USER_LOGGED_OUT:
				refreshView();
				break;
			}
		}
	};
	
	public void refreshView() {
		if (getUser().isLoggedIn()) {
			getActionBar().setVisibility(View.VISIBLE);
			mLoggedOut.setVisibility(View.GONE);
			mLoggedIn.setVisibility(View.VISIBLE);
			mName.setText(getUser().getPerson().getName());
			mOrganization.setText(getUser().getOrganizations().get(getUser().getOrganizationID()).getName());
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		} else{
			getActionBar().setVisibility(View.GONE);
			mLoggedIn.setVisibility(View.GONE);
			mLoggedOut.setVisibility(View.VISIBLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
}
