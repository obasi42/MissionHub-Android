package com.missionhub;

import greendroid.widget.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cr_wd.android.network.HttpResponse;
import com.missionhub.api.ApiHandler;
import com.missionhub.api.ContactsApi;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.GMetaPeople;
import com.missionhub.api.model.GPerson;
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
import com.missionhub.util.U;

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
	
	/** the contacts api options */
	public ContactsApi.Options mContactsOptions;
	
	/** the contacts in the list */
	public ArrayList<Person> mPeople;

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
		savedInstanceState.putParcelable("mContactsOptions", mContactsOptions);
		savedInstanceState.putSerializable("mPeople", mPeople);
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreInstanceState(savedInstanceState);
	}

	@SuppressWarnings("unchecked")
    private void restoreInstanceState(final Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentContactId = savedInstanceState.getLong("mCurrentContactId");
			mContactsOptions = savedInstanceState.getParcelable("mContactsOptions");
			mPeople = (ArrayList<Person>) savedInstanceState.getSerializable("mPeople");
		} else {
			mCurrentContactId = -1;
			mContactsOptions = new ContactsApi.Options();
		}
		refreshHomeButtonState();
	}
	
	private void restoreMenuFromOptions() {
		Set<String> status = mContactsOptions.getFilterValues("status");
		String assignedTo = mContactsOptions.getFilterValue("assigned_to");
		
		NavigationMenu menu;
		if (isTablet) {
			menu = mNavigationFragment.getNavigationMenu();
		} else {
			menu = getNavigationMenu();
		}
		
		if (!U.isNullEmpty(status) || !U.isNullEmpty(assignedTo)) {
			if (status.contains("uncontacted")) {
				menu.setSelectedItem(menu.findNavigationItemById(R.id.nav_my_contacts_inprogress));
			} else if (status.contains("completed")) {
				menu.setSelectedItem(menu.findNavigationItemById(R.id.nav_my_contacts_completed));
			} else {
				menu.setSelectedItem(menu.findNavigationItemById(R.id.nav_my_contacts_all));
			}
		} else {
			mContactsOptions.clearFilters();
			mContactsOptions.setFilter("assigned_to", "me");
			mContactsOptions.addFilter("status", "uncontacted");
			mContactsOptions.addFilter("status", "attempted_contact");
			mContactsOptions.addFilter("status", "contacted");
			menu.setSelectedItem(menu.findNavigationItemById(R.id.nav_my_contacts_inprogress));
		}
	}
	
	private void restoreContactList() {
		if (mPeople == null) {
			mPeople = new ArrayList<Person>();
		}
		
//		if (mPeople.isEmpty()) {
//			
//			ContactsApi.list(this, mContactsOptions, new ApiHandler(GMetaPeople.class){
//				@Override
//                public void onSuccess(HttpResponse response) {
//					super.onSuccess(response);
//					
//					Log.w("RESPONSE", response.responseBody);
//				}
//				
//				@Override
//				public void onSuccess(final Object gsonObject) {
//					GMetaPeople metaPeople = (GMetaPeople) gsonObject;
//					GPerson[] people = metaPeople.getPeople();
//
//					if (people != null && people.length > 0) {
//						ArrayList<Person> peopleToAdd = new ArrayList<Person>();
//						
//						PersonJsonSql.update(PeopleMyActivity.this, people, false, true, "PeopleMyActivity");
//						for(GPerson p : people) { 
//							Person person = getDbSession().getPersonDao().load(p.getId());
//							peopleToAdd.add(person);
//						}
//						
//						mContactListFragment.addPeople(peopleToAdd);
//						mPeople.addAll(peopleToAdd);
//					}
//				}
//
//				@Override
//				public void onError(final Throwable throwable) {
//					Log.e("ERROR", throwable.getMessage(), throwable);
//				}
//			});
//			
//			
//		} else {
//			mContactListFragment.addPeople(mPeople);
//		}
	}

	@Override
	public void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		restoreMenuFromOptions();
		restoreContactList();
		
		// demo content
		final List<Person> people = new ArrayList<Person>();

		for (int i = 0; i < 1; i++) {
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
		
		switch(item.getId()) {
		case R.id.nav_my_contacts_all:
			mContactsOptions.clearFilters();
			mContactsOptions.addFilter("assigned_to", "me");
			break;
		case R.id.nav_my_contacts_inprogress:
			mContactsOptions.clearFilters();
			mContactsOptions.addFilter("assigned_to", "me");
			mContactsOptions.addFilter("status", "uncontacted");
			mContactsOptions.addFilter("status", "attempted_contact");
			mContactsOptions.addFilter("status", "contacted");
			break;
		case R.id.nav_my_contacts_completed:
			mContactsOptions.clearFilters();
			mContactsOptions.addFilter("assigned_to", "me");
			mContactsOptions.addFilter("status", "completed");
			break;
		}
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