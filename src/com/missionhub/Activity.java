package com.missionhub;

import com.missionhub.helpers.Flurry;

import greendroid.app.GDActivity;

public class Activity extends GDActivity {
	
	public ApplicationUser getUser() {
		Application app = (Application) getApplicationContext();
		return app.getUser();
	}
	
	public ApplicationUser getUser(int personId) {
		Application app = (Application) getApplicationContext();
		return app.getUser(personId);
	}
	
	public Application getApp() {
		return (Application) getApplicationContext();
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
	
}