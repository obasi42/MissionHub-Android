package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;

public class ContactSurveysFragment extends BaseFragment {

	/** the person id of the displayed contact */
	private long mPersonId = -1;

	/** the person object of the displayed contact */
	private Person mPerson;

	public static ContactSurveysFragment instantiate(final long personId) {
		final Bundle bundle = new Bundle();
		bundle.putLong("personId", personId);

		final ContactSurveysFragment fragment = new ContactSurveysFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(false);

		final Bundle arguments = getArguments();
		mPersonId = arguments.getLong("personId", -1);
		mPerson = Application.getDb().getPersonDao().load(mPersonId);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact_surveys, null);

		return view;
	}

	public static class SurveysArrayAdapter extends ObjectArrayAdapter {

		public SurveysArrayAdapter(final Context context) {
			super(context);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			return getView(position, convertView, parent);
		}

	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void notifyPersonUpdated() {
		// TODO Auto-generated method stub

	}

	public boolean isWorking() {
		// TODO Auto-generated method stub
		return false;
	}

}