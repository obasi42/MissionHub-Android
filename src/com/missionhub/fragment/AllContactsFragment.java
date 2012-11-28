package com.missionhub.fragment;

import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
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
import com.missionhub.contactlist.ApiContactListProvider;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.contactlist.ContactListFragment.ContactListFragmentListener;
import com.missionhub.contactlist.ContactListProvider;
import com.missionhub.fragment.ContactAssignmentDialog.ContactAssignmentListener;
import com.missionhub.model.Person;
import com.missionhub.util.U;

public class AllContactsFragment extends MainFragment implements ContactListFragmentListener, ActionMode.Callback, ContactAssignmentListener {

	/** the contact list fragment */
	ContactListFragment mFragment;

	/** the refresh menu item */
	private MenuItem mRefreshItem;

	private ImageView mRefreshingView;

	private ActionMode mActionMode;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (!U.superGetRetainInstance(this)) {
			setRetainInstance(true);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.content_frame, null);

		if (mFragment == null) {
			mFragment = new AllContactsFragmentFragment();
			mFragment.setContactListFragmentListener(this);
			getChildFragmentManager().beginTransaction().add(R.id.content_frame, mFragment).commit();
		}

		// create the refreshing actionbar view
		mRefreshingView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		mRefreshItem = menu.add(Menu.NONE, R.id.menu_item_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	/**
	 * Updates the refresh icon based on the tasks
	 */
	public void updateRefreshIcon() {
		if (mRefreshItem == null || mRefreshingView == null) return;

		if (mFragment != null && mFragment.isWorking()) {
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
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		U.resetActionBar(getSherlockActivity());
		getSherlockActivity().getSupportActionBar().setTitle("All Contacts");
	}

	public static class AllContactsFragmentFragment extends ContactListFragment {
		@Override
		public ContactListProvider onCreateContactProvider() {
			final ApiContactListOptions options = new ApiContactListOptions();

			return new ApiContactListProvider(getActivity(), options);
		}
	}

	@Override
	public void onContactListProviderException(final ContactListFragment fragment, final Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWorkingChanged(final ContactListFragment fragment, final boolean working) {
		updateRefreshIcon();
	}

	@Override
	public boolean onContactLongClick(final ContactListFragment fragment, final Person person, final int position, final long id) {
		// TODO Auto-generated method stub
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
			final Set<Person> people = new HashSet<Person>(mFragment.getCheckedPeople());
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
				mFragment.clearChecked();
			}
		});
	}

	@Override
	public void onAssignmentCompleted() {
		mFragment.reload();
	}

	@Override
	public void onAssignmentCanceled() {}
}