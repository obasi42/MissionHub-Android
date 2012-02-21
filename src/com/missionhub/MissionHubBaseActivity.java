package com.missionhub;

import android.database.sqlite.SQLiteDatabase;

import com.missionhub.api.model.sql.DaoSession;

public class MissionHubBaseActivity extends MissionHubRoboFragmentActivity {

	/**
	 * Returns the session
	 * @return
	 */
	public Session getSession() {
		return ((MissionHubApplication) getApplicationContext()).getSession();
	}
	
	/**
	 * Shortcut to show the Indeterminate progress bar
	 */
	public void showProgress() {
		setSupportProgressBarIndeterminateVisibility(true);
	}
	
	/**
	 * Shortcut to hide the Indeterminate progress bar
	 */
	public void hideProgress() {
		setSupportProgressBarIndeterminateVisibility(false);
	}
	
	/**
	 * Gets the raw sqlite database for the application context
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getDb() {
		return ((MissionHubApplication) getApplicationContext()).getDb();
	}

	/**
	 * Returns the database session for the application context
	 * 
	 * @return
	 */
	public synchronized DaoSession getDbSession() {
		return ((MissionHubApplication) getApplicationContext()).getDbSession();
	}
}