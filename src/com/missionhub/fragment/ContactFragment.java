package com.missionhub.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.ViewPagerAdapter;
import com.missionhub.ui.widget.ContactAboutView;
import com.missionhub.ui.widget.ContactStatusView;
import com.missionhub.ui.widget.ContactSurveysView;
import com.missionhub.ui.widget.FragmentLoadingView;
import com.viewpagerindicator.PageIndicator;

public class ContactFragment extends MissionHubFragment {
	
	/** the personId for this contact */
	long mPersonId = -1;
	
	/** the sql person for this contact */
	Person mPerson;

	/** the loading view */
	FragmentLoadingView mLoading;

	/** the contact container */
	ViewGroup mContainer;

	/** the contact view pager */
	ViewPager mPager;

	/** the contact tab page indicator */
	PageIndicator mIndicator;

	/** the view pager adapter */
	ViewPagerAdapter mAdapter;

	/** the pager views */
	ContactStatusView mStatus;
	ContactAboutView mAbout;
	ContactSurveysView mSurveys;
	
	@Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
        	setPersonId(args.getLong("personId", -1));
        }
    }

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact, container, false);
		view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));

		mLoading = (FragmentLoadingView) view.findViewById(R.id.loading);
		mContainer = (ViewGroup) view.findViewById(R.id.container);

		mPager = (ViewPager) view.findViewById(R.id.pager);
		mIndicator = (PageIndicator) view.findViewById(R.id.indicator);

		mStatus = new ContactStatusView(inflater.getContext());
		mAbout = new ContactAboutView(inflater.getContext());
		mSurveys = new ContactSurveysView(inflater.getContext());

		mAdapter = new ViewPagerAdapter();
		mAdapter.setNotifyOnChange(false);
		mAdapter.addPage(mAbout, "About");
		mAdapter.addPage(mStatus, "Status");
		mAdapter.addPage(mSurveys, "Surveys");
		mAdapter.notifyDataSetChanged();

		mPager.setAdapter(mAdapter);

		mPager.setCurrentItem(1);
		mIndicator.setViewPager(mPager, 1);

		mContainer.setVisibility(View.VISIBLE);
		mLoading.setVisibility(View.GONE);

		return view;
	}
	
	public void redraw() {
		
	}
	
	public void setPersonId(long personId) {
		Person person = getMHActivity().getDbSession().getPersonDao().load(personId);
		if (person != null) {
			setPerson(person);
		}
	}
	
	public void setPerson(Person person) {
		mPersonId = person.getId();
		mPerson = person;
	}

	public static ContactFragment newInstance(final long personId) {
		final ContactFragment f = new ContactFragment();
		final Bundle args = new Bundle();
		args.putLong("personId", personId);
		f.setArguments(args);
		return f;
	}
	
	public long getPersonId() {
		return mPersonId;
	}
}