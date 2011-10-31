package com.missionhub;

import com.missionhub.helper.AnalyticsTracker;

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
		
		getTracker().setCustomVar("personId", String.valueOf(getUser().getId()), AnalyticsTracker.SCOPE_PAGE_LEVEL);
		getTracker().trackActivityView(this);
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