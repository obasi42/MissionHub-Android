package com.missionhub.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.missionhub.R;
import com.missionhub.api.PersonListOptions;
import com.missionhub.application.Session;
import com.missionhub.contactlist.ApiContactListProvider;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.contactlist.ContactListProvider;
import com.missionhub.ui.widget.LockedViewPager;
import com.missionhub.util.U;
import com.missionhub.util.U.FollowupStatus;
import org.holoeverywhere.LayoutInflater;

import java.util.EnumSet;

public class MyContactsFragment extends ContactListMainFragment implements OnPageChangeListener {

    private LockedViewPager mPager;
    private PagerTabStrip mTabStrip;
    private FragmentStatePagerAdapter mAdapter;

    private MyAllContactsFragment mAll;
    private MyInProgressContactsFragment mInProgress;
    private MyCompletedContactsFragment mCompleted;

    private int mPage = 1;

    public MyContactsFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(final int index) {
                switch (index) {
                    case 0:
                        mAll = new MyAllContactsFragment();
                        mAll.setContactListFragmentListener(MyContactsFragment.this);
                        return mAll;
                    case 1:
                        mInProgress = new MyInProgressContactsFragment();
                        mInProgress.setContactListFragmentListener(MyContactsFragment.this);
                        return mInProgress;
                    case 2:
                        mCompleted = new MyCompletedContactsFragment();
                        mCompleted.setContactListFragmentListener(MyContactsFragment.this);
                        return mCompleted;
                }
                throw new RuntimeException("Invalid pager index.");
            }

            @Override
            public String getPageTitle(final int index) {
                switch (index) {
                    case 0:
                        return getString(R.string.my_contacts_all);
                    case 1:
                        return getString(R.string.my_contacts_in_progress);
                    case 2:
                        return getString(R.string.my_contacts_completed);
                    default:
                        return "Page " + index;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_contacts, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = (LockedViewPager) view.findViewById(R.id.pager);
        mTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_title_strip);

        mPager.setPagingLocked(false);
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(this);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mPage);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSearchHelper().getSearchView().setQueryHint("Search My Contacts...");
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        U.resetActionBar(getSupportActivity());

        getSupportActivity().getSupportActionBar().setTitle(R.string.my_contacts_title);
    }

    public static class MyAllContactsFragment extends ContactListFragment {
        public MyAllContactsFragment() {
        }

        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder() //
                    .assignedTo(Session.getInstance().getPersonId()) //
                    .build();

            return new ApiContactListProvider(getSupportActivity(), options, false);
        }
    }

    public static class MyInProgressContactsFragment extends ContactListFragment {
        public MyInProgressContactsFragment() {
        }

        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder() //
                    .assignedTo(Session.getInstance().getPersonId()) //
                    .followupStatus(EnumSet.of(FollowupStatus.uncontacted, FollowupStatus.attempted_contact, FollowupStatus.contacted)) //
                    .build();

            return new ApiContactListProvider(getSupportActivity(), options);
        }
    }

    public static class MyCompletedContactsFragment extends ContactListFragment {
        public MyCompletedContactsFragment() {
        }

        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder() //
                    .assignedTo(Session.getInstance().getPersonId()) //
                    .followupStatus(FollowupStatus.completed) //
                    .build();

            return new ApiContactListProvider(getSupportActivity(), options, false);
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        final ContactListFragment fragment = getContactListFragment();
        if (fragment != null) {
            ((ApiContactListProvider) fragment.getProvider()).start();
            fragment.clearChecked();
        }
        finishActionMode();
    }

    @Override
    public void onPageSelected(final int position) {
        mPage = position;
        updateRefreshIcon();
    }

    @Override
    public ContactListFragment getContactListFragment() {
        switch (mPage) {
            case 0:
                return mAll;
            case 1:
                return mInProgress;
            case 2:
                return mCompleted;
        }
        return null;
    }

    @Override
    public void onSearchTextChange(final String query) {
        if (query.length() == 0) {
            getContactListFragment().removeAltProvider();
            mPager.setPagingLocked(false);
            mTabStrip.setVisibility(View.VISIBLE);
        } else {
            mPager.setPagingLocked(true);
            mTabStrip.setVisibility(View.GONE);

            PersonListOptions options = ((ApiContactListProvider) mAll.getProvider()).getOptions();
            options.addFilter("name_or_email_like", query);
            ApiContactListProvider searchProvider = new ApiContactListProvider(getSupportActivity(), options);
            getContactListFragment().setAltProvider(searchProvider);
            searchProvider.reload();
        }
    }

}