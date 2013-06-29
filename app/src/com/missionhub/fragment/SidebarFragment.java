package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ArrayListMultimap;
import com.missionhub.R;
import com.missionhub.activity.HostActivity;
import com.missionhub.application.Application;
import com.missionhub.event.OnHostFragmentChangedEvent;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.FragmentUtils;
import com.missionhub.util.SafeAsyncTask;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SidebarFragment extends Fragment {

    public static final String TAG = SidebarFragment.class.getSimpleName();
    private ListView mList;
    private SidebarListAdapter mAdapter;
    private SafeAsyncTask<Void> mAdapterTask;
    private Parcelable mCloneState;

    public SidebarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.retainInstance(this);
        Application.registerEventSubscriber(this, OnHostFragmentChangedEvent.class);
    }

    public void onEvent(OnHostFragmentChangedEvent event) {
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sidebar, group, false);
        mList = (ListView) view.findViewById(android.R.id.list);

        // set up adapter
        if (mAdapter == null) {
            mAdapter = new SidebarListAdapter(getSupportActivity());
            update();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);
        if (mCloneState != null) {
            mList.onRestoreInstanceState(mCloneState);
            mCloneState = null;
        }

        return mList;
    }

    public HostActivity getHostActivity() {
        return (HostActivity) getSupportActivity();
    }

    private void update() {
        cancelUpdate();

        mAdapterTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                ArrayList<Object> newItems = new ArrayList<Object>();

                HostedFragment hostedFragment = getHostActivity().getCurrentFragment();

                // top level item
                if (hostedFragment instanceof HostedSurveysFragment) {
                    newItems.add(getOrCreateMainItem("Contacts", HostedPeopleListFragment.class));
                } else if (hostedFragment instanceof HostedPeopleListFragment) {
                    newItems.add(getOrCreateMainItem("Surveys", HostedSurveysFragment.class));
                } else if (hostedFragment instanceof HostedProfileFragment) {
                    newItems.add(getOrCreateMainItem("Contacts", HostedPeopleListFragment.class));
                    newItems.add(getOrCreateMainItem("Surveys", HostedSurveysFragment.class));
                }

                // people list fragment
                if (hostedFragment instanceof HostedPeopleListFragment) {
                    // labels
                    newItems.add(getOrCreateHeaderItem("Labels", "labels"));

                    // leaders
                    newItems.add(getOrCreateHeaderItem("Leaders", "leaders"));


                    // permissions
                    newItems.add(getOrCreateHeaderItem("Permissions", "permissions"));
                }

                // surveys fragment
                if (hostedFragment instanceof HostedSurveysFragment) {
                    // permissions
                    newItems.add(getOrCreateHeaderItem("Surveys", "surveys"));
                }

                mAdapter.setNotifyOnChange(false);
                mAdapter.clear();
                mAdapter.addAll(newItems);

                return null;
            }

            @Override
            public void onSuccess(Void _) throws Exception {
                if (mAdapter == null) return;

                mAdapter.notifyDataSetChanged();
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
                mAdapterTask = null;
            }

            private SidebarListAdapter.HeaderItem getOrCreateHeaderItem(CharSequence text, CharSequence group) {
                SidebarListAdapter.HeaderItem item = mAdapter.getHeaderItem(group);
                if (item == null) {
                    item = new SidebarListAdapter.HeaderItem(text, group);
                }
                return item;
            }

            private SidebarListAdapter.MainItem getOrCreateMainItem(CharSequence text, Type object) {
                SidebarListAdapter.MainItem item = (SidebarListAdapter.MainItem) mAdapter.getItem(object);
                if (item == null) {
                    item = new SidebarListAdapter.MainItem(text, object);
                }
                return item;
            }
        };
        Application.getExecutor().execute(mAdapterTask.future());
    }

    @Override
    public void onDestroy() {
        cancelUpdate();
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    public void cancelUpdate() {
        try {
            mAdapterTask.cancel(true);
            mAdapterTask = null;
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
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                } else if (item instanceof HeaderItem) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar_header, viewGroup, false);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                } else if (item instanceof ExpandItem) {
                    view = getLayoutInflater().inflate(R.layout.item_progress, viewGroup, false);
                } else if (item instanceof MainItem) {
                    view = getLayoutInflater().inflate(R.layout.item_sidebar_main, viewGroup, false);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                } else {
                    throw new RuntimeException("Unknown item view type");
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (holder.text1 != null && item instanceof TextItem && ((TextItem) item).getText() != null) {
                holder.text1.setText(((TextItem) item).getText());
            } else {
                holder.text1.setText("");
            }

            return view;
        }

        public static class ViewHolder {
            TextView text1;
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

        public static class Item implements TextItem, GroupItem, ObjectItem {
            private CharSequence mText1;
            private Object mObject;
            private CharSequence mGroup;

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

        public static class ExpandItem implements GroupItem {
            boolean expanded = false;
            boolean loading = true;
            private CharSequence mGroup;
            private CharSequence showFewer = "Show Fewer";
            private CharSequence showMore = "Show More";
            private CharSequence group;

            public ExpandItem(CharSequence group) {
                mGroup = group;
            }

            @Override
            public CharSequence getGroup() {
                return mGroup;
            }
        }
    }

    public void clone(SidebarFragment oldFragment) {
        mAdapterTask = oldFragment.mAdapterTask;
        oldFragment.mAdapterTask = null;
        mAdapter = oldFragment.mAdapter;
        oldFragment.mAdapter = null;
        if (oldFragment.mList != null) {
            mCloneState = oldFragment.mList.onSaveInstanceState();
            oldFragment.mList.setAdapter(null);
            oldFragment.mList = null;
        }
    }

}
