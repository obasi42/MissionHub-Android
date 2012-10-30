package com.missionhub.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.missionhub.R;
import com.missionhub.application.Session;
import com.missionhub.fragment.AllContactsFragment;
import com.missionhub.fragment.ContactListFragment;
import com.missionhub.fragment.DirectoryFragment;
import com.missionhub.fragment.GroupsFragment;
import com.missionhub.fragment.MyContactsFragment;
import com.missionhub.fragment.SideMenuFragment.SideMenu;
import com.missionhub.fragment.SurveysFragment;
import com.missionhub.ui.item.SideMenuItem;

/**
 * This the "Host" activity. It controls the attachment of of all of the primary fragments such as My Contacts, All
 * Contacts, Surveys, etc.
 */
@SuppressLint("ValidFragment")
public class HostActivity extends BaseAuthenticatedMenuActivity {

	/** the hosted fragments */
	public static enum HostedFragment {
		MY_CONTACTS, ALL_CONTACTS, DIRECTORY, SURVEYS, GROUPS
	};

	/** the tag of the current fragment */
	private HostedFragment mCurrentHostedFragement = HostedFragment.MY_CONTACTS; // the initial fragment

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_host);
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (savedInstanceState == null) {
			final HostedFragment initFragment = mCurrentHostedFragement;
			mCurrentHostedFragement = null;
			showFragment(initFragment);
		} else {
			showFragment(mCurrentHostedFragement);
		}
	}

	/**
	 * Removed the current fragment and shows the fragment passed. If the new fragment == current fragment, no action
	 * will be taken.
	 * 
	 * @param hostedFragment
	 */
	public synchronized void showFragment(final HostedFragment hostedFragment) {
		if (mCurrentHostedFragement == hostedFragment) return;
		if (mCurrentHostedFragement == null) mCurrentHostedFragement = hostedFragment;

		// reset the actionbar to give the attached fragments a consistant attachment state
		getSupportActionBar().removeAllTabs();
		getSupportActionBar().setListNavigationCallbacks(null, null);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayShowTitleEnabled(Boolean.TRUE);
		getSupportActionBar().setTitle("");

		// get the fragment manager and set up the transaction with custom animations
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.activity_slide_in, R.anim.activity_slide_out);

		// try to find the fragment and re-attach it if it exists, otherwise add a new instance
		Fragment fragment = fm.findFragmentByTag(hostedFragment.name());
		final Fragment previous = fm.findFragmentByTag(mCurrentHostedFragement.name());
		if (fragment == null) {
			switch (hostedFragment) {
			case MY_CONTACTS:
				fragment = new ContactListFragment();
				break;
			case ALL_CONTACTS:
				fragment = new AllContactsFragment();
				break;
			case DIRECTORY:
				fragment = new DirectoryFragment();
				break;
			case SURVEYS:
				fragment = new SurveysFragment();
				break;
			case GROUPS:
				fragment = new GroupsFragment();
				break;
			}

			if (previous == null) {
				ft.add(R.id.host_container, fragment, hostedFragment.name());
			} else {
				ft.replace(R.id.host_container, fragment, hostedFragment.name());
			}
		} else {
			ft.attach(fragment);
		}
		ft.commit();
		fm.executePendingTransactions();

		mCurrentHostedFragement = hostedFragment;
	}

	/**
	 * Keep track of the current fragment
	 */
	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("mCurrentHostedFragement", mCurrentHostedFragement.name());
	}

	/**
	 * Restore the current fragment
	 */
	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			mCurrentHostedFragement = HostedFragment.valueOf(savedInstanceState.getString("mCurrentHostedFragement"));
		}
	}

	@Override
	public void onCreateSideMenu(final SideMenu menu) {
		menu.addItem(new SideMenuItem(R.id.menu_item_my_contacts, "My Contacts", R.drawable.ic_menu_my_contacts));
		menu.addItem(new SideMenuItem(R.id.menu_item_all_contacts, "All Contacts", R.drawable.ic_menu_all_contacts));
		menu.addItem(new SideMenuItem(R.id.menu_item_directory, "Directory", R.drawable.ic_menu_directory));
		menu.addItem(new SideMenuItem(R.id.menu_item_surveys, "Surveys", R.drawable.ic_menu_surveys));
		menu.addItem(new SideMenuItem(R.id.menu_item_groups, "Groups", R.drawable.ic_menu_groups));
		menu.addItem(new SideMenuItem(R.id.menu_item_logout, "Logout"));
	}

	/**
	 * Called when a side menu item is selected
	 */
	@Override
	public void onSideMenuItemSelected(final SideMenuItem item) {
		super.onSideMenuItemSelected(item);

		switch (item.id) {
		case R.id.menu_item_my_contacts:
			showFragment(HostedFragment.MY_CONTACTS);
			break;
		case R.id.menu_item_all_contacts:
			showFragment(HostedFragment.ALL_CONTACTS);
			break;
		case R.id.menu_item_directory:
			showFragment(HostedFragment.DIRECTORY);
			break;
		case R.id.menu_item_surveys:
			showFragment(HostedFragment.SURVEYS);
			break;
		case R.id.menu_item_groups:
			showFragment(HostedFragment.GROUPS);
			break;
		case R.id.menu_item_logout:
			Session.getInstance().logout();
			break;
		}
	}
}