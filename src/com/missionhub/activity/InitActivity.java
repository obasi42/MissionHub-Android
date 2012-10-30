package com.missionhub.activity;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.WazaBe.HoloEverywhere.widget.ProgressBar;
import com.WazaBe.HoloEverywhere.widget.TextView;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.Session.NoAccountException;
import com.missionhub.application.Session.SessionPickAccountEvent;
import com.missionhub.application.Session.SessionResumeErrorEvent;
import com.missionhub.application.Session.SessionResumeOfflineEvent;
import com.missionhub.application.Session.SessionResumeStatusEvent;
import com.missionhub.application.Session.SessionResumeSuccessEvent;
import com.missionhub.application.SettingsManager;
import com.missionhub.authenticator.AuthenticatorActivity;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.ui.widget.PickAccountDialog;
import com.missionhub.ui.widget.PickAccountDialog.AccountPickedEvent;

/**
 * The initial MissionHub activity.
 * 
 * Restores the user session and starts the HostActivity or prompts the user for login info.
 */
@ContentView(R.layout.activity_init)
public class InitActivity extends BaseActivity {

	@InjectView(R.id.loading) private ProgressBar mProgress;
	@InjectView(R.id.logo) private ImageView mLogo;
	@InjectView(R.id.status) private TextView mStatus;
	@InjectView(R.id.version) private TextView mVersion;

	/** the logging tag */
	public static final String TAG = InitActivity.class.getSimpleName();

	/** request code for authentication */
	private static final int REQUEST_AUTHENTICATION = 1;

	/** the account picker dialog */
	private PickAccountDialog mDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Application.registerEventSubscriber(this, SessionResumeSuccessEvent.class, SessionResumeErrorEvent.class, SessionResumeStatusEvent.class, SessionResumeOfflineEvent.class,
				SessionPickAccountEvent.class, AccountPickedEvent.class);

		Session.getInstance().resumeSession();
	}

	public void onEventMainThread(final SessionResumeSuccessEvent event) {
		final Intent intent = new Intent(this, HostActivity.class);
		startActivity(intent);
		finish();
	}

	public void onEventMainThread(final SessionResumeErrorEvent event) {
		Log.e(TAG, event.getClass().getName());

		if (event.exception instanceof NoAccountException) {
			// TODO: show login button

			return;
		}

		final ExceptionHelper eh = new ExceptionHelper(this, event.exception);
		eh.setPositiveButton(new DialogButton() {
			@Override
			public String getTitle() {
				return "Close";
			}

			@Override
			public void onClick(final DialogInterface dialog, final int whichButton) {
				dialog.dismiss();
			}
		});
		eh.show();
	}

	public void onEventMainThread(final SessionResumeStatusEvent event) {
		Log.e(TAG, event.getClass().getName());

		mStatus.setText(event.status);
	}

	public void onEventMainThread(final SessionResumeOfflineEvent event) {
		Log.e(TAG, event.getClass().getName());
	}

	public void onEventMainThread(final SessionPickAccountEvent event) {
		if (mDialog == null) {
			mDialog = new PickAccountDialog(this);
		}

		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	public void onEventMainThread(final AccountPickedEvent event) {
		if (event.personId > 0) {
			SettingsManager.setSessionLastUserId(event.personId);
			Session.getInstance().resumeSession();
		} else {
			createAccount();
		}
	}

	@Override
	public void onDestroy() {
		Application.unregisterEventSubscriber(this);
		super.onDestroy();
	}

	private void createAccount() {
		final Intent intent = new Intent(this, AuthenticatorActivity.class);
		startActivityForResult(intent, REQUEST_AUTHENTICATION);
	}
}
