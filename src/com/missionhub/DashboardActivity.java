package com.missionhub;

import com.missionhub.broadcast.SessionReceiver;

import android.os.Bundle;
import android.util.Log;

public class DashboardActivity extends MissionHubBaseActivity {
	
	/** logging tag */
	public static final String TAG = DashboardActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dashboard);
		
		SessionReceiver receiver = new SessionReceiver(this){
			@Override public void onVerifyPass() {
				Log.w("VERIFY", "Passed");
			}
		};
		receiver.register();
	}
	
}