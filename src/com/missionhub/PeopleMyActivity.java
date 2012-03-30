package com.missionhub;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.missionhub.api.model.sql.Person;
import com.missionhub.fragment.ContactFragment;
import com.missionhub.fragment.ContactListFragment;
import com.missionhub.fragment.ContactListFragment.OnContactClickListener;
import com.missionhub.fragment.PeopleMyCategoryFragment;
import com.missionhub.fragment.PeopleMyCategoryFragment.OnCategoryClickListener;

public class PeopleMyActivity extends MissionHubMainActivity implements OnCategoryClickListener, OnContactClickListener {
	
	private PeopleMyCategoryFragment categoryFragment;
	
	private ContactListFragment contactListFragment;
	
	private ContactFragment contactFragment;
	
	private boolean isTablet = true;
	
	private boolean mInContactView = false;
	
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
		contactListFragment.setOnContactClickListener(this);
		
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
		contactListFragment.addPeople(people);

		getNavigationMenu().attach(this.getClass());
	}

	@Override
	public void onContactClick(Person person) {
		showContact(person);
		
	}

	@Override
	public void onCategoryClick() {
		Toast.makeText(this, "Clicked Category", Toast.LENGTH_SHORT).show();
	}
	
	private void showContact(Person person) {
		if (isTablet) {
			Toast.makeText(this, "Clicked Tablet Contact " + person.getName(), Toast.LENGTH_SHORT).show();
			if (!mInContactView) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				//ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
				ft.hide(categoryFragment);
				ft.show(contactFragment);
				ft.commit(); 
			}
			
			//contactFragment.setContact(person);
			
			mInContactView = true;
		} else {
			Toast.makeText(this, "Clicked Phone Contact " + person.getName(), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, ContactActivity.class);
			intent.putExtra("personId", person.getId());
			startActivity(intent);
		}
	}
	
	private void showCategories() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		ft.hide(contactFragment);
		ft.show(categoryFragment);
		ft.commit();
		mInContactView = false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK && mInContactView)) {
	    	showCategories();
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}