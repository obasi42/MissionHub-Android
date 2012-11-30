package com.missionhub.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.widget.Toast;

import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.JsonComment;
import com.missionhub.application.Application;
import com.missionhub.application.DrawableCache;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.ContactAssignmentDialog.ContactAssignmentListener;
import com.missionhub.model.Assignment;
import com.missionhub.model.AssignmentDao;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.FollowupCommentDao;
import com.missionhub.model.Person;
import com.missionhub.model.Rejoicable;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ObjectArrayAdapter.SupportEnable;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.TimeAgo;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ContactInfoFragment extends BaseFragment implements ContactAssignmentListener {

	/** the logging tag */
	public static final String TAG = ContactInfoFragment.class.getName();

	/** the person id of the displayed contact */
	private long mPersonId = -1;

	/** the person object of the displayed contact */
	private Person mPerson;

	/** the list view */
	@InjectView(R.id.listview) private ListView mListView;

	/** the list view adapter */
	private CommentArrayAdapter mAdapter;

	/** the promote actionbar menu item */
	private MenuItem mPromoteItem;

	/** task used to update the comments */
	private SafeAsyncTask<List<FollowupComment>> mCommentTask;

	/** task used to save comment/status change */
	private SafeAsyncTask<Boolean> mSaveTask;

	/** task used to change roles (promote/demote) */
	private SafeAsyncTask<Boolean> mRoleTask;

	/** the progress item */
	private final ProgressItem mProgressItem = new ProgressItem();

	/** the empty item */
	private final EmptyItem mEmptyItem = new EmptyItem();

	/** if the more view is open */
	private boolean mMoreShowing = false;

	/** the header view */
	private View mHeader;

	/** the add comment header view */
	private View mHeaderComment;

	/** the contact's given name */
	@InjectView(R.id.given_name) private TextView mHeaderGivenName;

	/** the contact's family name */
	@InjectView(R.id.family_name) private TextView mHeaderFamilyName;

	/** the contact's avatar */
	@InjectView(R.id.avatar_image) private ImageView mHeaderAvatar;

	/** the call button */
	@InjectView(R.id.action_call) private ImageView mHeaderActionCall;

	/** the message button */
	@InjectView(R.id.action_message) private ImageView mHeaderActionMessage;

	/** the email button */
	@InjectView(R.id.action_email) private ImageView mHeaderActionEmail;

	/** the container for the phone number */
	@InjectView(R.id.phone_container) private View mHeaderContainerPhone;

	/** the container for the email address */
	@InjectView(R.id.email_container) private View mHeaderContainerEmail;

	/** the contact's phone number */
	@InjectView(R.id.phone) private TextView mHeaderPhone;

	/** the contact's email address */
	@InjectView(R.id.email) private TextView mHeaderEmail;

	/** the contact assignment button */
	@InjectView(R.id.assign) private Button mHeaderAssignment;

	/** the container for the more information view */
	@InjectView(R.id.more) private ViewGroup mHeaderMore;

	/** the more info collapse/expand text */
	@InjectView(R.id.expand) private TextView mHeaderMoreText;

	/** the more info gender */
	@InjectView(R.id.gender) private View mInfoGender;

	/** the more info birthday */
	@InjectView(R.id.birthday) private View mInfoBirthday;

	/** the more info address */
	@InjectView(R.id.address) private View mInfoAddress;

	/** the more info facebook link */
	@InjectView(R.id.facebook) private View mInfoFacebook;

	/** the comment data holder */
	private final CommentData mComment = new CommentData();

	/** the comment comment */
	@InjectView(R.id.comment) private EditText mCommentComment;

	/** the comment save button */
	@InjectView(R.id.save) private View mCommentSave;

	/** the comment received Christ rejoicable */
	@InjectView(R.id.rejoice_christ) private ImageView mCommentRejoiceChrist;

	/** the comment gospel presentation rejoicable */
	@InjectView(R.id.rejoice_gospel) private ImageView mCommentRejoiceGospel;

	/** the comment spiritual converstation rejoicable */
	@InjectView(R.id.rejoice_convo) private ImageView mCommentRejoiceConvo;

	/** the comment status */
	@InjectView(R.id.status) private Spinner mCommentStatus;

	/** the comment status adapter */
	private CommentStatusAdapter mCommentStatusAdapter;

	/** the dialog usesed for comment actions */
	private AlertDialog mCommentActionDialog;

	/** true when layout is completed */
	private boolean mLayoutComplete = false;

	/** image loader options for the avatar */
	private DisplayImageOptions mImageLoaderOptions;

	/**
	 * Creates a new ContactInfoFragment with a person id
	 * 
	 * @param personId
	 * @return
	 */
	public static ContactInfoFragment instantiate(final long personId) {
		final Bundle bundle = new Bundle();
		bundle.putLong("personId", personId);

		final ContactInfoFragment fragment = new ContactInfoFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		final Bundle arguments = getArguments();
		mPersonId = arguments.getLong("personId", -1);
		mPerson = Application.getDb().getPersonDao().load(mPersonId);

		mImageLoaderOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(200)).showImageForEmptyUri(R.drawable.default_contact).cacheInMemory().cacheOnDisc().build();

		refreshComments();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mHeaderComment = inflater.inflate(R.layout.fragment_contact_info_comment, null);
		mHeader = inflater.inflate(R.layout.fragment_contact_info_header, null);
		return inflater.inflate(R.layout.fragment_contact_info, null);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mListView.setOnScrollListener(new PauseOnScrollListener(false, true));

		// the header
		initHeaderView(mHeader);
		mListView.addHeaderView(mHeader);

		// the add comment box
		initCommentView(mHeaderComment);
		mListView.addHeaderView(mHeaderComment);

		// setup the adapter if needed
		if (mAdapter == null) {
			mAdapter = new CommentArrayAdapter(getActivity());
		} else {
			mAdapter.setContext(getActivity());
		}

		// set the list adapter
		mListView.setAdapter(mAdapter);

		// set the comment long click listener
		mListView.setOnItemLongClickListener(new CommentLongClickListener());

		mLayoutComplete = true;

		// sets the data in the header and add comment box
		notifyContactUpdated();

		// build the comment list from sql if the adapter is empty
		if (mAdapter.isEmpty()) {
			notifyCommentsUpdated();
		}
	}

	/**
	 * Sets the header view variables and sets up listeners
	 * 
	 * @param view
	 */
	public void initHeaderView(final View view) {
		mHeaderAssignment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final ContactAssignmentDialog dialog = ContactAssignmentDialog.show(getFragmentManager(), mPerson);
				dialog.setAssignmentListener(ContactInfoFragment.this);
			}
		});
		mHeaderMoreText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (mHeaderMore.getVisibility() == View.VISIBLE) {
					mMoreShowing = false;
				} else {
					mMoreShowing = true;
				}
				updateMoreShowing();
			}
		});
		mInfoAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				openAddress();
			}
		});
		mInfoFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				IntentHelper.openFacebookProfile(Long.parseLong(mPerson.getFb_id()));
			}
		});
	}

	/**
	 * Sets the comment box variables and listeners
	 * 
	 * @param view
	 */
	public void initCommentView(final View view) {
		mCommentComment.clearFocus();
		mCommentSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				postComment();
			}
		});
		mCommentRejoiceChrist.setTag(false);
		mCommentRejoiceChrist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				toggleRejoicable(mCommentRejoiceChrist, R.drawable.ic_rejoice_christ, R.drawable.ic_rejoice_christ_gray, R.string.rejoicable_christ);
			}
		});
		mCommentRejoiceGospel.setTag(false);
		mCommentRejoiceGospel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				toggleRejoicable(mCommentRejoiceGospel, R.drawable.ic_rejoice_gospel, R.drawable.ic_rejoice_gospel_gray, R.string.rejoicable_gospel);
			}
		});
		mCommentRejoiceConvo.setTag(false);
		mCommentRejoiceConvo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				toggleRejoicable(mCommentRejoiceConvo, R.drawable.ic_rejoice_convo, R.drawable.ic_rejoice_convo_gray, R.string.rejoicable_convo);
			}
		});
		if (mCommentStatusAdapter == null) {
			mCommentStatusAdapter = new CommentStatusAdapter(getActivity());
			for (final String status : U.getStatuses()) {
				mCommentStatusAdapter.add(status);
			}
		} else {
			mCommentStatusAdapter.setContext(getActivity());
		}
		mCommentStatus.setAdapter(mCommentStatusAdapter);
	}

	private void toggleRejoicable(final ImageView view, final int selected, final int unselected, final int description) {
		if ((Boolean) view.getTag()) {
			view.setTag(false);
			view.setImageDrawable(DrawableCache.getDrawable(unselected));
		} else {
			view.setTag(true);
			view.setImageDrawable(DrawableCache.getDrawable(selected));
			Toast.makeText(Application.getContext(), description, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateRefreshIcon();
	}

	@Override
	public void onDestroyView() {
		mLayoutComplete = false;

		updateCommentStateData();

		// avoid leaking context on rotation
		if (mCommentActionDialog != null && mCommentActionDialog.isShowing()) {
			mCommentActionDialog.dismiss();
		}
		super.onDestroyView();
	}

	/**
	 * Updates the data in the header and add comment box
	 */
	public void notifyContactUpdated() {
		if (mPerson == null) return;
		if (!mLayoutComplete) return;

		mPerson.refresh();

		// all this for a name...
		String givenName = mPerson.getFirst_name();
		String familyName = mPerson.getLast_name();
		if (U.isNullEmpty(givenName, familyName)) {
			if (!U.isNullEmpty(mPerson.getName())) {
				final String[] name = mPerson.getName().split(" ");
				if (name.length == 1) {
					givenName = "";
					familyName = name[0];
				} else if (name.length == 2) {
					givenName = name[0];
					familyName = name[1];
				} else if (name.length > 2) {
					givenName = mPerson.getName().replace(name[name.length - 1], "");
					familyName = name[name.length - 1];
				}
			}
		}

		if (!U.isNullEmpty(givenName)) {
			mHeaderGivenName.setText(givenName);
		} else {
			mHeaderGivenName.setText("");
		}

		if (!U.isNullEmpty(familyName)) {
			mHeaderFamilyName.setText(familyName);
		} else {
			mHeaderFamilyName.setText("");
		}

		// avatar
		if (!U.isNullEmpty(mPerson.getPicture())) {
			if (mPerson.getPicture().contains("facebook.com") && !U.isNullEmpty(mPerson.getFb_id())) {
				ImageLoader.getInstance().displayImage("fb://" + mPerson.getFb_id(), mHeaderAvatar, mImageLoaderOptions);
			} else {
				ImageLoader.getInstance().displayImage(mPerson.getPicture(), mHeaderAvatar, mImageLoaderOptions);
			}
		} else {
			ImageLoader.getInstance().displayImage(null, mHeaderAvatar, mImageLoaderOptions);
		}

		// calling/messaging
		if (!U.isNullEmpty(mPerson.getPhone_number())) {
			final String prettyNumber = U.formatPhoneNumber(mPerson.getPhone_number());
			mHeaderPhone.setText(prettyNumber);
			mHeaderContainerPhone.setVisibility(View.VISIBLE);
			if (U.hasPhoneAbility(getActivity())) {
				mHeaderActionCall.setVisibility(View.VISIBLE);
				mHeaderActionCall.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						IntentHelper.dialNumber(prettyNumber);
					}
				});
				mHeaderActionMessage.setVisibility(View.VISIBLE);
				mHeaderActionMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						IntentHelper.sendSms(prettyNumber);
					}
				});
				mHeaderPhone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View v) {
						IntentHelper.viewNumber(prettyNumber);
					}
				});
			} else {
				mHeaderActionCall.setVisibility(View.GONE);
				mHeaderActionMessage.setVisibility(View.GONE);
			}
		} else {
			mHeaderContainerPhone.setVisibility(View.GONE);
			mHeaderActionCall.setVisibility(View.GONE);
			mHeaderActionMessage.setVisibility(View.GONE);
		}

		// emailing
		if (!U.isNullEmpty(mPerson.getEmail_address())) {
			mHeaderEmail.setText(mPerson.getEmail_address());
			mHeaderContainerEmail.setVisibility(View.VISIBLE);
			mHeaderActionEmail.setVisibility(View.VISIBLE);
			final String emailAddress = mPerson.getEmail_address();
			final OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(final View v) {
					IntentHelper.sendEmail(emailAddress);
				}
			};
			mHeaderEmail.setOnClickListener(listener);
			mHeaderActionEmail.setOnClickListener(listener);
		} else {
			mHeaderActionEmail.setVisibility(View.GONE);
			mHeaderContainerEmail.setVisibility(View.GONE);
		}

		// assignment
		final Assignment assignment = Application.getDb().getAssignmentDao().queryBuilder()
				.where(AssignmentDao.Properties.Person_id.eq(mPerson.getId()), AssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).limit(1).unique();

		if (assignment == null) {
			mHeaderAssignment.setText("Unassigned");
		} else {
			final Person assignedTo = Application.getDb().getPersonDao().load(assignment.getAssigned_to_id());

			if (!U.isNullEmpty(assignedTo.getName())) {
				mHeaderAssignment.setText(assignedTo.getName());
			} else {
				mHeaderAssignment.setText("Assignment Unknown");
			}
		}

		// set the "more info" view
		if (!U.isNullEmpty(mPerson.getGender())) {
			((TextView) mInfoGender.findViewById(android.R.id.text1)).setText(mPerson.getGender());
			mInfoGender.setVisibility(View.VISIBLE);
		} else {
			mInfoGender.setVisibility(View.GONE);
		}

		if (!U.isNullEmpty(mPerson.getBirthday())) {

			try {
				final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				final Date dateStr = formatter.parse(mPerson.getBirthday());
				final SimpleDateFormat formatter2 = new SimpleDateFormat("MMMM dd", Locale.US);
				((TextView) mInfoBirthday.findViewById(android.R.id.text1)).setText(formatter2.format(dateStr));
			} catch (final Exception e) {
				((TextView) mInfoBirthday.findViewById(android.R.id.text1)).setText(mPerson.getBirthday());
			}
			mInfoBirthday.setVisibility(View.VISIBLE);
		} else {
			mInfoBirthday.setVisibility(View.GONE);
		}

		mInfoBirthday.findViewById(R.id.divider).setVisibility(View.GONE);
		mInfoAddress.setVisibility(View.GONE);
		mInfoAddress.findViewById(R.id.divider).setVisibility(View.GONE);

		updateMoreShowing();

		// the comment view
		updateCommentBox();

		// updates the promotion/demotion menu item
		updatePromoteDemote();
	}

	/**
	 * Rebuilds the data in the comment list
	 */
	public void notifyCommentsUpdated() {
		if (mPerson == null) return;
		if (!mLayoutComplete) return;

		mPerson.resetFollowup_comments();

		mAdapter.setNotifyOnChange(false);
		mAdapter.clear();

		final List<FollowupComment> comments = Application.getDb().getFollowupCommentDao().queryBuilder()
				.where(FollowupCommentDao.Properties.Contact_id.eq(mPersonId), FollowupCommentDao.Properties.Deleted_at.isNull()).orderDesc(FollowupCommentDao.Properties.Updated_at).list();
		for (final FollowupComment comment : comments) {
			mAdapter.add(new CommentItem(comment));
		}

		if (mAdapter.isEmpty()) {
			mAdapter.add(mEmptyItem);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Expands or collapses the more info area based on mMoreShowing
	 */
	private void updateMoreShowing() {
		if (mMoreShowing) {
			mHeaderMore.setVisibility(View.VISIBLE);
			mHeaderMoreText.setText(R.string.contact_info_collapse);
			mHeaderMoreText.setCompoundDrawablesWithIntrinsicBounds(null, null, DrawableCache.getDrawable(R.drawable.ic_action_collapse), null);
		} else {
			mHeaderMore.setVisibility(View.GONE);
			mHeaderMoreText.setText(R.string.contact_info_expand);
			mHeaderMoreText.setCompoundDrawablesWithIntrinsicBounds(null, null, DrawableCache.getDrawable(R.drawable.ic_action_expand), null);
		}
	}

	/**
	 * Refreshes the person from through the api
	 */
	public synchronized void refreshContact() {
		getParent().refreshContact();
		updateRefreshIcon();
	}

	/**
	 * Refreshes the comments through the api
	 */
	public synchronized void refreshComments() {
		if (mCommentTask != null) return;

		if (mAdapter != null) {
			mAdapter.remove(mEmptyItem);
		}

		mCommentTask = new SafeAsyncTask<List<FollowupComment>>() {

			@Override
			public List<FollowupComment> call() throws Exception {
				return Api.getComments(mPersonId).get();
			}

			@Override
			public void onSuccess(final List<FollowupComment> comments) {
				notifyCommentsUpdated();
			}

			@Override
			public void onFinally() {
				mCommentTask = null;
				updateRefreshIcon();
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
				eh.makeToast("Failed to refresh comments.");
			}

			@Override
			public void onInterrupted(final Exception e) {
				onException(e);
			}
		};
		updateRefreshIcon();
		Application.getExecutor().execute(mCommentTask.future());
	}

	/**
	 * The list adapter for the comment list
	 */
	private static class CommentArrayAdapter extends ObjectArrayAdapter {

		/** time ago object to generate recent dates */
		private static final TimeAgo sTimeAgo = new TimeAgo();

		/** date format to generate less recent dates */
		private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("E d MMM yyyy hh:mma", Locale.US);

		/** date used for comparisons */
		private static final Date sWeekAgo = new Date(System.currentTimeMillis() - (7 * 1000 * 60 * 60 * 24));

		private final DisplayImageOptions mImageLoaderOptions;

		public CommentArrayAdapter(final Context context) {
			super(context);

			mImageLoaderOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(200)).showImageForEmptyUri(R.drawable.default_contact)
					.showStubImage(R.drawable.default_contact).cacheInMemory().cacheOnDisc().build();
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final Object item = getItem(position);
			View view = convertView;

			if (view == null) {
				final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (item instanceof CommentItem) {
					final ViewHolder holder = new ViewHolder();
					view = inflater.inflate(R.layout.item_contact_comment, null);
					holder.avatar = (ImageView) view.findViewById(R.id.avatar);
					holder.name = (TextView) view.findViewById(R.id.name);
					holder.time = (TextView) view.findViewById(R.id.time);
					holder.comment = (TextView) view.findViewById(R.id.comment);
					holder.status = (TextView) view.findViewById(R.id.status);
					holder.rejoiceChrist = (ImageView) view.findViewById(R.id.rejoice_christ);
					holder.rejoiceConvo = (ImageView) view.findViewById(R.id.rejoice_convo);
					holder.rejoiceGospel = (ImageView) view.findViewById(R.id.rejoice_gospel);
					view.setTag(holder);
				} else if (item instanceof EmptyItem) {
					view = inflater.inflate(R.layout.item_contact_comment_empty, null);
				} else if (item instanceof ProgressItem) {
					view = inflater.inflate(R.layout.item_contact_comment_progress, null);
				}
			}

			if (item instanceof CommentItem) {
				final ViewHolder holder = (ViewHolder) view.getTag();
				final CommentItem i = (CommentItem) item;

				if (i.comment != null) {
					i.comment.refresh();

					if (i.getCommenter() != null) {
						if (!U.isNullEmpty(i.getCommenter().getName())) {
							holder.name.setText(i.getCommenter().getName());
							holder.name.setVisibility(View.VISIBLE);
						} else {
							holder.name.setVisibility(View.GONE);
						}
						ImageLoader.getInstance().displayImage(i.getCommenter().getPicture(), holder.avatar, mImageLoaderOptions);
					}

					if (!U.isNullEmpty(i.comment.getUpdated_at())) {
						final Date updated = i.comment.getUpdated_at();
						if (updated.before(sWeekAgo)) {
							holder.time.setText(sDateFormat.format(i.comment.getUpdated_at()));
						} else {
							holder.time.setText(sTimeAgo.timeAgo(i.comment.getUpdated_at()));
						}
						holder.time.setVisibility(View.VISIBLE);
					} else {
						holder.time.setVisibility(View.GONE);
					}

					if (!U.isNullEmpty(i.comment.getComment())) {
						holder.comment.setText(i.comment.getComment());
						holder.comment.setVisibility(View.VISIBLE);
					} else {
						holder.comment.setVisibility(View.GONE);
					}

					if (!U.isNullEmpty(i.comment.getStatus())) {
						holder.status.setText(U.translateStatus(i.comment.getStatus()));
						holder.status.setVisibility(View.VISIBLE);
					} else {
						holder.status.setVisibility(View.GONE);
					}

					holder.rejoiceConvo.setVisibility(View.GONE);
					holder.rejoiceChrist.setVisibility(View.GONE);
					holder.rejoiceGospel.setVisibility(View.GONE);

					i.comment.resetRejoicables();
					final List<Rejoicable> rejoicables = i.comment.getRejoicables();
					for (final Rejoicable r : rejoicables) {
						if (r.getWhat().contains("spiritual_conversation")) {
							holder.rejoiceConvo.setVisibility(View.VISIBLE);
						}
						if (r.getWhat().contains("prayed_to_receive")) {
							holder.rejoiceChrist.setVisibility(View.VISIBLE);

						}
						if (r.getWhat().contains("gospel_presentation")) {
							holder.rejoiceGospel.setVisibility(View.VISIBLE);

						}
					}
				}
			}
			return view;
		}

		/** view holder for performance */
		class ViewHolder {
			ImageView avatar;
			TextView name;
			TextView time;
			TextView comment;
			TextView status;
			ImageView rejoiceChrist;
			ImageView rejoiceGospel;
			ImageView rejoiceConvo;
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			return getView(position, convertView, parent);
		}

	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// mPromoteItem = menu.add(Menu.NONE, R.id.menu_item_permissions, Menu.NONE,
		// R.string.action_promote).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
		// updatePromoteDemote();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:
			refreshContact();
			refreshComments();
			return true;
		case R.id.menu_item_permissions:
			togglePromoteDemote();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Represents a comment in the comment list
	 */
	private static class CommentItem {
		FollowupComment comment;
		Person commenter;

		public CommentItem(final FollowupComment comment) {
			this.comment = comment;
		}

		public Person getCommenter() {
			if (commenter == null) {
				commenter = Application.getDb().getPersonDao().load(comment.getCommenter_id());
			}
			return commenter;
		}
	}

	/**
	 * Represents a progress item in the comment list
	 */
	private static class ProgressItem implements SupportEnable {
		@Override
		public boolean isEnabled() {
			return false;
		}
	}

	/**
	 * Represents an empty item in the comment list
	 */
	private static class EmptyItem implements SupportEnable {
		@Override
		public boolean isEnabled() {
			return false;
		}
	}

	/**
	 * Updates the refresh icon based on the tasks
	 */
	public void updateRefreshIcon() {
		if (mAdapter == null) return;

		if (mCommentTask != null) {
			mAdapter.setNotifyOnChange(false);
			mAdapter.remove(mProgressItem);
			mAdapter.insert(mProgressItem, 0);
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter.remove(mProgressItem);
		}

		getParent().updateRefreshIcon();
	}

	/**
	 * Called by the parent fragment to see if this frament is busy
	 */
	public boolean isWorking() {
		return mCommentTask != null || mSaveTask != null || mRoleTask != null;
	}

	/**
	 * The long click listener for the comment list that generates context menu like dialogs.
	 */
	private class CommentLongClickListener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
			final Object object = adapter.getItemAtPosition(position);

			if (object instanceof CommentItem) {
				final CommentItem item = (CommentItem) object;

				final List<CharSequence> actionItems = new ArrayList<CharSequence>();

				// only allow deletion if admin or commenter
				if (Session.getInstance().isAdmin() || item.getCommenter().getId() == Session.getInstance().getPersonId()) {
					actionItems.add(getString(R.string.action_delete));
				}

				if (!actionItems.isEmpty()) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(R.string.contact_comment_actions);

					final CharSequence[] items = new CharSequence[actionItems.size()];
					actionItems.toArray(items);

					builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							onClickCommentAction(actionItems.get(which), item);
						}
					});

					mCommentActionDialog = builder.show();
					return true;
				}
			}
			return false;
		}
	};

	/**
	 * Called when a comment action is clicked
	 * 
	 * @param action
	 * @param item
	 */
	public void onClickCommentAction(final CharSequence action, final CommentItem item) {
		if (action.equals(getString(R.string.action_delete))) {
			deleteComment(item);
		}
	}

	/**
	 * Deletes a comment via the api
	 * 
	 * @param item
	 */
	public void deleteComment(final CommentItem item) {
		final SafeAsyncTask<Boolean> task = new SafeAsyncTask<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return Api.deleteComment(item.comment.getId()).get();
			}

			@Override
			public void onSuccess(final Boolean success) {
				if (isAdded()) {
					if (success) {
						if (mAdapter != null) {
							mAdapter.remove(item);
						}
						Toast.makeText(Application.getContext(), R.string.contact_comment_deleted, Toast.LENGTH_SHORT).show();
						refreshComments();
					} else {
						onException(new Exception("Server returned error."));
					}
				}
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
				eh.makeToast(R.string.contact_cannot_delete_comment);
			}

			@Override
			public void onInterrupted(final Exception e) {

			}
		};
		Application.getExecutor().execute(task.future());
	}

	/**
	 * Posts a comment from the data in the view
	 */
	public void postComment() {
		if (mSaveTask != null) return;

		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mCommentComment.getWindowToken(), 0);
		mCommentComment.clearFocus();

		updateCommentStateData();

		final String comment = mComment.comment;
		final String status = mComment.status;
		final List<String> rejoicables = new ArrayList<String>(mComment.rejoicables);

		if (U.isNullEmpty(comment) && rejoicables.isEmpty() && status.equalsIgnoreCase(mPerson.getStatus())) {
			Toast.makeText(getActivity(), R.string.contact_cannot_comment, Toast.LENGTH_LONG).show();
			return;
		}

		mSaveTask = new SafeAsyncTask<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				final JsonComment jsonComment = new JsonComment(mPersonId, comment, status, rejoicables);
				return Api.addComment(jsonComment).get();
			}

			@Override
			public void onSuccess(final Boolean success) {
				if (success) {
					Toast.makeText(Application.getContext(), R.string.contact_comment_saved, Toast.LENGTH_SHORT).show();
					if (isVisible()) {
						mCommentStatus.setSelection(U.getStatuses().indexOf(status), false);
					}
					mPerson.setStatus(status);
					mPerson.update();
					clearCommentBox();
					refreshContact();
					refreshComments();
				} else {
					onException(new Exception("Server returned error."));
				}
			}

			@Override
			public void onFinally() {
				mSaveTask = null;
				updateRefreshIcon();
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
				eh.makeToast(R.string.contact_comment_failed_to_save);
			}

			@Override
			public void onInterrupted(final Exception e) {

			}
		};
		updateRefreshIcon();
		Application.getExecutor().execute(mSaveTask.future());
	}

	/**
	 * The status spinner adapter
	 */
	private static class CommentStatusAdapter extends ObjectArrayAdapter {

		public CommentStatusAdapter(final Context context) {
			super(context);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final String status = (String) getItem(position);
			View view = convertView;

			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_simple_status, null);
			}

			final TextView tv = (TextView) view.findViewById(R.id.text);
			tv.setText(U.translateStatus(status));

			return view;
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			final View view = getView(position, convertView, parent);
			final int padding = Math.round(U.dpToPixel(12));
			view.setPadding(padding, padding, padding, padding);
			return view;
		}
	}

	/**
	 * updates mComment with current data from the view
	 */
	private void updateCommentStateData() {
		mComment.comment = mCommentComment.getText().toString();
		mComment.status = (String) mCommentStatus.getSelectedItem();
		mComment.rejoicables.clear();
		if ((Boolean) mCommentRejoiceChrist.getTag()) {
			mComment.rejoicables.add("prayed_to_receive");
		}
		if ((Boolean) mCommentRejoiceGospel.getTag()) {
			mComment.rejoicables.add("gospel_presentation");
		}
		if ((Boolean) mCommentRejoiceConvo.getTag()) {
			mComment.rejoicables.add("spiritual_conversation");
		}
	}

	/**
	 * Clears the data in the comment box
	 */
	private void clearCommentBox() {
		mComment.clear();
		mCommentComment.setText("");
		mCommentComment.clearFocus();
		mCommentStatus.setSelection(U.getStatuses().indexOf(mPerson.getStatus()), false);
		mCommentRejoiceGospel.setTag(false);
		mCommentRejoiceChrist.setTag(false);
		mCommentRejoiceConvo.setTag(false);
		updateCommentBox();
	}

	/**
	 * Updates the comment box with the contacts status and data in mComment
	 */
	private void updateCommentBox() {
		if (mPerson.getStatus() != null) {
			mCommentStatus.setSelection(U.getStatuses().indexOf(mPerson.getStatus()), false);
		}

		// restore comment from mComment
		if (mComment != null) {
			if (mComment.comment != null) {
				mCommentComment.setText(mComment.comment);
			}
			if (mComment.status != null) {
				final int index = U.getStatuses().indexOf(mComment.status);
				if (index > -1) {
					mCommentStatus.setSelection(index);
				}
			}
			mCommentRejoiceGospel.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_gospel_gray));
			mCommentRejoiceChrist.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_christ_gray));
			mCommentRejoiceConvo.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_convo_gray));
			if (mComment.rejoicables != null) {
				for (final String r : mComment.rejoicables) {
					if (r.contains("spiritual_conversation")) {
						mCommentRejoiceConvo.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_convo));
					}
					if (r.contains("prayed_to_receive")) {
						mCommentRejoiceChrist.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_christ));
					}
					if (r.contains("gospel_presentation")) {
						mCommentRejoiceGospel.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_gospel));
					}
				}
			}
		}
	}

	/**
	 * Represents the data in a comment
	 */
	private static class CommentData {
		public String comment;
		public String status;
		public ArrayList<String> rejoicables = new ArrayList<String>();

		public void clear() {
			comment = null;
			status = null;
			rejoicables = new ArrayList<String>();
		}
	}

	public ContactFragment getParent() {
		return (ContactFragment) getParentFragment();
	}

	/**
	 * Toggles the contact's role between leader and contact
	 */
	private void togglePromoteDemote() {
		if (mPerson.isLeader(Session.getInstance().getOrganizationId())) {
			demotePerson();
		} else {
			promotePerson();
		}
	}

	/**
	 * Promotes the current person
	 */
	private void promotePerson() {
		if (!Session.getInstance().isAdmin() || mPerson.isAdmin(Session.getInstance().getOrganizationId())) {
			Toast.makeText(getActivity(), R.string.action_no_permissions, Toast.LENGTH_LONG).show();
			return;
		}
		if (mPerson.isAdminOrLeader(Session.getInstance().getOrganizationId())) {
			Toast.makeText(getActivity(), R.string.contact_already_leader, Toast.LENGTH_LONG).show();
			return;
		}

		changeRole(Person.LABEL_LEADER);
	}

	/**
	 * Demotes a the current person
	 */
	private void demotePerson() {
		if (!Session.getInstance().isAdmin() || mPerson.isAdmin(Session.getInstance().getOrganizationId())) {
			Toast.makeText(getActivity(), R.string.action_no_permissions, Toast.LENGTH_LONG).show();
			return;
		}
		if (!mPerson.isLeader(Session.getInstance().getOrganizationId()) && !mPerson.isAdmin(Session.getInstance().getOrganizationId())) {
			Toast.makeText(getActivity(), R.string.contact_already_contact, Toast.LENGTH_LONG).show();
			return;
		}

		changeRole(Person.LABEL_CONTACT);
	}

	/**
	 * Updates the promotion menu item
	 */
	public void updatePromoteDemote() {
		if (mPerson == null || mPromoteItem == null) return;

		if (Session.getInstance().isAdmin() && !mPerson.isAdmin(Session.getInstance().getOrganizationId())) {
			if (mPerson.isLeader(Session.getInstance().getOrganizationId())) {
				mPromoteItem.setTitle(R.string.action_demote);
			} else {
				mPromoteItem.setTitle(R.string.action_promote);
			}
			mPromoteItem.setVisible(true);
			mPromoteItem.setEnabled(true);
		} else {
			mPromoteItem.setVisible(false);
		}
	}

	/**
	 * Changes a contact's role
	 * 
	 * @param role
	 */
	private void changeRole(final String role) {
		if (mRoleTask != null) {
			mRoleTask.cancel(true);
		}

		if (mPromoteItem != null) {
			mPromoteItem.setEnabled(false);
		}

		mRoleTask = new SafeAsyncTask<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				if (role == Person.LABEL_LEADER) {
					return Api.addRole(mPersonId, Person.LABEL_LEADER).get();
				} else {
					return Api.removeRole(mPersonId, Person.LABEL_LEADER).get();
				}
			}

			@Override
			public void onSuccess(final Boolean success) {
				if (success) {
					Toast.makeText(Application.getContext(), R.string.contact_role_changed, Toast.LENGTH_SHORT).show();
					refreshContact();
				} else {
					onException(new Exception("Server returned error."));
				}
			}

			@Override
			public void onFinally() {
				if (mPromoteItem != null) {
					mPromoteItem.setEnabled(true);
				}
				mRoleTask = null;
				updateRefreshIcon();
			}

			@Override
			public void onException(final Exception e) {
				final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
				eh.makeToast(R.string.contact_role_failed);
			}

			@Override
			public void onInterrupted(final Exception e) {

			}

		};
		Application.getExecutor().execute(mRoleTask.future());
	}

	public void openAddress() {

	}

	@Override
	public void onAssignmentCompleted() {
		notifyContactUpdated();
	}

	@Override
	public void onAssignmentCanceled() {}

}
