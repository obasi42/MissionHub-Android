package com.missionhub.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GContactAssignment;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.DisplayUtils;
import com.missionhub.util.SafeAsyncTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignmentDialogFragment extends RefreshableDialogFragment implements OnItemClickListener {

    /**
     * the list of people to assign
     */
    private Set<Person> mPeople;
    /**
     * the list filters to assign
     */
    private PeopleListOptions mFilters;
    /**
     * the progress view
     */
    private View mProgress;
    /**
     * The person list provider
     */
    private AssignmentListAdapter mAdapter;
    /**
     * the task used to process assignments
     */
    private SafeAsyncTask<Void> mAssignTask;
    /**
     * the task used to refresh leaders
     */
    private SafeAsyncTask<Void> mRefreshLeadersTask;
    /**
     * the task used to build the leader adapter
     */
    private SafeAsyncTask<List<Object>> mBuildTask;
    private View mListContainer;


    public AssignmentDialogFragment() {

    }

    public static AssignmentDialogFragment showForResult(FragmentManager fm, final Person person, Integer requestCode) {
        final List<Person> people = new ArrayList<Person>();
        people.add(person);
        return showForResult(fm, people, requestCode);
    }

    public static AssignmentDialogFragment showForResult(FragmentManager fm, final Collection<Person> people, Integer requestCode) {
        final Bundle args = new Bundle();

        final HashSet<Long> peopleIds = new HashSet<Long>();
        for (Person p : people) {
            peopleIds.add(p.getId());
        }
        args.putSerializable("peopleIds", peopleIds);
        return AssignmentDialogFragment.show(AssignmentDialogFragment.class, fm, args, requestCode);
    }

    public static AssignmentDialogFragment showForResult(FragmentManager fm, long[] checkedItemIds, Integer action_assign) {
        return showForResult(fm, Person.getAllById(checkedItemIds), action_assign);
    }

    public static AssignmentDialogFragment showForResult(FragmentManager fm, final PeopleListOptions filters, Integer requestCode) {
        final Bundle args = new Bundle();
        args.putSerializable("filters", filters);
        return AssignmentDialogFragment.show(AssignmentDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
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
        title.setIcon(R.drawable.ic_action_assign);
        title.setTitle(R.string.assignment_title);
    }

    @Override
    public View onCreateRefreshableView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListContainer = view.findViewById(R.id.container);
        final ListView mList = (ListView) view.findViewById(android.R.id.list);
        if (mAdapter == null) {
            mAdapter = new AssignmentListAdapter(getSupportActivity());
            rebuildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        mProgress = view.findViewById(R.id.progress_container);

        if (mFilters != null) {
            view.findViewById(R.id.warning).setVisibility(View.VISIBLE);
        }

        updateProgressView();
        updateRefreshIcon();
    }

    private void rebuildAdapter() {
        try {
            mBuildTask.cancel(true);
        } catch (Exception e) { /* ignore */ }

        mBuildTask = new SafeAsyncTask<List<Object>>() {
            @Override
            public List<Object> call() throws Exception {
                List<Object> objects = new ArrayList<Object>();

                boolean showNone = false;
                boolean showMe = false;

                if (mPeople != null) {
                    for (final Person person : mPeople) {
                        final List<ContactAssignment> assignments = Application.getDb().getContactAssignmentDao().queryBuilder()
                                .where(ContactAssignmentDao.Properties.Person_id.eq(person.getId()), ContactAssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).list();

                        if (assignments.size() > 0) {
                            showNone = true;
                            for (final ContactAssignment assignment : assignments) {
                                if (assignment.getAssigned_to_id().compareTo(Session.getInstance().getPersonId()) != 0) {
                                    showMe = true;
                                }
                            }
                        } else {
                            showMe = true;
                        }
                    }
                } else {
                    showNone = true;
                    showMe = true;
                }

                if (showNone) {
                    objects.add(new NoneItem());
                }

                if (showMe) {
                    objects.add(new MeItem(Session.getInstance().getPerson()));
                }

                objects.add(new DividerItem("Leaders"));

                List<Person> leaders = Session.getInstance().getOrganization().getUsersAdmins();
                leaders.remove(Session.getInstance().getPerson());
                if (mPeople != null) {
                    leaders.removeAll(mPeople);
                }

                for (Person person : leaders) {
                    objects.add(new LeaderItem(person));
                }

                return objects;
            }

            @Override
            protected void onSuccess(List<Object> objects) throws Exception {
                synchronized (mAdapter.getLock()) {
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.clear();
                    mAdapter.addAll(objects);
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
                updateRefreshIcon();
            }
        };
        updateRefreshIcon();
        Application.getExecutor().execute(mBuildTask.future());
    }

    private void doLeaderAssignment(final Person leader) {
        if (mAssignTask != null) {
            mAssignTask.cancel(true);
        }
        mAssignTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                List<Long> personIds;
                if (mPeople != null) {
                    personIds = new ArrayList<Long>(Person.getIds(mPeople));
                } else {
                    personIds = Api.listPersonIds(mFilters).get();
                }

                if (leader == null) {
                    Api.bulkDeleteContactAssignments(personIds).get();
                } else {
                    final List<GContactAssignment> assignments = new ArrayList<GContactAssignment>();
                    for (final Long id : personIds) {
                        final ContactAssignment oldAssignment = Application.getDb().getContactAssignmentDao().queryBuilder()
                                .where(ContactAssignmentDao.Properties.Person_id.eq(id), ContactAssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).unique();

                        final GContactAssignment assignment = new GContactAssignment();
                        if (oldAssignment != null) {
                            assignment.id = oldAssignment.getId();
                        }

                        assignment.assigned_to_id = leader.getId();
                        assignment.person_id = id;
                        assignment.organization_id = Session.getInstance().getOrganizationId();

                        assignments.add(assignment);
                    }
                    Api.bulkUpdateContactAssignments(assignments).get();
                }
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                Application.showToast(R.string.assignment_complete, Toast.LENGTH_SHORT);
                AssignmentDialogFragment.this.dismiss();
            }

            @Override
            public void onFinally() {
                mAssignTask = null;
                updateProgressView();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.assignment_failed);

                AssignmentDialogFragment.this.cancel();
            }
        };
        updateProgressView();
        Application.getExecutor().execute(mAssignTask.future());
    }

    public void showProgress() {
        hideRefresh();
        mListContainer.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        showRefresh();
        mProgress.setVisibility(View.GONE);
        mListContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
        final Object item = adapter.getItemAtPosition(position);

        if (item instanceof NoneItem) {
            doLeaderAssignment(null);
        } else if (item instanceof MeItem) {
            doLeaderAssignment(Session.getInstance().getPerson());
        } else if (item instanceof LeaderItem) {
            doLeaderAssignment(((LeaderItem) item).leader);
        }
    }

    @Override
    public void onRefresh() {
        if (mRefreshLeadersTask != null) return;

        mRefreshLeadersTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Organization org = Api.getOrganization(Session.getInstance().getOrganizationId(), ApiOptions.builder()
                        .include(Include.users)
                        .include(Include.organizational_permission)
                        .build()).get();

                org.refreshAll();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                rebuildAdapter();
            }

            @Override
            public void onFinally() {
                mRefreshLeadersTask = null;
                updateRefreshIcon();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Update Failed");
            }
        };
        updateRefreshIcon();
        Application.getExecutor().submit(mRefreshLeadersTask.future());
    }

    @Override
    public void onDestroy() {
        try {
            mRefreshLeadersTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }
        try {
            mBuildTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }
        super.onDestroy();
    }

    private void updateRefreshIcon() {
        if (mRefreshLeadersTask != null || mBuildTask != null) {
            startRefreshAnimation();
        } else {
            stopRefreshAnimation();
        }
    }

    private void updateProgressView() {
        if (mAssignTask != null) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    private static class NoneItem {
    }

    private static class DividerItem extends ObjectArrayAdapter.DisabledItem {
        public CharSequence title;

        public DividerItem(CharSequence title) {
            this.title = title;
        }
    }

    private static class MeItem {
        public Person me;

        public MeItem(final Person me) {
            this.me = me;
        }
    }

    private static class LeaderItem {
        public Person leader;

        public LeaderItem(final Person leader) {
            this.leader = leader;
        }
    }

    private static class AssignmentListAdapter extends ObjectArrayAdapter {

        final int mPicturePx = Math.round(DisplayUtils.dpToPixel(50));
        final DisplayImageOptions mImageLoaderOptions;
        final AnimateOnceImageLoadingListener mImageLoadingListener;

        public AssignmentListAdapter(final Context context) {
            super(context);

            mImageLoaderOptions = DisplayUtils.getContactImageDisplayOptions();
            mImageLoadingListener = new AnimateOnceImageLoadingListener(250);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final Object object = getItem(position);
            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                if (object instanceof DividerItem) {
                    view = getLayoutInflater().inflate(R.layout.item_assignment_divider, parent, false);
                } else {
                    view = getLayoutInflater().inflate(R.layout.item_assignment, parent, false);
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
                ImageLoader.getInstance().displayImage(item.leader.getPictureUrl(mPicturePx, mPicturePx), holder.icon, mImageLoaderOptions, mImageLoadingListener);
            } else if (object instanceof MeItem) {
                final MeItem item = (MeItem) object;
                holder.text1.setText("Me");
                ImageLoader.getInstance().displayImage(item.me.getPictureUrl(mPicturePx, mPicturePx), holder.icon, mImageLoaderOptions, mImageLoadingListener);
            } else if (object instanceof DividerItem) {
                final DividerItem item = (DividerItem) object;
                if (StringUtils.isNotEmpty(item.title)) {
                    holder.text1.setText(item.title);
                    holder.text1.setVisibility(View.VISIBLE);
                } else {
                    holder.text1.setVisibility(View.GONE);
                }
            } else if (object instanceof NoneItem) {
                holder.text1.setText("Unassigned");
                holder.icon.setImageResource(R.drawable.ic_cancel);
            }
            return view;
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        private class ViewHolder {
            public TextView text1;
            public ImageView icon;
        }
    }
}