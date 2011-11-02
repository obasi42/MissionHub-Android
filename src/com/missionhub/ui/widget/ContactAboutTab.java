package com.missionhub.ui.widget;

import java.util.Iterator;
import java.util.List;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.ProgressItem;

import com.missionhub.Activity;
import com.missionhub.R;
import com.missionhub.api.model.sql.Assignment;
import com.missionhub.api.model.sql.Education;
import com.missionhub.api.model.sql.Interest;
import com.missionhub.api.model.sql.Location;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.Person;
import com.missionhub.helper.Helper;
import com.missionhub.helper.U;
import com.missionhub.ui.ContactHeaderSmallFragment;
import com.missionhub.ui.widget.item.ContactAboutItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactAboutTab extends LinearLayout {

	private Activity activity;

	private ContactHeaderSmallFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private ProgressItem progressItem;

	private Person person;

	public ContactAboutTab(Context context) {
		super(context);
		activity = (Activity) context;
		setup();
	}

	public ContactAboutTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity) context;
		setup();
	}

	public void setup() {
		LayoutInflater.from(activity).inflate(R.layout.tab_contact_about, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_about)).findViewById(R.id.listview_contact_about);
		
		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_about_header, null);
		mListView.addHeaderView(header, null, false);
		
		mHeader = (ContactHeaderSmallFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_about_header);

		mListAdapter = new ItemAdapter(activity);
		mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ContactAboutItem item = (ContactAboutItem) mListAdapter.getItem(position);
				if (item.action != null) {
					item.action.run();
				}
			}
		});

		progressItem = new ProgressItem(activity.getString(R.string.loading), true);
		progressItem.enabled = false;
	}

	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
	}

	public void update(boolean partial) {
		if (person == null)
			return;

		mListAdapter.setNotifyOnChange(false);
		mListAdapter.clear();

		// Email
		if (!U.nullOrEmpty(person.getEmail_address()))
			mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_email_address), person.getEmail_address(), getResources().getDrawable(
					R.drawable.action_email), new ContactAboutItem.Action() {
				@Override
				public void run() {
					Helper.sendEmail(activity, person.getEmail_address());
				}
			}));

		// Phone
		if (!U.nullOrEmpty(person.getPhone_number()))
			mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_phone_number), Helper.formatPhoneNumber(person.getPhone_number()), getResources()
					.getDrawable(R.drawable.action_call), new ContactAboutItem.Action() {
				@Override
				public void run() {
					Helper.makePhoneCall(activity, person.getPhone_number());
				}
			}));

		// Facebook
		if (!U.nullOrEmpty(person.getFb_id()))
			mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_facebook_header), activity.getString(R.string.contact_info_facebook_link),
					getResources().getDrawable(R.drawable.action_facebook), new ContactAboutItem.Action() {
						@Override
						public void run() {
							Helper.openFacebookProfile(activity, person.getFb_id());
						}
					}));

		mListAdapter.add(progressItem);

		if (!partial) {

			// Role
			if (activity.getUser().hasRole("admin")) {
				String contactRole = "contact";
				for (OrganizationalRole role : person.getOrganizationalRole()) {
					if (role.getOrg_id() == activity.getUser().getOrganizationID()) {
						contactRole = role.getRole();
						break;
					}
				}
				if (contactRole.equals("contact")) {
					mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_role), activity.getString(R.string.contact_role_promote), null,
							new ContactAboutItem.Action() {
								@Override
								public void run() {
									// TODO:
								}
							}));
				} else if (contactRole.equals("leader")) {
					mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_role), activity.getString(R.string.contact_role_demote), null,
							new ContactAboutItem.Action() {
								@Override
								public void run() {
									// TODO:
								}
							}));
				} else if (contactRole.equals("admin")) {
					mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_role), activity.getString(R.string.contact_role_admin), null,
							new ContactAboutItem.Action() {
								@Override
								public void run() {
									// TODO:
								}
							}));
				}
			}

			// Assignment
			List<Assignment> assignedTo = person.getAssigned_to_contacts();
			for (Assignment a : assignedTo) {
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_assigned_to), activity.getApp().getDbSession().getPersonDao()
						.load(a.getAssigned_to_id()).getName(), null, new ContactAboutItem.Action() {
					@Override
					public void run() {
						// TODO:
					}
				}));
			}

			// First Contact Date
			if (!U.nullOrEmpty(person.getFirst_contact_date()))
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_first_contact_date), person.getFirst_contact_date().toLocaleString()));

			// Surveyed Date
			if (!U.nullOrEmpty(person.getDate_surveyed()))
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_surveyed_date), person.getDate_surveyed().toLocaleString()));

			// Birthday
			if (!U.nullOrEmpty(person.getBirthday())) {
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_birthday), person.getBirthday()));
			}

			// Interests
			StringBuffer interests = new StringBuffer();
			Iterator<Interest> interestItr = person.getInterest().iterator();
			while (interestItr.hasNext()) {
				final Interest interest = interestItr.next();
				interests.append(interest.getName());
				if (interestItr.hasNext()) {
					interests.append(", ");
				}
			}
			if (interests.length() > 0) {
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_interests), interests.toString()));
			}

			// Education
			Iterator<Education> eduItr = person.getEducation().iterator();
			while (eduItr.hasNext()) {
				final Education edu = eduItr.next();

				String title = edu.getType();
				if (title == null)
					title = activity.getString(R.string.contact_info_education);

				StringBuffer value = new StringBuffer();

				if (edu.getSchool_name() != null) {
					value.append(edu.getSchool_name());
				}

				if (edu.getYear_name() != null) {
					if (value.length() > 0) {
						value.append(" " + activity.getString(R.string.contact_info_class_of) + " ");
					}
					value.append(edu.getYear_name());
				}
				mListAdapter.add(new ContactAboutItem(title, value.toString()));
			}

			// Location
			Iterator<Location> locationItr = person.getLocation().iterator();
			while (locationItr.hasNext()) {
				final Location loc = locationItr.next();
				mListAdapter.add(new ContactAboutItem(activity.getString(R.string.contact_info_location), loc.getName()));
			}

			// Progress
			mListAdapter.remove(progressItem);
		}

		mListAdapter.notifyDataSetChanged();
	}
}