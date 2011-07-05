package com.missionhub;

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