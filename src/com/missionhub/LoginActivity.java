package com.missionhub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.login);
	}
	
	public void clickClose(View view) {
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}