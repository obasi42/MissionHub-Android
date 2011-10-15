package com.missionhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.missionhub.auth.Auth;
import com.missionhub.auth.User;
import com.missionhub.config.Preferences;
import com.missionhub.helpers.Flurry;

public class MissionHubActivity extends Activity {
	
	/* Logging Tag */
	public static final String TAG = MissionHubActivity.class.getName();
	
	/* Views */
	private LinearLayout mLoggedOut;
	private LinearLayout mLoggedIn;
	private RelativeLayout mLogoutBar;
	private TextView mName;
	
	/* Activity Result Constants */
	public final int RESULT_LOGIN_ACTIVITY = 0;
	public final int RESULT_PROFILE_ACTIVITY = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Application.initVersion(this);
		Preferences.setLastRunVersion(this, Application.getVersion());
		Application.restoreApplicationState(savedInstanceState);
		
		mLogoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		mLoggedIn = (LinearLayout) findViewById(R.id.loggedin);
		mLoggedOut = (LinearLayout) findViewById(R.id.loggedout);
		
		mName = (TextView) findViewById(R.id.name);
		
		Auth.checkToken(this, checkTokenHandler);
		
		Flurry.pageView("Main");
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putAll(Application.saveApplicationState(b));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		Application.restoreApplicationState(b);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_OK) {
			Auth.checkToken(this, checkTokenHandler);
		}
		if(requestCode == RESULT_PROFILE_ACTIVITY && resultCode == RESULT_OK && data.hasExtra("logout")) {
			logout();
		}
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   Flurry.startSession(this);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   Flurry.endSession(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}

	public void clickAbout(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_learn_more)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.alert_learn_more_msg)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Flurry.event("Main.AboutLink");
						Uri uri = Uri.parse("http://missionhub.com?mobile=0");
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				})
				.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
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
	
	public void clickLogout(View view) {
		logout();
	}
	
	public void clickProfile(View view) {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivityForResult(i, RESULT_PROFILE_ACTIVITY);
	}	
	
	public void clickSurveys(View view) {
		Intent i = new Intent(this, SurveysActivity.class);
		startActivity(i);
	}
	
	public void logout() {
		Auth.logout(getBaseContext());
		Flurry.event("Main.Logout");
		refreshView();
	}
	
	/* Handles messages from Auth.checkToken */
	private Handler checkTokenHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
				case Auth.SUCCESS: refreshView(); break;
				case Auth.RETRY: Auth.checkToken(MissionHubActivity.this, checkTokenHandler); break;
			}
		}
	};
	
	/**
	 * Refreshes the main view based on user's logged in status
	 */
	public void refreshView() {
		if (Auth.isLoggedIn()) {
			mLoggedOut.setVisibility(View.GONE);
			mLoggedIn.setVisibility(View.VISIBLE);
			mLogoutBar.setVisibility(View.VISIBLE);
			mName.setText(User.getContact().getPerson().getName());
		} else {
			mLoggedIn.setVisibility(View.GONE);
			mLoggedOut.setVisibility(View.VISIBLE);
			mLogoutBar.setVisibility(View.GONE);
		}
	}
}
