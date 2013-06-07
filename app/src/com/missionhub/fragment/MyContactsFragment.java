//package com.missionhub.fragment;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.PagerTabStrip;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.View;
//
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.missionhub.R;
//import com.missionhub.api.PeopleListOptions;
//import com.missionhub.application.Application;
//import com.missionhub.application.Session;
//import com.missionhub.ui.widget.LockableViewPager;
//import com.missionhub.util.U.FollowupStatus;
//import org.holoeverywhere.LayoutInflater;
//
//import java.util.EnumSet;
//
//public class MyContactsFragment extends ContactListMainFragment implements OnPageChangeListener {
//
//    private LockableViewPager mPager;
//    private PagerTabStrip mTabStrip;
//    private FragmentStatePagerAdapter mAdapter;
//
//    private MyAllContactsFragment mAll;
//    private MyInProgressContactsFragment mInProgress;
//    private MyCompletedContactsFragment mCompleted;
//
//    private int mPage = 1;
//
//    public MyContactsFragment() {
//    }
//
//    @Override
//    public void onPrepareActionBar(ActionBar actionBar) {
//        actionBar.setTitle(R.string.my_contacts_title);
//    }
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//        setHasOptionsMenu(true);
//
//        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
//            @Override
//            public Fragment getItem(final int index) {
//                switch (index) {
//                    case 0:
//                        mAll = new MyAllContactsFragment();
//                        mAll.setContactListFragmentListener(MyContactsFragment.this);
//                        return mAll;
//                    case 1:
//                        mInProgress = new MyInProgressContactsFragment();
//                        mInProgress.setContactListFragmentListener(MyContactsFragment.this);
//                        return mInProgress;
//                    case 2:
//                        mCompleted = new MyCompletedContactsFragment();
//                        mCompleted.setContactListFragmentListener(MyContactsFragment.this);
//                        return mCompleted;
//                }
//                throw new RuntimeException("Invalid pager index.");
//            }
//
//            @Override
//            public String getPageTitle(final int index) {
//                switch (index) {
//                    case 0:
//                        return getString(R.string.my_contacts_all);
//                    case 1:
//                        return getString(R.string.my_contacts_in_progress);
//                    case 2:
//                        return getString(R.string.my_contacts_completed);
//                    default:
//                        return "Page " + index;
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return 3;
//            }
//        };
//    }
//
//    @Override
//    public View onCreateView(final LayoutInflater inflater, final Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_my_contacts, null);
//    }
//
//    @Override
//    public void onViewCreated(final View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mPager = (LockableViewPager) view.findViewById(R.id.pager);
//        mTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_title_strip);
//
//        mPager.setOffscreenPageLimit(3);
//        mPager.setOnPageChangeListener(this);
//        mPager.setAdapter(mAdapter);
//        mPager.setCurrentItem(mPage);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        getSearchHelper().getSearchView().setQueryHint("Search My Contacts...");
//    }
//
//    @Override
//    public void onActivityCreated(final Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        Application.trackView("My Contacts");
//    }
//
//    public static class MyAllContactsFragment extends ContactListFragment {
//        public MyAllContactsFragment() {
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//
//            Application.trackView("My Contacts/All");
//        }
//
//        @Override
//        public ContactListProvider onCreateContactProvider() {
//            final PeopleListOptions options = PeopleListOptions.builder() //
//                    .assignedTo(Session.getInstance().getPersonId()) //
//                    .build();
//
//            return new ApiContactListProvider(getSupportActivity(), options, false);
//        }
//    }
//
//    public static class MyInProgressContactsFragment extends ContactListFragment {
//        public MyInProgressContactsFragment() {
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//
//            Application.trackView("My Contacts/In-Progress");
//        }
//
//        @Override
//        public ContactListProvider onCreateContactProvider() {
//            final PeopleListOptions options = PeopleListOptions.builder() //
//                    .assignedTo(Session.getInstance().getPersonId()) //
//                    .followupStatus(EnumSet.of(FollowupStatus.uncontacted, FollowupStatus.attempted_contact, FollowupStatus.contacted)) //
//                    .build();
//
//            return new ApiContactListProvider(getSupportActivity(), options);
//        }
//    }
//
//    public static class MyCompletedContactsFragment extends ContactListFragment {
//        public MyCompletedContactsFragment() {
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//
//            Application.trackView("My Contacts/Completed");
//        }
//
//        @Override
//        public ContactListProvider onCreateContactProvider() {
//            final PeopleListOptions options = PeopleListOptions.builder() //
//                    .assignedTo(Session.getInstance().getPersonId()) //
//                    .followupStatus(FollowupStatus.completed) //
//                    .build();
//
//            return new ApiContactListProvider(getSupportActivity(), options, false);
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(final int state) {
//    }
//
//    @Override
//    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
//        final ContactListFragment fragment = getContactListFragment();
//        if (fragment != null) {
//            ((ApiContactListProvider) fragment.getProvider()).start();
//            fragment.clearChecked();
//        }
//        finishActionMode();
//    }
//
//    @Override
//    public void onPageSelected(final int position) {
//        mPage = position;
//        updateRefreshIcon();
//    }
//
//    @Override
//    public ContactListFragment getContactListFragment() {
//        switch (mPage) {
//            case 0:
//                return mAll;
//            case 1:
//                return mInProgress;
//            case 2:
//                return mCompleted;
//        }
//        return null;
//    }
//
//    @Override
//    public void onSearchTextChange(final String query) {
//        if (query.length() == 0) {
//            getContactListFragment().removeAltProvider();
//            mPager.setPagingLocked(LockableViewPager.LOCK_NONE);
//            mTabStrip.setVisibility(View.VISIBLE);
//        } else {
//            mPager.setPagingLocked(LockableViewPager.LOCK_BOTH);
//            mTabStrip.setVisibility(View.GONE);
//
//            PeopleListOptions options = ((ApiContactListProvider) mAll.getProvider()).getOptions();
//            options.addFilter("name_or_email_like", query);
//            ApiContactListProvider searchProvider = new ApiContactListProvider(getSupportActivity(), options);
//            getContactListFragment().setAltProvider(searchProvider);
//            searchProvider.reload();
//        }
//    }
//
//}