package com.missionhub.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.api.ListOptions;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.event.OnHostedListOptionsChangedEvent;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.dialog.AssignmentDialogFragment;
import com.missionhub.fragment.dialog.BulkUpdateDialogFragment;
import com.missionhub.fragment.dialog.CheckAllDialog;
import com.missionhub.fragment.dialog.DeletePeopleDialogFragment;
import com.missionhub.fragment.dialog.EditContactDialogFragment;
import com.missionhub.fragment.dialog.InteractionDialogFragment;
import com.missionhub.model.InteractionType;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.people.ApiPeopleListProvider;
import com.missionhub.people.DynamicPeopleListProvider;
import com.missionhub.people.PeopleListView;
import com.missionhub.people.PersonAdapterViewProvider;
import com.missionhub.ui.AdapterViewProvider;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.SearchHelper;
import com.missionhub.ui.widget.CheckmarkImageView;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TaskUtils;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import java.util.HashSet;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class HostedPeopleListFragment extends HostedFragment implements AdapterView.OnItemSelectedListener,
        PeopleListView.OnPersonClickListener, DynamicPeopleListProvider.OnExceptionListener, OnRefreshListener,
        SearchHelper.OnSearchQueryChangedListener, CheckAllDialog.CheckAllDialogListener, ActionMode.Callback {

    public static final String TAG = HostedPeopleListFragment.class.getSimpleName();
    private PullToRefreshLayout mPullToRefreshLayout;
    private PeopleListView mList;
    private SelectableApiPeopleListProvider mProvider;
    private SearchView mSearchView;
    private CheckmarkImageView mCheckmark;
    private TextView mCheckmarkText;
    private Spinner mDisplaySpinner;
    private ObjectArrayAdapter mDisplaySpinnerAdapter;
    private int mDisplayPosition;
    private Spinner mOrderSpinner;
    private ObjectArrayAdapter mOrderSpinnerAdapter;
    private int mOrderPosition;
    private SafeAsyncTask<Void> mReloadStatusTask;
    private SearchHelper mSearchHelper;
    private CheckmarkHelper mCheckmarkHelper = new CheckmarkHelper();
    private ActionMode mActionMode;

    private View mFilterIndicator;
    private TextView mFilterIndicatorText;
    private TextView mFilterIndicatorClear;
    private SafeAsyncTask<String> mIndicatorTask;
    private int mOrderSpinnerVisibility;

    public HostedPeopleListFragment() {
        // empty fragment constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Application.registerEventSubscriber(this, OnSidebarItemClickedEvent.class, OnOrganizationChangedEvent.class);
    }

    @Override
    public void onPrepareActionBar(ActionBar actionBar) {
        actionBar.setTitle("Contacts");
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OnSidebarItemClickedEvent event) {
        if (mProvider == null) return;

        Object item = event.getItem();
        PeopleListOptions options = mProvider.getPeopleListOptions();

        String search = options.getFilterValue("name_or_email_like");

        if (item instanceof Person) {
            options.toggleSingle("assigned_to", ((Person) item).getId());
        } else if (item instanceof Label) {
            options.toggleSingle("labels", ((Label) item).getId());
        } else if (item instanceof Permission) {
            options.toggleSingle("permissions", ((Permission) item).getId());
        } else if (item instanceof InteractionType) {
            options.toggleSingle("interactions", ((InteractionType) item).getId());
        } else {
            return;
        }

        if (StringUtils.isNotEmpty(search)) {
            options.setFilter("name_or_email_like", search);
        }

        mProvider.setPeopleListOptions(options);
        Application.getEventBus().postSticky(new OnHostedListOptionsChangedEvent(mProvider.getPeopleListOptions()));
        refreshIndicator();

        closeMenu();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OnOrganizationChangedEvent event) {
        if (mProvider == null) return;

        mProvider.reload();
    }

    private void clearFilters() {
        if (mProvider == null) return;

        PeopleListOptions options = mProvider.getPeopleListOptions();
        options.clearFilters();
        mSearchHelper.clear();
        mProvider.setPeopleListOptions(options);
        Application.getEventBus().postSticky(new OnHostedListOptionsChangedEvent(mProvider.getPeopleListOptions()));
        refreshIndicator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list, parent, false);

        // set up the person list and adapter
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getSupportActivity())
            .allChildrenArePullable()
            .listener(this)
            .setup(mPullToRefreshLayout);

        mList = (PeopleListView) view.findViewById(android.R.id.list);
        if (mProvider == null) {
            mProvider = new SelectableApiPeopleListProvider(inflater.getContext());
        } else {
            mProvider.setContext(inflater.getContext());
        }
        mProvider.setOnExceptionListener(this);
        try {
            mProvider.registerDataSetObserver(mCheckmarkHelper);
        } catch (IllegalStateException exception) { /* ignore already registered */ }
        Application.getEventBus().postSticky(new OnHostedListOptionsChangedEvent(mProvider.getPeopleListOptions()));

        mList.setProvider(mProvider);
        mList.setOnPersonClickListener(this);
        mList.setOnPersonCheckedListener(mCheckmarkHelper);
        //mList.setOnScrollListener(new NetworkImageOnScrollListener(false, true));

        // set up the filter indicator
        mFilterIndicator = view.findViewById(R.id.filter_indicator);
        mFilterIndicatorText = (TextView) view.findViewById(R.id.filter_indicator_text);
        mFilterIndicatorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Layout layout = mFilterIndicatorText.getLayout();
                if (layout != null) {
                    if (layout.getLineCount() > 0) {
                        if (layout.getEllipsisCount(layout.getLineCount() - 1) > 0) {
                            mFilterIndicatorText.setSingleLine(false);
                            return;
                        }
                    }
                }
                mFilterIndicatorText.setSingleLine(true);
            }
        });
        mFilterIndicatorClear = (TextView) view.findViewById(R.id.filter_indicator_clear);
        mFilterIndicatorClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilters();
            }
        });

        // set up the list controller
        mSearchView = (SearchView) view.findViewById(R.id.search);
        if (mSearchHelper == null) {
            mSearchHelper = new SearchHelper();
            mSearchHelper.setOnSearchQueryChangedListener(this);
        }
        mSearchHelper.setSearchView(mSearchView);

        mCheckmark = (CheckmarkImageView) view.findViewById(R.id.checkmark);
        mCheckmark.setOnClickListener(mCheckmarkHelper);
        mCheckmarkText = (TextView) view.findViewById(R.id.checkmark_text);
        mCheckmarkHelper.refreshCheckedState();
        mDisplaySpinner = (Spinner) view.findViewById(R.id.display);
        mDisplaySpinner.setOnItemSelectedListener(this);
        mOrderSpinner = (Spinner) view.findViewById(R.id.order);
        mOrderSpinner.setOnItemSelectedListener(this);

        if (mDisplaySpinnerAdapter == null) {
            mDisplaySpinnerAdapter = buildDisplaySpinner(inflater.getContext());
        } else {
            mDisplaySpinnerAdapter.setContext(inflater.getContext());
        }
        mDisplaySpinner.setAdapter(mDisplaySpinnerAdapter);

        if (mOrderSpinnerAdapter == null) {
            mOrderSpinnerAdapter = buildOrderSpinner(inflater.getContext());
        } else {
            mOrderSpinnerAdapter.setContext(inflater.getContext());
        }
        mOrderSpinner.setAdapter(mOrderSpinnerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onResume();

        if (mActionMode != null) {
            startActionMode(true);
        }
        mCheckmarkHelper.refreshCheckedState();
        refreshIndicator();
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrderSpinner.setVisibility(mOrderSpinnerVisibility);
    }

    @Override
    public void onPause() {
        mOrderSpinnerVisibility = mOrderSpinner.getVisibility();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        finishActionMode();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            mReloadStatusTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        StringRunnableItem item = (StringRunnableItem) parent.getItemAtPosition(position);
        if (parent == mDisplaySpinner) {
            if (mDisplayPosition == position) return;
            mDisplayPosition = position;
        } else if (parent == mOrderSpinner) {
            if (mOrderPosition == position) return;
            mOrderPosition = position;
        }
        try {
            item.run();
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPersonClick(PeopleListView list, Person person, int position, long id) {
        openProfile(person.getId());
    }

    public void openProfile(long personId) {
        ChangeHostFragmentEvent event = new ChangeHostFragmentEvent(HostedProfileFragment.class);
        event.setAddToBackstack(true);
        event.setInAnimation(R.anim.slide_in_right, R.anim.slide_in_left);
        event.setOutAnimation(R.anim.slide_out_left, R.anim.slide_out_right);

        Bundle bundle = new Bundle();
        bundle.putLong("personId", personId);
        event.setNewInstance(true, bundle);

        Application.postEvent(event);
    }

    @Override
    public void onException(Throwable t) {
        if (getSupportActivity() == null) return;
        ExceptionHelper eh = new ExceptionHelper(getSupportActivity(), t);
        eh.setPositiveButton(new ExceptionHelper.DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_retry);
            }

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mProvider != null) {
                    mProvider.setPaused(false);
                    mProvider.load();
                }
                dialog.dismiss();
            }
        });
        eh.setNeutralButton(new ExceptionHelper.DialogButton() {
            @Override
            public String getTitle() {
                return getString(R.string.action_cancel);
            }

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mProvider != null) {
                    mProvider.setDone(true);
                    mProvider.cancelLoadMore();
                }
                dialog.dismiss();
            }
        });
        eh.show();
    }

    @Override
    public void onRefreshStarted(View view) {
        if (mProvider == null) return;

        mProvider.reload();

        mReloadStatusTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                int time = 0;
                while (time < 20000 && mProvider.isLoading()) {
                    Thread.sleep(100);
                    time += 100;
                }
                return null;
            }

            @Override
            public void onFinally() {
                if (mPullToRefreshLayout != null) {
                    mPullToRefreshLayout.setRefreshComplete();
                }
            }
        };
        Application.getExecutor().execute(mReloadStatusTask.future());
    }

    @Override
    public void onSearchQueryChanged(String query) {
        if (mProvider == null) return;

        PeopleListOptions options = mProvider.getPeopleListOptions();
        if (StringUtils.isNotEmpty(query)) {
            options.setFilter("name_or_email_like", query);
        } else {
            options.removeFilter("name_or_email_like");
        }
        mProvider.setPeopleListOptions(options);
        refreshIndicator();
    }

    private SimpleSpinnerAdapter buildDisplaySpinner(Context context) {
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(context);
        adapter.add(new StringRunnableItem(R.string.display_status, new Runnable() {
            @Override
            public void run() {
                mOrderSpinner.setVisibility(View.VISIBLE);
                mProvider.setDisplay(PersonAdapterViewProvider.Display.STATUS);
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_gender, new Runnable() {
            @Override
            public void run() {
                mOrderSpinner.setVisibility(View.VISIBLE);
                mProvider.setDisplay(PersonAdapterViewProvider.Display.GENDER);
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_permission, new Runnable() {
            @Override
            public void run() {
                mOrderSpinner.setVisibility(View.VISIBLE);
                mProvider.setDisplay(PersonAdapterViewProvider.Display.PERMISSION);
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_labels, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.LABELS);
                mOrderSpinner.setSelection(0, false); // no filters for labels
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
                mOrderSpinner.setVisibility(View.GONE);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_phone, new Runnable() {
            @Override
            public void run() {
                mOrderSpinner.setVisibility(View.VISIBLE);
                mProvider.setDisplay(PersonAdapterViewProvider.Display.PHONE);
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_email, new Runnable() {
            @Override
            public void run() {
                mOrderSpinner.setVisibility(View.VISIBLE);
                mProvider.setDisplay(PersonAdapterViewProvider.Display.EMAIL);
                ((StringRunnableItem) mOrderSpinner.getSelectedItem()).run();
            }
        }));
        return adapter;
    }

    private SimpleSpinnerAdapter buildOrderSpinner(Context context) {
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(context);
        adapter.add(new StringRunnableItem(R.string.sort_off, new Runnable() {
            @Override
            public void run() {
                PeopleListOptions options = mProvider.getPeopleListOptions();
                if (!options.getOrders().isEmpty()) {
                    options.clearOrders();
                    mProvider.setPeopleListOptions(options);
                }
            }
        }));
        adapter.add(new StringRunnableItem(R.string.sort_asc, new Runnable() {
            @Override
            public void run() {
                PeopleListOptions options = mProvider.getPeopleListOptions();
                options.clearOrders();
                switch (mProvider.getDisplay()) {
                    case STATUS:
                        options.addOrder("followup_status", ListOptions.Direction.ASC);
                        break;
                    case GENDER:
                        options.addOrder("gender", ListOptions.Direction.ASC);
                        break;
                    case EMAIL:
                        options.addOrder("primary_email", ListOptions.Direction.ASC);
                        break;
                    case PHONE:
                        options.addOrder("primary_phone", ListOptions.Direction.ASC);
                        break;
                    case PERMISSION:
                        options.addOrder("permission", ListOptions.Direction.ASC);
                        break;
                }
                options.addOrder("last_name", ListOptions.Direction.ASC);
                options.addOrder("first_name", ListOptions.Direction.ASC);
                mProvider.setPeopleListOptions(options);
                mProvider.setPeopleListOptions(options);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.sort_desc, new Runnable() {
            @Override
            public void run() {
                PeopleListOptions options = mProvider.getPeopleListOptions();
                options.clearOrders();
                switch (mProvider.getDisplay()) {
                    case STATUS:
                        options.addOrder("followup_status", ListOptions.Direction.DESC);
                        break;
                    case GENDER:
                        options.addOrder("gender", ListOptions.Direction.DESC);
                        break;
                    case EMAIL:
                        options.addOrder("primary_email", ListOptions.Direction.DESC);
                        break;
                    case PHONE:
                        options.addOrder("primary_phone", ListOptions.Direction.DESC);
                        break;
                    case PERMISSION:
                        options.addOrder("permission", ListOptions.Direction.DESC);
                        break;
                }
                options.addOrder("last_name", ListOptions.Direction.ASC);
                options.addOrder("first_name", ListOptions.Direction.ASC);
                mProvider.setPeopleListOptions(options);
            }
        }));
        return adapter;
    }

    @Override
    public void setAllChecked(boolean all) {
        mCheckmarkHelper.setAllChecked(all);
    }

    public static class StringRunnableItem {
        private int mText;
        private Runnable mRunnable;

        public StringRunnableItem(int text, Runnable runnable) {
            mText = text;
            mRunnable = runnable;
        }

        public void run() {
            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }

    public static class SelectableApiPeopleListProvider extends ApiPeopleListProvider {

        public SelectableApiPeopleListProvider(Context context) {
            super(context);
        }

        @Override
        public AdapterViewProvider onCreateViewProvider() {
            return new PersonAdapterViewProvider();
        }

        public void setDisplay(PersonAdapterViewProvider.Display display) {
            ((PersonAdapterViewProvider) getAdapterViewProvider()).setLine2(display);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        public PersonAdapterViewProvider.Display getDisplay() {
            return ((PersonAdapterViewProvider) getAdapterViewProvider()).getLine2();
        }
    }

    public static class SimpleSpinnerAdapter extends ObjectArrayAdapter<StringRunnableItem> {

        public SimpleSpinnerAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup, R.layout.simple_spinner_item);
        }

        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup, R.layout.simple_spinner_dropdown_item);
        }

        public View getView(int i, View view, ViewGroup viewGroup, int layout) {
            StringRunnableItem item = getItem(i);
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(layout, viewGroup, false);
                holder.text1 = (android.widget.TextView) view.findViewById(android.R.id.text1);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text1.setText(item.mText);

            return view;
        }

        private static class ViewHolder {
            android.widget.TextView text1;
        }
    }

    public class CheckmarkHelper extends DataSetObserver implements PeopleListView.OnPersonCheckedListener, View.OnClickListener {
        private boolean mAllChecked;
        private CheckAllDialog mDialog;

        public boolean isAllChecked() {
            return mAllChecked;
        }

        public void setAllChecked() {
            if (mDialog == null || mDialog.getFragmentManager() != getChildFragmentManager()) {
                mDialog = new CheckAllDialog();
            }
            mDialog.show(getChildFragmentManager());
        }

        public void setAllChecked(boolean all) {
            if (mList == null) return;

            mAllChecked = all;
            mList.setAllItemsChecked();
            refreshCheckedState();
        }

        public void setNoneChecked() {
            if (mList == null) return;

            mList.clearChoices();
            mAllChecked = false;
            refreshCheckedState();
        }

        @Override
        public void onChanged() {
            if (mAllChecked) {
                setAllChecked(true);
            } else {
                refreshCheckedState();
            }
        }

        @Override
        public void onPersonChecked(PeopleListView list, Person person, int position, boolean checked) {
            refreshCheckedState();
        }

        @Override
        public void onAllPeopleUnchecked() {
            refreshCheckedState();
        }

        public synchronized void refreshCheckedState() {
            if (mList == null || mCheckmark == null || mCheckmarkText == null) return;

            if (mList.getCheckedItemCount() > 0) {
                if (mAllChecked) {
                    mCheckmark.setCheckmarkState(CheckmarkImageView.STATE_ALL);
                    mCheckmarkText.setText("NONE");
                } else {
                    mCheckmark.setCheckmarkState(CheckmarkImageView.STATE_SOME);
                    mCheckmarkText.setText("SOME");
                }
                startActionMode(false);
            } else {
                mCheckmark.setCheckmarkState(CheckmarkImageView.STATE_NONE);
                mCheckmarkText.setText("ALL");
                finishActionMode();
                closeDialog();
            }
        }

        @Override
        public void onClick(View view) {
            if (mList.getCheckedItemCount() > 0) {
                setNoneChecked();
            } else {
                setAllChecked();
            }
        }

        public void closeDialog() {
            if (mDialog != null && mDialog.isVisible()) {
                mDialog.cancel();
                mDialog = null;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem addContact = menu.add(Menu.NONE, R.id.action_add_contact, Menu.NONE, R.string.action_add_contact).setIcon(R.drawable.ic_action_add_contact);
        MenuItemCompat.setShowAsAction(addContact, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItem addInteraction = menu.add(Menu.NONE, R.id.action_interaction, Menu.NONE, R.string.action_record_interaction).setIcon(R.drawable.ic_action_interaction);
        MenuItemCompat.setShowAsAction(addInteraction, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_interaction:
                InteractionDialogFragment.showForResult(getChildFragmentManager(), R.id.action_interaction);
                return true;
            case R.id.action_add_contact:
                EditContactDialogFragment.showForResult(getChildFragmentManager(), R.id.action_add_contact);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        MenuItem assign = menu.add(Menu.NONE, R.id.action_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign);
        MenuItemCompat.setShowAsAction(assign, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        MenuItem label = menu.add(Menu.NONE, R.id.action_label, Menu.NONE, R.string.action_labels).setIcon(R.drawable.ic_action_labels);
        MenuItemCompat.setShowAsAction(label, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        MenuItem permission = menu.add(Menu.NONE, R.id.action_permission, Menu.NONE, R.string.action_permissions).setIcon(R.drawable.ic_action_permissions);
        MenuItemCompat.setShowAsAction(permission, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        MenuItem delete = menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, R.string.action_delete).setIcon(R.drawable.ic_action_delete);
        MenuItemCompat.setShowAsAction(delete, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

//      MenuItem archive = menu.add(Menu.NONE, R.id.action_archive, Menu.NONE, R.string.action_archive).setIcon(R.drawable.ic_action_archive);
//      MenuItemCompat.setShowAsAction(archive, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        MenuItem email = menu.add(Menu.NONE, R.id.action_email, Menu.NONE, R.string.action_email).setIcon(R.drawable.ic_action_email);
        MenuItemCompat.setShowAsAction(email, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        MenuItem text = menu.add(Menu.NONE, R.id.action_text, Menu.NONE, R.string.action_text).setIcon(R.drawable.ic_action_text);
        MenuItemCompat.setShowAsAction(text, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }

    @Override
    public synchronized boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_assign:
                if (mCheckmarkHelper.isAllChecked()) {
                    AssignmentDialogFragment.showForResult(getChildFragmentManager(), mProvider.getPeopleListOptions(), R.id.action_assign);
                } else {
                    AssignmentDialogFragment.showForResult(getChildFragmentManager(), mList.getCheckedItemIds(), R.id.action_assign);
                }
                break;
            case R.id.action_label:
                if (mCheckmarkHelper.isAllChecked()) {
                    BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_LABELS, mProvider.getPeopleListOptions(), R.id.action_label);
                } else {
                    BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_LABELS, mList.getCheckedItemIds(), R.id.action_label);
                }
                break;
            case R.id.action_permission:
                if (mCheckmarkHelper.isAllChecked()) {
                    BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_PERMISSIONS, mProvider.getPeopleListOptions(), R.id.action_permission);
                } else {
                    BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_PERMISSIONS, mList.getCheckedItemIds(), R.id.action_permission);
                }
                break;
            case R.id.action_delete:
                if (mCheckmarkHelper.isAllChecked()) {
                    DeletePeopleDialogFragment.showForResult(getChildFragmentManager(), mProvider.getPeopleListOptions(), R.id.action_delete);
                } else {
                    DeletePeopleDialogFragment.showForResult(getChildFragmentManager(), mList.getCheckedItemIds(), R.id.action_delete);
                }
                break;
            case R.id.action_archive:
                // TODO: implement archive
                break;
            case R.id.action_email:
                IntentHelper.sendEmail(mList.getCheckedItemIds());
                finishActionMode();
                break;
            case R.id.action_text:
                IntentHelper.sendSms(mList.getCheckedItemIds());
                finishActionMode();
                break;
        }

        return true;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        mActionMode = null;
        if (isVisible() && isResumed()) {
            mCheckmarkHelper.setNoneChecked();
        }
    }

    protected void startActionMode(boolean force) {
        if (mActionMode == null || force) {
            mActionMode = getSupportActivity().startSupportActionMode(this);
        }
    }

    protected void finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (resultCode != RESULT_OK) return super.onFragmentResult(requestCode, resultCode, data);

        mCheckmarkHelper.setNoneChecked();

        switch (requestCode) {
            case R.id.action_assign:
                if (mProvider.getPeopleListOptions().hasFilter("assigned_to")) {
                    mProvider.reload();
                }
                return true;
            case R.id.action_add_contact:
                if (data != null && data instanceof Long) {
                    openProfile((Long) data);
                }
                return true;
            case R.id.action_delete:
                mProvider.removeAllById((HashSet<Long>) data);
                return true;
            case R.id.action_label:
                if (mProvider.getPeopleListOptions().hasFilter("labels")) {
                    mProvider.reload();
                } else {
                    mProvider.notifyDataSetChanged();
                }
                break;
            case R.id.action_permission:
                if (mProvider.getPeopleListOptions().hasFilter("permissions")) {
                    mProvider.reload();
                } else {
                    mProvider.notifyDataSetChanged();
                }
        }

        return super.onFragmentResult(requestCode, resultCode, data);
    }

    private void refreshIndicator() {
        if (mProvider == null || mFilterIndicator == null) return;
        final PeopleListOptions options = mProvider.getPeopleListOptions();

        TaskUtils.cancel(mIndicatorTask);

        mIndicatorTask = new SafeAsyncTask<String>() {
            @Override
            public String call() throws Exception {
                return options.toHumanString();
            }

            @Override
            protected void onSuccess(String s) throws Exception {
                if (!StringUtils.isEmpty(s)) {
                    mFilterIndicatorText.setText(Html.fromHtml(s));
                    mFilterIndicator.setVisibility(View.VISIBLE);
                } else {
                    mFilterIndicator.setVisibility(View.GONE);
                }
            }
        };
        mIndicatorTask.execute();
    }

    @Override
    public void onDestroy() {
        TaskUtils.cancel(mIndicatorTask);
        super.onDestroy();
    }
}

