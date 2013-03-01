package com.missionhub.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.R;
import com.missionhub.android.activity.ContactActivity;
import com.missionhub.android.api.PersonListOptions;
import com.missionhub.android.application.Session;
import com.missionhub.android.contactlist.ApiContactListProvider;
import com.missionhub.android.contactlist.ContactListFragment;
import com.missionhub.android.contactlist.ContactListFragment.ContactListFragmentListener;
import com.missionhub.android.contactlist.ContactListProvider;
import com.missionhub.android.exception.ExceptionHelper;
import com.missionhub.android.fragment.dialog.ContactAssignmentDialogFragment;
import com.missionhub.android.fragment.dialog.ContactLabelsDialogFragment;
import com.missionhub.android.fragment.dialog.EditContactDialogFragment;
import com.missionhub.android.model.Person;
import com.missionhub.android.util.U;
import com.missionhub.android.util.U.FollowupStatus;
import org.holoeverywhere.LayoutInflater;

import java.util.EnumSet;

public class MyContactsFragment extends MainFragment implements OnPageChangeListener, ContactListFragmentListener, ActionMode.Callback {

    /**
     * the view pager
     */
    private ViewPager mPager;

    /**
     * the view pager adapter
     */
    private FragmentStatePagerAdapter mAdapter;

    /**
     * the all contacts fragment
     */
    private MyAllContactsFragment mAll;

    /**
     * the in-progress contacts fragment
     */
    private MyInProgressContactsFragment mInProgress;

    /**
     * the completed contacts fragment
     */
    private MyCompletedContactsFragment mCompleted;

    /**
     * the current pager page
     */
    private int mPage = 1;

    /**
     * the refresh menu item
     */
    private MenuItem mRefreshItem;

    private ImageView mRefreshingView;

    private ActionMode mActionMode;

    public static int REQUEST_ASSIGNMENT = 1;
    public static int REQUEST_LABELS = 2;
    public static int REQUEST_EDIT_CONTACT = 3;

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mRefreshingView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);
        return inflater.inflate(R.layout.fragment_my_contacts, null);
    }

    @Override
    public void onViewCreated(final View view) {
        super.onViewCreated(view);

        mPager = (ViewPager) view.findViewById(R.id.pager);

        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(this);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mPage);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(Menu.NONE, R.id.action_add_contact, Menu.NONE, R.string.action_add_contact).setIcon(R.drawable.ic_action_add_contact)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        mRefreshItem = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        U.resetActionBar(getSupportActivity());
        getSupportActivity().getSupportActionBar().setTitle(R.string.my_contacts_title);
    }

    public static class MyAllContactsFragment extends ContactListFragment {
        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder() //
                    .assignedTo(Session.getInstance().getPersonId()) //
                    .build();

            return new ApiContactListProvider(getSupportActivity(), options, false);
        }
    }

    public static class MyInProgressContactsFragment extends ContactListFragment {
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
        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder() //
                    .assignedTo(Session.getInstance().getPersonId()) //
                    .followupStatus(FollowupStatus.completed) //
                    .build();

            return new ApiContactListProvider(getSupportActivity(), options, false);
        }
    }

    /**
     * Updates the refresh icon based on the tasks
     */
    public void updateRefreshIcon() {
        if (mRefreshItem == null || mRefreshingView == null) return;

        final ContactListFragment fragment = getCurrentFragment();

        if (fragment == null || !fragment.isVisible()) return;

        if (fragment.isWorking()) {
            final Animation rotation = AnimationUtils.loadAnimation(getSupportActivity(), R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            mRefreshingView.startAnimation(rotation);
            mRefreshItem.setActionView(mRefreshingView);
        } else {
            mRefreshingView.clearAnimation();
            mRefreshItem.setActionView(null);
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        final ContactListFragment fragment = getCurrentFragment();
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                final ContactListFragment fragment = getCurrentFragment();
                if (fragment != null) {
                    fragment.reload();
                    return true;
                }
                break;
            case R.id.action_add_contact:
                EditContactDialogFragment.showForResult(getSupportActivity(), getChildFragmentManager(), true, REQUEST_EDIT_CONTACT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ContactListFragment getCurrentFragment() {
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

    // /** the search menu item helper */
    // private SearchMenuItemHelper mSearchMenuItemHelper;
    //
    // /** the contact list options provider */
    // ContactListOptionsProvider mProvider;
    //
    // /** the provider for search results */
    // ContactListOptionsProvider mSearchProvider;
    //
    // /** the contact list options */
    // ContactListOptions mOptions = new ContactListOptions();
    //
    // @Override
    // public void onCreate(final Bundle savedInstanceState) {
    // super.onCreate(savedInstanceState);
    //
    // if (savedInstanceState != null) {
    // mProvider = (ContactListOptionsProvider)
    // ObjectStore.getInstance().retrieveObject(savedInstanceState.getString("mProvider"));
    // mSearchProvider = (ContactListOptionsProvider)
    // ObjectStore.getInstance().retrieveObject(savedInstanceState.getString("mSearchProvider"));
    // }
    //
    // setHasOptionsMenu(true);
    // }
    //
    // @Override
    // public void onAttach(final Activity activity) {
    // super.onAttach(activity);
    //
    // mSearchMenuItemHelper = new SearchMenuItemHelper(this);
    // mSearchMenuItemHelper.setListener(this);
    // }
    //
    // @Override
    // public void onActivityCreated(Bundle savedInstanceState) {
    // super.onActivityCreated(savedInstanceState);
    //
    // if (mSearchMenuItemHelper != null) mSearchMenuItemHelper.onRestoreInstanceState(savedInstanceState);
    // }
    //
    // @Override
    // public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    // super.onCreateOptionsMenu(menu, inflater);
    //
    // mSearchMenuItemHelper.onCreateOptionsMenu(menu, inflater);
    // }
    //
    // @Override
    // public ContactListProvider onCreateProvider() {
    // if (mProvider == null)
    // mProvider = new ContactListOptionsProvider(mOptions);
    //
    // if (mSearchProvider == null)
    // mSearchProvider = new ContactListOptionsProvider();
    //
    // return mProvider;
    // }
    //
    // @Override
    // public void onSearchTextChange(final String query) {
    // if (query.length() == 0) {
    // setProvider(mProvider);
    // } else {
    // final ContactListOptions options = new ContactListOptions();
    // options.addFilter("name", query);
    // mSearchProvider.setOptions(options);
    // setProvider(mSearchProvider);
    // }
    // }
    //
    // @Override
    // public void onSearchSubmit(final String query) {
    // onSearchTextChange(query);
    // }
    //
    // @Override
    // public void onSaveInstanceState(Bundle outState) {
    // super.onSaveInstanceState(outState);
    //
    // outState.putString("mProvider", ObjectStore.getInstance().storeObject(mProvider, this));
    // outState.putString("mSearchProvider", ObjectStore.getInstance().storeObject(mSearchProvider, this));
    //
    // if (mSearchMenuItemHelper != null) mSearchMenuItemHelper.onSaveInstanceState(outState);
    // }

    @Override
    public void onContactListProviderException(final ContactListFragment fragment, final Exception exception) {
        final ExceptionHelper ex = new ExceptionHelper(getSupportActivity(), exception);
        ex.makeToast();
    }

    @Override
    public void onWorkingChanged(final ContactListFragment fragment, final boolean working) {
        updateRefreshIcon();

    }

    @Override
    public boolean onContactLongClick(final ContactListFragment fragment, final Person person, final int position, final long id) {
        return false;
    }

    @Override
    public void onContactClick(final ContactListFragment fragment, final Person person, final int position, final long id) {
        ContactActivity.start(getSupportActivity(), person);
    }

    @Override
    public void onContactChecked(final ContactListFragment fragment, final Person person, final int position, final boolean checked) {
        if (mActionMode == null && checked == true) {
            mActionMode = getSupportActivity().startActionMode(this);
        }
    }

    @Override
    public void onAllContactsUnchecked(final ContactListFragment fragment) {
        finishActionMode();
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        menu.add(Menu.NONE, R.id.action_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.action_label, Menu.NONE, R.string.action_label).setIcon(R.drawable.ic_action_label)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        if (item.getItemId() == R.id.action_assign) {
            ContactAssignmentDialogFragment.showForResult(getSupportActivity(), getChildFragmentManager(), getCurrentFragment().getCheckedPeople(), REQUEST_ASSIGNMENT);
        }
        if (item.getItemId() == R.id.action_label) {
            ContactLabelsDialogFragment.showForResult(getSupportActivity(), getChildFragmentManager(), getCurrentFragment().getCheckedPeople(), REQUEST_LABELS);
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        if (getCurrentFragment().getCheckedItemCount() > 0) {
            getCurrentFragment().clearChecked();
        }
        mActionMode = null;
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (requestCode == REQUEST_EDIT_CONTACT && resultCode == RESULT_OK) {
            if (data != null && data instanceof Person) {
                ContactActivity.start(getSupportActivity(), (Person) data);
                mAll.reload();
                mInProgress.reload();
                return true;
            }
        } else if (requestCode == REQUEST_ASSIGNMENT && resultCode == RESULT_OK) {
            getCurrentFragment().reload();
            finishActionMode();
            return true;
        } else if (requestCode == REQUEST_LABELS && resultCode == RESULT_OK) {
            getCurrentFragment().reload();
            finishActionMode();
            return true;
        }
        return super.onFragmentResult(requestCode, resultCode, data);
    }

    private void finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

}