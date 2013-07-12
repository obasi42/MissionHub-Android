package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.application.Application;
import com.missionhub.application.DrawableCache;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.BaseFragment;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GContactAssignment;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.LockableViewPager;
import com.missionhub.util.DisplayUtils;
import com.missionhub.util.SafeAsyncTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactAssignmentDialogFragment extends RefreshableDialogFragment implements OnKeyListener, OnItemClickListener {

    /**
     * the view pager
     */
    private LockableViewPager mPager;

    /**
     * the progress view
     */
    private View mProgress;

    /**
     * the view pager adapter
     */
    private FragmentStatePagerAdapter mPagerAdapter;

    /**
     * the current view pager page
     */
    private int mPage = 0;

    /**
     * the first or index fragment
     */
    private IndexFragment mIndexFragment;

    /**
     * the selection fragment for leaders/organizations
     */
    private SelectionFragment mSelectionFragment;

    /**
     * the list of people to assign
     */
    final private Set<Person> mPeople = new HashSet<Person>();

    /**
     * the task used to process assignments
     */
    private SafeAsyncTask<Void> mTask;

    /**
     * the task used to refresh leaders
     */
    private SafeAsyncTask<Void> mRefreshLeadersTask;

    public ContactAssignmentDialogFragment() {
    }

    public static ContactAssignmentDialogFragment show(FragmentManager fm, final Person person) {
        return showForResult(fm, person, null);
    }

    public static ContactAssignmentDialogFragment show(FragmentManager fm, final Collection<Person> people) {
        return showForResult(fm, people, null);
    }

    public static ContactAssignmentDialogFragment showForResult(FragmentManager fm, final Person person, Integer requestCode) {
        final List<Person> people = new ArrayList<Person>();
        people.add(person);
        return showForResult(fm, people, requestCode);
    }

    public static ContactAssignmentDialogFragment showForResult(FragmentManager fm, final Collection<Person> people, Integer requestCode) {
        final Bundle args = new Bundle();

        final HashSet<Long> peopleIds = new HashSet<Long>();
        for (Person p : people) {
            peopleIds.add(p.getId());
        }
        args.putSerializable("peopleIds", peopleIds);
        return ContactAssignmentDialogFragment.show(ContactAssignmentDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            @SuppressWarnings("unchecked") final HashSet<Long> peopleIds = (HashSet<Long>) getArguments().getSerializable("peopleIds");
            if (peopleIds != null) {
                mPeople.clear();
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
        title.setTitle(R.string.assignment_title);
    }

    @Override
    public View onCreateRefreshableView(final LayoutInflater inflater, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_dialog, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = (LockableViewPager) view.findViewById(R.id.pager);
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
        mPager.setPagingLocked(LockableViewPager.LOCK_BOTH);
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
        if (mPeople.size() > 1) {
            setTitle(R.string.assignment_title_mass);
        } else {
            setTitle(R.string.assignment_title);
        }
        hideRefresh();
    }

    /**
     * Shows the selection fragment for groups
     */
    public void showGroups(final boolean animate) {
        mSelectionFragment.showGroups();
        mPage = 1;
        mPager.setCurrentItem(1, animate);
        setTitle(R.string.assignment_title_group);
    }

    /**
     * Shows the selection fragment for leaders
     */
    public void showLeaders(final boolean animate) {
        mSelectionFragment.showLeaders();
        mPage = 1;
        mPager.setCurrentItem(1, animate);

        setTitle(R.string.assignment_title_leader);
        showRefresh();
    }

    public static class AssignNoneItem {
    }

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


    public static class SelectionItem {
        public int id;
        public String text;

        public SelectionItem(final int id, final String text) {
            this.id = id;
            this.text = text;
        }
    }

    public static class ContactAssignmentAdapter extends ObjectArrayAdapter {

        final int mPicturePx = Math.round(DisplayUtils.dpToPixel(50));
        final DisplayImageOptions mImageLoaderOptions;
        final AnimateOnceImageLoadingListener mImageLoadingListener;

        public ContactAssignmentAdapter(final Context context) {
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
                if (object instanceof LeaderItem) {
                    view = getLayoutInflater().inflate(R.layout.item_assignment_leader, null);
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
                ImageLoader.getInstance().displayImage(item.leader.getPictureUrl(mPicturePx, mPicturePx), holder.icon, mImageLoaderOptions, mImageLoadingListener);
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
                ImageLoader.getInstance().displayImage(item.me.getPictureUrl(mPicturePx, mPicturePx), holder.icon, mImageLoaderOptions, mImageLoadingListener);
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
        public AssignmentFragment() {
        }

        public ContactAssignmentDialogFragment getDialog() {
            return (ContactAssignmentDialogFragment) getParentFragment();
        }
    }

    public static class IndexFragment extends AssignmentFragment {

        /**
         * the listview
         */
        private ListView mListView;

        /**
         * the listview adapter
         */
        private ContactAssignmentAdapter mAdapter;

        public static final int INDEX = 0;
        public static final int LEADERS = 1;
        public static final int GROUPS = 2;

        public IndexFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_assignment_dialog_index, null);

            mListView = (ListView) view.findViewById(android.R.id.list);

            if (mAdapter == null) {
                mAdapter = new ContactAssignmentAdapter(getSupportActivity());
                buildAdapter();
            } else {
                mAdapter.setContext(getSupportActivity());
            }

            mListView.setOnItemClickListener(getDialog());
            mListView.setAdapter(mAdapter);

            return view;
        }

        private void buildAdapter() {
            if (mAdapter == null || getDialog() == null || getDialog().getPeople() == null) return;

            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();

            boolean showMe = false;
            boolean showNone = false;
            final boolean showLeaders = true;
            final boolean showGroups = false;

            for (final Person person : getDialog().getPeople()) {

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

            if (showMe) {
                mAdapter.add(new AssignMeItem(Session.getInstance().getPerson()));
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

        /**
         * the listview
         */
        private ListView mListView;

        /**
         * the leader listview adapter
         */
        private ContactAssignmentAdapter mLeaderAdapter;

        /**
         * the groups listview adapter
         */
        private ContactAssignmentAdapter mGroupsAdapter;

        /**
         * the search adapter
         */
        private ContactAssignmentAdapter mSearchAdapter;

        public SelectionFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_assignment_dialog_selection, null);

            mListView = (ListView) view.findViewById(android.R.id.list);
            mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

            if (mLeaderAdapter == null) {
                mLeaderAdapter = new ContactAssignmentAdapter(getSupportActivity());
            } else {
                mLeaderAdapter.setContext(getSupportActivity());
            }
            if (mGroupsAdapter == null) {
                mGroupsAdapter = new ContactAssignmentAdapter(getSupportActivity());
            } else {
                mGroupsAdapter.setContext(getSupportActivity());
            }
            if (mSearchAdapter == null) {
                mSearchAdapter = new ContactAssignmentAdapter(getSupportActivity());
            } else {
                mSearchAdapter.setContext(getSupportActivity());
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
            if (mLeaderAdapter == null || getDialog() == null || getDialog().getPeople() == null)
                return;

            mLeaderAdapter.setNotifyOnChange(false);
            mLeaderAdapter.clear();
//          TODO: implement
//            final List<OrganizationalPermission> roles = Application
//                    .getDb()
//                    .getOrganizationalPermissionDao()
//                    .queryBuilder()
//                    .where(OrganizationalRoleDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId()),
//                            OrganizationalRoleDao.Properties.Role_id.in(U.Role.admin.id(), U.Role.leader.id())).list();
//
//            final Set<Person> leaders = new HashSet<Person>();
//            for (final OrganizationalRole role : roles) {
//                leaders.add(Application.getDb().getPersonDao().load(role.getPerson_id()));
//            }
//
//            for (final Person leader : U.sortPeople(leaders, true)) {
//                mLeaderAdapter.add(new LeaderItem(leader));
//            }

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
        return getSupportActivity().onKeyDown(keyCode, event);
    }

    private void doLeaderAssignment(final Person leader) {
        if (mTask != null) {
            mTask.cancel(true);
        }
        mTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                if (leader == null) {
                    final List<Long> personIds = new ArrayList<Long>();
                    for (final Person p : getPeople()) {
                        personIds.add(p.getId());
                    }
                    Api.bulkDeleteContactAssignments(personIds).get();
                } else {
                    final List<GContactAssignment> assignments = new ArrayList<GContactAssignment>();
                    for (final Person p : getPeople()) {
                        final ContactAssignment oldAssignment = Application.getDb().getContactAssignmentDao().queryBuilder()
                                .where(ContactAssignmentDao.Properties.Person_id.eq(p.getId()), ContactAssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId())).unique();

                        final GContactAssignment assignment = new GContactAssignment();
                        if (oldAssignment != null) {
                            assignment.id = oldAssignment.getId();
                        }

                        assignment.assigned_to_id = leader.getId();
                        assignment.person_id = p.getId();
                        assignment.organization_id = Session.getInstance().getOrganizationId();

                        assignments.add(assignment);
                    }
                    Api.bulkUpdateContactAssignments(assignments).get();
                }
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                Toast.makeText(Application.getContext(), R.string.assignment_complete, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFinally() {
                mTask = null;
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.assignment_failed);

                ContactAssignmentDialogFragment.this.cancel();
            }

            @Override
            public void onInterrupted(final Exception e) {

            }

        };
        Application.getExecutor().execute(mTask.future());

        getDialog().setTitle(R.string.assignment_progress);
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
            doLeaderAssignment(Session.getInstance().getPerson());
        } else if (item instanceof SelectionItem) {
            if (((SelectionItem) item).id == IndexFragment.LEADERS) {
                showLeaders(true);
            } else if (((SelectionItem) item).id == IndexFragment.GROUPS) {
                showGroups(true);
            }
        } else if (item instanceof LeaderItem) {
            doLeaderAssignment(((LeaderItem) item).leader);
        }
    }

    @Override
    public void onRefresh() {
        if (mSelectionFragment.mSelection == IndexFragment.GROUPS) {
            // TODO: groups
        } else if (mSelectionFragment.mSelection == IndexFragment.LEADERS) {
            if (mRefreshLeadersTask != null) return;

            mRefreshLeadersTask = new SafeAsyncTask<Void>() {

                @Override
                public Void call() throws Exception {
                    Api.getOrganization(Session.getInstance().getOrganizationId(), ApiOptions.builder()//
                            .include(Include.users) //
                            .include(Include.organizational_permission) //
                            .build()).get();
                    return null;
                }

                @Override
                public void onSuccess(final Void _) {
                    mSelectionFragment.buildLeaderAdapter();
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

                @Override
                public void onInterrupted(final Exception e) {

                }

            };
            updateRefreshIcon();
            Application.getExecutor().submit(mRefreshLeadersTask.future());
        }
    }

    @Override
    public void onDestroy() {
        try {
            mRefreshLeadersTask.cancel(true);
        } catch (final Exception e) {
            /* ignore */
        }
        super.onDestroy();
    }

    private void updateRefreshIcon() {
        if (mRefreshLeadersTask != null) {
            startRefreshAnimation();
        } else {
            stopRefreshAnimation();
        }
    }
}