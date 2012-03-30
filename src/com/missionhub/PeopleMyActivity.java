package com.missionhub;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.missionhub.api.model.sql.Person;
import com.missionhub.fragment.ContactFragment;
import com.missionhub.fragment.ContactListFragment;
import com.missionhub.fragment.PeopleMyCategoryFragment;

public class PeopleMyActivity extends MissionHubMainActivity {

	private PeopleMyCategoryFragment categoryFragment;
	private ContactListFragment contactListFragment;
	private ContactFragment contactFragment;

	private boolean isTablet = true;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_people_my, R.layout.activity_people_my_tablet);

		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();

		categoryFragment = (PeopleMyCategoryFragment) fm.findFragmentById(R.id.people_my_category);
		contactListFragment = (ContactListFragment) fm.findFragmentById(R.id.people_my_contact_list);
		contactFragment = (ContactFragment) fm.findFragmentById(R.id.people_my_contact);

		if (categoryFragment == null) {
			isTablet = false;
		}

		if (isTablet) {
			ft.hide(contactFragment);
		}

		ft.commit();

		final List<Person> people = new ArrayList<Person>();
		people.add(getSession().getUser().getPerson());
		contactListFragment.addPeople(people);

		getNavigationMenu().attach(this.getClass());
	}
}