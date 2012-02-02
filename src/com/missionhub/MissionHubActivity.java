package com.missionhub;

import com.missionhub.api.ApiHelper;
import com.missionhub.broadcast.SessionReceiver;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MissionHubActivity extends MissionHubBaseActivity {

	@InjectView(R.id.btn_login) Button btnLogin;
	@InjectView(R.id.btn_about) Button btnAbout;

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ApiHelper.configAutoLogin(this);		
		
		SessionReceiver receiver = new SessionReceiver(this) {
			@Override
			public void onVerifyStart() {
				Log.e("RECEIVER", "VERIFY START");
			}
			
			@Override
			public void onVerifyPass() {
				Log.e("RECEIVER", "VERIFY PASS");
			}
			
			@Override
			public void onVerifyFail(Throwable t) {
				Log.e("RECEIVER", t.getMessage(), t);
			}
			
			@Override
			public void onLogin(String accessToken) {
				Log.e("RECEIVER", "LOGIN: " + accessToken);	
			}
			
			@Override
			public void onLogout() {
				Log.e("RECEIVER", "LOGOUT");
			}
		};
		receiver.register();
		
		// make user log in if session can't be resumed
		if (!getSession().resumeSession(true)) {
			Intent intent = new Intent(this, Login.class);
			startActivity( intent );
			finish();
		}
		
		setContentView(R.layout.activity_missionhub);
		
		this.getSupportActionBar().hide();
	}
}