package com.missionhub;

import java.util.Iterator;
import java.util.List;

import com.missionhub.api.PeopleSql;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.model.sql.Assignment;
import com.missionhub.api.model.sql.Education;
import com.missionhub.api.model.sql.Interest;
import com.missionhub.api.model.sql.Location;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.Person;
import com.missionhub.helper.Helper;
import com.missionhub.helper.TakePhotoHelper;
import com.missionhub.helper.U;
import com.missionhub.ui.ContactHeaderFragment;
import com.missionhub.ui.ContactHeaderSmallFragment;
import com.missionhub.ui.widget.Tab;
import com.missionhub.ui.widget.item.ContactAboutItem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ItemAdapter;

public class ContactActivity2 extends Activity {

	public static final String TAG = ContactActivity2.class.getSimpleName();

	private int personId = -1;
	private Person person;

	private TabHost mTabHost;
	ActionBarItem pictureActionBarItem;
	private ListView mAboutListView;
	private ListView mStatusListView;
	private ListView mSurveysListView;

	private ContactHeaderFragment mContactStatusHeader;
	private ContactHeaderSmallFragment mContactAboutHeader;
	private ContactHeaderSmallFragment mContactSurveysHeader;

	private ItemAdapter mAboutListAdapter;
	private ItemAdapter mStatusListAdapter;
	private ItemAdapter mSurveysListAdapter;

	private static final String TAG_ABOUT = "about";
	private static final String TAG_STATUS = "status";
	private static final String TAG_SURVEYS = "surveys";

	private static final String PROGRESS_TAKE_PHOTO = "TAKE_PHOTO";

	private static final int RESULT_TAKE_PHOTO = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		personId = getIntent().getIntExtra("personId", -1);
		if (personId < 0) {
			Toast.makeText(getApplicationContext(), R.string.contact_invalid_person, Toast.LENGTH_LONG).show();
			finish();
		}

		setActionBarContentView(R.layout.activity_contact);
		getActionBar().setType(ActionBar.Type.Dashboard);

		// setProgressVisible(true);

		// pictureActionBarItem = addActionBarItem(ActionBarItem.Type.TakePhoto,
		// R.id.action_bar_take_photo);
		// getActionBar().removeItem(pictureActionBarItem);
		addActionBarItem(ActionBarItem.Type.Export, R.id.action_bar_export);
		addActionBarItem(ActionBarItem.Type.Edit, R.id.action_bar_edit);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		setupAboutList();
		setupStatusList();
		setupSurveysList();

		setupTab(R.id.tab_contact_about, TAG_ABOUT, R.string.contact_tab_about, R.drawable.gd_action_bar_info);
		setupTab(R.id.tab_contact_status, TAG_STATUS, R.string.contact_tab_status, R.drawable.gd_action_bar_list);
		setupTab(R.id.tab_contact_surveys, TAG_SURVEYS, R.string.contact_tab_surveys, R.drawable.gd_action_bar_slideshow);

		mTabHost.setCurrentTabByTag(TAG_STATUS);

		getApiNotifier().subscribe(personListener, Type.UPDATE_PERSON, Type.JSON_PEOPLE_ON_START, Type.JSON_PEOPLE_ON_FINISH, Type.JSON_PEOPLE_ON_FAILURE);

		person = getApp().getDbSession().getPersonDao().load(personId);

		updateHeaders();

		update();
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_take_photo:
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(TakePhotoHelper.getTempFile(this)));
			startActivityForResult(intent, RESULT_TAKE_PHOTO);
			showProgress(PROGRESS_TAKE_PHOTO);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_TAKE_PHOTO:
				TakePhotoHelper.onActivityResult(this, requestCode, data);
				break;
			}
		}

		if (requestCode == RESULT_TAKE_PHOTO)
			hideProgress(PROGRESS_TAKE_PHOTO);
	}

	private Handler personListener = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Type.JSON_PEOPLE_ON_START.ordinal() == msg.what) {
				showProgress(msg.getData().getString("tag"));
			}

			if (Type.UPDATE_PERSON.ordinal() == msg.what) {
				if (msg.getData().getInt("personId") == personId) {
					person = getApp().getDbSession().getPersonDao().load(personId);
					updateHeaders();
					updateAboutList();
					updateSurveysList();
				}
			}

			if (Type.JSON_PEOPLE_ON_FINISH.ordinal() == msg.what) {
				hideProgress(msg.getData().getString("tag"));
			}

			if (Type.JSON_PEOPLE_ON_FAILURE.ordinal() == msg.what) {
				Throwable t = (Throwable) msg.getData().getSerializable("throwable");
				Log.e("THROWABLE", "THROWABLE", t);
			}
		}
	};

	private void setupTab(final int contentId, final String tag, int labelId, int iconId) {
		final Tab tab = Tab.inflate(mTabHost.getContext(), null, labelId, iconId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tab).setContent(contentId);
		mTabHost.addTab(setContent);
	}

	public void updateHeaders() {
		mContactAboutHeader.setPerson(person);
		mContactStatusHeader.setPerson(person);
		mContactSurveysHeader.setPerson(person);
	}

	private void update() {
		updateContact();
		updateStatusList();
	}

	private void updateContact() {
		PeopleSql.get(this, personId, ContactActivity2.this.toString());
	}

	private void setupAboutList() {
		mAboutListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_about)).findViewById(R.id.listview_contact_about);

		mContactAboutHeader = (ContactHeaderSmallFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact_about_header);

		mAboutListAdapter = new ItemAdapter(this);
		mAboutListView.setAdapter(mAboutListAdapter);
		
		mAboutListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ContactAboutItem item = (ContactAboutItem) mAboutListAdapter.getItem(position);
				if (item.action != null) {
					item.action.run();
				}
			}
		});
	}

	private void setupStatusList() {
		mStatusListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_status)).findViewById(R.id.listview_contact_status);

		LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_contact_status_header, null);
		mStatusListView.addHeaderView(header, null, false);

		mContactStatusHeader = (ContactHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact_status_header);

		mStatusListAdapter = new ItemAdapter(this);
		mStatusListView.setAdapter(mStatusListAdapter);
	}

	private void setupSurveysList() {
		mSurveysListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_surveys)).findViewById(R.id.listview_contact_surveys);

		mContactSurveysHeader = (ContactHeaderSmallFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_contact_surveys_header);

		mSurveysListAdapter = new ItemAdapter(this);
		mSurveysListView.setAdapter(mSurveysListAdapter);
	}

	private void updateAboutList() {
		if (person == null)
			return;

		mAboutListAdapter.setNotifyOnChange(false);
		mAboutListAdapter.clear();

		// Email
		if (!U.nullOrEmpty(person.getEmail_address()))
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_email_address), person.getEmail_address(), getResources()
					.getDrawable(R.drawable.action_email), new ContactAboutItem.Action() {
				@Override
				public void run() {
					Helper.sendEmail(ContactActivity2.this, person.getEmail_address());
				}
			}));

		// Phone
		if (!U.nullOrEmpty(person.getPhone_number()))
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_phone_number), Helper.formatPhoneNumber(person.getPhone_number()), getResources()
					.getDrawable(R.drawable.action_call), new ContactAboutItem.Action() {
				@Override
				public void run() {
					Helper.makePhoneCall(ContactActivity2.this, person.getPhone_number());
				}
			}));

		// Facebook
		if (!U.nullOrEmpty(person.getFb_id()))
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_facebook_header), getString(R.string.contact_info_facebook_link), getResources()
					.getDrawable(R.drawable.action_facebook), new ContactAboutItem.Action() {
				@Override
				public void run() {
					Helper.openFacebookProfile(ContactActivity2.this, person.getFb_id());
				}
			}));

		// Role
		if (getUser().hasRole("admin")) {
			String contactRole = "contact";
			for (OrganizationalRole role : person.getOrganizationalRole()) {
				if (role.getOrg_id() == getUser().getOrganizationID()) {
					contactRole = role.getRole();
					break;
				}
			}
			if (contactRole.equals("contact")) {
				mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_role), getString(R.string.contact_role_promote), null, new ContactAboutItem.Action() {
					@Override
					public void run() {
						// TODO:
					}
				}));
			} else if (contactRole.equals("leader")) {
				mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_role), getString(R.string.contact_role_demote), null, new ContactAboutItem.Action() {
					@Override
					public void run() {
						// TODO:
					}
				}));
			} else if (contactRole.equals("admin")) {
				mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_role), getString(R.string.contact_role_admin), null, new ContactAboutItem.Action() {
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
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_assigned_to), getApp().getDbSession().getPersonDao().load(a.getAssigned_to_id()).getName(),
					null, new ContactAboutItem.Action() {
						@Override
						public void run() {
							// TODO:
						}
					}));
		}

		// First Contact Date
		if (!U.nullOrEmpty(person.getFirst_contact_date()))
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_first_contact_date), person.getFirst_contact_date().toLocaleString()));

		// Surveyed Date
		if (!U.nullOrEmpty(person.getDate_surveyed()))
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_surveyed_date), person.getDate_surveyed().toLocaleString()));

		// Birthday
		if (!U.nullOrEmpty(person.getBirthday())) {
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_birthday), person.getBirthday()));
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
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_interests), interests.toString()));
		}

		// Education
		Iterator<Education> eduItr = person.getEducation().iterator();
		while (eduItr.hasNext()) {
			final Education edu = eduItr.next();

			String title = edu.getType();
			if (title == null)
				title = getString(R.string.contact_info_education);

			StringBuffer value = new StringBuffer();

			if (edu.getSchool_name() != null) {
				value.append(edu.getSchool_name());
			}

			if (edu.getYear_name() != null) {
				if (value.length() > 0) {
					value.append(" " + getString(R.string.contact_info_class_of) + " ");
				}
				value.append(edu.getYear_name());
			}

			mAboutListAdapter.add(new ContactAboutItem(title, value.toString()));
		}

		Iterator<Location> locationItr = person.getLocation().iterator();
		while (locationItr.hasNext()) {
			final Location location = locationItr.next();
			mAboutListAdapter.add(new ContactAboutItem(getString(R.string.contact_info_location), location.getName()));
		}

		mAboutListAdapter.notifyDataSetChanged();
	}

	private void updateStatusList() {
		if (person == null)
			return;

	}

	private void updateSurveysList() {
		if (person == null)
			return;

	}
}