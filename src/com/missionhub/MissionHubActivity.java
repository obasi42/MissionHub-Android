package com.missionhub;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.missionhub.auth.Auth;
import com.missionhub.auth.User;
import com.missionhub.config.Config;

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	
	final Handler mHandler = new Handler();
	
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	private RelativeLayout logoutBar;
	private TextView txtLogoutbarName;
	
	public final int LOGIN_WINDOW_ACTIVITY = 0;
	public final int PROFILE_ACTIVITY = 1;
	
	public static String APP_VERSION;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			Application.setVersion(String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
		} catch (NameNotFoundException e) {}
		Application.restoreApplicationState(savedInstanceState);
		
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		logoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		txtLogoutbarName = (TextView) findViewById(R.id.txt_logoutbar_name);
		
		if (!Auth.checkToken(this, checkTokenHandler)) {
			refreshView();
		}
		
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Main");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
	}
	
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
		if (requestCode == LOGIN_WINDOW_ACTIVITY && resultCode == RESULT_OK) {
			Auth.checkToken(this, checkTokenHandler);
		}
		if(requestCode == PROFILE_ACTIVITY && resultCode == RESULT_OK && data.hasExtra("logout")) {
			logout();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   User.initFlurryUser();
	   FlurryAgent.onStartSession(this, Config.flurryKey);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   User.initFlurryUser();
	   FlurryAgent.onEndSession(this);
	}

	public void clickAbout(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_learn_more)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.alert_learn_more_msg)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							FlurryAgent.onEvent("Main.AboutLink");
						} catch (Exception e) {}
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

	public void clickLogin(View view) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, LOGIN_WINDOW_ACTIVITY);
	}

	public void clickContacts(View view) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}
	
	public void clickSurveys(View view) {
		Intent i = new Intent(this, SurveysActivity.class);
		startActivity(i);
	}
	
	public void clickLogout(View view) {
		logout();
	}
	
	public void logout() {
		Auth.logout(getBaseContext());
		try {
			User.initFlurryUser();
			FlurryAgent.onEvent("Main.Logout");
		} catch (Exception e) {}
		refreshView();
	}
	
	public void clickProfile(View view) {
		Intent i = new Intent(this, ProfileActivity.class);
		startActivityForResult(i, PROFILE_ACTIVITY);
	}	
	
	/**
	 * Refreshes the main view based on user's logged in status
	 */
	public void refreshView() {
		if (Auth.isLoggedIn()) {
			loggedOut.setVisibility(View.GONE);
			loggedIn.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.VISIBLE);
			txtLogoutbarName.setText(User.getContact().getPerson().getName());
		} else {
			loggedIn.setVisibility(View.GONE);
			loggedOut.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.GONE);
		}
		User.initFlurryUser();
	}
}
