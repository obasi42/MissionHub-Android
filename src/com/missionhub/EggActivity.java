package com.missionhub;

import java.util.HashMap;

import com.flurry.android.FlurryAgent;
import com.missionhub.auth.User;
import com.missionhub.config.Config;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class EggActivity extends Activity {

	private TextView title;
	private ImageView iv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.egg);
		
		title = (TextView) findViewById(R.id.egg_title);
		iv = (ImageView) findViewById(R.id.egg_picture);
		
		User.initFlurryUser();
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Egg");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("userid", String.valueOf(User.getContact().getPerson().getId()));
			FlurryAgent.onEvent("EggView", params);
		} catch (Exception e) {}
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   User.initFlurryUser();
	   FlurryAgent.onStartSession(this, Config.flurryKey);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   User.initFlurryUser();
	   FlurryAgent.onEndSession(this);
	}
	
	private int clicks = 0;
	public void clickPicture(View v) {
		clicks++;
		if (clicks == 20) {
			clicks = 0;
			title.setText(R.string.egg_title_todd);
			iv.setImageResource(R.drawable.todd);
		}
	}
}