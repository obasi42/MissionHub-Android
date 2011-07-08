package com.missionhub;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GError;
import com.missionhub.api.GPerson;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.DisplayError;

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	public static final String TAG2 = "API";
	final Handler mHandler = new Handler();
	private ProgressDialog mProgressDialog;
	
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	private RelativeLayout logoutBar;
	private TextView txtLogoutbarName;
	
	public final int LOGIN_WINDOW_ACTIVITY = 0;
	public final int PROFILE_ACTIVITY = 1;
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		User.setFromBundle(savedInstanceState, this);
		
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		logoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		txtLogoutbarName = (TextView) findViewById(R.id.txt_logoutbar_name);
		
		if (!checkToken()) {
			refreshView();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putAll(User.getAsBundle());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		User.setFromBundle(b, this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_WINDOW_ACTIVITY && resultCode == RESULT_OK) {
			Log.i(TAG, data.getStringExtra("token"));
			refreshView();
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

	public void clickAbout(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_learn_more)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.alert_learn_more_msg)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
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
		User.setToken(null);
		User.setLoggedIn(false);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("token");
		editor.commit();
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
			loggedOut.setVisibility(View.GONE);
			loggedIn.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.VISIBLE);
			txtLogoutbarName.setText(User.getContact().getPerson().getName());
		} else {
			loggedIn.setVisibility(View.GONE);
			loggedOut.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Returns a users stored access token
	 * @return stored access token
	 */
	public String getStoredToken() {
		return "c1d65450bcb7c26efcedcd41497cae4b66e2194388c8c124914499b2094ebbed";
		//SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		//return settings.getString("token", null);
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
