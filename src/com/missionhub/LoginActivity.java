package com.missionhub;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

public class LoginActivity extends Activity {
	
	WebView wv;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.login);
		
		wv = (WebView) findViewById(R.id.webview_login);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	finish();
	    	overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void clickClose(View view) {
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}