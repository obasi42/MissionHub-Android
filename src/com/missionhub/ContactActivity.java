package com.missionhub;

import com.missionhub.fragment.ContactFragment;

import android.os.Bundle;

public class ContactActivity extends MissionHubBaseActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
            ContactFragment contact = new ContactFragment();
            contact.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, contact).commit();
        }
    }
	
}
