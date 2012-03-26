package com.missionhub;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.api.model.sql.DaoSession;

public class MissionHubBaseActivity extends SherlockFragmentActivity {

	private MenuItem progressMenuItem;
	private ImageView progressImageView;
	private Animation progressAnimation;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** Called when the activity is destroyed */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Shortcut to the MissionHubApplication
	 * 
	 * @return
	 */
	public MissionHubApplication getMHApplication() {
		return ((MissionHubApplication) getApplicationContext());
	}

	/**
	 * Returns the session
	 * 
	 * @return
	 */
	public Session getSession() {
		return getMHApplication().getSession();
	}

	@Override
	public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
		if (progressMenuItem == null) {
			super.setSupportProgressBarIndeterminateVisibility(visible);
		} else {
			updateProgressAnimation(visible);
		}
	}
	
	/**
	 * Shortcut to show the Indeterminate progress bar
	 */
	public void showProgress() {
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	/**
	 * Shortcut to hide the Indeterminate progress bar
	 */
	public void hideProgress() {
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

	/**
	 * Gets the raw sqlite database for the application context
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getDb() {
		return getMHApplication().getDb();
	}

	/**
	 * Returns the database session for the application context
	 * 
	 * @return
	 */
	public synchronized DaoSession getDbSession() {
		return getMHApplication().getDbSession();
	}
	
	/**
	 * Sets the menu item to use as a indeterminate progress bar
	 * @param item
	 */
	public void setSupportProgressBarIndeterminateItem(MenuItem item) {
		progressMenuItem = item;
		
		if (progressMenuItem != null) {			
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			progressImageView = (ImageView) layoutInflater.inflate(R.layout.widget_refresh_button, null);
			progressImageView.setImageDrawable(item.getIcon());
			progressAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_cw);
			progressAnimation.setRepeatCount(Animation.INFINITE);
		}
	}
	
	/**
	 * Starts/stops animation on progress change
	 */
	private void updateProgressAnimation(boolean visible) {
		if (progressMenuItem == null || progressImageView == null) return;
		
		if (visible) {
			progressMenuItem.setEnabled(false);
			progressImageView.startAnimation(progressAnimation);
			progressMenuItem.setActionView(progressImageView);
		} else {
			progressMenuItem.setEnabled(true);
			progressImageView.clearAnimation();
			progressMenuItem.setActionView(null);
		}
	}
}