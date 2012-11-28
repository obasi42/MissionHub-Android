package com.missionhub.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.Toast;

import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockDialogFragment;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.ContactAssignmentType;
import com.missionhub.application.Application;
import com.missionhub.application.DrawableCache;
import com.missionhub.application.Session;
import com.missionhub.application.Session.NoPersonException;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Assignment;
import com.missionhub.model.AssignmentDao;
import com.missionhub.model.Group;
import com.missionhub.model.OrganizationalRole;
import com.missionhub.model.OrganizationalRoleDao;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.LockedViewPager;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ContactAssignmentDialog extends RoboSherlockDialogFragment implements OnKeyListener, OnItemClickListener {

	/** the view pager */
	private LockedViewPager mPager;

	/** the view pager adapter */
	private FragmentStatePagerAdapter mPagerAdapter;

	/** the current view pager page */
	private int mPage = 0;

	/** the first or index fragment */
	private IndexFragment mIndexFragment;

	/** the selection fragment for leaders/organizations */
	private SelectionFragment mSelectionFragment;

	/** the list of people to assign */
	private Set<Person> mPeople = new HashSet<Person>();

	/** the contact assignment listener */
	private WeakReference<ContactAssignmentListener> mListener;

	/** true after the dialog has been dismissed */
	private boolean mCanceled = false;

	/** the task used to process assignments */
	private RoboAsyncTask<Boolean> mTask;

	/** the progress view */
	private View mProgress;

	public ContactAssignmentDialog() {}

	/**
	 * Creates a new assignment dialog for the given person
	 * 
	 * @param person
	 * @return
	 */
	public static ContactAssignmentDialog getInstance(final Person person) {
		final HashSet<Person> people = new HashSet<Person>();
		people.add(person);
		return getInstance(people);
	}

	/**
	 * Creates a new mass assignment dialog for a group of people
	 * 
	 * @param people
	 * @return
	 */
	public static ContactAssignmentDialog getInstance(final Set<Person> people) {
		final ContactAssignmentDialog dialog = new ContactAssignmentDialog();
		final Bundle args = new Bundle();
		final HashSet<Person> copyList = new HashSet<Person>(people);
		args.putSerializable("people", copyList);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getParentFragment() == null) {
			setRetainInstance(true);
		}

		if (getArguments() != null) {
			@SuppressWarnings("unchecked") final HashSet<Person> people = (HashSet<Person>) getArguments().getSerializable("people");
			if (people != null) {
				mPeople = new HashSet<Person>(people);
			}
		}

		setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Sherlock_Light_Dialog);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_assignment_dialog, null);

		mCanceled = false;
		mPager = (LockedViewPager) view.findViewById(R.id.pager);
		mProgress = view.findViewById(R.id.progress_container);

		if (mPagerAdapter == null) {
			mPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
				@Override
				public Fragment getItem(final int position) {
					switch (position) {
					case 0:
						mIndexFragment = new IndexFragment();
						return mIndexFragment;
					case 1:
						mSelectionFragment = new SelectionFragment();
						return mSelectionFragment;
					default:
						throw new RuntimeException("invalid pager index");
					}
				}

				@Override
				public int getCount() {
					return 2;
				}
			};
		}
		mPager.setOffscreenPageLimit(1);
		mPager.setPagingLocked(true);
		mPager.setAdapter(mPagerAdapter);

		if (mPage == 0) {
			showIndex(false);
		} else {
			if (mSelectionFragment != null) {
				if (mSelectionFragment.mSelection == IndexFragment.LEADERS) {
					showLeaders(false);
				} else if (mSelectionFragment.mSelection == IndexFragment.GROUPS) {
					showGroups(false);
				} else {
					mPager.setCurrentItem(mPage);
				}
			} else {
				mPager.setCurrentItem(mPage);
			}
		}

		if (mTask != null) {
			showProgress();
		}

		return view;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCancelable(false);
		getDialog().setOnKeyListener(this);
	}

	/**
	 * Shows the index fragment
	 */
	public void showIndex(final boolean animate) {
		mPage = 0;
		mPager.setCurrentItem(0, animate);
		if (getDialog() != null) {
			if (mPeople.size() > 1) {
				getDialog().setTitle("Mass Assign To:");
			} else {
				getDialog().setTitle("Assign To:");
			}
		}
	}

	/**
	 * Shows the selection fragment for groups
	 */
	public void showGroups(final boolean animate) {
		mSelectionFragment.showGroups();
		mPage = 1;
		mPager.setCurrentItem(1, animate);
		if (getDialog() != null) {
			getDialog().setTitle("Assign to Group");
		}
	}

	/**
	 * Shows the selection fragment for leaders
	 */
	public void showLeaders(final boolean animate) {
		mSelectionFragment.showLeaders();
		mPage = 1;
		mPager.setCurrentItem(1, animate);
		if (getDialog() != null) {
			getDialog().setTitle("Assign to Leader");
		}
	}

	public static class AssignNoneItem {}

	public static class AssignMeItem {
		public Person me;

		public AssignMeItem(final Person me) {
			this.me = me;
		}
	}

	public static class LeaderItem {
		public Person leader;

		public LeaderItem(final Person leader) {
			this.leader = leader;
		}
	}

	public static class GroupItem {
		public Group group;

		public GroupItem(final Group group) {
			this.group = group;
		}
	}

	public static class SelectionItem {
		public int id;
		public String text;

		public SelectionItem(final int id, final String text) {
			this.id = id;
			this.text = text;
		}
	}

	public static class ContactAssignmentAdapter extends ObjectArrayAdapter {

		final DisplayImageOptions mImageLoaderOptions;

		public ContactAssignmentAdapter(final Context context) {
			super(context);

			mImageLoaderOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(200)).showImageForEmptyUri(R.drawable.default_contact)
					.showStubImage(R.drawable.default_contact).cacheInMemory().cacheOnDisc().build();
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final Object object = getItem(position);
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				if (object instanceof LeaderItem) {
					view = getLayoutInflater().inflate(R.layout.item_assignment_leader, null);
				} else if (object instanceof GroupItem) {
					view = getLayoutInflater().inflate(R.layout.item_assignment_group, null);
				} else if (object instanceof SelectionItem) {
					view = getLayoutInflater().inflate(R.layout.item_assignment_selection, null);
				} else if (object instanceof AssignMeItem) {
					view = getLayoutInflater().inflate(R.layout.item_assignment_me, null);
				} else if (object instanceof AssignNoneItem) {
					view = getLayoutInflater().inflate(R.layout.item_assignment_none, null);
				}
				holder.icon = (ImageView) view.findViewById(android.R.id.icon);
				holder.text1 = (TextView) view.findViewById(android.R.id.text1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (object instanceof LeaderItem) {
				final LeaderItem item = (LeaderItem) object;
				holder.text1.setText(item.leader.getName());

				if (!U.isNullEmpty(item.leader.getPicture())) {
					ImageLoader.getInstance().displayImage(item.leader.getPicture(), holder.icon, mImageLoaderOptions);
				}
			} else if (object instanceof GroupItem) {
				final GroupItem item = (GroupItem) object;
				holder.text1.setText(item.group.getName());
			} else if (object instanceof SelectionItem) {
				final SelectionItem item = (SelectionItem) object;
				holder.text1.setText(item.text);
				if (item.id == IndexFragment.GROUPS) {
					holder.icon.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_group));
				} else if (item.id == IndexFragment.LEADERS) {
					holder.icon.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_leader));
				} else {
					holder.icon.setImageDrawable(null);
				}
			} else if (object instanceof AssignMeItem) {
				final AssignMeItem item = (AssignMeItem) object;
				holder.text1.setText("Me");
				if (!U.isNullEmpty(item.me.getPicture())) {
					ImageLoader.getInstance().displayImage(item.me.getPicture(), holder.icon, mImageLoaderOptions);
				}
			} else if (object instanceof AssignNoneItem) {
				// nothing to do here
			}

			return view;
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			return getView(position, convertView, parent);
		}

		public class ViewHolder {
			public TextView text1;
			public ImageView icon;
		}
	}

	public Set<Person> getPeople() {
		return mPeople;
	}

	public static abstract class AssignmentFragment extends BaseFragment {
		public ContactAssignmentDialog getDialog() {
			return (ContactAssignmentDialog) getParentFragment();
		}
	}

	public static class IndexFragment extends AssignmentFragment {

		/** the listview */
		private ListView mListView;

		/** the listview adapter */
		private ContactAssignmentAdapter mAdapter;

		public static final int INDEX = 0;
		public static final int LEADERS = 1;
		public static final int GROUPS = 2;

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
			final View view = inflater.inflate(R.layout.fragment_assignment_dialog_index, null);

			mListView = (ListView) view.findViewById(R.id.listview);

			if (mAdapter == null) {
				mAdapter = new ContactAssignmentAdapter(getActivity());
				buildAdapter();
			} else {
				mAdapter.setContext(getActivity());
			}

			mListView.setOnItemClickListener(getDialog());
			mListView.setAdapter(mAdapter);

			return view;
		}

		private void buildAdapter() {
			if (mAdapter == null || getDialog() == null || getDialog().getPeople() == null) return;

			mAdapter.setNotifyOnChange(false);
			mAdapter.clear();

			boolean showNone = true;
			boolean showMe = true;
			final boolean showLeaders = true;
			final boolean showGroups = false;

			for (final Person person : getDialog().getPeople()) {

				final List<Assignment> assignments = Application.getDb().getAssignmentDao().queryBuilder()
						.where(AssignmentDao.Properties.Person_id.eq(person.getId()), AssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).list();

				if (assignments.size() > 0) {
					for (final Assignment assignment : assignments) {
						if (assignment.getAssigned_to_id().compareTo(Session.getInstance().getPersonId()) == 0) {
							showMe = false;
							break;
						}
					}
				} else {
					showNone = false;
				}
			}

			if (showMe) {
				try {
					mAdapter.add(new AssignMeItem(Session.getInstance().getPerson()));
				} catch (final NoPersonException e) {
					/** should be impossible to get here */
				}
			}

			if (showNone) {
				mAdapter.add(new AssignNoneItem());
			}

			if (showLeaders) {
				mAdapter.add(new SelectionItem(LEADERS, "Leader"));
			}

			if (showGroups) {
				mAdapter.add(new SelectionItem(GROUPS, "Groups"));
			}
		}
	}

	public static class SelectionFragment extends AssignmentFragment {

		private int mSelection = 0;

		/** the search view */
		// TODO:

		/** the listview */
		private ListView mListView;

		/** the leader listview adapter */
		private ContactAssignmentAdapter mLeaderAdapter;

		/** the groups listview adapter */
		private ContactAssignmentAdapter mGroupsAdapter;

		/** the search adapter */
		private ContactAssignmentAdapter mSearchAdapter;

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
			final View view = inflater.inflate(R.layout.fragment_assignment_dialog_selection, null);

			mListView = (ListView) view.findViewById(R.id.listview);
			mListView.setOnScrollListener(new PauseOnScrollListener(false, true));

			if (mLeaderAdapter == null) {
				mLeaderAdapter = new ContactAssignmentAdapter(getActivity());
			} else {
				mLeaderAdapter.setContext(getActivity());
			}
			if (mGroupsAdapter == null) {
				mGroupsAdapter = new ContactAssignmentAdapter(getActivity());
			} else {
				mGroupsAdapter.setContext(getActivity());
			}
			if (mSearchAdapter == null) {
				mSearchAdapter = new ContactAssignmentAdapter(getActivity());
			} else {
				mSearchAdapter.setContext(getActivity());
			}

			mListView.setOnItemClickListener(getDialog());

			switch (mSelection) {
			case IndexFragment.GROUPS:
				showGroups();
				break;
			case IndexFragment.LEADERS:
				showLeaders();
				break;
			}

			return view;
		}

		public void search(final String term) {
			if (mSelection == IndexFragment.LEADERS) {
				searchLeaders(term);
			} else if (mSelection == IndexFragment.GROUPS) {
				searchGroups(term);
			}
		}

		public void searchLeaders(final String term) {

		}

		public void searchGroups(final String term) {

		}

		public void showGroups() {
			if (mListView == null) return;
			if (mGroupsAdapter.isEmpty()) buildGroupsAdapter();
			mSelection = IndexFragment.GROUPS;
			mListView.setAdapter(mGroupsAdapter);
		}

		public void showLeaders() {
			if (mListView == null) return;
			if (mLeaderAdapter.isEmpty()) buildLeaderAdapter();
			mSelection = IndexFragment.LEADERS;
			mListView.setAdapter(mLeaderAdapter);
		}

		private void buildGroupsAdapter() {
			// TODO:
		}

		private void buildLeaderAdapter() {
			if (mLeaderAdapter == null || getDialog() == null || getDialog().getPeople() == null) return;

			mLeaderAdapter.setNotifyOnChange(false);
			mLeaderAdapter.clear();

			final List<OrganizationalRole> roles = Application
					.getDb()
					.getOrganizationalRoleDao()
					.queryBuilder()
					.where(OrganizationalRoleDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId()),
							OrganizationalRoleDao.Properties.Role.in(Person.LABEL_ADMIN, Person.LABEL_LEADER)).list();

			final Set<Person> leaders = new HashSet<Person>();
			for (final OrganizationalRole role : roles) {
				leaders.add(Application.getDb().getPersonDao().load(role.getPerson_id()));
			}

			for (final Person leader : U.sortPeople(leaders, true)) {
				mLeaderAdapter.add(new LeaderItem(leader));
			}

			mLeaderAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Override back button to go to index page or dismiss dialog
	 */
	@Override
	public boolean onKey(final DialogInterface dialog, final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			if (mPage == 1) {
				showIndex(true);
			} else {
				cancel();
			}
			return true;
		}
		return getActivity().onKeyDown(keyCode, event);
	}

	public void setAssignmentListener(final ContactAssignmentListener listener) {
		mListener = new WeakReference<ContactAssignmentListener>(listener);
	}

	public ContactAssignmentListener getAssignmentListener() {
		if (mListener != null) {
			return mListener.get();
		}
		return null;
	}

	/**
	 * Override to keep dialog from being dismissed on rotation
	 */
	@Override
	public void onDestroyView() {
		if (getDialog() != null && U.superGetRetainInstance(this)) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	/**
	 * Notifies the listener and dismisses the dialog
	 */
	public void cancel() {
		postCanceled();
		dismiss();
	}

	/**
	 * Override to notify the assignment listener
	 */
	@Override
	public void dismiss() {
		postComplete();
		super.dismiss();
	}

	public void postComplete() {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (!mCanceled && getAssignmentListener() != null) {
						getAssignmentListener().onAssignmentCompleted();
					}
				} catch (final Exception e) { /* ignore */}
			}
		});
	}

	public void postCanceled() {
		mCanceled = true;
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (getAssignmentListener() != null) {
						mCanceled = true;
						getAssignmentListener().onAssignmentCanceled();
					}
				} catch (final Exception e) { /* ignore */}
			}
		});
	}

	public static interface ContactAssignmentListener {
		public void onAssignmentCompleted();

		public void onAssignmentCanceled();
	}

	/**
	 * Creates and shows the assignment dialog for given person
	 * 
	 * @param fm
	 * @param person
	 * @return
	 */
	public static ContactAssignmentDialog show(final FragmentManager fm, final Person person) {
		final HashSet<Person> people = new HashSet<Person>();
		people.add(person);
		return show(fm, people);
	}

	/**
	 * Creates and shows the mass assignment dialog for a group of people
	 * 
	 * @param fm
	 * @param people
	 * @return
	 */
	public static ContactAssignmentDialog show(final FragmentManager fm, final Set<Person> people) {
		final FragmentTransaction ft = fm.beginTransaction();
		final Fragment prev = fm.findFragmentByTag("assignment_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		final ContactAssignmentDialog fragment = ContactAssignmentDialog.getInstance(people);
		fragment.show(ft, "assignment_dialog");
		return fragment;
	}

	private void doGroupAssignment(final Group group) {

	}

	private void doLeaderAssignment(final Person leader) {
		if (mTask != null) {
			mTask.cancel(true);
		}
		mTask = new RoboAsyncTask<Boolean>(Application.getContext()) {

			@Override
			public Boolean call() throws Exception {
				final List<Long> personIds = new ArrayList<Long>();
				for (final Person p : getPeople()) {
					personIds.add(p.getId());
				}

				boolean status = false;

				if (leader == null) {
					status = Api.deleteContactAssigment(personIds).get();
				} else {
					final List<Long> toIds = new ArrayList<Long>();
					toIds.add(leader.getId());
					status = Api.createContactAssignment(personIds, ContactAssignmentType.leader, toIds).get();
				}

				if (status) {
					Api.getPeople(personIds).get();
				}

				return status;
			}

			@Override
			public void onSuccess(final Boolean sucess) {
				if (sucess) {
					Toast.makeText(getContext(), "Assignment complete", Toast.LENGTH_SHORT).show();
					dismiss();
				} else {
					onException(new Exception("Server returned error."));
				}

			}

			@Override
			public void onFinally() {
				mTask = null;
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(getContext(), e);
				eh.makeToast("Failed to assign contact(s).");

				ContactAssignmentDialog.this.cancel();
			}

			@Override
			public void onInterrupted(final Exception e) {

			}

		};
		Application.getExecutor().execute(mTask.future());

		getDialog().setTitle("Performing assignment...");
		showProgress();
	}

	public void showProgress() {
		mPager.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		mProgress.setVisibility(View.GONE);
		mPager.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
		final Object item = adapter.getItemAtPosition(position);

		if (item instanceof AssignNoneItem) {
			doLeaderAssignment(null);
		} else if (item instanceof AssignMeItem) {
			try {
				doLeaderAssignment(Session.getInstance().getPerson());
			} catch (final NoPersonException e) {
				/** should be impossible to get here */
			}
		} else if (item instanceof SelectionItem) {
			if (((SelectionItem) item).id == IndexFragment.LEADERS) {
				showLeaders(true);
			} else if (((SelectionItem) item).id == IndexFragment.GROUPS) {
				showGroups(true);
			}
		} else if (item instanceof GroupItem) {
			doGroupAssignment(((GroupItem) item).group);
		} else if (item instanceof LeaderItem) {
			doLeaderAssignment(((LeaderItem) item).leader);
		}
	}
}