//package com.missionhub.fragment;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.View;
//import android.view.ViewGroup;
//import com.actionbarsherlock.app.ActionBar;
//import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
//import com.missionhub.R;
//import com.missionhub.api.Api;
//import com.missionhub.api.Api.Include;
//import com.missionhub.api.ApiOptions;
//import com.missionhub.application.Application;
//import com.missionhub.exception.ExceptionHelper;
//import com.missionhub.model.Person;
//import com.missionhub.ui.NavigationSpinnerAdapter;
//import com.missionhub.ui.ProgressItemHelper;
//import com.missionhub.ui.widget.LockableViewPager;
//import com.missionhub.util.SafeAsyncTask;
//import com.missionhub.util.U;
//import org.holoeverywhere.LayoutInflater;
//import org.holoeverywhere.app.Activity;
//import org.holoeverywhere.app.Fragment;
//import org.holoeverywhere.widget.ProgressBar;
//import org.holoeverywhere.widget.Toast;
//
//public class ContactFragment extends BaseFragment implements OnNavigationListener, ViewPager.OnPageChangeListener {
//
//    /**
//     * the person id of the displayed contact
//     */
//    private long mPersonId = -1;
//
//    /**
//     * the person object of the displayed contact
//     */
//    private Person mPerson;
//
//    /**
//     * the current pager page
//     */
//    private int mPage = 0;
//
//    /**
//     * the view pager
//     */
//    private LockableViewPager mPager;
//
//    /**
//     * the progress view
//     */
//    private ProgressBar mProgress;
//
//    /**
//     * the view pager adapter
//     */
//    private FragmentStatePagerAdapter mAdapter;
//
//    /**
//     * the contact info fragment
//     */
//    private ContactInfoFragment mInfoFragment;
//
//    /**
//     * the survey results fragment
//     */
//    private ContactSurveysFragment mSurveysFragment;
//
//    /**
//     * the task used to refresh the person
//     */
//    private SafeAsyncTask<Person> mRefreshTask;
//
//    /**
//     * the progress item helper
//     */
//    private final ProgressItemHelper mProgressHelper = new ProgressItemHelper();
//
//    public ContactFragment() {
//    }
//
//    /**
//     * instantiate a new fragment for the given person
//     */
//    public static ContactFragment instantiate(final Person person) {
//        return instantiate(person.getId());
//    }
//
//    /**
//     * instantiate a new fragment for the given person id
//     */
//    public static ContactFragment instantiate(final long personId) {
//        final Bundle bundle = new Bundle();
//        bundle.putLong("personId", personId);
//
//        final ContactFragment fragment = new ContactFragment();
//        fragment.setArguments(bundle);
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (!U.superGetRetainInstance(this)) {
//            setRetainInstance(true);
//        }
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onAttach(final Activity activity) {
//        super.onAttach(activity);
//
//        if (getArguments() != null) {
//            mPersonId = getArguments().getLong("personId", -1);
//        }
//
//        if (mPersonId < 0) {
//            Toast.makeText(getSupportActivity(), "No target person id.", Toast.LENGTH_SHORT).show();
//            activity.finish();
//            return;
//        }
//
//        if (mPerson == null) {
//            mPerson = Application.getDb().getPersonDao().load(mPersonId);
//            notifyPersonUpdated();
//            refreshPerson();
//        }
//    }
//
//    @Override
//    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//        mProgressHelper.onCreateView(inflater);
//        return inflater.inflate(R.layout.fragment_contact, null);
//    }
//
//    @Override
//    public void onDestroyView() {
//        mProgressHelper.onDestroyView();
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        Application.trackView("Contact");
//    }
//
//    @Override
//    public void onDestroy() {
//        try {
//            mRefreshTask.cancel(true);
//        } catch (final Exception e) {
//            /* ignore */
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mPager = (LockableViewPager) view.findViewById(R.id.pager);
//        mProgress = (ProgressBar) view.findViewById(R.id.progress);
//
//        if (mPerson != null) {
//            mProgress.setVisibility(View.INVISIBLE);
//            mPager.setVisibility(View.VISIBLE);
//        } else {
//            mPager.setVisibility(View.INVISIBLE);
//            mProgress.setVisibility(View.VISIBLE);
//        }
//
//        if (mAdapter == null) {
//            mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
//
//                @Override
//                public Fragment getItem(final int index) {
//                    switch (index) {
//                        case 0:
//                            mInfoFragment = new ContactInfoFragment();
//                            notifyPersonUpdated();
//                            return mInfoFragment;
//                        case 1:
//                            mSurveysFragment = new ContactSurveysFragment();
//                            notifyPersonUpdated();
//                            return mSurveysFragment;
//                        default:
//                            throw new RuntimeException("Index out of bounds");
//                    }
//                }
//
//                @Override
//                public int getCount() {
//                    return 2;
//                }
//            };
//        }
//        mPager.setOnPageChangeListener(this);
//        mPager.setOffscreenPageLimit(2);
//        mPager.setAdapter(mAdapter);
//        mPager.setCurrentItem(mPage);
//    }
//
//    @Override
//    public void onActivityCreated(final Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        U.resetActionBar(getSupportActivity());
//        getSupportActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        getSupportActivity().getSupportActionBar().setListNavigationCallbacks(new NavigationSpinnerAdapter(getSupportActivity(), R.string.contact_nav_info, R.string.contact_nav_surveys), this);
//        getSupportActivity().getSupportActionBar().setSelectedNavigationItem(mPager.getCurrentItem());
//    }
//
//    /**
//     * Called on action bar menu item selected
//     */
//    @Override
//    public boolean onNavigationItemSelected(final int index, final long id) {
//        switch (index) {
//            case 0:
//                mPager.setCurrentItem(0, true);
//                return true;
//            case 1:
//                mPager.setCurrentItem(1, true);
//                return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onPageScrollStateChanged(final int state) {
//    }
//
//    @Override
//    public void onPageScrolled(final int index, final float positionOffset, final int positionOffsetPixels) {
//    }
//
//    @Override
//    public void onPageSelected(final int index) {
//        if (mPage != index) {
//            getSupportActivity().getSupportActionBar().setSelectedNavigationItem(mPager.getCurrentItem());
//            mPage = index;
//        }
//    }
//
//    @Override
//    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        mProgressHelper.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        if (item.getItemId() == R.id.action_refresh) {
//            refreshPerson();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Refreshes the person data from the MissionHub API
//     */
//    private synchronized void refreshPerson() {
//        try {
//            mRefreshTask.cancel(true);
//        } catch (final Exception e) {
//            /* ignore */
//        }
//
//        addProgress("refreshPerson");
//
//        mRefreshTask = new SafeAsyncTask<Person>() {
//
//            @Override
//            public Person call() throws Exception {
//                final Person person = Api.getPerson(mPersonId, ApiOptions.builder() //
//                        .include(Include.answer_sheets) //
//                        .include(Include.answers) //
//                        .include(Include.comments_on_me) //
//                        .include(Include.rejoicables) //
//                        .include(Include.assigned_tos) //
//                        .include(Include.assigned_to) //
//                        .include(Include.current_address) //
//                        .include(Include.email_addresses) //
//                        .include(Include.organizational_roles) //
//                        .include(Include.phone_numbers) //
//                        .build()).get();
//
//                person.refreshAll();
//                return person;
//            }
//
//            @Override
//            public void onSuccess(final Person person) {
//                mPerson = person;
//
//                if (isVisible() && mPager != null) {
//                    mProgress.setVisibility(View.INVISIBLE);
//                    mPager.setVisibility(View.VISIBLE);
//                }
//
//                notifyPersonUpdated();
//            }
//
//            @Override
//            public void onFinally() {
//                mRefreshTask = null;
//
//                removeProgress("refreshPerson");
//
//                if (mPerson == null) {
//                    finishWithNoPerson();
//                }
//            }
//
//            @Override
//            public void onException(final Exception e) {
//                if (mPerson != null) {
//                    final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
//                    eh.makeToast("Failed to refresh contact.");
//                }
//            }
//
//            @Override
//            public void onInterrupted(final Exception e) {
//            }
//        };
//        Application.getExecutor().execute(mRefreshTask.future());
//    }
//
//    /**
//     * Adds a task to the progress item helper
//     *
//     * @param task
//     */
//    public void addProgress(final Object task) {
//        mProgressHelper.addProgress(task);
//    }
//
//    /**
//     * Removes a task from the progress item helper
//     *
//     * @param task
//     */
//    public void removeProgress(final Object task) {
//        mProgressHelper.removeProgress(task);
//    }
//
//    /**
//     * Returns true when the progress helper has the given task
//     *
//     * @param task
//     * @return
//     */
//    public boolean hasProgress(final Object task) {
//        return mProgressHelper.hasProgress(task);
//    }
//
//    /**
//     * Displays an error toast and finishes the activity.
//     */
//    private void finishWithNoPerson() {
//        Application.showToast("Could not load person.", Toast.LENGTH_SHORT);
//        if (getSupportActivity() != null) {
//            getSupportActivity().finish();
//        }
//    }
//
//    /**
//     * Notifies the attached fragments that the person has been updated
//     */
//    private void notifyPersonUpdated() {
//        if (mPerson != null) {
//            if (mInfoFragment != null) {
//                mInfoFragment.notifyPersonUpdated(mPerson);
//            }
//            if (mSurveysFragment != null) {
//                mSurveysFragment.notifyPersonUpdated(mPerson);
//            }
//        }
//    }
//}