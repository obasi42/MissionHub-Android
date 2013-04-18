package com.missionhub.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.application.Application;
import com.missionhub.application.DrawableCache;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.dialog.ContactAssignmentDialogFragment;
import com.missionhub.fragment.dialog.ContactLabelsDialogFragment;
import com.missionhub.fragment.dialog.FragmentResult;
import com.missionhub.model.*;
import com.missionhub.model.gson.GFollowupComment;
import com.missionhub.model.gson.GRejoicable;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ObjectArrayAdapter.SupportEnable;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TimeAgo;
import com.missionhub.util.U;
import com.missionhub.util.U.FollowupStatus;
import com.missionhub.util.U.Gender;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactInfoFragment extends BaseFragment {

    /**
     * the logging tag
     */
    public static final String TAG = ContactInfoFragment.class.getName();

    /**
     * the person to display info about
     */
    private Person mPerson;

    /**
     * the list view
     */
    private ListView mListView;

    /**
     * the list view adapter
     */
    private CommentArrayAdapter mAdapter;

    /**
     * the task used for refresh comments
     */
    private SafeAsyncTask<Void> mRefreshLabelsTask;

    /**
     * the task used for refresh comments
     */
    private SafeAsyncTask<Person> mRefreshCommentsTask;

    /**
     * the comment data holder
     */
    private GFollowupComment mComment = new GFollowupComment();

    /**
     * the dialog used for comment actions
     */
    private AlertDialog mCommentActionDialog;

    /**
     * true when layout is completed
     */
    private boolean mLayoutComplete = false;

    /**
     * image loader options for the avatar
     */
    private DisplayImageOptions mImageLoaderOptions;

    /**
     * the progress item
     */
    private final ProgressItem mProgressItem = new ProgressItem();

    /**
     * the empty item
     */
    private final EmptyItem mEmptyItem = new EmptyItem();

    /**
     * if the more view is open
     */
    private boolean mMoreShowing = false;

    /**
     * the header view
     */
    private View mHeader;

    /**
     * the add comment header view
     */
    private View mHeaderComment;

    /**
     * the contact's given name
     */
    private TextView mHeaderGivenName;

    /**
     * the contact's family name
     */
    private TextView mHeaderFamilyName;

    /**
     * the contact's avatar
     */
    private ImageView mHeaderAvatar;

    /**
     * the call button
     */
    private ImageView mHeaderActionCall;

    /**
     * the message button
     */
    private ImageView mHeaderActionMessage;

    /**
     * the email button
     */
    private ImageView mHeaderActionEmail;

    /**
     * the container for the phone number
     */
    private View mHeaderContainerPhone;

    /**
     * the container for the email address
     */
    private View mHeaderContainerEmail;

    /**
     * the contact's phone number
     */
    private TextView mHeaderPhone;

    /**
     * the contact's email address
     */
    private TextView mHeaderEmail;

    /**
     * the contact assignment button
     */
    private Button mHeaderAssignment;

    /**
     * the container for the more information view
     */
    private ViewGroup mHeaderMore;

    /**
     * the more info collapse/expand text
     */
    private TextView mHeaderMoreText;

    /**
     * the comment comment
     */
    private EditText mCommentComment;

    /**
     * the comment save button
     */
    private View mCommentSave;

    /**
     * the comment received Christ rejoicable
     */
    private ImageView mCommentRejoiceChrist;

    /**
     * the comment gospel presentation rejoicable
     */
    private ImageView mCommentRejoiceGospel;

    /**
     * the comment spiritual converstation rejoicable
     */
    private ImageView mCommentRejoiceConvo;

    /**
     * the comment status
     */
    private Spinner mCommentStatus;

    /**
     * the comment status adapter
     */
    private CommentStatusAdapter mCommentStatusAdapter;

    public static int REQUEST_ASSIGNMENT = 1;
    public static int REQUEST_LABELS = 2;
    public static int REQUEST_EDIT_CONTACT = 3;

    private AnimateOnceImageLoadingListener mLoadingListener = new AnimateOnceImageLoadingListener(250);

    public ContactInfoFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoaderOptions = U.getContactImageDisplayOptions();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contact_info, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

        mHeader = inflater.inflate(R.layout.fragment_contact_info_header, null);
        mHeaderComment = inflater.inflate(R.layout.fragment_contact_info_comment, null);

        mListView.addHeaderView(mHeader);
        mListView.addHeaderView(mHeaderComment);

        mHeaderGivenName = (TextView) mHeader.findViewById(R.id.given_name);
        mHeaderFamilyName = (TextView) mHeader.findViewById(R.id.family_name);
        mHeaderAvatar = (ImageView) mHeader.findViewById(R.id.avatar_image);
        mHeaderActionCall = (ImageView) mHeader.findViewById(R.id.action_call);
        mHeaderActionMessage = (ImageView) mHeader.findViewById(R.id.action_message);
        mHeaderActionEmail = (ImageView) mHeader.findViewById(R.id.action_email);
        mHeaderContainerPhone = mHeader.findViewById(R.id.phone_container);
        mHeaderContainerEmail = mHeader.findViewById(R.id.email_container);
        mHeaderPhone = (TextView) mHeader.findViewById(R.id.phone);
        mHeaderEmail = (TextView) mHeader.findViewById(R.id.email);
        mHeaderAssignment = (Button) mHeader.findViewById(R.id.assign);
        mHeaderMore = (ViewGroup) mHeader.findViewById(R.id.more);
        mHeaderMoreText = (TextView) mHeader.findViewById(R.id.expand);

        mCommentComment = (EditText) mHeaderComment.findViewById(R.id.comment);
        mCommentSave = mHeaderComment.findViewById(R.id.save);
        mCommentRejoiceChrist = (ImageView) mHeaderComment.findViewById(R.id.rejoice_christ);
        mCommentRejoiceGospel = (ImageView) mHeaderComment.findViewById(R.id.rejoice_gospel);
        mCommentRejoiceConvo = (ImageView) mHeaderComment.findViewById(R.id.rejoice_convo);
        mCommentStatus = (Spinner) mHeaderComment.findViewById(R.id.status);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        initHeaderView(mHeader);
        initCommentView(mHeaderComment);

        // setup the adapter if needed
        if (mAdapter == null) {
            mAdapter = new CommentArrayAdapter(getSupportActivity());
        } else {
            mAdapter.setContext(getSupportActivity());
        }

        // set the list adapter
        mListView.setAdapter(mAdapter);

        // set the comment long click listener
        mListView.setOnItemLongClickListener(new CommentLongClickListener());

        mLayoutComplete = true;

        // sets the data in the header and add comment box
        notifyPersonUpdated();
    }

    @Override
    public void onStart() {
        super.onStart();

        Application.trackView("Contact/Info");
    }

    @Override
    public void onDestroy() {
        try {
            mRefreshCommentsTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }
        super.onDestroy();
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
                ContactAssignmentDialogFragment.showForResult(getChildFragmentManager(), mPerson, REQUEST_ASSIGNMENT);
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
            mCommentStatusAdapter = new CommentStatusAdapter(getSupportActivity());
            for (final FollowupStatus status : U.FollowupStatus.values()) {
                mCommentStatusAdapter.add(status);
            }
        } else {
            mCommentStatusAdapter.setContext(getSupportActivity());
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
    public void onDestroyView() {
        mLayoutComplete = false;

        updateCommentStateData();

        // avoid leaking context on rotation
        if (mCommentActionDialog != null && mCommentActionDialog.isShowing()) {
            mCommentActionDialog.dismiss();
        }
        super.onDestroyView();
    }

    public void notifyPersonUpdated(final Person person) {
        mPerson = person;
        notifyPersonUpdated();
    }

    /**
     * Updates the data in the header and add comment box
     */
    private void notifyPersonUpdated() {
        if (!mLayoutComplete || mPerson == null) return;

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
        int px = Math.round(U.dpToPixel(128));
        String picture = U.getProfilePicture(mPerson, px, px);
        if (!U.isNullEmpty(picture)) {
            ImageLoader.getInstance().displayImage(picture, mHeaderAvatar, mImageLoaderOptions, mLoadingListener);
        } else {
            mHeaderAvatar.setImageDrawable(DrawableCache.getDrawable(R.drawable.default_contact));
        }

        // calling/messaging
        mHeaderActionCall.setVisibility(View.GONE);
        mHeaderActionMessage.setVisibility(View.GONE);
        mHeaderContainerPhone.setVisibility(View.GONE);
        final Phonenumber.PhoneNumber phoneNumber = U.parsePhoneNumber(mPerson.getPrimaryPhoneNumber());
        if (phoneNumber != null) {
            if (PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber)) {
                mHeaderPhone.setText(PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
                if (IntentHelper.canDialNumber() && U.hasPhoneAbility(Application.getContext())) {
                    mHeaderPhone.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentHelper.dialNumber(phoneNumber);
                        }
                    });
                    mHeaderActionCall.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentHelper.dialNumber(phoneNumber);
                        }
                    });
                    mHeaderActionCall.setVisibility(View.VISIBLE);
                }
                if (IntentHelper.canSendSms()) {
                    mHeaderActionMessage.setVisibility(View.VISIBLE);
                    mHeaderActionMessage.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            IntentHelper.sendSms(phoneNumber);
                        }
                    });
                }
            } else {
                mHeaderPhone.setText(phoneNumber.getRawInput());
            }
            mHeaderContainerPhone.setVisibility(View.VISIBLE);
        }

        // emailing
        final EmailAddress emailAddress = mPerson.getPrimaryEmailAddress();
        if (emailAddress != null) {
            mHeaderEmail.setText(emailAddress.getEmail());
            mHeaderContainerEmail.setVisibility(View.VISIBLE);

            if (IntentHelper.canSendEmail()) {
                mHeaderActionEmail.setVisibility(View.VISIBLE);
                final OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        IntentHelper.sendEmail(emailAddress.getEmail(), null, null);
                    }
                };
                mHeaderEmail.setOnClickListener(listener);
                mHeaderActionEmail.setOnClickListener(listener);
            }
        } else {
            mHeaderActionEmail.setVisibility(View.GONE);
            mHeaderContainerEmail.setVisibility(View.GONE);
        }

        // assignment
        final ContactAssignment assignment = mPerson.getContactAssignment();
        if (assignment == null) {
            mHeaderAssignment.setText("Unassigned");
        } else {
            final Person assignedTo = Application.getDb().getPersonDao().load(assignment.getAssigned_to_id());
            if (assignedTo != null && !U.isNullEmpty(assignedTo.getName())) {
                mHeaderAssignment.setText(assignedTo.getName());
            } else {
                mHeaderAssignment.setText("Assignment Loading...");
            }
        }

        MoreInfoAdapter more = new MoreInfoAdapter(getSupportActivity());

        final Gender gender = mPerson.getGenderEnum();
        String birthdate = null;
        if (mPerson.getBirth_date() != null) {
            final SimpleDateFormat format = new SimpleDateFormat("MMMM dd", Locale.US);
            try {
                birthdate = format.format(mPerson.getBirth_date());
            } catch (Exception e) {
                birthdate = mPerson.getBirth_date().toString();
            }
        }
        final Address address = mPerson.getCurrentAddress();

        if (!U.isNullEmpty(gender, birthdate) || (address != null && address.isComplete())) {
            more.add(new MoreInfoAdapter.SectionHeaderItem(getString(R.string.contact_label_personal)));

            if (!U.isNullEmpty(gender)) {
                more.add(new MoreInfoAdapter.TextItem(gender.toString(), getString(R.string.contact_label_gender)));
            }
            if (!U.isNullEmpty(birthdate)) {
                more.add(new MoreInfoAdapter.TextItem(birthdate, getString(R.string.contact_label_birthday)));
            }
            if (!U.isNullEmpty(address) && address.isComplete()) {
                final String line1 = U.concatinate(", ", true, address.getAddress1(), address.getAddress2());
                final String line2 = U.concatinate(", ", true, address.getCity(), address.getState(), address.getZip(), address.getCountry());
                MoreInfoAdapter.TextItem item = new MoreInfoAdapter.TextItem(line1, line2);
                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.openMap(mPerson.getCurrentAddress());
                    }
                });
                more.add(item);
            }
        }

        final Long fbUid = mPerson.getFb_uid();
        if (fbUid != null) {
            more.add(new MoreInfoAdapter.SectionHeaderItem(getString(R.string.contact_label_links)));

            MoreInfoAdapter.IconTextItem item = new MoreInfoAdapter.IconTextItem(getString(R.string.facebook), R.drawable.ic_facebook);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.openFacebookProfile(mPerson.getFb_uid());
                }
            });
            more.add(item);
        }

        final List<Long> labelIds = mPerson.getLables(Session.getInstance().getOrganizationId());
        if (!labelIds.isEmpty()) {
            more.add(new MoreInfoAdapter.SectionHeaderItem(getString(R.string.contact_label_labels)));

            boolean needLabelUpdate = false;
            for (long labelId : labelIds) {
                final Role label = Application.getDb().getRoleDao().load(labelId);
                if (label != null) {
                    MoreInfoAdapter.IconTextItem item = new MoreInfoAdapter.IconTextItem(label.getTranslatedName(), R.drawable.ic_label);
                    item.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContactLabelsDialogFragment.showForResult(getChildFragmentManager(), mPerson, REQUEST_LABELS);
                        }
                    });
                    more.add(item);
                } else {
                    needLabelUpdate = true;
                }
            }

            if (needLabelUpdate) {
                refreshLabels();
            }
        }

        mHeaderMore.removeAllViews();
        for (int i = 0; i < more.getCount(); i++) {
            mHeaderMore.addView(more.getView(i, null, null));
        }

        if (!more.isEmpty()) {
            mHeaderMoreText.setVisibility(View.VISIBLE);
        } else {
            mHeaderMoreText.setVisibility(View.INVISIBLE);
        }

        updateMoreShowing();

        // the comment view
        updateCommentBox();

        // comments
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();

        final List<FollowupComment> comments = Application.getDb().getFollowupCommentDao().queryBuilder()
                .where(FollowupCommentDao.Properties.Contact_id.eq(mPerson.getId()), FollowupCommentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId()))
                .orderDesc(FollowupCommentDao.Properties.Updated_at).list();
        for (final FollowupComment comment : comments) {
            comment.refresh();
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
     * The list adapter for the comment list
     */
    private static class CommentArrayAdapter extends ObjectArrayAdapter {

        private AnimateOnceImageLoadingListener mLoadingListener = new AnimateOnceImageLoadingListener(250);

        /**
         * time ago object to generate recent dates
         */
        private static final TimeAgo sTimeAgo = new TimeAgo();

        /**
         * date format to generate less recent dates
         */
        private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("E d MMM yyyy hh:mma", Locale.US);

        /**
         * date used for comparisons
         */
        private static final Date sWeekAgo = new Date(System.currentTimeMillis() - (7 * 1000 * 60 * 60 * 24));

        private final DisplayImageOptions mImageLoaderOptions;

        public CommentArrayAdapter(final Context context) {
            super(context);

            mImageLoaderOptions = U.getContactImageDisplayOptions();
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
                    if (i.getCommenter() != null) {
                        Person person = i.getCommenter();

                        if (!U.isNullEmpty(person.getName())) {
                            holder.name.setText(i.getCommenter().getName());
                            holder.name.setVisibility(View.VISIBLE);
                        } else {
                            holder.name.setVisibility(View.GONE);
                        }

                        int px = Math.round(U.dpToPixel(40));
                        String picture = U.getProfilePicture(person, px, px);
                        ImageLoader.getInstance().displayImage(picture, holder.avatar, mImageLoaderOptions, mLoadingListener);
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
                        holder.status.setText(FollowupStatus.valueOf(i.comment.getStatus()).toString());
                        holder.status.setVisibility(View.VISIBLE);
                    } else {
                        holder.status.setVisibility(View.GONE);
                    }

                    holder.rejoiceConvo.setVisibility(View.GONE);
                    holder.rejoiceChrist.setVisibility(View.GONE);
                    holder.rejoiceGospel.setVisibility(View.GONE);

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

        /**
         * view holder for performance
         */
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
                if (Session.getInstance().isAdmin() || item.getCommenter().getId().compareTo(Session.getInstance().getPersonId()) == 0) {
                    actionItems.add(getString(R.string.action_delete));
                }

                if (!actionItems.isEmpty()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
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
    }

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
        if (getParent().hasProgress(item.comment.getId())) return;

        getParent().addProgress("delete_comment_" + item.comment.getId());

        final SafeAsyncTask<Void> task = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Api.deleteFollowupComment(item.comment.getId()).get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                if (mAdapter != null) {
                    mAdapter.remove(item);
                    if (mAdapter.isEmpty()) {
                        mAdapter.add(mEmptyItem);
                    }
                }
                if (isAdded()) {
                    Application.showToast(R.string.contact_comment_deleted, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFinally() {
                getParent().removeProgress("delete_comment_" + item.comment.getId());
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
        if (getParent().hasProgress("postComment")) return;

        final InputMethodManager imm = (InputMethodManager) getSupportActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCommentComment.getWindowToken(), 0);
        mCommentComment.clearFocus();

        updateCommentStateData();

        if (U.isNullEmpty(mComment.comment) && (mComment.rejoicables == null || mComment.rejoicables.length == 0) && mComment.status.equalsIgnoreCase(mPerson.getStatus().toString())) {
            Toast.makeText(getSupportActivity(), R.string.contact_cannot_comment, Toast.LENGTH_LONG).show();
            return;
        }

        getParent().addProgress("postComment");

        if (mAdapter != null) {
            mAdapter.insert(mProgressItem, 0);
        }

        mComment.commenter_id = Session.getInstance().getPersonId();
        mComment.contact_id = mPerson.getId();
        mComment.organization_id = Session.getInstance().getOrganizationId();

        final SafeAsyncTask<Void> task = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Api.createFollowupComment(mComment).get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                Toast.makeText(Application.getContext(), R.string.contact_comment_saved, Toast.LENGTH_SHORT).show();

                clearCommentBox();

                if (mComment.status != null && isVisible()) {
                    mCommentStatus.setSelection(U.FollowupStatus.valueOf(mComment.status).ordinal(), false);
                }

                if (mPerson != null) {
                    mPerson.resetOrganizationalRoleList();
                    mPerson.resetStatus();
                }

                refreshComments();
            }

            @Override
            public void onFinally() {
                getParent().removeProgress("postComment");

                if (mAdapter != null) {
                    mAdapter.remove(mProgressItem);
                }
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
        Application.getExecutor().execute(task.future());
    }

    private synchronized void refreshComments() {
        try {
            mRefreshCommentsTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }

        getParent().addProgress("refreshComments");

        mRefreshCommentsTask = new SafeAsyncTask<Person>() {

            @Override
            public Person call() throws Exception {
                final Person person = Api.getPerson(mPerson.getId(), ApiOptions.builder() //
                        .include(Include.comments_on_me) //
                        .include(Include.rejoicables) //
                        .include(Include.organizational_roles) //
                        .build()).get();

                person.refreshAll();
                return person;
            }

            @Override
            public void onSuccess(final Person person) {
                notifyPersonUpdated();
            }

            @Override
            public void onFinally() {
                mRefreshCommentsTask = null;
                if (getParent() != null) {
                    getParent().removeProgress("refreshComments");
                }
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Failed to refresh followup comments.");
            }

            @Override
            public void onInterrupted(final Exception e) {
            }
        };
        Application.getExecutor().execute(mRefreshCommentsTask.future());
    }

    private synchronized void refreshLabels() {
        try {
            mRefreshLabelsTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }

        getParent().addProgress("refreshLabels");

        mRefreshLabelsTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Api.listRoles().get();
                return null;
            }

            @Override
            public void onSuccess(Void t) {
                notifyPersonUpdated();
            }

            @Override
            public void onFinally() {
                mRefreshLabelsTask = null;
                if (getParent() != null) {
                    getParent().removeProgress("refreshLabels");
                }
            }

            @Override
            public void onException(final Exception e) {
            }

            @Override
            public void onInterrupted(final Exception e) {
            }
        };
        Application.getExecutor().execute(mRefreshLabelsTask.future());
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
            final FollowupStatus status = (FollowupStatus) getItem(position);
            View view = convertView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_simple_status, null);
            }

            final TextView tv = (TextView) view.findViewById(android.R.id.text1);
            tv.setText(status.toString());

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
        mComment.status = ((FollowupStatus) mCommentStatus.getSelectedItem()).name();

        final List<GRejoicable> rejoicables = new ArrayList<GRejoicable>();
        if ((Boolean) mCommentRejoiceChrist.getTag()) {
            rejoicables.add(U.Rejoicable.prayed_to_receive.rejoicable());
        }
        if ((Boolean) mCommentRejoiceGospel.getTag()) {
            rejoicables.add(U.Rejoicable.gospel_presentation.rejoicable());
        }
        if ((Boolean) mCommentRejoiceConvo.getTag()) {
            rejoicables.add(U.Rejoicable.spiritual_conversation.rejoicable());
        }
        mComment.rejoicables = rejoicables.toArray(new GRejoicable[rejoicables.size()]);
    }

    /**
     * Clears the data in the comment box
     */
    private void clearCommentBox() {
        mComment = new GFollowupComment();
        mCommentComment.setText("");
        mCommentComment.clearFocus();
        mCommentStatus.setSelection(mPerson.getStatus().ordinal(), false);
        mCommentRejoiceGospel.setTag(false);
        mCommentRejoiceChrist.setTag(false);
        mCommentRejoiceConvo.setTag(false);
        updateCommentBox();
    }

    /**
     * Updates the comment box with the contacts status and data in mComment
     */
    private void updateCommentBox() {
        final FollowupStatus status = mPerson.getStatus(Session.getInstance().getOrganizationId());
        if (status != null) {
            mCommentStatus.setSelection(status.ordinal(), false);
        }

        // restore comment from mComment
        if (mComment != null) {
            if (mComment.comment != null) {
                mCommentComment.setText(mComment.comment);
            }
            if (mComment.status != null) {
                mCommentStatus.setSelection(FollowupStatus.valueOf(mComment.status).ordinal());
            }
            mCommentRejoiceGospel.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_gospel_gray));
            mCommentRejoiceChrist.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_christ_gray));
            mCommentRejoiceConvo.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_convo_gray));
            if (mComment.rejoicables != null) {
                for (final GRejoicable r : mComment.rejoicables) {
                    if (r.what.contains(U.Rejoicable.spiritual_conversation.name())) {
                        mCommentRejoiceConvo.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_convo));
                        mCommentRejoiceConvo.setTag(true);
                    }
                    if (r.what.contains(U.Rejoicable.prayed_to_receive.name())) {
                        mCommentRejoiceChrist.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_christ));
                        mCommentRejoiceChrist.setTag(true);
                    }
                    if (r.what.contains(U.Rejoicable.gospel_presentation.name())) {
                        mCommentRejoiceGospel.setImageDrawable(DrawableCache.getDrawable(R.drawable.ic_rejoice_gospel));
                        mCommentRejoiceGospel.setTag(true);
                    }
                }
            }
        }
    }

    public ContactFragment getParent() {
        return (ContactFragment) getParentFragment();
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (requestCode == REQUEST_ASSIGNMENT && resultCode == FragmentResult.RESULT_OK) {
            mPerson.resetContactAssignments();
            notifyPersonUpdated();
            return true;
        } else if (requestCode == REQUEST_LABELS && resultCode == FragmentResult.RESULT_OK) {
            mPerson.resetLabels();
            notifyPersonUpdated();
            return true;
        }
        return super.onFragmentResult(requestCode, resultCode, data);
    }

    /**
     * Creates the information for the more info section
     */
    public static class MoreInfoAdapter extends ObjectArrayAdapter {

        public MoreInfoAdapter(Context context, int maxViewTypes) {
            super(context, maxViewTypes);
        }

        public MoreInfoAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            Object object = getItem(position);

            if (object instanceof SectionHeaderItem) {
                SectionHeaderItem item = (SectionHeaderItem) object;
                view = getLayoutInflater().inflate(R.layout.item_contact_more_header);
                TextView header = (TextView) view.findViewById(android.R.id.text1);

                if (!U.isNullEmpty(item.text1)) {
                    header.setText(item.text1);
                    header.setVisibility(View.VISIBLE);
                } else {
                    header.setVisibility(View.GONE);
                }
            } else if (object instanceof TextItem) {
                TextItem item = (TextItem) object;
                view = getLayoutInflater().inflate(R.layout.item_contact_more_text);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                if (!U.isNullEmpty(item.text1)) {
                    text1.setText(item.text1);
                    text1.setVisibility(View.VISIBLE);
                } else {
                    text1.setVisibility(View.GONE);
                }

                if (!U.isNullEmpty(item.text2)) {
                    text2.setText(item.text2);
                    text2.setVisibility(View.VISIBLE);
                } else {
                    text2.setVisibility(View.GONE);
                }
            } else if (object instanceof IconTextItem) {
                IconTextItem item = (IconTextItem) object;
                view = getLayoutInflater().inflate(R.layout.item_contact_more_icontext);
                ImageView icon = (ImageView) view.findViewById(android.R.id.icon1);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);

                if (item.drawableId != 0) {
                    icon.setImageDrawable(DrawableCache.getDrawable(item.drawableId));
                    icon.setVisibility(View.VISIBLE);
                } else {
                    icon.setVisibility(View.INVISIBLE);
                }

                if (!U.isNullEmpty(item.text1)) {
                    text1.setText(item.text1);
                    text1.setVisibility(View.VISIBLE);
                } else {
                    text1.setVisibility(View.GONE);
                }
            }

            if (!(object instanceof SectionHeaderItem)) {
                Object nextItem = null;
                try {
                    nextItem = getItem(position + 1);
                } catch (Exception e) { /* ignore */ }
                if (nextItem == null || nextItem instanceof SectionHeaderItem) {
                    View divider = view.findViewById(R.id.divider);
                    if (divider != null) {
                        divider.setVisibility(View.GONE);
                    }
                }
            }

            if (object instanceof SimpleTextItem) {
                SimpleTextItem item = (SimpleTextItem) object;
                if (item.onClickListener != null) {
                    view.setOnClickListener(item.onClickListener);
                }
            }

            return view;
        }

        public static abstract class SimpleTextItem {

            public String text1;
            public OnClickListener onClickListener;

            public SimpleTextItem(String text1) {
                this.text1 = text1;
            }

            public void setOnClickListener(OnClickListener onClickListener) {
                this.onClickListener = onClickListener;
            }

        }

        public static class SectionHeaderItem extends SimpleTextItem {
            public SectionHeaderItem(String text1) {
                super(text1);
            }
        }

        public static class TextItem extends SimpleTextItem {
            public String text2;

            public TextItem(String text1) {
                super(text1);
            }

            public TextItem(String text1, String text2) {
                super(text1);
                this.text2 = text2;
            }
        }

        public static class IconTextItem extends SimpleTextItem {
            public int drawableId;

            public IconTextItem(String text1, int drawableId) {
                super(text1);
                this.drawableId = drawableId;
            }
        }

    }
}
