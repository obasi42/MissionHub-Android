package com.missionhub.fragment;

import org.holoeverywhere.widget.Toast;

import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.Api.Include;
import com.missionhub.application.Application;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Person;
import com.missionhub.ui.NavigationSpinnerAdapter;
import com.missionhub.ui.widget.LockedViewPager;
import com.missionhub.util.U;
import com.missionhub.util.facebook.FacebookImageDownloader;

public class ContactFragment extends BaseFragment implements OnNavigationListener, OnPageChangeListener {

	/** the person id of the displayed contact */
	private long mPersonId = -1;

	/** the person object of the displayed contact */
	private Person mPerson;

	/** the current pager page */
	private int mPage = 0;

	/** the view pager */
	@InjectView(R.id.pager) private LockedViewPager mPager;

	/** the view pager adapter */
	private FragmentStatePagerAdapter mAdapter;

	/** the contact info fragment */
	private ContactInfoFragment mInfoFragment;

	/** the survey results fragment */
	private ContactSurveysFragment mSurveysFragment;

	/** the task used to update the contact */
	private SafeAsyncTask<Person> mContactTask;

	/** the refresh actionbar menu item */
	private MenuItem mRefreshItem;

	/** the image view for the actionbar refreshing icon */
	private ImageView mRefreshingView;

	public static ContactFragment instantiate(final Person person) {
		return instantiate(person.getId());
	}

	public static ContactFragment instantiate(final long personId) {
		final Bundle bundle = new Bundle();
		bundle.putLong("personId", personId);

		final ContactFragment fragment = new ContactFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		final Person oldPerson = mPerson;

		if (getArguments() != null) {
			mPersonId = getArguments().getLong("personId", -1);
			mPerson = Application.getDb().getPersonDao().load(mPersonId);
		}

		if (mPersonId < 0 || mPerson == null) {
			if (mContactTask != null) {
				mContactTask.cancel(true);
			}
			Toast.makeText(getActivity(), "No person provided for this fragment.", Toast.LENGTH_SHORT).show();
			activity.finish();
		}

		if (oldPerson == null) {
			refreshContact();
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mRefreshingView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_contact, null);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mAdapter == null) {
			mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

				@Override
				public Fragment getItem(final int index) {
					Log.e("CREATE FRAGMENT", "CREATE FRAGMENT " + index);
					switch (index) {
					case 0:
						mInfoFragment = ContactInfoFragment.instantiate(mPersonId);
						return mInfoFragment;
					case 1:
						mSurveysFragment = ContactSurveysFragment.instantiate(mPersonId);
						return mSurveysFragment;
					default:
						throw new RuntimeException("Index out of bounds");
					}
				}

				@Override
				public int getCount() {
					return 2;
				}
			};
		}

		mPager.setPagingLocked(false);
		mPager.setOnPageChangeListener(this);
		mPager.setOffscreenPageLimit(2); // prevent the fragments from being removed
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(mPage);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		U.resetActionBar(getSherlockActivity());

		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(new NavigationSpinnerAdapter(getActivity(), R.string.contact_nav_info, R.string.contact_nav_surveys), this);
		getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(mPager.getCurrentItem());
	}

	@Override
	public boolean onNavigationItemSelected(final int index, final long id) {
		if (mRefreshingView != null && mRefreshItem != null) {
			mRefreshingView.clearAnimation();
			mRefreshItem.setActionView(null);
		}
		getSherlockActivity().invalidateOptionsMenu();
		switch (index) {
		case 0:
			mPager.setCurrentItem(0, true);
			return true;
		case 1:
			mPager.setCurrentItem(1, true);
			return true;
		}
		return false;
	}

	@Override
	public void onPageScrollStateChanged(final int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(mPager.getCurrentItem());
		}
		mPage = mPager.getCurrentItem();
	}

	@Override
	public void onPageScrolled(final int index, final float positionOffset, final int positionOffsetPixels) {}

	@Override
	public void onPageSelected(final int index) {}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		// TODO remove this logic when nested fragment menus work
		if (mInfoFragment != null && mPage == 0) {
			mInfoFragment.onCreateOptionsMenu(menu, inflater);
		}
		if (mSurveysFragment != null && mPage == 1) {
			mSurveysFragment.onCreateOptionsMenu(menu, inflater);
		}

		mRefreshItem = menu.add(Menu.NONE, R.id.menu_item_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		updateRefreshIcon();

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// TODO remove this logic when nested fragment menus work
		if (item.getItemId() == R.id.menu_item_refresh) {
			refreshContact();
			return true;
		}
		if (mPage == 0) {
			return mInfoFragment.onOptionsItemSelected(item);
		} else if (mPage == 1) {
			return mSurveysFragment.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	public synchronized void refreshContact() {
		if (mContactTask != null) {
			mContactTask.cancel(true);
		}
		mContactTask = new SafeAsyncTask<Person>() {

			@Override
			public Person call() throws Exception {
				return Api.getPerson(mPersonId, ApiOptions.builder() //
					.include(Include.answer_sheets)
					.include(Include.answers)
					.include(Include.comments_on_me)
					.include(Include.contact_assignments)
					.include(Include.current_address)
					.include(Include.email_addresses)
					.include(Include.organizational_roles)
					.include(Include.phone_numbers)
					.build()).get();
			}

			@Override
			public void onSuccess(final Person person) {
				mPersonId = person.getId();
				mPerson = person;
				FacebookImageDownloader.removeFromCache(mPerson);
				if (mInfoFragment != null) {
					mInfoFragment.notifyContactUpdated();
				}
				if (mSurveysFragment != null) {
					mSurveysFragment.notifyContactUpdated();
				}
			}

			@Override
			public void onFinally() {
				mContactTask = null;
				updateRefreshIcon();
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
				eh.makeToast("Failed to refresh contact.");
			}

			@Override
			public void onInterrupted(final Exception e) {

			}
		};
		updateRefreshIcon();
		Application.getExecutor().execute(mContactTask.future());
	}

	public boolean isInfoWorking() {
		if (mInfoFragment != null) {
			return mInfoFragment.isWorking();
		}
		return false;
	}

	public boolean isSurveysWorking() {
		if (mSurveysFragment != null) {
			return mSurveysFragment.isWorking();
		}
		return false;
	}

	/**
	 * Updates the refresh icon based on the tasks
	 */
	public void updateRefreshIcon() {
		if (mRefreshItem == null || mRefreshingView == null) return;

		if (mContactTask != null || isInfoWorking() || isSurveysWorking()) {
			final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			mRefreshingView.startAnimation(rotation);
			mRefreshItem.setActionView(mRefreshingView);
		} else {
			mRefreshingView.clearAnimation();
			mRefreshItem.setActionView(null);
		}
	}

}