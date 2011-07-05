package com.missionhub;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.User;

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

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	public static final String TAG2 = "API";
	final Handler mHandler = new Handler();
	private ProgressDialog mProgressDialog;
	
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	private RelativeLayout logoutBar;
	private TextView txtLogoutbarName;
	
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		logoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		txtLogoutbarName = (TextView) findViewById(R.id.txt_logoutbar_name);
		
<<<<<<< HEAD
		
		
		
		
		refreshView();
}

	public final int LOGIN_WINDOW_ACTIVITY = 0;
	public void clickLogin(View view) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, LOGIN_WINDOW_ACTIVITY);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_WINDOW_ACTIVITY && resultCode == RESULT_OK) {
			Log.i(TAG, data.getStringExtra("token"));
			refreshView();
		}
	}
=======
		User.token = getStoredToken();
		User.isLoggedIn = false;
		if (User.token != null && !User.token.equalsIgnoreCase("")) {
			JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
				@Override
				public void onStart() {
					//mProgressDialog
				}
				
				@Override
				public void onSuccess(JSONObject object) {
					
					Log.i(TAG2, "SUCCESS!");
					Log.i(TAG2, object.toString());
					
				}
				
				@Override
				public void onFailure(Throwable e) {
					Log.i(TAG2, e.toString());
					Log.i(TAG2, "stuff");
				}
				
				@Override
				public void onFinish() {
					Log.i(TAG2, "I'm finished!");
				}
			};
			Api.getPeople("me", responseHandler);
		} else {
			refreshView();
		}
		// FUTURE END OF MAIN
>>>>>>> 837a14104605a0446eae3d18bb30b3741954fccc

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

	public void clickContact(View view) {

	}

	public void testingApi() {
		
		LoginActivity.token = "43941a348dbb0b6c6e88763338baa5bedc08ddaa3c139106c700b8a45e1e8205";
		JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				Log.i(TAG2, "Starting");
			}
			
			@Override
			public void onSuccess(JSONArray object) {

				Log.i(TAG2, "SUCCESS!");
				Log.i(TAG2, object.toString());
				
			}
			
			@Override
			public void onFailure(Throwable e) {
				Log.i(TAG2, e.toString());
				Log.i(TAG2, "stuff");
			}
			
			@Override
			public void onFinish() {
				Log.i(TAG2, "I'm finished!");
			}
		};
		
		JsonHttpResponseHandler responseHandler2 = new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				Log.i(TAG2, "Starting");
			}
			
			@Override
			public void onSuccess(JSONArray object) {

				Log.i(TAG2, "SUCCESS!");
				Log.i(TAG2, object.toString());
				
			}
			
			@Override
			public void onFailure(Throwable e) {
				Log.i(TAG2, e.toString());
				Log.i(TAG2, "stuff");
			}
			
			@Override
			public void onFinish() {
				Log.i(TAG2, "I'm finished!");
			}
		};
		
//		Api.getPeople(1282204, responseHandler);
//		Api.getPeople("me", responseHandler);
//		
//		ArrayList<Integer> ids = new ArrayList<Integer>();
//		ids.add(1282204);
//		ids.add(244771);
//		Api.getPeople(ids, responseHandler);
//		
//		HashMap<String,String> options = new HashMap<String,String>();
//		options.put("limit","15");
//		options.put("start", "0");
//		options.put("assigned_to_id", "none");
//		options.put("sort", "time");
//		options.put("direction", "ASC");
//		
//		Api.getContactsList(options, responseHandler);
//		Api.getContacts(1282204, responseHandler);
		
		
		ArrayList<String> rejoicables = new ArrayList<String>();
		rejoicables.add("gospel_presentation");
		
		Api.postFollowupComment(1282204, 244771, "completed", "Hi from the Android API", responseHandler, rejoicables);
	}
	
	public void refreshView() {
		if (User.isLoggedIn) {
			loggedOut.setVisibility(View.GONE);
			loggedIn.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.VISIBLE);
			txtLogoutbarName.setText("My Name");
		} else {
			loggedIn.setVisibility(View.GONE);
			loggedOut.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.GONE);
		}
	}
<<<<<<< HEAD
=======

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

	public void clickContact(View view) {

	}
	
	public String getStoredToken() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("token", null);
	}
>>>>>>> 837a14104605a0446eae3d18bb30b3741954fccc
}
