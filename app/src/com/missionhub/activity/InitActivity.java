package com.missionhub.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.Session.*;
import com.missionhub.application.SettingsManager;
import com.missionhub.authenticator.AuthenticatorActivity;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.ExceptionHelper.DialogButton;
import com.missionhub.ui.widget.PickAccountDialog;
import com.missionhub.ui.widget.PickAccountDialog.AccountPickedEvent;
import com.missionhub.util.U;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

/**
 * The initial MissionHub activity.
 * <p/>
 * Restores the user session and starts the MainActivity or prompts the user for login info.
 */
public class InitActivity extends BaseActivity {

    /**
     * the logging tag
     */
    public static final String TAG = InitActivity.class.getSimpleName();

    /**
     * request code for authentication
     */
    private static final int REQUEST_AUTHENTICATION = 1;

    /**
     * the main missionhub logo
     */
    private ImageView mLogo;

    /**
     * the login button
     */
    private Button mLogin;

    /**
     * the status text
     */
    private TextView mStatus;

    /**
     * the indeterminate progress bar
     */
    private ProgressBar mProgress;

    /**
     * the missionhub version number
     */
    private TextView mVersion;

    /**
     * the acount picker dialog
     */
    private PickAccountDialog mAccountDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);

        mLogo = (ImageView) findViewById(R.id.logo);
        mLogin = (Button) findViewById(R.id.btn_login);
        mStatus = (TextView) findViewById(R.id.status);
        mProgress = (ProgressBar) findViewById(R.id.loading);
        mVersion = (TextView) findViewById(R.id.version);

        mVersion.setText(Application.getVersionName());

        mLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Session.getInstance().canPickAccount()) {
                    showAccountPicker();
                } else {
                    createAccount();
                }
            }
        });

        showLoginButton();

        Application.registerEventSubscriber(this, SessionResumeSuccessEvent.class, SessionResumeErrorEvent.class, SessionResumeStatusEvent.class, SessionPickAccountEvent.class,
                AccountPickedEvent.class);

        Session.getInstance().resumeSession();
    }

    private void hideProgress() {
        mProgress.setVisibility(View.INVISIBLE);
        mStatus.setVisibility(View.INVISIBLE);
    }

    public void showProgress(final String status) {
        mProgress.setVisibility(View.VISIBLE);
        mLogin.setVisibility(View.INVISIBLE);
        if (!U.isNullEmpty(status)) {
            mStatus.setText(status);
            mStatus.setVisibility(View.VISIBLE);
        } else {
            mStatus.setVisibility(View.INVISIBLE);
        }
    }

    private void showLoginButton() {
        hideProgress();
        mLogin.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(final SessionResumeSuccessEvent event) {
        Log.e("HERE", "SUCCESS");

        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onEventMainThread(final SessionResumeErrorEvent event) {
        showLoginButton();

        if (event.exception instanceof NoAccountException) {
            return;
        }

        final ExceptionHelper eh = new ExceptionHelper(this, event.exception);
        eh.setPositiveButton(new DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_close);
            }

            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                dialog.dismiss();
            }
        });
        eh.show();
    }

    public void onEventMainThread(final SessionResumeStatusEvent event) {
        showProgress(event.status);
    }

    public void onEventMainThread(final SessionPickAccountEvent event) {
        showLoginButton();
        showAccountPicker();
    }

    private void showAccountPicker() {
        showLoginButton();
        if (mAccountDialog == null) {
            mAccountDialog = new PickAccountDialog();
            mAccountDialog.setCancelable(false);
        }

        mAccountDialog.show(getSupportFragmentManager(), "mAccountDialog");
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHENTICATION) {
            if (resultCode == Activity.RESULT_OK) {
                Session.getInstance().resumeSession();
            } else {
                showLoginButton();
            }
        }
    }
}