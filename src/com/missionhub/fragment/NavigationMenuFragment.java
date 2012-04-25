package com.missionhub.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.missionhub.MissionHubBaseActivity;
import com.missionhub.R;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.NavigationMenu.NavigationMenuInterface;
import com.missionhub.ui.NavigationMenu.OnNavigationItemSelectedListener;
import com.missionhub.ui.widget.item.NavigationItem;

public class NavigationMenuFragment extends MissionHubFragment implements OnItemClickListener, NavigationMenuInterface, OnNavigationItemSelectedListener {

	private ListView mListView;

	private NavigationMenu mMenu;

	private NavigationItem mCurrentItem;

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		mMenu = new NavigationMenu(true, activity, this, this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_navigation_menu, container, false);
		view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));

		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemClickListener(this);

		mListView.setAdapter(mMenu.getAdapter());

		return view;
	}

	@Override
	public void onItemClick(final AdapterView<?> adapter, final View listView, final int position, final long id) {
		if (mMenu != null && mMenu.getAdapter().getItem(position) != mCurrentItem) {
			final NavigationItem item = (NavigationItem) mMenu.getAdapter().getItem(position);
			if (item != mCurrentItem) {
				mMenu.onNavigationItemSelected(position, id);
			}
			mCurrentItem = item;
		}
	}

	@Override
	public boolean onNavigationItemSelected(final NavigationItem item) {
		final MissionHubBaseActivity activity = getMHActivity();
		if (activity != null && activity instanceof OnNavigationItemSelectedListener) {
			return ((OnNavigationItemSelectedListener) activity).onNavigationItemSelected(item);
		}
		return false;
	}

	@Override
	public void onCreateNavigationMenu(final NavigationMenu menu) {}

	public NavigationMenu getNavigationMenu() {
		return mMenu;
	}

	@Override
	public void onCreateSideNavigationMenu(final NavigationMenu menu) {
		Log.e("HERE", "HERE");
		final MissionHubBaseActivity activity = getMHActivity();
		if (activity != null && activity instanceof NavigationMenuInterface) {
			Log.e("HERE2", "HERE2");
			((NavigationMenuInterface) activity).onCreateSideNavigationMenu(menu);
		}
	}
}