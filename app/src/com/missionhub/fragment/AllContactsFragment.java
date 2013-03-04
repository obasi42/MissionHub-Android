package com.missionhub.fragment;

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
import com.missionhub.R;
import com.missionhub.activity.ContactActivity;
import com.missionhub.api.PersonListOptions;
import com.missionhub.contactlist.ApiContactListProvider;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.contactlist.ContactListFragment.ContactListFragmentListener;
import com.missionhub.contactlist.ContactListProvider;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.dialog.ContactAssignmentDialogFragment;
import com.missionhub.fragment.dialog.ContactLabelsDialogFragment;
import com.missionhub.fragment.dialog.EditContactDialogFragment;
import com.missionhub.model.Person;
import com.missionhub.util.U;
import org.holoeverywhere.LayoutInflater;

public class AllContactsFragment extends MainFragment implements ContactListFragmentListener, ActionMode.Callback {

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

    public static int REQUEST_ASSIGNMENT = 1;
    public static int REQUEST_LABELS = 2;
    public static int REQUEST_EDIT_CONTACT = 3;

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
                EditContactDialogFragment.showForResult(getChildFragmentManager(), false, REQUEST_EDIT_CONTACT);
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
        if (mActionMode == null && checked) {
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
            ContactAssignmentDialogFragment.showForResult(getChildFragmentManager(), mFragment.getCheckedPeople(), REQUEST_ASSIGNMENT);
        }
        if (item.getItemId() == R.id.action_label) {
            ContactLabelsDialogFragment.showForResult(getChildFragmentManager(), mFragment.getCheckedPeople(), REQUEST_LABELS);
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
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (requestCode == REQUEST_EDIT_CONTACT && resultCode == RESULT_OK) {
            if (data != null && data instanceof Person) {
                ContactActivity.start(getSupportActivity(), (Person) data);
                mFragment.reload();
                return true;
            }
        } else if (requestCode == REQUEST_ASSIGNMENT && resultCode == RESULT_OK) {
            mFragment.reload();
            return true;
        } else if (requestCode == REQUEST_LABELS && resultCode == RESULT_OK) {
            mFragment.reload();
            return true;
        }
        return super.onFragmentResult(requestCode, resultCode, data);
    }
}