package com.missionhub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.missionhub.api.ApiNotifier;
import com.missionhub.helper.Flurry;

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
	
	public ApiNotifier getApiNotifier() {
		return getApp().getApiNotifier();
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
	
	public List<String> progress = Collections.synchronizedList(new ArrayList<String>());
	
	public void showProgress(String item) {
		progress.add(item);
		setProgressVisible(true);
	}
	
	public void hideProgress(String item) {
		progress.remove(item);
		if (progress.isEmpty()) {
			setProgressVisible(false);
		}
	}
}