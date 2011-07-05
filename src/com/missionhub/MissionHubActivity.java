package com.missionhub;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.missionhub.api.Api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	public static final String TAG2 = "API";
	final Handler mHandler = new Handler();
	private ProgressDialog mProgressDialog;
	
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		
		refreshView();

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
	
	public void refreshView() {
		if (LoginActivity.token != null && LoginActivity.isLoggedIn) {
			Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
			slideOut.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					loggedOut.setVisibility(View.GONE);
					Animation slideIn = AnimationUtils.loadAnimation(MissionHubActivity.this, R.anim.slide_in_bottom);
					loggedIn.startAnimation(slideIn);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationStart(Animation animation) {
					loggedIn.setVisibility(View.VISIBLE);
				}
			});
			loggedOut.startAnimation(slideOut);
		} else {
			Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
			slideOut.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					loggedIn.setVisibility(View.GONE);
					Animation slideIn = AnimationUtils.loadAnimation(MissionHubActivity.this, R.anim.slide_in_bottom);
					loggedOut.startAnimation(slideIn);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationStart(Animation animation) {
					loggedOut.setVisibility(View.VISIBLE);
				}
			});
			loggedIn.startAnimation(slideOut);
		}
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

	public void clickContact(View view) {

	}
}
