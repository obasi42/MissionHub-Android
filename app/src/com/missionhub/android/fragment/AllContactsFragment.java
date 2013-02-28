package com.missionhub.android.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.R;
import com.missionhub.android.activity.ContactActivity;
import com.missionhub.android.api.PersonListOptions;
import com.missionhub.android.contactlist.ApiContactListProvider;
import com.missionhub.android.contactlist.ContactListFragment;
import com.missionhub.android.contactlist.ContactListFragment.ContactListFragmentListener;
import com.missionhub.android.contactlist.ContactListProvider;
import com.missionhub.android.exception.ExceptionHelper;
import com.missionhub.android.fragment.dialog.ContactAssignmentDialogFragment;
import com.missionhub.android.fragment.dialog.ContactAssignmentDialogFragment.ContactAssignmentListener;
import com.missionhub.android.fragment.dialog.EditContactDialogFragment;
import com.missionhub.android.fragment.dialog.EditContactDialogFragment.AddContactListener;
import com.missionhub.android.model.Person;
import com.missionhub.android.util.U;
import org.holoeverywhere.LayoutInflater;

import java.util.HashSet;
import java.util.Set;

public class AllContactsFragment extends MainFragment implements ContactListFragmentListener, ActionMode.Callback, ContactAssignmentListener, AddContactListener {

    /**
     * the contact list fragment
     */
    ContactListFragment mFragment;

    /**
     * the refresh menu item
     */
    private MenuItem mRefreshItem;

    /**
     * view to set as action view while refreshing
     */
    private ImageView mRefreshingView;

    /**
     * the action mode
     */
    private ActionMode mActionMode;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_frame, null);

        if (mFragment == null) {
            mFragment = new AllContactsFragmentFragment();
            mFragment.setContactListFragmentListener(this);
            getChildFragmentManager().beginTransaction().add(R.id.content_frame, mFragment).commit();
        }

        // create the refreshing actionbar view
        mRefreshingView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(Menu.NONE, R.id.action_add_contact, Menu.NONE, R.string.action_add_contact).setIcon(R.drawable.ic_action_add_contact)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        mRefreshItem = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mFragment != null) {
                    mFragment.reload();
                    return true;
                }
                break;
            case R.id.action_add_contact:
                final EditContactDialogFragment dialog = EditContactDialogFragment.show(getChildFragmentManager(), false);
                dialog.setAddContactListener(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the refresh icon based on the tasks
     */
    public void updateRefreshIcon() {
        if (mRefreshItem == null || mRefreshingView == null) return;

        if (mFragment != null && mFragment.isWorking()) {
            final Animation rotation = AnimationUtils.loadAnimation(getSupportActivity(), R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            mRefreshingView.startAnimation(rotation);
            mRefreshItem.setActionView(mRefreshingView);
        } else {
            mRefreshingView.clearAnimation();
            mRefreshItem.setActionView(null);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        U.resetActionBar(getSupportActivity());
        getSupportActivity().getSupportActionBar().setTitle("All Contacts");
    }

    public static class AllContactsFragmentFragment extends ContactListFragment {
        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder().build();

            return new ApiContactListProvider(getSupportActivity(), options);
        }
    }

    @Override
    public void onContactListProviderException(final ContactListFragment fragment, final Exception exception) {
        final ExceptionHelper ex = new ExceptionHelper(getSupportActivity(), exception);
        ex.makeToast();
    }

    @Override
    public void onWorkingChanged(final ContactListFragment fragment, final boolean working) {
        updateRefreshIcon();
    }

    @Override
    public boolean onContactLongClick(final ContactListFragment fragment, final Person person, final int position, final long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onContactClick(final ContactListFragment fragment, final Person person, final int position, final long id) {
        ContactActivity.start(getSupportActivity(), person);
    }

    @Override
    public void onContactChecked(final ContactListFragment fragment, final Person person, final int position, final boolean checked) {
        if (mActionMode == null && checked == true) {
            mActionMode = getSupportActivity().startActionMode(this);
        }
    }

    @Override
    public void onAllContactsUnchecked(final ContactListFragment fragment) {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        menu.add(Menu.NONE, R.id.action_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;

    }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        if (item.getItemId() == R.id.action_assign) {
            final Set<Person> people = new HashSet<Person>(mFragment.getCheckedPeople());
            ContactAssignmentDialogFragment.show(getChildFragmentManager(), people).setAssignmentListener(this);
        }
        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mFragment.clearChecked();
            }
        });
    }

    @Override
    public void onAssignmentCompleted() {
        mFragment.reload();
    }

    @Override
    public void onAssignmentCanceled() {
    }

    @Override
    public void onContactAdded(final Person contact) {
        ContactActivity.start(getSupportActivity(), contact);
        mFragment.reload();
    }

    @Override
    public void onAddContactCanceled() {

    }
}