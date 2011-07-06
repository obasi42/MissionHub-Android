package com.missionhub;

import com.google.gson.Gson;
import com.missionhub.api.GContact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ContactActivity extends Activity {
	
	public static final String TAG = ContactActivity.class.getName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Intent i = getIntent();
		GContact contact = null;
		if (i != null) {
			try {
				Gson gson = new Gson();
				contact = gson.fromJson(i.getStringExtra("contactJSON"), GContact.class);
			} catch (Exception e) {}
		}
		if (contact == null) {
			finish();
			return;
		}
		
		Log.i(TAG, contact.getPerson().getName());
		
		setContentView(R.layout.contact);
	}
}