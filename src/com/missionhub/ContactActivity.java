package com.missionhub;

import android.os.Bundle;

import com.missionhub.fragment.ContactFragment;

public class ContactActivity extends MissionHubBaseActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			final ContactFragment contact = new ContactFragment();
			contact.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, contact).commit();
		}
	}

}
