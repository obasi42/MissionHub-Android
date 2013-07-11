package com.missionhub.fragment.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.widget.SearchView;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Interaction;
import com.missionhub.model.InteractionType;
import com.missionhub.model.Person;
import com.missionhub.model.generic.InteractionVisibility;
import com.missionhub.model.gson.GInteraction;
import com.missionhub.people.ApiPeopleListProvider;
import com.missionhub.people.DynamicPeopleListProvider;
import com.missionhub.people.PeopleListProvider;
import com.missionhub.people.PeopleListView;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ViewArrayPagerAdapter;
import com.missionhub.ui.widget.LockableViewPager;
import com.missionhub.util.DateUtils;
import com.missionhub.util.SafeAsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.internal.DialogTitle;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.DatePicker;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.TimePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class InteractionDialogFragment extends BaseDialogFragment implements ViewPager.OnPageChangeListener, DialogInterface.OnKeyListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    public static final String TAG = InteractionDialogFragment.class.getSimpleName();

    private GInteraction mInteraction = new GInteraction();

    private ImageView mIcon;
    private DialogTitle mTitle;
    private ImageView mRefresh;
    private Animation mRefreshAnimation;
    private LockableViewPager mViewPager;
    private Button mButton1;
    private Button mButton2;

    private ViewArrayPagerAdapter mPagerAdaper;

    // interaction form
    private View mInteractionForm;
    private TextView mInteractionInitiators;
    private Spinner mInteractionType;
    private SpinnerAdapter<InteractionType> mInteractionTypeAdapter;
    private TextView mInteractionReceiver;
    private Spinner mInteractionVisibility;
    private SpinnerAdapter<InteractionVisibility> mInteractionVisibilityAdapter;
    private TextView mInteractionDateTime;
    private EditText mInteractionComment;

    // date/time form
    private View mDateTimeView;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    // people list form
    private View mPeopleView;
    private TextView mPeopleListTitle;
    private SearchView mPeopleListSearchView;
    private PeopleListView mPeopleListView;
    private ApiPeopleListProvider mReceiverPeopleListProvider;
    private PeopleListProvider mInitiatorsPeopleListProvider;

    private SafeAsyncTask<Void> mRefreshInitiatorsTask;
    private SafeAsyncTask<Void> mSaveInteractionTask;

    private static final int STATE_FORM = 0;
    private static final int STATE_DATE_TIME = 1;
    private static final int STATE_RECEIVER = 2;
    private static final int STATE_INITIATORS = 3;

    private int mState = STATE_FORM;
    private SafeAsyncTask<List<Person>> mRebuildInitiatorsProviderTask;

    public static void showForResult(FragmentManager fm, int requestCode) {
        showForResult(InteractionDialogFragment.class, fm, requestCode);
    }

    public static void showForResult(FragmentManager fm, long receiverId, Collection<Long> initiatorIds, int requestCode) {
        Bundle args = new Bundle();
        args.putLong("receiverId", receiverId);
        args.putSerializable("initiatorIds", new HashSet<Long>(initiatorIds));
        showForResult(InteractionDialogFragment.class, fm, args, requestCode);
    }

    public static void showForResult(FragmentManager fm, long interactionId, int requestCode) {
        Bundle args = new Bundle();
        args.putLong("interactionId", interactionId);
        showForResult(InteractionDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

        // register for time change ticks to keep the time box up to date
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        Application.getContext().registerReceiver(mTimeChangedReceiver, intentFilter);

        if (getArguments() != null) {
            synchronized (this) {
                setReceiverId(getArguments().getLong("receiverId", 0));
                setInitiatorIds((HashSet<Long>) getArguments().getSerializable("initiatorIds"));
                setInteractionId(getArguments().getLong("interactionId", 0));
            }
        }

        if (mInteraction.initiator_ids == null || mInteraction.initiator_ids.length == 0) {
            mInteraction.initiator_ids = new Long[]{Session.getInstance().getPersonId()};
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override
    public void onDestroy() {
        Application.getContext().unregisterReceiver(mTimeChangedReceiver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frame = inflater.inflate(R.layout.dialog_fragment_interaction, container, false);

        // the container view
        mIcon = (ImageView) frame.findViewById(R.id.icon);
        mTitle = (DialogTitle) frame.findViewById(R.id.alertTitle);
        if (isNew()) {
            mTitle.setText(R.string.interaction_dialog_title_new);
        } else {
            mTitle.setText(R.string.interaction_dialog_title_edit);
        }
        mRefresh = (ImageView) frame.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRefresh();
            }
        });
        mRefreshAnimation = AnimationUtils.loadAnimation(getSupportActivity(), R.anim.clockwise_refresh);
        mRefreshAnimation.setRepeatCount(Animation.INFINITE);
        mViewPager = (LockableViewPager) frame.findViewById(R.id.pager);
        mButton1 = (Button) frame.findViewById(R.id.button1);
        mButton2 = (Button) frame.findViewById(R.id.button2);

        // the interaction form
        mInteractionForm = inflater.inflate(R.layout.dialog_fragment_interaction_form, mViewPager, false);
        mInteractionInitiators = (TextView) mInteractionForm.findViewById(R.id.initiators);
        mInteractionInitiators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState(STATE_INITIATORS, true);
            }
        });
        mInteractionType = (Spinner) mInteractionForm.findViewById(R.id.type);
        if (mInteractionTypeAdapter == null) {
            mInteractionTypeAdapter = new SpinnerAdapter<InteractionType>(getSupportActivity());
            rebuildTypeSpinnerAdapter();
        } else {
            mInteractionTypeAdapter.setContext(getSupportActivity());
        }
        mInteractionType.setAdapter(mInteractionTypeAdapter);
        mInteractionReceiver = (TextView) mInteractionForm.findViewById(R.id.receiver);
        mInteractionReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState(STATE_RECEIVER, true);
            }
        });
        mInteractionVisibility = (Spinner) mInteractionForm.findViewById(R.id.visibility);
        if (mInteractionVisibilityAdapter == null) {
            mInteractionVisibilityAdapter = new SpinnerAdapter<InteractionVisibility>(getSupportActivity());
            rebuildVisibilitySpinnerAdapter();
        } else {
            mInteractionVisibilityAdapter.setContext(getSupportActivity());
        }
        mInteractionVisibility.setAdapter(mInteractionVisibilityAdapter);
        mInteractionDateTime = (TextView) mInteractionForm.findViewById(R.id.date_time);
        mInteractionDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState(STATE_DATE_TIME, true);
            }
        });
        mInteractionComment = (EditText) mInteractionForm.findViewById(R.id.comment);

        // the date/time form
        mDateTimeView = inflater.inflate(R.layout.dialog_fragment_interaction_datetime, mViewPager, false);
        mDatePicker = (DatePicker) mDateTimeView.findViewById(R.id.date);
        mDatePicker.setOnDateChangedListener(this);
        mTimePicker = (TimePicker) mDateTimeView.findViewById(R.id.time);
        mTimePicker.setOnTimeChangedListener(this);

        // the people list
        mPeopleView = inflater.inflate(R.layout.dialog_fragment_interaction_people, mViewPager, false);
        mPeopleListTitle = (TextView) mPeopleView.findViewById(R.id.title);
        mPeopleListSearchView = (SearchView) mPeopleView.findViewById(R.id.search);
        mPeopleListView = (PeopleListView) mPeopleView.findViewById(android.R.id.list);

        if (mInitiatorsPeopleListProvider == null) {
            mInitiatorsPeopleListProvider = new PeopleListProvider(getSupportActivity());
            rebuildInitiatorsProvider();
        } else {
            mInitiatorsPeopleListProvider.setContext(getSupportActivity());
        }
        if (mReceiverPeopleListProvider == null) {
            mReceiverPeopleListProvider = new ApiPeopleListProvider(getSupportActivity(), false);
            mReceiverPeopleListProvider.setOnExceptionListener(new DynamicPeopleListProvider.OnExceptionListener() {
                @Override
                public void onException(Throwable t) {

                }
            });
            mReceiverPeopleListProvider.setLoadingListener(new DynamicPeopleListProvider.OnLoadingListener() {
                @Override
                public void onLoading(boolean loading) {
                    updateRefreshState();
                }
            });
        } else {
            mReceiverPeopleListProvider.setContext(getSupportActivity());
        }

        // set up the pager
        mPagerAdaper = new ViewArrayPagerAdapter();
        mPagerAdaper.addView(mInteractionForm);
        mViewPager.setAdapter(mPagerAdaper);
        mViewPager.setOnPageChangeListener(this);

        setState(mState, false);

        restoreFromInteraction();

        return frame;
    }

    public synchronized void setInteractionId(long id) {
        if (id > 0) {
            Interaction interaction = Application.getDb().getInteractionDao().load(id);
            if (interaction != null) {
                mInteraction = GInteraction.from(interaction);
                restoreFromInteraction();
                setState(STATE_FORM, false);
            }
        }
    }

    public synchronized void setReceiverId(long receiverId) {
        mInteraction.receiver_id = receiverId;
        updateReceiverBox();
    }

    private synchronized void updateReceiverBox() {
        if (mInteractionReceiver == null) return;

        if (mInteraction.receiver_id > 0) {
            Person receiver = Application.getDb().getPersonDao().load(mInteraction.receiver_id);
            mInteractionReceiver.setText(receiver.getName());
        } else {
            mInteractionReceiver.setText("");
        }
    }

    public synchronized void setInitiatorIds(Collection<Long> initiatorIds) {
        if (initiatorIds == null) return;
        mInteraction.initiator_ids = initiatorIds.toArray(new Long[initiatorIds.size()]);
        updateInitiatorsBox();
    }

    private synchronized void updateInitiatorsBox() {
        if (mInteractionInitiators == null) return;

        List<String> names = new ArrayList<String>();
        if (mInteraction.initiator_ids != null) {
            for (long initiatorId : mInteraction.initiator_ids) {
                Person receiver = Application.getDb().getPersonDao().load(initiatorId);
                if (StringUtils.isNotEmpty(receiver.getName())) {
                    names.add(receiver.getName());
                } else {
                    names.add("Id: " + receiver.getId());
                }
            }
        }

        if (names.isEmpty()) {
            mInteractionInitiators.setText("");
        } else {
            mInteractionInitiators.setText(StringUtils.join(names, ", "));
        }
    }


    private synchronized void updateCommentBox() {
        if (mInteractionComment == null) return;

        if (StringUtils.isNotEmpty(mInteraction.comment)) {
            mInteractionComment.setText(mInteraction.comment);
        } else {
            mInteractionComment.setText("");
        }
    }

    private synchronized void updateDateTimeBox(boolean updatePickers) {
        if (mInteractionDateTime == null) return;

        DateTime dateTime = DateUtils.parseSqlTimestamp(mInteraction.timestamp);
        if (dateTime == null) {
            dateTime = DateTime.now(DateTimeZone.UTC);
        }

        mInteractionDateTime.setText(dateTime.toString(DateTimeFormat.forPattern("d MMM yyyy h:mm a").withZone(DateTimeZone.getDefault())));

        if (updatePickers) {
            updateDateTimePickersQuietly(dateTime);
        }
    }

    private synchronized void updateDateTimePickersQuietly(DateTime dateTime) {
        if (mDatePicker == null || mTimePicker == null) return;

        dateTime = dateTime.toDateTime(DateTimeZone.getDefault());

        mDatePicker.setOnDateChangedListener(null);
        mDatePicker.updateDate(dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
        mDatePicker.setOnDateChangedListener(this);

        mTimePicker.setOnTimeChangedListener(null);
        mTimePicker.setCurrentHour(dateTime.getHourOfDay());
        mTimePicker.setCurrentMinute(dateTime.getMinuteOfHour());
        mTimePicker.setOnTimeChangedListener(this);
    }

    private synchronized void updateVisiblitySpinner() {
        if (mInteractionVisibility == null || mInteractionVisibilityAdapter == null) return;

        synchronized (mInteractionVisibilityAdapter.getLock()) {
            for (int i = 0; i < mInteractionVisibilityAdapter.getCount(); i++) {
                if (mInteractionVisibilityAdapter.getItem(i).name().equals(mInteraction.privacy_setting)) {
                    mInteractionVisibility.setSelection(i);
                    return;
                }
            }
        }
        mInteractionVisibility.setSelection(0);
    }


    private synchronized void updateTypeSpinner() {
        if (mInteractionType == null || mInteractionTypeAdapter == null) return;

        synchronized (mInteractionTypeAdapter.getLock()) {
            for (int i = 0; i < mInteractionTypeAdapter.getCount(); i++) {
                if (mInteractionTypeAdapter.getItem(i).getId() == mInteraction.interaction_type_id) {
                    mInteractionType.setSelection(i);
                    return;
                }
            }
        }
        mInteractionType.setSelection(0);
    }

    private synchronized void rebuildTypeSpinnerAdapter() {
        mInteractionTypeAdapter.setNotifyOnChange(false);
        mInteractionTypeAdapter.clear();

        List<InteractionType> types = Session.getInstance().getOrganization().getAllInteractionTypes();
        for (InteractionType type : types) {
            mInteractionTypeAdapter.add(type);
        }

        mInteractionTypeAdapter.notifyDataSetChanged();
    }

    private synchronized void rebuildVisibilitySpinnerAdapter() {
        mInteractionVisibilityAdapter.setNotifyOnChange(false);
        mInteractionVisibilityAdapter.clear();

        mInteractionVisibilityAdapter.add(InteractionVisibility.everyone);
        if (Session.getInstance().getOrganization().getParent() != null) {
            mInteractionVisibilityAdapter.add(InteractionVisibility.parent);
        }
        mInteractionVisibilityAdapter.add(InteractionVisibility.organization);
        mInteractionVisibilityAdapter.add(InteractionVisibility.admins);
        mInteractionVisibilityAdapter.add(InteractionVisibility.me);

        mInteractionTypeAdapter.notifyDataSetChanged();
    }

    public synchronized void saveToInteraction() {
        mInteraction.privacy_setting = ((InteractionVisibility) mInteractionVisibility.getSelectedItem()).name();
        mInteraction.interaction_type_id = ((InteractionType) mInteractionType.getSelectedItem()).getId();
        mInteraction.comment = mInteractionComment.getText().toString();
    }

    public synchronized void restoreFromInteraction() {
        updateReceiverBox();
        updateInitiatorsBox();
        updateDateTimeBox(true);
        updateVisiblitySpinner();
        updateTypeSpinner();
        updateCommentBox();
    }

    @Override
    public synchronized void onDestroyView() {
        saveToInteraction();

        super.onDestroyView();
    }

    private void setState(int state, boolean scroll) {
        if (mViewPager == null || (mState == state && isResumed())) return;

        mState = state;
        switch (mState) {
            case STATE_FORM:
                mViewPager.setCurrentItem(0, scroll);
                mViewPager.setPagingLocked(LockableViewPager.LOCK_BOTH);
                break;
            case STATE_DATE_TIME:
                setSecondaryPage(mDateTimeView, scroll);
                break;
            case STATE_RECEIVER:
                setSecondaryPage(mPeopleView, scroll);
                break;
            case STATE_INITIATORS:
                setSecondaryPage(mPeopleView, scroll);
                break;
        }

        updateButtonStates();
        updateRefreshState();
        updatePersonListViewState();
    }

    private void updateButtonStates() {
        if (mButton1 == null || mButton2 == null) return;

        switch (mState) {
            case STATE_FORM:
                mButton1.setEnabled(true);
                mButton1.setText(R.string.action_cancel);
                mButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancel();
                    }
                });
                mButton1.setVisibility(View.VISIBLE);

                mButton2.setEnabled(true);
                mButton2.setText(R.string.action_save);
                mButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveInteraction();
                    }
                });
                mButton2.setVisibility(View.VISIBLE);
                break;
            case STATE_DATE_TIME:
            case STATE_RECEIVER:
            case STATE_INITIATORS:
                mButton1.setVisibility(View.GONE);
                mButton2.setEnabled(true);
                mButton2.setText(R.string.action_done);
                mButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setState(STATE_FORM, true);
                    }
                });
                mButton2.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateRefreshState() {
        if (mRefresh == null || mRefreshAnimation == null) return;

        switch (mState) {
            case STATE_RECEIVER:
            case STATE_INITIATORS:
                mRefresh.setVisibility(View.VISIBLE);

                if (mRefreshInitiatorsTask != null || mRebuildInitiatorsProviderTask != null || mReceiverPeopleListProvider.isLoading()) {
                    mRefresh.startAnimation(mRefreshAnimation);
                    mRefresh.setEnabled(false);
                } else {
                    mRefresh.clearAnimation();
                    mRefresh.setEnabled(true);
                }
                break;
            default:
                mRefresh.clearAnimation();
                mRefresh.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePersonListViewState() {
        switch (mState) {
            case STATE_RECEIVER:
                mPeopleListTitle.setText("Receiver");
                mPeopleListSearchView.setVisibility(View.VISIBLE);
                mPeopleListView.setProvider(mReceiverPeopleListProvider);
                mPeopleListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                //setCheckedPositions(mPeopleListView, mInteraction.receiver_id);
                break;
            case STATE_INITIATORS:
                mPeopleListTitle.setText("Initiator(s)");
                mPeopleListSearchView.setVisibility(View.GONE);
                mPeopleListView.setProvider(mInitiatorsPeopleListProvider);
                mPeopleListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                //setCheckedPositions(mPeopleListView, mInteraction.initiator_ids);
                break;
        }
    }

    private void saveInteraction() {
        // TODO:
    }

    private void setSecondaryPage(View view, boolean scroll) {
        if (mPagerAdaper.getCount() > 1) {
            mPagerAdaper.setView(1, view);
        } else {
            mPagerAdaper.addView(view);
        }
        mViewPager.setCurrentItem(1, scroll);
        mViewPager.setPagingLocked(LockableViewPager.LOCK_NONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (mViewPager.getCurrentItem() == 0) {
                setState(0, false);
            }
            if (mState == STATE_FORM) {
                if (mPagerAdaper.getCount() > 1) {
                    mPagerAdaper.removeView(1);
                }
            }
        }
    }

    @Override
    public synchronized void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DateTime dateTime = DateUtils.fixInstantDateTime(year, monthOfYear + 1, dayOfMonth, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
        mInteraction.timestamp = DateUtils.toSqlTimestamp(dateTime);
        updateDateTimeBox(false);
    }

    @Override
    public synchronized void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        DateTime dateTime = DateUtils.fixInstantDateTime(mDatePicker.getYear(), mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth(), hourOfDay, minute);
        mInteraction.timestamp = DateUtils.toSqlTimestamp(dateTime);
        updateDateTimeBox(false);
    }

    private static class SpinnerAdapter<T> extends ObjectArrayAdapter<T> {

        private HashMap<Integer, Drawable> mDrawableCache = new HashMap<Integer, Drawable>();

        public SpinnerAdapter(Context context) {
            super(context);
        }

        @Override
        public void setContext(Context context) {
            mDrawableCache.clear();
            super.setContext(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_interaction_dialog_spinner, parent, false);
                holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Object object = getItem(position);
            if (object instanceof InteractionType) {
                holder.text1.setText(((InteractionType) object).getTranslatedName());
                int iconId = ((InteractionType) object).getIconResource();
                if (iconId != 0) {
                    Drawable drawable = mDrawableCache.get(iconId);
                    if (drawable == null) {
                        drawable = getContext().getResources().getDrawable(iconId);
                        mDrawableCache.put(iconId, drawable);
                    }
                    holder.icon.setImageDrawable(drawable);
                    holder.icon.setVisibility(View.VISIBLE);
                } else {
                    holder.icon.setVisibility(View.GONE);
                }
            } else if (object instanceof InteractionVisibility) {
                holder.text1.setText(object.toString());
            }
            return view;
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        private static class ViewHolder {
            TextView text1;
            ImageView icon;
        }
    }

    public boolean isNew() {
        return StringUtils.isEmpty(mInteraction.created_at);
    }

    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(this);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (mViewPager != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mViewPager.pageBackward(true)) {
                cancel();
            }
            return true;
        }
        return getSupportActivity().dispatchKeyEvent(event);
    }

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mInteraction != null && StringUtils.isEmpty(mInteraction.timestamp)) {
                updateDateTimeBox(true);
            }
        }
    };

    private void rebuildInitiatorsProvider() {
        if (mRebuildInitiatorsProviderTask != null) return;

        mRebuildInitiatorsProviderTask = new SafeAsyncTask<List<Person>>() {
            @Override
            public List<Person> call() throws Exception {
                return Session.getInstance().getOrganization().getUsersAdmins();
            }

            @Override
            public void onSuccess(List<Person> people) {
                synchronized (mInitiatorsPeopleListProvider.getLock()) {
                    mInitiatorsPeopleListProvider.setNotifyOnChange(false);
                    mInitiatorsPeopleListProvider.clear();
                    mInitiatorsPeopleListProvider.addAll(people);
                    mInitiatorsPeopleListProvider.notifyDataSetChanged();
                }
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onFinally() {
                mRebuildInitiatorsProviderTask = null;
                updateRefreshState();
            }

        };
        updateRefreshState();
        Application.getExecutor().execute(mRebuildInitiatorsProviderTask.future());
    }

    private void refreshInitiators() {
        if (mRefreshInitiatorsTask != null) return;

        mRefreshInitiatorsTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Session.getInstance().updateCurrentOrganization(true).get();
                return null;
            }

            @Override
            public void onSuccess(Void _) {
                rebuildInitiatorsProvider();
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onFinally() {
                mRefreshInitiatorsTask = null;
                updateRefreshState();
            }

        };
        updateRefreshState();
        Application.getExecutor().execute(mRefreshInitiatorsTask.future());
    }

    private void clickRefresh() {
        switch (mState) {
            case STATE_INITIATORS:
                refreshInitiators();
                break;
            case STATE_RECEIVER:
                mReceiverPeopleListProvider.reload();
                break;
        }
    }
}
