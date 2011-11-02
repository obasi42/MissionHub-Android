package com.missionhub.ui.widget;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.ProgressItem;

import com.missionhub.Activity;
import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.ContactHeaderFragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ContactStatusTab extends LinearLayout {

	private Activity activity;

	private ContactHeaderFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private ProgressItem progressItem;

	private Person person;

	public ContactStatusTab(Context context) {
		super(context);
		activity = (Activity) context;
		setup();
	}

	public ContactStatusTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity) context;
		setup();
	}

	public void setup() {
		LayoutInflater.from(activity).inflate(R.layout.tab_contact_status, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_status)).findViewById(R.id.listview_contact_status);

		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_status_header, null);
		mListView.addHeaderView(header, null, false);

		mHeader = (ContactHeaderFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_status_header);

		mListAdapter = new ItemAdapter(activity);
		mListView.setAdapter(mListAdapter);

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

		mListAdapter.notifyDataSetChanged();
	}
}