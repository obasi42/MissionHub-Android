package com.missionhub;

import java.util.HashMap;

import com.missionhub.auth.User;
import com.missionhub.helpers.Flurry;

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
		
		Application.restoreApplicationState(savedInstanceState);
		
		title = (TextView) findViewById(R.id.egg_title);
		iv = (ImageView) findViewById(R.id.egg_picture);
		
		Flurry.pageView("Egg");
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("userid", String.valueOf(User.getContact().getPerson().getId()));
			Flurry.event("EggView", params);
		} catch (Exception e) {}
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   Flurry.startSession(this);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   Flurry.endSession(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putAll(Application.saveApplicationState(b));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		Application.restoreApplicationState(b);
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