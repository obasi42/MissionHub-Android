package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.CheckmarkImageView;
import com.missionhub.util.SafeAsyncTask;

import org.apache.commons.lang3.ArrayUtils;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionLabelDialogFragment extends RefreshableDialogFragment implements AdapterView.OnItemClickListener {

    public static final int TYPE_LABELS = 0;
    public static final int TYPE_PERMISSIONS = 1;
    private HashSet<Person> mPeople;
    private PeopleListOptions mFilters;
    private int mType = TYPE_LABELS;
    private SafeAsyncTask<Void> mSaveTask;
    private SafeAsyncTask<Void> mRefreshTask;
    private SafeAsyncTask<List<Item>> mBuildTask;
    private TextView mWarning;
    private View mListContainer;
    private ListView mList;
    private PermissionLabelAdapter<Item> mAdapter;
    private View mProgress;
    private TextView mProgressText;

    public PermissionLabelDialogFragment() {
    }

    public static PermissionLabelDialogFragment showForResult(FragmentManager fm, int type, final Person person, Integer requestCode) {
        return showForResult(fm, type, person.getId(), requestCode);
    }

    public static PermissionLabelDialogFragment showForResult(FragmentManager fm, int type, long personId, Integer requestCode) {
        return showForResult(fm, type, new long[]{personId}, requestCode);
    }

    public static PermissionLabelDialogFragment showForResult(FragmentManager fm, int type, final long[] people, Integer requestCode) {
        final Bundle args = new Bundle();
        args.putInt("type", type);
        args.putSerializable("peopleIds", new HashSet<Long>(Arrays.asList(ArrayUtils.toObject(people))));
        return showForResult(fm, type, Person.getAllById(people), requestCode);
    }

    public static PermissionLabelDialogFragment showForResult(FragmentManager fm, int type, final PeopleListOptions filters, Integer requestCode) {
        final Bundle args = new Bundle();
        args.putSerializable("filters", filters);
        args.putInt("type", type);
        return PermissionLabelDialogFragment.show(PermissionLabelDialogFragment.class, fm, args, requestCode);
    }

    public static PermissionLabelDialogFragment showForResult(FragmentManager fm, int type, final Collection<Person> people, Integer requestCode) {
        final Bundle args = new Bundle();
        args.putSerializable("peopleIds", new HashSet<Long>(Person.getIds(people)));
        args.putInt("type", type);
        return PermissionLabelDialogFragment.show(PermissionLabelDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mType = getArguments().getInt("type", 0);
            mFilters = (PeopleListOptions) getArguments().getSerializable("filters");
            final Collection<Long> peopleIds = (Collection<Long>) getArguments().getSerializable("peopleIds");
            if (peopleIds != null) {
                mPeople = new HashSet<Person>();
                for (Long id : peopleIds) {
                    Person person = Application.getDb().getPersonDao().load(id);
                    if (person != null) {
                        mPeople.add(person);
                    }
                }
            }
        }
    }

    @Override
    public void onCreateDialogTitle(DialogTitle title) {
        switch (mType) {
            case TYPE_LABELS:
                title.setIcon(R.drawable.ic_action_labels);
                title.setTitle(R.string.action_labels);
                break;
            case TYPE_PERMISSIONS:
                title.setIcon(R.drawable.ic_action_permissions);
                title.setTitle(R.string.action_permissions);
                break;
        }
    }

    @Override
    public AlertDialog.Builder onCreateRefreshableDialog(final Bundle savedInstanceState) {
        final View view = getSupportActivity().getLayoutInflater().inflate(R.layout.fragment_permissions_labels_dialog, null);
        mProgress = view.findViewById(R.id.progress_container);
        mProgressText = (TextView) view.findViewById(android.R.id.text1);
        mWarning = (TextView) view.findViewById(R.id.warning);
        mListContainer = view.findViewById(R.id.container);
        mList = (ListView) view.findViewById(android.R.id.list);

        if (mAdapter == null) {
            mAdapter = new PermissionLabelAdapter(getSupportActivity());
            rebuildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        mList.setOnItemClickListener(this);
        mList.setAdapter(mAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.action_save, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel();
            }
        });

        switch (mType) {
            case TYPE_PERMISSIONS:
                mProgressText.setText("Saving permissions");
                mWarning.setText("Setting permissions in mass assign mode.");
                break;
            case TYPE_LABELS:
                mProgressText.setText("Saving labels");
                mWarning.setText("Setting labels in mass assign mode.");
                break;
        }

        if (mFilters != null) {
            mWarning.setVisibility(View.VISIBLE);
        }

        updateUIState();

        return builder;
    }

    private synchronized void rebuildAdapter() {
        try {
            mBuildTask.cancel(true);
        } catch (Exception e) { /* ignore */ }

        mBuildTask = new SafeAsyncTask<List<Item>>() {
            @Override
            public List<Item> call() throws Exception {
                List<Item> items = new ArrayList<Item>();

                switch (mType) {
                    case TYPE_LABELS:
                        final HashMap<Long, Integer> labelCounts = new HashMap<Long, Integer>(); // label id, count of people having label

                        if (mPeople != null) {
                            for (Person p : mPeople) {
                                p.resetLabels();
                                final List<Long> labelIds = p.getLables(Session.getInstance().getOrganizationId());
                                for (long labelId : labelIds) {
                                    Integer count = labelCounts.get(labelId);
                                    if (count == null) {
                                        count = 0;
                                    }
                                    labelCounts.put(labelId, count + 1);
                                }
                            }
                        }

                        final List<Label> labels = Session.getInstance().getOrganization().getAllLabels();
                        for (Label label : labels) {
                            items.add(new Item(label, determineCheckboxState(labelCounts, label.getId())));
                        }
                        break;
                    case TYPE_PERMISSIONS:
                        final HashMap<Long, Integer> permissionCounts = new HashMap<Long, Integer>(); // permission id, count of people having permission
                        if (mPeople != null) {
                            for (Person p : mPeople) {
                                p.resetPermissionCache();
                                long permission = p.getPermission(Session.getInstance().getOrganizationId());
                                Integer count = permissionCounts.get(permission);
                                if (count == null) {
                                    count = 0;
                                }
                                permissionCounts.put(permission, count + 1);
                            }
                        }
                        final Item adminItem = new Item(Permission.getPermission(Permission.ADMIN), determineCheckboxState(permissionCounts, Permission.ADMIN));
                        if (!Session.getInstance().isAdmin()) {
                            adminItem.setEnabled(false);
                        }
                        items.add(adminItem);
                        items.add(new Item(Permission.getPermission(Permission.USER), determineCheckboxState(permissionCounts, Permission.USER)));
                        items.add(new Item(Permission.getPermission(Permission.NO_PERMISSIONS), determineCheckboxState(permissionCounts, Permission.NO_PERMISSIONS)));
                        break;
                }
                return items;
            }

            @Override
            protected void onSuccess(List<Item> items) throws Exception {
                synchronized (mAdapter.getLock()) {
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.clear();
                    mAdapter.addAll(items);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mBuildTask = null;
                updateUIState();
            }
        };
        updateUIState();
        Application.getExecutor().execute(mBuildTask.future());
    }

    private int determineCheckboxState(HashMap<Long, Integer> counts, long id) {
        Integer count = counts.get(id);
        if (mFilters == null && (count == null || count == 0)) {
            return CheckmarkImageView.STATE_NONE;
        } else if (mPeople != null && count >= mPeople.size()) {
            return CheckmarkImageView.STATE_ALL;
        } else {
            return CheckmarkImageView.STATE_SOME;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getItemAtPosition(position);

        switch (mType) {
            case TYPE_LABELS:
                if ((item.state == CheckmarkImageView.STATE_NONE && !item.partial) || item.state == CheckmarkImageView.STATE_SOME) {
                    item.state = CheckmarkImageView.STATE_ALL;
                } else if (item.state == CheckmarkImageView.STATE_NONE && item.partial) {
                    item.state = CheckmarkImageView.STATE_SOME;
                } else if (item.state == CheckmarkImageView.STATE_ALL) {
                    item.state = CheckmarkImageView.STATE_NONE;
                }
                break;
            case TYPE_PERMISSIONS:
                synchronized (mAdapter.getLock()) {
                    if (item.state == CheckmarkImageView.STATE_NONE || item.state == CheckmarkImageView.STATE_SOME) {
                        for (Item i : mAdapter.getObjects()) {
                            i.state = CheckmarkImageView.STATE_NONE;
                        }
                        item.state = CheckmarkImageView.STATE_ALL;
                    } else if (item.state == CheckmarkImageView.STATE_ALL) {
                        for (Item i : mAdapter.getObjects()) {
                            if (i.getId() == Permission.NO_PERMISSIONS) {
                                i.state = CheckmarkImageView.STATE_ALL;
                            } else {
                                i.state = CheckmarkImageView.STATE_NONE;
                            }
                        }
                    }
                }
                break;
        }

        mAdapter.notifyDataSetChanged();
    }

    private void updateUIState() {
        if (mRefreshTask != null || mBuildTask != null) {
            startRefreshAnimation();
        } else {
            stopRefreshAnimation();
        }
        if (mList != null && mProgress != null) {
            if (mSaveTask != null) {
                mProgress.setVisibility(View.VISIBLE);
                mListContainer.setVisibility(View.GONE);
                setButtonEnabled(AlertDialog.BUTTON_POSITIVE, false);
            } else {
                mProgress.setVisibility(View.GONE);
                mListContainer.setVisibility(View.VISIBLE);
                setButtonEnabled(AlertDialog.BUTTON_POSITIVE, true);
            }
        }
    }

    private synchronized void save() {
        if (mAdapter == null) return;
        try {
            mSaveTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }

        final List<Item> items = new ArrayList<Item>(mAdapter.getObjects());

        mSaveTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {

                final Set<Long> remove = new HashSet<Long>();
                final Set<Long> add = new HashSet<Long>();

                for (Item item : items) {
                    if (item.state == CheckmarkImageView.STATE_ALL) {
                        add.add(item.getId());
                    } else if (item.state == CheckmarkImageView.STATE_NONE) {
                        remove.add(item.getId());
                    }
                }

                List<Long> personIds;
                if (mFilters != null) {
                    personIds = Api.listPersonIds(mFilters).get();
                } else {
                    personIds = new ArrayList<Long>(Person.getIds(mPeople));
                }

                switch (mType) {
                    case TYPE_LABELS:
                        Api.bulkUpdateLabels(personIds, add, remove, ApiOptions.builder().include(Api.Include.organizational_labels).build()).get();
                        break;
                    case TYPE_PERMISSIONS:
                        Long permission = null;
                        if (!add.isEmpty()) {
                            permission = add.iterator().next();
                        }
                        Api.bulkUpdatePermissions(personIds, permission, remove, ApiOptions.builder().include(Api.Include.organizational_permission).build()).get();
                        for (Person p : Person.getAllById(personIds)) {
                            p.resetPermissionCache();
                            p.invalidateViewCache();
                        }
                        break;
                }
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                switch (mType) {
                    case TYPE_LABELS:
                        Application.showToast("Labels updated", Toast.LENGTH_SHORT);
                        break;
                    case TYPE_PERMISSIONS:
                        Application.showToast("Permissions updated", Toast.LENGTH_SHORT);

                        break;
                }
                dismiss();
            }

            @Override
            public void onFinally() {
                mSaveTask = null;
                updateUIState();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Updating Labels Failed");
            }
        };

        updateUIState();
        Application.getExecutor().submit(mSaveTask.future());
    }

    @Override
    public void onDestroy() {
        try {
            mBuildTask.cancel(true);
        } catch (final Exception e) { /* ignore */ }
        try {
            mRefreshTask.cancel(true);
        } catch (final Exception e) { /* ignore */ }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        try {
            mRefreshTask.cancel(true);
        } catch (Exception e) { /* ignore */ }

        mRefreshTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                switch (mType) {
                    case TYPE_LABELS:
                        Api.listLabels().get();
                        break;
                    case TYPE_PERMISSIONS:
                        Api.listPermissions().get();
                        break;
                }
                return null;
            }

            @Override
            protected void onSuccess(Void aVoid) throws Exception {
                rebuildAdapter();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mRefreshTask = null;
                updateUIState();
            }
        };
        updateUIState();
        Application.getExecutor().execute(mRefreshTask.future());
    }

    private static class Item implements ObjectArrayAdapter.SupportEnable {
        private Object object;
        private int state = CheckmarkImageView.STATE_NONE;
        private boolean partial = false;
        private boolean enabled = true;

        public Item(Object object, int state) {
            this.object = object;
            this.state = state;
            if (state == CheckmarkImageView.STATE_SOME) {
                partial = true;
            }
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getId() {
            if (object instanceof Label) {
                return ((Label) object).getId();
            } else if (object instanceof Permission) {
                return ((Permission) object).getId();
            }
            throw new RuntimeException("Cannot get id from object");
        }
    }

    private static class PermissionLabelAdapter<T> extends ObjectArrayAdapter<T> {
        public PermissionLabelAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = (Item) getItem(position);

            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();

                view = getLayoutInflater().inflate(R.layout.item_checkable_textview, null);
                holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                holder.checkmark = (CheckmarkImageView) view.findViewById(android.R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Object object = item.object;

            String text1 = "";
            if (object instanceof Label) {
                text1 = ((Label) object).getTranslatedName();
            } else if (object instanceof Permission) {
                text1 = ((Permission) object).getTranslatedName();
            }
            holder.text1.setText(text1);
            holder.checkmark.setCheckmarkState(item.state);

            return view;
        }

        private static class ViewHolder {
            TextView text1;
            CheckmarkImageView checkmark;
        }

    }
}