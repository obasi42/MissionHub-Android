package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.Role;
import com.missionhub.model.RoleDao;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.U;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.*;

public class ContactLabelsDialogFragment extends RefreshableDialogFragment implements AdapterView.OnItemClickListener {

    private final HashSet<Person> mPeople = new HashSet<Person>();
    private SafeAsyncTask<Void> mSaveTask;
    private SafeAsyncTask<Void> mRefreshTask;
    private ListView mList;
    private ObjectArrayAdapter mAdapter;
    private View mProgress;

    public ContactLabelsDialogFragment() {
    }

    public static ContactLabelsDialogFragment show(FragmentManager fm, final Person person) {
        return showForResult(fm, person, null);
    }

    public static ContactLabelsDialogFragment show(FragmentManager fm, final Collection<Person> people) {
        return showForResult(fm, people, null);
    }

    public static ContactLabelsDialogFragment showForResult(FragmentManager fm, final Person person, Integer requestCode) {
        final List<Person> people = new ArrayList<Person>();
        people.add(person);
        return showForResult(fm, people, requestCode);
    }

    public static ContactLabelsDialogFragment showForResult(FragmentManager fm, final Collection<Person> people, Integer requestCode) {
        final Bundle args = new Bundle();

        final HashSet<Long> peopleIds = new HashSet<Long>();
        for(Person p : people) {
            peopleIds.add(p.getId());
        }
        args.putSerializable("peopleIds", peopleIds);
        return ContactLabelsDialogFragment.show(ContactLabelsDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            @SuppressWarnings("unchecked") final HashSet<Long> peopleIds = (HashSet<Long>) getArguments().getSerializable("peopleIds");
            if (peopleIds != null) {
                mPeople.clear();
                for(Long id : peopleIds) {
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
        title.setTitle(R.string.action_label);
    }

    @Override
    public void onRefresh() {
        refreshLabels();
    }

    @Override
    public AlertDialog.Builder onCreateRefreshableDialog(final Bundle savedInstanceState) {
        final View view = getSupportActivity().getLayoutInflater().inflate(R.layout.fragment_labels_dialog, null);
        mProgress = view.findViewById(R.id.progress_container);
        mList = (ListView) view.findViewById(android.R.id.list);

        if (mAdapter == null) {
            mAdapter = new LabelsAdapter(getSupportActivity());
            buildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(SelectableListView.CHOICE_MODE_NONE);
        mList.setOnItemClickListener(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setView(view);
        builder.setPositiveButton("Save", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        builder.setNeutralButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel();
            }
        });

        updateState();

        return builder;
    }

    private synchronized void buildAdapter() {
        if (mAdapter == null || mPeople == null) return;

        // load the system roles
        List<Role> systemRoles = Application.getDb().getRoleDao().queryBuilder().where(RoleDao.Properties.Organization_id.eq(0)).orderDesc(RoleDao.Properties.Organization_id).list();

        // load the organization role
        List<Role> organizationRoles = Application.getDb().getRoleDao().queryBuilder().where(RoleDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).orderAsc(RoleDao.Properties.Name).list();

        // determine which roles are in use and how many people are using them
        HashMap<Long, Integer> labelCounts = new HashMap<Long, Integer>(); // role id, count of people having label
        for(Person p : mPeople) {
            p.resetLabels();
            final List<Long> labelIds = p.getLables(Session.getInstance().getOrganizationId());
            for(long labelId : labelIds) {
                Integer count = labelCounts.get(labelId);
                if (count == null) {
                    count = 0;
                }
                labelCounts.put(labelId, count + 1);
            }
        }

        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();

        boolean isAdmin = Session.getInstance().isAdmin();
        for(Role role : systemRoles) {
            if (!isAdmin && role.getId() == 1) continue;

            mAdapter.add(new RoleItem(role, determineSelected(labelCounts, role.getId())));
        }
        for(Role role : organizationRoles) {
            mAdapter.add(new RoleItem(role, determineSelected(labelCounts, role.getId())));
        }

        mAdapter.notifyDataSetChanged();

        if (mAdapter.getCount() == 0) {
            refreshLabels();
        }
    }

    private int determineSelected(HashMap<Long, Integer> counts, long labelId) {
        Integer count = counts.get(labelId);
        if (count == null || count == 0) {
            return RoleItem.SELECTED_NONE;
        } else if (count >= mPeople.size()) {
            return RoleItem.SELECTED_ALL;
        } else {
            return RoleItem.SELECTED_SOME;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RoleItem item = (RoleItem) parent.getItemAtPosition(position);

        if ((item.selected == RoleItem.SELECTED_NONE && !item.partial) || item.selected == RoleItem.SELECTED_SOME) {
            item.selected = RoleItem.SELECTED_ALL;
        } else if (item.selected == RoleItem.SELECTED_NONE && item.partial) {
            item.selected = RoleItem.SELECTED_SOME;
        } else if (item.selected == RoleItem.SELECTED_ALL) {
            item.selected = RoleItem.SELECTED_NONE;
        }

        mAdapter.notifyDataSetChanged();
    }

    private static class RoleItem {

        private static final int SELECTED_NONE = 0;
        public static final int SELECTED_SOME = 1;
        public static final int SELECTED_ALL = 2;

        private Role role;
        private int selected = SELECTED_NONE;
        private boolean partial = false;

        public RoleItem(Role role, int selected) {
            this.role = role;
            this.selected = selected;
            if (selected == SELECTED_SOME) {
                partial = true;
            }
        }
    }

    private void updateState() {
        if (mRefreshTask != null) {
            startRefreshAnimation();
        } else {
            stopRefreshAnimation();
        }
        if (mList != null && mProgress != null) {
            if (mSaveTask != null) {
                mProgress.setVisibility(View.VISIBLE);
                mList.setVisibility(View.GONE);
                setButtonEnabled(AlertDialog.BUTTON_POSITIVE, false);
            } else {
                mProgress.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
                setButtonEnabled(AlertDialog.BUTTON_POSITIVE, true);
            }
        }
    }

    private static class LabelsAdapter extends ObjectArrayAdapter {
        public LabelsAdapter(Context context) {
            super(context);
        }

        private Drawable mSelectedNone;
        private Drawable mSelectedSome;
        private Drawable mSelectedAll;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RoleItem item = (RoleItem) getItem(position);

            View view  = convertView;
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();

                view = getLayoutInflater().inflate(R.layout.item_label, null);
                holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                holder.checkbox = (ImageView) view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Role role = item.role;
            int selected = item.selected;

            if (role != null) {

                final String text1 = role.getTranslatedName();
                if (text1 != null) {
                    holder.text1.setText(text1);
                    holder.text1.setVisibility(View.VISIBLE);
                } else {
                    holder.text1.setVisibility(View.GONE);
                }

                switch (selected) {
                    case RoleItem.SELECTED_NONE:
                        if (mSelectedNone == null) {
                            mSelectedNone = getContext().getResources().getDrawable(R.drawable.btn_check_off_holo_light);
                        }
                        holder.checkbox.setImageDrawable(mSelectedNone);
                        break;
                    case RoleItem.SELECTED_SOME:
                        if (mSelectedSome == null) {
                            mSelectedSome = getContext().getResources().getDrawable(R.drawable.btn_check_on_disable_holo_light);
                        }
                        holder.checkbox.setImageDrawable(mSelectedSome);
                        break;
                    case RoleItem.SELECTED_ALL:
                        if (mSelectedAll == null) {
                            mSelectedAll = getContext().getResources().getDrawable(R.drawable.btn_check_on_holo_light);
                        }
                        holder.checkbox.setImageDrawable(mSelectedAll);
                        break;
                }
            } else {
                Log.e("ContactLabelDialogFragment", "Role was null");
            }

            return view;
        }

        private static class ViewHolder {
            TextView text1;
            ImageView checkbox;
        }

    }

    private synchronized void save() {
        if (mAdapter == null || mPeople == null) return;
        try {
            mSaveTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }

        final List<Person> people = new ArrayList<Person>(mPeople);
        final List<RoleItem> roleItems = new ArrayList<RoleItem>();

        final int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            try {
                roleItems.add((RoleItem) mAdapter.getItem(i));
            } catch (Exception e) {
                // nearly impossible to get here
            }
        }

        mSaveTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                final List<Long> personIds = new ArrayList<Long>();
                for (Person person : people) {
                    personIds.add(person.getId());
                }

                final List<Long> addRoles = new ArrayList<Long>();
                final List<Long> removeRoles = new ArrayList<Long>();
                for(RoleItem item : roleItems) {
                    if (item.selected == RoleItem.SELECTED_ALL) {
                        addRoles.add(item.role.getId());
                    } else if (item.selected == RoleItem.SELECTED_NONE) {
                        removeRoles.add(item.role.getId());
                    }
                }

                Api.bulkUpdateRoles(personIds, addRoles, removeRoles, ApiOptions.builder().include(Api.Include.organizational_roles).build()).get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                Toast.makeText(Application.getContext(), "Labels Updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFinally() {
                mSaveTask = null;
                updateState();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Updating Labels Failed");
            }

            @Override
            public void onInterrupted(final Exception e) {

            }
        };

        updateState();
        Application.getExecutor().submit(mSaveTask.future());
    }

    private void refreshLabels() {
        try {
            mRefreshTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }

        mRefreshTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Api.listRoles().get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                buildAdapter();
            }

            @Override
            public void onFinally() {
                mRefreshTask = null;
                updateState();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Refresh Failed");
            }

            @Override
            public void onInterrupted(final Exception e) {

            }
        };

        updateState();
        Application.getExecutor().submit(mRefreshTask.future());
    }

    @Override
    public void onDestroy() {
        try {
            mRefreshTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }
        super.onDestroy();
    }
}