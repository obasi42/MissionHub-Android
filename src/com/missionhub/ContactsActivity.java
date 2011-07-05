package com.missionhub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ContactsActivity extends Activity {

	private ListView contactsList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		
		contactsList = (ListView) findViewById(R.id.contacts_list);
	}
}