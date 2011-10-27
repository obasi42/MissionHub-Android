package com.missionhub;

import java.util.HashMap;

import com.missionhub.helpers.Flurry;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class EggActivity extends Activity {

	private ImageView iv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setActionBarContentView(R.layout.egg);
		
		setTitle(R.string.egg_title);
		iv = (ImageView) findViewById(R.id.egg_picture);
		
		Flurry.pageView(this, "Egg");
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("userid", String.valueOf(getUser().getId()));
			Flurry.event(this, "EggView", params);
		} catch (Exception e) {}
	}
	
	private int clicks = 0;
	public void clickPicture(View v) {
		clicks++;
		if (clicks == 20) {
			clicks = 0;
			setTitle(R.string.egg_title_todd);
			iv.setImageResource(R.drawable.todd);
		}
	}
}