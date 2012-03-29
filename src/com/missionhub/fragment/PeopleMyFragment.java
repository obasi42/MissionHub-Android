package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.ui.MainMenu;
import com.missionhub.ui.widget.item.NavigationItem;
import com.missionhub.util.U;

public class PeopleMyFragment extends MissionHubFragment implements OnItemClickListener {
	
	private PeopleMyCategoryFragment categoryFragment;
	private ContactListFragment contactListFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view;
		
		if (U.isOldTablet(inflater.getContext())) {
			view = inflater.inflate(R.layout.fragment_people_my_tablet, container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_people_my, container, false);
		}
		
		categoryFragment = (PeopleMyCategoryFragment) getFragmentManager().findFragmentById(R.id.people_my_category_fragment);
		contactListFragment = (ContactListFragment) getFragmentManager().findFragmentById(R.id.people_my_contact_list_fragment);
		
		categoryFragment.setOnItemClickListener(this);
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		menu.add(Menu.NONE, R.string.action_add, 0, R.string.action_add).setIcon(R.drawable.ic_action_user_add).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		final MenuItem refresh = menu.add(Menu.NONE, R.string.action_refresh, 1, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		setSupportProgressBarIndeterminateItem(refresh);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.string.action_add:

			return true;
		case R.string.action_refresh:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateMainMenu(final MainMenu menu, final ItemAdapter adapter) {
		if (U.isPhone(getActivity())) {
			adapter.add(new NavigationItem("My Contacts"));
			adapter.add(new NavigationItem("My Contacts", "All"));
			adapter.add(new NavigationItem("My Contacts", "In Progress"));
			adapter.add(new NavigationItem("My Contacts", "Completed"));
		}
	}

	@Override
	public boolean onNavigationItemSelected(final NavigationItem item) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getActivity(), "Clicked " + arg3, Toast.LENGTH_SHORT).show();
	}
}