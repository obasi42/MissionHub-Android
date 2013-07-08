package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.common.collect.ArrayListMultimap;
import com.missionhub.R;
import com.missionhub.activity.AboutActivity;
import com.missionhub.activity.HostActivity;
import com.missionhub.api.ListOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.event.OnHostedListOptionsChangedEvent;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.event.OnHostFragmentChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.fragment.dialog.SelectOrganizationDialogFragment;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.model.Survey;
import com.missionhub.model.SurveyDao;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.SupportActivatedTextView;
import com.missionhub.util.FragmentUtils;
import com.missionhub.util.SafeAsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SidebarFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = SidebarFragment.class.getSimpleName();
    private ListView mList;
    private Parcelable mListState;
    private SidebarListAdapter mAdapter;
    private SafeAsyncTask<List<Object>> mBuildTask;
    private ListOptions mListOptions;
    private WeakReference<HostedFragment> mHostedFragment;

    public SidebarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.retainInstance(this);

        Application.registerEventSubscriber(this, OnHostFragmentChangedEvent.class, OnOrganizationChangedEvent.class);
        Application.getEventBus().registerSticky(this, OnHostedListOptionsChangedEvent.class);
    }

    @SuppressWarnings("unused")
    public void onEvent(OnHostFragmentChangedEvent event) {
        if (mHostedFragment != null && mHostedFragment.get() != event.mFragment) {
            mHostedFragment = new WeakReference<HostedFragment>(event.mFragment);
            update();
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(OnOrganizationChangedEvent event) {
        update();
    }

    @SuppressWarnings("unused")
    public void onEvent(OnHostedListOptionsChangedEvent event) {
        mListOptions = event.getOptions();
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sidebar, group, false);
        mList = (ListView) view.findViewById(android.R.id.list);

        // get the last list options changed event
        if (mListOptions == null) {
            OnHostedListOptionsChangedEvent event = (OnHostedListOptionsChangedEvent) Application.getEventBus().getStickyEvent(OnHostedListOptionsChangedEvent.class);
            if (event != null) {
                mListOptions = event.getOptions();
            }
        }

        // set up adapter
        if (mAdapter == null) {
            mAdapter = new SidebarListAdapter(getSupportActivity());
            update();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);
        if (mListState != null) {
            mList.onRestoreInstanceState(mListState);
            mListState = null;
        }
        mList.setOnItemClickListener(this);

        return mList;
    }

    public HostActivity getHostActivity() {
        return (HostActivity) getSupportActivity();
    }

    private void update() {
        if (mAdapter == null) return;

        cancelUpdate();

        mBuildTask = new SafeAsyncTask<List<Object>>() {
            @Override
            public List<Object> call() throws Exception {
                ArrayList<Object> newItems = new ArrayList<Object>();

                HostedFragment hostedFragment = getHostActivity().getCurrentFragment();

                // top level item
                if (hostedFragment instanceof HostedSurveysFragment) {
                    newItems.add(getOrCreateMainItem("Contacts", R.id.menu_item_contacts));
                } else if (hostedFragment instanceof HostedPeopleListFragment) {
                    newItems.add(getOrCreateMainItem("Surveys", R.id.menu_item_surveys));
                } else if (hostedFragment instanceof HostedProfileFragment) {
                    newItems.add(getOrCreateMainItem("Contacts", R.id.menu_item_contacts));
                    newItems.add(getOrCreateMainItem("Surveys", R.id.menu_item_surveys));
                }

                // people list fragment
                if (hostedFragment instanceof HostedPeopleListFragment) {
                    // labels
                    newItems.add(getOrCreateHeaderItem("Labels", "labels"));
                    List<Label> labels = Session.getInstance().getOrganization().getAllLabels();
                    SidebarListAdapter.ExpandItem labelExpand = getOrCreateExpandItem("labels");
                    int lLimit = 5;
                    if (labelExpand.isExpanded()) {
                        lLimit = labels.size();
                    }
                    for (int i = 0; i < lLimit; i++) {
                        SidebarListAdapter.Item item = getOrCreateLabelItem(labels.get(i));
                        if (item != null) {
                            newItems.add(item);
                        }
                    }
                    newItems.add(labelExpand);

                    // admins/users
                    newItems.add(getOrCreateHeaderItem("Users", "users"));
                    SidebarListAdapter.ExpandItem userExpand = getOrCreateExpandItem("users");

                    List<Person> users = Session.getInstance().getOrganization().getUsersAdmins();
                    Person currentUser = Session.getInstance().getPerson();
                    users.remove(currentUser);
                    newItems.add(getOrCreateUserItem(currentUser));

                    int uLimit = 4;
                    if (userExpand.isExpanded()) {
                        uLimit = users.size();
                    }
                    for (int i = 0; i < uLimit; i++) {
                        SidebarListAdapter.Item item = getOrCreateUserItem(users.get(i));
                        if (item != null) {
                            newItems.add(item);
                        }
                    }
                    newItems.add(userExpand);

                    // permissions
                    newItems.add(getOrCreateHeaderItem("Permissions", "permissions"));
                    newItems.add(getOrCreatePermissionItem(Permission.ADMIN));
                    newItems.add(getOrCreatePermissionItem(Permission.USER));
                    newItems.add(getOrCreatePermissionItem(Permission.NO_PERMISSIONS));
                }

                // surveys fragment
                if (hostedFragment instanceof HostedSurveysFragment) {
                    newItems.add(getOrCreateHeaderItem("Surveys", "surveys"));
                    List<Survey> surveys = Application.getDb().getSurveyDao().queryBuilder().where(SurveyDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).list();
                    for (Survey survey : surveys) {
                        getOrCreateSurveyItem(survey);
                    }
                }

                newItems.add(getOrCreateHeaderItem("Settings", "settings"));
                newItems.add(getOrCreateMainItem("Organization", R.id.menu_item_organization));
                newItems.add(getOrCreateMainItem("About", R.id.menu_item_about));
                newItems.add(getOrCreateMainItem("Help", R.id.menu_item_help));
                newItems.add(getOrCreateMainItem("Logout", R.id.menu_item_logout));

                return newItems;
            }

            @Override
            public void onSuccess(List<Object> newItems) throws Exception {
                if (mAdapter == null) return;

                synchronized (mAdapter.getLock()) {
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.clear();
                    mAdapter.addAll(newItems);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onInterrupted(Exception e) {

            }

            @Override
            public void onException(Exception e) throws RuntimeException {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onThrowable(Throwable t) throws RuntimeException {
                Log.e(TAG, t.getMessage(), t);
            }

            @Override
            public void onFinally() {
                mBuildTask = null;
            }

            private SidebarListAdapter.HeaderItem getOrCreateHeaderItem(CharSequence text, CharSequence group) {
                SidebarListAdapter.HeaderItem item = mAdapter.getHeaderItem(group);
                if (item == null) {
                    item = new SidebarListAdapter.HeaderItem(text, group);
                }
                return item;
            }

            private SidebarListAdapter.MainItem getOrCreateMainItem(CharSequence text, Object object) {
                SidebarListAdapter.MainItem item = (SidebarListAdapter.MainItem) mAdapter.getItem(object);
                if (item == null) {
                    item = new SidebarListAdapter.MainItem(text, object);
                }
                return item;
            }

            private SidebarListAdapter.Item getOrCreateItem(CharSequence text, Object object, CharSequence group) {
                SidebarListAdapter.Item item = (SidebarListAdapter.Item) mAdapter.getItem(object);
                if (item == null) {
                    item = new SidebarListAdapter.Item(text, object, group);
                }
                return item;
            }

            private SidebarListAdapter.ExpandItem getOrCreateExpandItem(CharSequence group) {
                SidebarListAdapter.ExpandItem item = mAdapter.getExpandItem(group);
                if (item == null) {
                    item = new SidebarListAdapter.ExpandItem(group);
                }
                return item;
            }

            private SidebarListAdapter.Item getOrCreatePermissionItem(long permissionId) {
                Permission permission = Permission.getPermission(permissionId);

                boolean selected = mListOptions.hasFilter("permissions", String.valueOf(permission.getId()));

                SidebarListAdapter.Item item = getOrCreateItem(permission.getTranslatedName(), permission, "permissions");
                item.setSelected(selected);
                return item;
            }

            private SidebarListAdapter.Item getOrCreateUserItem(Person person) {
                if (person == null) return null;
                if (StringUtils.isEmpty(person.getName())) return null;

                boolean selected = mListOptions.hasFilter("assigned_to", String.valueOf(person.getId()));

                SidebarListAdapter.Item item = getOrCreateItem(person.getName(), person, "users");
                item.setSelected(selected);
                return item;
            }

            private SidebarListAdapter.Item getOrCreateLabelItem(Label label) {
                if (label == null) return null;
                if (StringUtils.isEmpty(label.getTranslatedName())) return null;

                boolean selected = mListOptions.hasFilter("labels", String.valueOf(label.getId()));

                SidebarListAdapter.Item item = getOrCreateItem(label.getTranslatedName(), label, "labels");
                item.setSelected(selected);
                return item;
            }

            private SidebarListAdapter.Item getOrCreateSurveyItem(Survey survey) {
                if (survey == null) return null;
                if (StringUtils.isEmpty(survey.getTitle())) return null;

                return getOrCreateItem(survey.getTitle(), survey, "surveys");
            }
        };
        Application.getExecutor().execute(mBuildTask.future());
    }

    @Override
    public void onDestroy() {
        cancelUpdate();
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    public void cancelUpdate() {
        try {
            mBuildTask.cancel(true);
            mBuildTask = null;
        } catch (Exception e) { /* ignore */ }
    }

    private static class SidebarListAdapter extends ObjectArrayAdapter<Object> {

        private HashMap<Object, Object> mTrackedItems = new HashMap<Object, Object>();
        private ArrayListMultimap<CharSequence, Object> mGroupObjects = ArrayListMultimap.create();

        public SidebarListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Object item = getItem(i);
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                if (item instanceof Item) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar, viewGroup, false);
                    holder.text1 = (SupportActivatedTextView) view.findViewById(android.R.id.text1);
                } else if (item instanceof HeaderItem) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar_header, viewGroup, false);
                    holder.text1 = (SupportActivatedTextView) view.findViewById(android.R.id.text1);
                } else if (item instanceof ExpandItem) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar_expand, viewGroup, false);
                    holder.text1 = (SupportActivatedTextView) view.findViewById(android.R.id.text1);
                } else if (item instanceof MainItem) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar_main, viewGroup, false);
                    holder.text1 = (SupportActivatedTextView) view.findViewById(android.R.id.text1);
                } else {
                    throw new RuntimeException("Unknown item view type");
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (holder.text1 != null) {
                if (item instanceof TextItem && ((TextItem) item).getText() != null) {
                    holder.text1.setText(((TextItem) item).getText());
                } else {
                    holder.text1.setText("");
                }

                if (item instanceof SelectableItem) {
                    if (((SelectableItem) item).isSelected()) {
                        holder.text1.setSupportActivated(true);
                    } else {
                        holder.text1.setSupportActivated(false);
                    }
                }
            }
            return view;
        }

        public static class ViewHolder {
            SupportActivatedTextView text1;
        }

        public void clearTracked() {
            synchronized (getLock()) {
                mTrackedItems.clear();
                mGroupObjects.clear();
            }
        }

        @Override
        public void add(final Object object) {
            synchronized (getLock()) {
                trackObject(object);
            }
            super.add(object);
        }

        @Override
        public void addAll(final Collection<Object> objects) {
            synchronized (getLock()) {
                for (Object object : objects) {
                    trackObject(object);
                }
            }
            super.addAll(objects);
        }

        @Override
        public void remove(final Object object) {
            synchronized (getLock()) {
                untrackObject(object);
            }
            super.remove(object);
        }

        @Override
        public void removeAll(final Collection<Object> objects) {
            synchronized (getLock()) {
                for (Object object : objects) {
                    untrackObject(object);
                }
            }
        }

        private void trackObject(Object object) {
            synchronized (getLock()) {
                mTrackedItems.put(getTrackedObjectKey(object), object);
                if (object instanceof Item) {
                    mGroupObjects.put(((Item) object).getGroup(), object);
                }
            }
        }

        private void untrackObject(Object object) {
            synchronized (getLock()) {
                mTrackedItems.remove(getTrackedObjectKey(object));
                if (object instanceof Item) {
                    mGroupObjects.remove(((Item) object).getGroup(), object);
                }
            }
        }

        private Object getTrackedObjectKey(Object object) {
            if (object instanceof ObjectItem) {
                return ((ObjectItem) object).getObject();
            }
            if (object instanceof GroupItem) {
                CharSequence group = ((GroupItem) object).getGroup();

                if (group != null) {
                    return group + object.getClass().getSimpleName();
                }
            }
            throw new RuntimeException("Unknown Object Key");
        }

        public Object getItem(Object object) {
            return mTrackedItems.get(object);
        }

        public HeaderItem getHeaderItem(CharSequence group) {
            synchronized (getLock()) {
                return (HeaderItem) mTrackedItems.get(group + HeaderItem.class.getSimpleName());
            }
        }

        public ExpandItem getExpandItem(CharSequence group) {
            synchronized (getLock()) {
                synchronized (getLock()) {
                    return (ExpandItem) mTrackedItems.get(group + ExpandItem.class.getSimpleName());
                }
            }
        }

        public static interface TextItem {
            public CharSequence getText();
        }

        public static interface GroupItem {
            public CharSequence getGroup();
        }

        public static interface ObjectItem {
            public Object getObject();
        }

        public static interface SelectableItem {
            public boolean isSelected();

            public void setSelected(boolean selected);
        }

        public static class Item implements TextItem, GroupItem, ObjectItem, SelectableItem {
            private CharSequence mText1;
            private Object mObject;
            private CharSequence mGroup;
            private boolean mSelected;

            public Item(CharSequence text, Object object, CharSequence group) {
                mText1 = text;
                mObject = object;
                mGroup = group;
            }

            @Override
            public CharSequence getGroup() {
                return mGroup;
            }

            @Override
            public Object getObject() {
                return mObject;
            }

            @Override
            public CharSequence getText() {
                return mText1;
            }

            @Override
            public boolean isSelected() {
                return mSelected;
            }

            @Override
            public void setSelected(boolean selected) {
                mSelected = selected;
            }
        }

        public static class HeaderItem extends ObjectArrayAdapter.DisabledItem implements TextItem, GroupItem {
            private CharSequence mText1;
            private CharSequence mGroup;

            public HeaderItem(CharSequence text, CharSequence group) {
                mText1 = text;
                mGroup = group;
            }

            @Override
            public CharSequence getText() {
                return mText1;
            }

            @Override
            public CharSequence getGroup() {
                return mGroup;
            }
        }

        public static class MainItem implements TextItem, ObjectItem {
            private CharSequence mText1;
            private Object mObject;

            public MainItem(CharSequence text, Object object) {
                mText1 = text;
                mObject = object;
            }

            @Override
            public Object getObject() {
                return mObject;
            }

            @Override
            public CharSequence getText() {
                return mText1;
            }
        }

        public static class ExpandItem implements GroupItem, TextItem {
            private boolean mExpanded = false;
            private CharSequence mGroup;
            private CharSequence mShowFewer = "SHOW FEWER";
            private CharSequence mShowMore = "SHOW MORE";

            public ExpandItem(CharSequence group) {

                mGroup = group;
            }

            @Override
            public CharSequence getGroup() {
                return mGroup;
            }

            public boolean isExpanded() {
                return mExpanded;
            }

            public void setExpanded(boolean expanded) {
                mExpanded = expanded;
            }

            @Override
            public CharSequence getText() {
                if (isExpanded()) {
                    return mShowFewer;
                } else {
                    return mShowMore;
                }
            }

            public void toggleExpanded() {
                mExpanded = !mExpanded;
            }
        }
    }

    public void clone(SidebarFragment oldFragment) {
        mBuildTask = oldFragment.mBuildTask;
        oldFragment.mBuildTask = null;
        mAdapter = oldFragment.mAdapter;
        oldFragment.mAdapter = null;
        mListOptions = oldFragment.mListOptions;
        oldFragment.mListOptions = null;
        if (oldFragment.mList != null) {
            mListState = oldFragment.mList.onSaveInstanceState();
            oldFragment.mList.setAdapter(null);
            oldFragment.mList = null;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object item = adapterView.getItemAtPosition(position);
        if (item instanceof SidebarListAdapter.ExpandItem) {
            ((SidebarListAdapter.ExpandItem) item).toggleExpanded();
            update();
            return;
        }
        if (item instanceof SidebarListAdapter.ObjectItem) {
            item = ((SidebarListAdapter.ObjectItem) item).getObject();
        }
        Application.postEvent(new OnSidebarItemClickedEvent(this, item));
    }
}
