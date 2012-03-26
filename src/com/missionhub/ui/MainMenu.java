package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.SubtitleItem;
import greendroid.widget.item.TextItem;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.PeopleAllActivity;
import com.missionhub.PeopleDirectoryActivity;
import com.missionhub.PeopleMyActivity;
import com.missionhub.R;
import com.missionhub.util.U;

public class MainMenu implements OnNavigationListener {

	public static final int PEOPLE_MY = 0;
	public static final int PEOPLE_ALL = 1;
	public static final int PEOPLE_DIRECTORY = 2;
	public static final int GROUPS = 3;
	public static final int SURVEYS = 4;

	private final MissionHubBaseActivity activity;
	private ItemAdapter adapter;
	private OnNavigationListener navigationListener = this;
	private int currentPosition = PEOPLE_MY;

	private MainMenu(final MissionHubBaseActivity activity, final int defaultPosition) {
		this.activity = activity;

		createAdapter();

		applyMenu();

		currentPosition = defaultPosition;
		activity.getSupportActionBar().setSelectedNavigationItem(defaultPosition);
	}

	public static MainMenu initialize(final MissionHubBaseActivity activity, final int defaultPosition) {
		return new MainMenu(activity, defaultPosition);
	}

	private void createAdapter() {
		final Context context = activity.getSupportActionBar().getThemedContext();
		adapter = new ItemAdapter(context);

		final Resources res = activity.getResources();
		final String[] titles = res.getStringArray(R.array.menu_titles);
		final String[] subtitles = res.getStringArray(R.array.menu_subtitles);

		for (int i = 0; i < titles.length; i++) {
			final String title = titles[i];
			String subtitle = "";
			try {
				subtitle = subtitles[i];
			} catch (final Exception e) {}

			if (U.isNullEmpty(subtitle)) {
				adapter.add(new TextItem(title));
			} else {
				adapter.add(new SubtitleItem(title, subtitle));
			}
		}
	}

	private void applyMenu() {
		activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
		activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		activity.getSupportActionBar().setListNavigationCallbacks(adapter, navigationListener);
	}

	@Override
	public boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		switch (itemPosition) {
		case PEOPLE_MY:
			final Intent myIntent = new Intent(activity, PeopleMyActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(myIntent);
			activity.finish();
			return true;
		case PEOPLE_ALL:
			final Intent allIntent = new Intent(activity, PeopleAllActivity.class);
			allIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(allIntent);
			return true;
		case PEOPLE_DIRECTORY:
			final Intent directoryIntent = new Intent(activity, PeopleDirectoryActivity.class);
			directoryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(directoryIntent);
			return true;
		case GROUPS:

			return true;
		case SURVEYS:

			return true;
		default:
			return false;
		}
	}

	public OnNavigationListener getOnNavigationListener() {
		return navigationListener;
	}

	public void setOnNavigationListener(final OnNavigationListener listener) {
		navigationListener = listener;
		applyMenu();
	}

}