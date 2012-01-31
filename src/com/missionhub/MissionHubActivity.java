package com.missionhub;

import java.util.ArrayList;

import com.missionhub.broadcast.SessionBroadcast;
import com.missionhub.broadcast.SessionReceiver;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MissionHubActivity extends MissionHubBaseActivity {

	@InjectView(R.id.btn_login) Button btnLogin;
	@InjectView(R.id.btn_about) Button btnAbout;

	final ArrayList<Long> times = new ArrayList<Long>();

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		// make user login
//		if (!getUser().isLoggedIn()) {
//			Intent intent = new Intent(this, Login.class);
//		    startActivity( intent );
//		    finish();
//		}
		
		setContentView(R.layout.activity_missionhub);
		
		
		SessionReceiver receiver = new SessionReceiver(this) {
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
		
		SessionBroadcast.broadcastLogin(this, "My Access Token");
		SessionBroadcast.broadcastLogout(this);		
		
		this.getSupportActionBar().hide();
		
	}
}