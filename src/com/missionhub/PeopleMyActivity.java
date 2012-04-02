package com.missionhub;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.api.model.sql.Person;
import com.missionhub.fragment.ContactFragment;
import com.missionhub.fragment.ContactListFragment;
import com.missionhub.fragment.ContactListFragment.OnContactListListener;
import com.missionhub.fragment.PeopleMyCategoryFragment;
import com.missionhub.fragment.PeopleMyCategoryFragment.OnCategoryClickListener;

public class PeopleMyActivity extends MissionHubMainActivity implements OnCategoryClickListener, OnContactListListener {

	private PeopleMyCategoryFragment categoryFragment;

	private ContactListFragment contactListFragment;

	private ContactFragment contactFragment;

	private boolean isTablet = true;

	private boolean mInContactView = false;
	
	private ActionMode mMode;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_people_my, R.layout.activity_people_my_tablet);

		final FragmentManager fm = getSupportFragmentManager();
		categoryFragment = (PeopleMyCategoryFragment) fm.findFragmentById(R.id.people_my_category);
		contactListFragment = (ContactListFragment) fm.findFragmentById(R.id.people_my_contact_list);
		contactFragment = (ContactFragment) fm.findFragmentById(R.id.people_my_contact);

		if (categoryFragment != null) {
			categoryFragment.setOnCategoryClickListener(this);
		}
		contactListFragment.setOnContactListListener(this);

		if (categoryFragment == null) {
			isTablet = false;
		}

		final FragmentTransaction ft = fm.beginTransaction();
		if (isTablet) {
			ft.hide(contactFragment);
		}
		ft.commit();

		final List<Person> people = new ArrayList<Person>();
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		people.add(getSession().getUser().getPerson());
		contactListFragment.addPeople(people);

		getNavigationMenu().attach(this.getClass());
	}

	@Override
	public void onContactClick(final Person person) {
		showContact(person);
	}

	@Override
	public void onCheckContact(final Person person) {
		Toast.makeText(this, "Checked " + person.getName(), Toast.LENGTH_SHORT).show();
		if (mMode == null) {
			mMode = startActionMode(new AnActionModeOfEpicProportions());
		}
	}

	@Override
	public void onUncheckContact(final Person person) {
		Toast.makeText(this, "Unchecked " + person.getName(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUncheckAllContacts() {
		Toast.makeText(this, "Unchecked all Contacts", Toast.LENGTH_SHORT).show();
		if (mMode != null) {
            mMode.finish();
            mMode = null;
        }
	}

	@Override
	public void onCategoryClick() {
		Toast.makeText(this, "Clicked Category", Toast.LENGTH_SHORT).show();
	}

	private void showContact(final Person person) {
		if (isTablet) {
			Toast.makeText(this, "Clicked Tablet Contact " + person.getName(), Toast.LENGTH_SHORT).show();
			if (!mInContactView) {
				final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// ft.setCustomAnimations(R.anim.slide_in_right,
				// R.anim.slide_out_left);
				ft.hide(categoryFragment);
				ft.show(contactFragment);
				ft.commit();
			}

			// contactFragment.setContact(person);

			mInContactView = true;
		} else {
			Toast.makeText(this, "Clicked Phone Contact " + person.getName(), Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(this, ContactActivity.class);
			intent.putExtra("personId", person.getId());
			startActivity(intent);
		}
	}

	private void showCategories() {
		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		ft.hide(contactFragment);
		ft.show(categoryFragment);
		ft.commit();
		mInContactView = false;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK && mInContactView)) {
			showCategories();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            
            menu.add("Save")
                .setIcon(R.drawable.ic_action_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Toast.makeText(PeopleMyActivity.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
            mode.finish();
            mMode = null;
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

}