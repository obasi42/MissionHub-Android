package com.missionhub.android.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.app.MissionHubBaseActivity;

/**
 * The base MissionHubFragment
 */
public class MissionHubFragment extends SherlockFragment {

	private float mWeight = 1f;

	/**
	 * Returns the activity cast to the MissionHubBaseActivity
	 */
	public MissionHubBaseActivity getMHActivity() {
		return (MissionHubBaseActivity) getSherlockActivity();
	}

	/**
	 * Sets the menu item to use as a indeterminate progress bar
	 * 
	 * @param item
	 */
	public void setSupportProgressBarIndeterminateItem(final MenuItem item) {
		getMHActivity().setSupportProgressBarIndeterminateItem(item);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void setLayoutWeight(final float weight) {
		mWeight = weight;
		if (getView() != null) {
			final View view = getView();
			final LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
			params.weight = mWeight;
			view.setLayoutParams(params);
		}
	}

	public float getLayoutWeight() {
		return mWeight;
	}
}
