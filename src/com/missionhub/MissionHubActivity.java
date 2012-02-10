package com.missionhub;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.missionhub.api.ApiHelper;
import com.missionhub.config.Preferences;

/**
 * The main MissionHub Activity.
 */
public class MissionHubActivity extends MissionHubBaseActivity {
	
	public static final String TAG = MissionHubActivity.class.getSimpleName();
	
	public final int RESULT_LOGIN_ACTIVITY = 0;

	@InjectView(R.id.btn_login) Button btnLogin;
	@InjectView(R.id.btn_about) Button btnAbout;

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//ApiHelper.configAutoLogin(this);
		
		Preferences.removeAccessToken(this);
		
		// redirect to Dashboard if session can be resumed
		if (getSession().resumeSession(true)) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		}
		
		// show content
		setContentView(R.layout.activity_missionhub);
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				startActivityForResult(new Intent(MissionHubActivity.this, LoginActivity.class), 0);
			}
		});
		
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_OK) {
			startActivity(new Intent(this, DashboardActivity.class));
			finish();
		}
		if (requestCode == RESULT_LOGIN_ACTIVITY && resultCode == RESULT_FIRST_USER) {
			startActivityForResult(new Intent(MissionHubActivity.this, LoginActivity.class), 0);
		}
	}
}