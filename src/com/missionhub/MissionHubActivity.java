package com.missionhub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.broadcast.SessionReceiver;
import com.missionhub.ui.DisplayError;

/**
 * The main MissionHub Activity.
 */
public class MissionHubActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = MissionHubActivity.class.getSimpleName();

	/** login activity result constant */
	public final int RESULT_LOGIN_ACTIVITY = 1;

	/** blocking progress dialog */
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getSession().isValid()) {
			startMain();
		} else if (getSession().isUpdating()) {
			blockingUpdate();
		}

		final SessionReceiver sr = new SessionReceiver(this) {
			@Override
			public void onLogin(final String accessToken) {
				startMain();
			}
		};
		sr.register(SessionBroadcast.NOTIFY_LOGIN);

		setContentView(R.layout.activity_missionhub);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == Activity.RESULT_OK) {
			blockingUpdate();
			Session.resumeSession(getMHApplication());
		}
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == LoginActivity.RESULT_RETRY) {
			clickLogin(null);
		}
	}

	@Override
	public void onStop() {
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onStop();
	}

	private void blockingUpdate() {

		dialog = ProgressDialog.show(this, getString(R.string.progress_loading), getString(R.string.progress_logging_in), true, false);

		final SessionReceiver sr = new SessionReceiver(this) {

			@Override
			public void onUpdateSuccess() {
				SessionBroadcast.broadcastLogin(context, getSession().getAccessToken());
				unregister();
			}

			@Override
			public void onUpdateError(final Throwable t) {
				Log.w(TAG, t.getMessage(), t);

				if (dialog.getWindow() != null) {
					dialog.dismiss();

					final AlertDialog ad = DisplayError.display(MissionHubActivity.this, t);
					ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.action_retry), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.dismiss();
							blockingUpdate();
							Session.resumeSession(getMHApplication());
						}
					});
					ad.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.action_close), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int id) {
							dialog.dismiss();
						}
					});
					ad.show();

				}

				unregister();
			}
		};
		sr.register(SessionBroadcast.NOTIFY_SESSION_UPDATE_START, SessionBroadcast.NOTIFY_SESSION_UPDATE_SUCCESS, SessionBroadcast.NOTIFY_SESSION_UPDATE_ERROR);
	}

	/**
	 * Starts the main activity
	 */
	private void startMain() {
		final Intent intent = new Intent(this, MissionHubMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
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