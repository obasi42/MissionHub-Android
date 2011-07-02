package com.missionhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MissionHubActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void clickLogin(View view) {
		Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
	
	public void clickAbout(View view) {
		
	}
	
	public void clickContact(View view) {
		
	}
}