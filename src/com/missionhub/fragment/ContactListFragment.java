package com.missionhub.fragment;

import java.util.List;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.WazaBe.HoloEverywhere.widget.Toast;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.ContactListOptions;
import com.missionhub.model.Person;
import com.missionhub.ui.ContactListProvider;
import com.missionhub.ui.widget.ContactListView;
import com.missionhub.ui.widget.ContactListView.OnContactCheckedListener;
import com.missionhub.ui.widget.ContactListView.OnContactClickListener;
import com.missionhub.ui.widget.ContactListView.OnContactLongClickListener;

public class ContactListFragment extends BaseFragment implements OnContactCheckedListener, OnContactClickListener, OnContactLongClickListener {

	/** the logging tag */
	public static final String TAG = ContactListFragment.class.getSimpleName();

	/** the list provider */
	private ContactListProvider mProvider;

	@InjectView(R.id.contact_list) private ContactListView mListView;

	public ContactListFragment() {}

	public ContactListView getListView() {
		return mListView;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			mProvider = new ContactListProvider() {

				public ContactListOptions options = new ContactListOptions();

				@Override
				public List<Person> getMore() throws Exception {
					options.setFilter("assigned_to", "me");
					
					final List<Person> people = Api.getContactList(options).get();

					// set up the options for the next run
					if (people.size() < options.getLimit()) {
						options.setIsAtEnd(true);
						options.incrementStart(people.size());
					} else {
						options.advanceStart();
					}

					return people;
				}

				@Override
				public boolean isAtEnd() {
					return options.isAtEnd();
				}

				@Override
				public void onError(final Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			};
			mListView.setProvider(mProvider);
		}
		mListView.setOnContactClickListener(this);
		mListView.setOnContactCheckedListener(this);
		mListView.setOnContactLongClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mProvider = mListView.getProvider();
	}

	@Override
	public boolean onContactLongClick(final Person person, final int position, final long id) {

		Toast.makeText(getActivity(), "Long Click: " + person.getName(), Toast.LENGTH_SHORT).show();
		
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onContactClick(final Person person, final int position, final long id) {
		
		Toast.makeText(getActivity(), "Click: " + person.getName(), Toast.LENGTH_SHORT).show();
		
		// TODO Auto-generated method stub

	}

	@Override
	public void onContactChecked(final Person person, final int position, final boolean checked) {
		// TODO Auto-generated method stub

		Toast.makeText(getActivity(), "Checked: " + person.getName(), Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onAllContactsUnchecked() {
		// TODO Auto-generated method stub

		Toast.makeText(getActivity(), "Unchecked All", Toast.LENGTH_SHORT).show();
		
	}

}