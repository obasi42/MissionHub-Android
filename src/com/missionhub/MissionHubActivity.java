package com.missionhub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.broadcast.SessionReceiver;

/**
 * The main MissionHub Activity.
 */
public class MissionHubActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = MissionHubActivity.class.getSimpleName();

	/** login activity result constant */
	public final int RESULT_LOGIN_ACTIVITY = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getSession().isValid()) {
			startMain();
		} else if (getSession().isUpdating()) {
			// TODO: display blocking dialog
			Toast.makeText(this, "Updating", Toast.LENGTH_LONG).show();
		}

		final SessionReceiver sr = new SessionReceiver(this) {
			@Override
			public void onLogin(final String accessToken) {
				Log.d(TAG, "Logged In");
				startMain();
			}
		};
		sr.register(SessionBroadcast.NOTIFY_LOGIN);

		setContentView(R.layout.activity_missionhub);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == LoginActivity.RESULT_OK) {
			final SessionReceiver sr = new SessionReceiver(this) {
				@Override
				public void onUpdateSuccess() {
					SessionBroadcast.broadcastLogin(context, getSession().getAccessToken());
					unregister();
				}
				
				@Override
				public void onUpdateError(Throwable t) {
					// TODO: show error
					Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
					unregister();
				}
			};
			sr.register(SessionBroadcast.NOTIFY_LOGIN);
			getSession().update();
			
			// TODO: block interface
		}
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == LoginActivity.RESULT_RETRY) {
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
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						// TODO:
						// getTracker().trackEvent(MissionHubActivity.class.getCanonicalName(),
						// "Click", "About", 0);
						final Uri uri = Uri.parse("http://missionhub.com?mobile=0");
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				}).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
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