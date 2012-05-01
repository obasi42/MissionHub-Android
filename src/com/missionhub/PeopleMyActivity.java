package com.missionhub;

import greendroid.widget.item.Item;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.api.model.sql.Person;
import com.missionhub.fragment.ContactFragment;
import com.missionhub.fragment.ContactListFragment;
import com.missionhub.fragment.ContactListFragment.OnContactListListener;
import com.missionhub.fragment.NavigationMenuFragment;
import com.missionhub.fragment.NavigationMenuFragment.NavigationMenuFragmentInterface;
import com.missionhub.ui.DynamicLayoutAdapter.DynamicLayoutInterface;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.widget.item.ContactListItem;
import com.missionhub.ui.widget.item.NavigationItem;

public class PeopleMyActivity extends MissionHubMainActivity implements OnContactListListener, NavigationMenuFragmentInterface, DynamicLayoutInterface {

	/** the left navigation menu fragment */
	private NavigationMenuFragment mNavigationFragment;

	/** the contact list fragment */
	private ContactListFragment mContactListFragment;

	/** the action mode */
	private ActionMode mMode;

	/** if the device is currently in tablet mode */
	private boolean isTablet;

	/** the id currently shown contact */
	private long mCurrentContactId;

	/** the contact request code */
	public static final int REQUEST_CODE_CONTACT = 1;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_people_my, R.layout.activity_people_my_tablet);

		isTablet = getDisplayMode().isTablet();

		restoreInstanceState(savedInstanceState);

		initFragments();
		restoreFragmentState();
	}

	private void initFragments() {
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		if (isTablet) {
			mNavigationFragment = (NavigationMenuFragment) fm.findFragmentByTag(NavigationMenuFragment.class.getName());
			if (mNavigationFragment == null) {
				mNavigationFragment = new NavigationMenuFragment();
				ft.add(R.id.container, mNavigationFragment, NavigationMenuFragment.class.getName());
			} else {
				ft.attach(mNavigationFragment);
			}
			mNavigationFragment.setLayoutWeight(70f);
			mNavigationFragment.setRetainInstance(true);
		}

		mContactListFragment = (ContactListFragment) fm.findFragmentByTag(ContactListFragment.class.getName());
		if (mContactListFragment == null) {
			mContactListFragment = new ContactListFragment();
			ft.add(R.id.container, mContactListFragment, ContactListFragment.class.getName());
		} else {
			ft.attach(mContactListFragment);
		}
		mContactListFragment.setLayoutWeight(30f);
		mContactListFragment.setRetainInstance(true);
		mContactListFragment.setOnContactListListener(this);

		ft.commit();
	}

	private void restoreFragmentState() {
		if (mCurrentContactId > -1) {
			final Person person = getDbSession().getPersonDao().load(mCurrentContactId);
			if (person != null) {
				showContact(person);
			} else {
				mCurrentContactId = -1;
			}
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putLong("mCurrentContactId", mCurrentContactId);
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreInstanceState(savedInstanceState);
	}

	private void restoreInstanceState(final Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentContactId = savedInstanceState.getLong("mCurrentContactId");
		} else {
			mCurrentContactId = -1;
		}
		refreshHomeButtonState();
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		final List<Person> people = new ArrayList<Person>();

		for (int i = 0; i < 2; i++) {
			people.add(getSession().getUser().getPerson());
		}
		mContactListFragment.addPeople(people);
		
		//showContact(getSession().getUser().getPerson());
	}

	@Override
	public void onContactClick(final Person person) {
		showContact(person);
	}

	@Override
	public void onCheckContact(final Person person) {
		if (mMode == null) {
			mMode = startActionMode(new AnActionModeOfEpicProportions());
		}
	}

	@Override
	public void onUncheckContact(final Person person) {

	}

	@Override
	public void onUncheckAllContacts() {
		if (mMode != null) {
			mMode.finish();
			mMode = null;
		}
	}

	public boolean isInContactMode() {
		return mCurrentContactId > -1;
	}

	private void showContact(final Person person) {
		if (isInContactMode()) {
			return;
		}

		mCurrentContactId = person.getId();

		if (isTablet) {
			final FragmentManager fm = getSupportFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();

			ft.hide(mNavigationFragment);
			ContactFragment contactFragment = (ContactFragment) fm.findFragmentByTag("contact" + mCurrentContactId);
			if (contactFragment != null) {
				ft.attach(contactFragment);
			} else {
				contactFragment = ContactFragment.newInstance(person.getId());
				contactFragment.setRetainInstance(true);
				contactFragment.setLayoutWeight(18f);
				ft.add(R.id.container, contactFragment, "contact" + mCurrentContactId);
			}
			mContactListFragment.setContactActivated(person);
			ft.commit();
			
			refreshHomeButtonState();
		} else {
			final Intent intent = new Intent(this, ContactActivity.class);
			intent.putExtra("personId", person.getId());
			startActivityForResult(intent, REQUEST_CODE_CONTACT);
		}
	}

	private void closeContact() {	
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();		
		final ContactFragment contactFragment = (ContactFragment) fm.findFragmentByTag("contact" + mCurrentContactId);
		mCurrentContactId = -1;
		if (contactFragment != null) {
			ft.detach(contactFragment);
		}
		ft.show(mNavigationFragment);
		ft.commit();
		
		mContactListFragment.setContactActivated(null);
		refreshHomeButtonState();
	}

	private void refreshHomeButtonState() {
		if (isInContactMode()) {
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			getSupportActionBar().setHomeButtonEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTACT) {
			mCurrentContactId = -1;
			refreshHomeButtonState();
		}
	}

	@Override
	public void onBackPressed() {
		if (isInContactMode()) {
			closeContact();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {

			menu.add("Save").setIcon(R.drawable.ic_action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
			Toast.makeText(PeopleMyActivity.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
			mode.finish();
			mMode = null;
			return true;
		}

		@Override
		public void onDestroyActionMode(final ActionMode mode) {}
	}

	@Override
	public void onCreateNavigationMenu(final NavigationMenu menu) {
		super.onCreateNavigationMenu(menu);

		if (!getDisplayMode().isTablet()) {
			menu.addDivider(R.id.nav_divider).setTitle("My Contacts");
			menu.add(R.id.nav_my_contacts_all).setTitle("All");
			menu.add(R.id.nav_my_contacts_inprogress).setTitle("In Progress");
			menu.add(R.id.nav_my_contacts_completed).setTitle("Completed");
		}
	}

	@Override
	public void onCreateFragmentNavigationMenu(final NavigationMenu menu) {
		menu.add(R.id.nav_my_contacts_all).setTitle("All");
		menu.add(R.id.nav_my_contacts_inprogress).setTitle("In Progress");
		menu.add(R.id.nav_my_contacts_completed).setTitle("Completed");
	}

	@Override
	public boolean onNavigationItemSelected(final NavigationItem item) {
		toggleMenuItems(item);
		return true;
	}

	private void toggleMenuItems(final NavigationItem item) {
		switch (item.getId()) {
		case R.id.nav_my_contacts_all:
		case R.id.nav_my_contacts_completed:
		case R.id.nav_my_contacts_inprogress:
			final NavigationItem topItem = getNavigationMenu().findNavigationItemById(R.id.nav_my_contacts);
			topItem.setSubtitle(item.getTitle());
			getNavigationMenu().setSelectedItem(topItem);
			getNavigationMenu().showAll();
			getNavigationMenu().hide(item);
			if (mNavigationFragment != null) {
				mNavigationFragment.setSelectedNavigationItem(item);
			}
		}
	}

    @Override
    public Integer getLayoutResource(final Item item) {
    	if (item.getClass() == ContactListItem.class) {
    		final DisplayMode dm = getDisplayMode();
			final boolean isTablet = dm.isTablet() && dm.isW1024dp();
    		if (!isTablet || isInContactMode()) {
    			return ContactListItem.LAYOUT_NORMAL;
    		} else {
    			return ContactListItem.LAYOUT_TABLET;
    		}
    	}
    	return null;
    }
}