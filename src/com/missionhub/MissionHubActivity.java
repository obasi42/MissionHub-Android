package com.missionhub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * The main MissionHub Activity.
 */
public class MissionHubActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = MissionHubActivity.class.getSimpleName();

	/** login activity result constant */
	public final int RESULT_LOGIN_ACTIVITY = 1;

	//@InjectView(R.id.btn_about) Button btnAbout;
	//@InjectView(R.id.btn_login) Button btnLogin;

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// redirect to first screen if session can be resumed
		if (getSession() != null) {
			startMain();
		}

		setContentView(R.layout.activity_missionhub);
	}

	@Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_OK) {
			startMain();
		}
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_FIRST_USER) {
			clickLogin(null);
		}
	}
	
	/**
	 * Starts the main activity
	 */
	private void startMain() {
		startActivity(new Intent(this, PeopleMyActivity.class));
		finish();
	}

	/**
	 * Called when the about button is clicked
	 */
	public void clickAbout(final View v) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.missionhub_alert_about).setIcon(R.drawable.ic_dialog_info).setMessage(R.string.missionhub_alert_about_content)
				.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
					@Override public void onClick(final DialogInterface dialog, final int id) {
						// TODO:
						// getTracker().trackEvent(MissionHubActivity.class.getCanonicalName(),
						// "Click", "About", 0);
						final Uri uri = Uri.parse("http://missionhub.com?mobile=0");
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				}).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
					@Override public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Called when the login button is clicked
	 */
	public void clickLogin(final View v) {
		startActivityForResult(new Intent(MissionHubActivity.this, LoginActivity.class), RESULT_LOGIN_ACTIVITY);
	}
}