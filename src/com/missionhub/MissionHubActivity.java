package com.missionhub;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GContact;
import com.missionhub.api.GError;
import com.missionhub.api.GPerson;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.DisplayError;

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	
	final Handler mHandler = new Handler();
	private ProgressDialog mProgressDialog;
	
	private ImageView logo;
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	private RelativeLayout logoutBar;
	private TextView txtLogoutbarName;
	
	public final int LOGIN_WINDOW_ACTIVITY = 0;
	public final int PROFILE_ACTIVITY = 1;
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	public static String APP_VERSION;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			User.setAppVersion(String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
		} catch (NameNotFoundException e) {}
		User.setFromBundle(savedInstanceState, this);
		
		logo = (ImageView) findViewById(R.id.img_logo);
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		logoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		txtLogoutbarName = (TextView) findViewById(R.id.txt_logoutbar_name);
		
		if (!checkToken()) {
			refreshView();
		}
		
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Main");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		if (User.isLoggedIn())
			b.putAll(User.getAsBundle());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		User.setFromBundle(b, this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_WINDOW_ACTIVITY && resultCode == RESULT_OK) {
			checkToken();
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
	   User.setFlurryUser();
	   FlurryAgent.onStartSession(this, Config.flurryKey);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   User.setFlurryUser();
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
	
	public void clickLogout(View view) {
		logout();
	}
	
	public void logout() {
		User.logout();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("token");
		editor.remove("orgID");
		editor.commit();
		try {
			FlurryAgent.onEvent("Main.Logout");
			User.setFlurryUser();
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
		if (User.isLoggedIn()) {
			Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_down);
			a.setFillBefore(true);
			a.setFillEnabled(true);
			a.setFillAfter(true);
			logo.startAnimation(a);
			
			loggedOut.setVisibility(View.GONE);
			loggedIn.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.VISIBLE);
			txtLogoutbarName.setText(User.getContact().getPerson().getName());
		} else {
			Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_up);
			a.setFillEnabled(true);
			a.setFillBefore(true);
			a.setFillAfter(true);
			logo.startAnimation(a);
			loggedIn.setVisibility(View.GONE);
			loggedOut.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.GONE);
		}
		User.setFlurryUser();
	}
	
	/**
	 * Returns a users stored access token
	 * @return stored access token
	 */
	public String getStoredToken() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("token", null);
	}
	
	/**
	 * Check Stored Access Token
	 * @return true if has stored token
	 */
	private boolean checkToken() {
		User.setToken(getStoredToken());
		User.setLoggedIn(false);
		if (User.getToken() != null && !User.getToken().equalsIgnoreCase("")) {
			AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					mProgressDialog = ProgressDialog.show(MissionHubActivity.this, "", MissionHubActivity.this.getString(R.string.alert_logging_in), true);
				}
				@Override
				public void onSuccess(String response) {
					Gson gson = new Gson();
					try{
						GError error = gson.fromJson(response, GError.class);
						onFailure(new MHError(error));
					} catch (Exception out){
						try {
							GPerson[] people = gson.fromJson(response, GPerson[].class);
							if (people.length > 0) {
								GContact contact = new GContact();
								contact.setPerson(people[0]);
								User.setContact(contact);
								User.setOrgID(User.getOrgIDPreference(MissionHubActivity.this));
								User.setLoggedIn(true);
								refreshView();
							}
						} catch(Exception e) {
							onFailure(e);
						}
					}
				}
				@Override
				public void onFailure(Throwable e) {
					Log.e(TAG, "Auto Login Failed", e);
					AlertDialog ad = DisplayError.display(MissionHubActivity.this, e);
					ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							checkToken();
						}
					});
					ad.show();
					MHError.onFlurryError(e, "Main.checkToken");
				}
				@Override
				public void onFinish() {
					mProgressDialog.dismiss();
				}
			};
			Api.getPeople("me", responseHandler);
			return true;
		}
		return false;
	}
}
