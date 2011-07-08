package com.missionhub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MHEgg extends Activity {

	private TextView title;
	private ImageView iv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.egg);
		
		title = (TextView) findViewById(R.id.egg_title);
		iv = (ImageView) findViewById(R.id.egg_picture);
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