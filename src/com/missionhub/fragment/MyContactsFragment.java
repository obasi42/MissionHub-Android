package com.missionhub.fragment;

import java.util.HashSet;
import java.util.Set;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.activity.ContactActivity;
import com.missionhub.api.ApiContactListOptions;
import com.missionhub.api.ApiContactListOptions.Status;
import com.missionhub.application.Session;
import com.missionhub.contactlist.ApiContactListProvider;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.contactlist.ContactListFragment.ContactListFragmentListener;
import com.missionhub.contactlist.ContactListProvider;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.AddContactDialog.AddContactListener;
import com.missionhub.fragment.ContactAssignmentDialog.ContactAssignmentListener;
import com.missionhub.model.Person;
import com.missionhub.util.U;

public class MyContactsFragment extends MainFragment implements OnPageChangeListener, ContactListFragmentListener, ActionMode.Callback, ContactAssignmentListener, AddContactListener {

	/** the view pager */
	@InjectView(R.id.pager) private ViewPager mPager;

	/** the view pager adapter */
	private FragmentStatePagerAdapter mAdapter;

	/** the all contacts fragment */
	private MyAllContactsFragment mAll;

	/** the in-progress contacts fragment */
	private MyInProgressContactsFragment mInProgress;

	/** the completed contacts fragment */
	private MyCompletedContactsFragment mCompleted;

	/** the current pager page */
	private int mPage = 1;

	/** the refresh menu item */
	private MenuItem mRefreshItem;

	private ImageView mRefreshingView;

	private ActionMode mActionMode;

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
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_contacts, null);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mPager.setOffscreenPageLimit(2);
		mPager.setOnPageChangeListener(this);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(mPage);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.add(Menu.NONE, R.id.menu_item_add_contact, Menu.NONE, R.string.action_add_contact).setIcon(R.drawable.ic_action_add_contact)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		mRefreshItem = menu.add(Menu.NONE, R.id.menu_item_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		U.resetActionBar(getSherlockActivity());
		getSherlockActivity().getSupportActionBar().setTitle(R.string.my_contacts_title);
	}

	public static class MyAllContactsFragment extends ContactListFragment {
		@Override
		public ContactListProvider onCreateContactProvider() {
			final ApiContactListOptions options = new ApiContactListOptions();
			options.setFilterAssignedTo(Session.getInstance().getPersonId());

			return new ApiContactListProvider(getActivity(), options, false);
		}
	}

	public static class MyInProgressContactsFragment extends ContactListFragment {
		@Override
		public ContactListProvider onCreateContactProvider() {
			final ApiContactListOptions options = new ApiContactListOptions();
			options.setFilterAssignedTo(Session.getInstance().getPersonId());
			options.setFilterStatus(Status.uncontacted, Status.attempted_contact, Status.contacted);

			return new ApiContactListProvider(getActivity(), options);
		}
	}

	public static class MyCompletedContactsFragment extends ContactListFragment {
		@Override
		public ContactListProvider onCreateContactProvider() {
			final ApiContactListOptions options = new ApiContactListOptions();
			options.setFilterAssignedTo(Session.getInstance().getPersonId());
			options.setFilterStatus(Status.completed);

			return new ApiContactListProvider(getActivity(), options, false);
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
			final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			mRefreshingView.startAnimation(rotation);
			mRefreshItem.setActionView(mRefreshingView);
		} else {
			mRefreshingView.clearAnimation();
			mRefreshItem.setActionView(null);
		}
	}

	@Override
	public void onPageScrollStateChanged(final int state) {}

	@Override
	public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
		final ContactListFragment fragment = getCurrentFragment();
		if (fragment != null) {
			((ApiContactListProvider) fragment.getProvider()).start();
			fragment.clearChecked();
		}
		if (mActionMode != null) {
			mActionMode.finish();
			mActionMode = null;
		}
	}

	@Override
	public void onPageSelected(final int position) {
		mPage = position;
		updateRefreshIcon();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:
			final ContactListFragment fragment = getCurrentFragment();
			if (fragment != null) {
				fragment.reload();
				return true;
			}
			break;
		case R.id.menu_item_add_contact:
			final AddContactDialog dialog = AddContactDialog.show(getChildFragmentManager(), true);
			dialog.setAddContactListener(this);
			break;
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
		final ExceptionHelper ex = new ExceptionHelper(getActivity(), exception);
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
		ContactActivity.start(getActivity(), person);
	}

	@Override
	public void onContactChecked(final ContactListFragment fragment, final Person person, final int position, final boolean checked) {
		if (mActionMode == null && checked == true) {
			mActionMode = getSherlockActivity().startActionMode(this);
		}
	}

	@Override
	public void onAllContactsUnchecked(final ContactListFragment fragment) {
		if (mActionMode != null) {
			mActionMode.finish();
			mActionMode = null;
		}
	}

	@Override
	public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
		menu.add(Menu.NONE, R.id.menu_item_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;

	}

	@Override
	public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
		if (item.getItemId() == R.id.menu_item_assign) {
			final Set<Person> people = new HashSet<Person>(getCurrentFragment().getCheckedPeople());
			ContactAssignmentDialog.show(getChildFragmentManager(), people).setAssignmentListener(this);
		}
		mode.finish();
		return true;
	}

	@Override
	public void onDestroyActionMode(final ActionMode mode) {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				getCurrentFragment().clearChecked();
			}
		});
	}

	@Override
	public void onAssignmentCompleted() {
		getCurrentFragment().reload();
	}

	@Override
	public void onAssignmentCanceled() {}

	@Override
	public void onContactAdded(final Person contact) {
		ContactActivity.start(getActivity(), contact);
		mAll.reload();
		mInProgress.reload();
	}

	@Override
	public void onAddContactCanceled() {
		// TODO Auto-generated method stub
	}

}