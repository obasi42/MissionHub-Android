package com.missionhub.fragment;

import android.os.Bundle;
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
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.dialog.ContactAssignmentDialogFragment;
import com.missionhub.fragment.dialog.ContactLabelsDialogFragment;
import com.missionhub.fragment.dialog.EditContactDialogFragment;
import com.missionhub.model.Person;
import com.missionhub.ui.SearchMenuItemHelper;
import com.missionhub.util.U;
import org.holoeverywhere.LayoutInflater;

public abstract class ContactListMainFragment extends MainFragment implements ContactListFragment.ContactListFragmentListener, ActionMode.Callback, SearchMenuItemHelper.SearchMenuItemListener {

    private MenuItem mRefreshItem;

    private ImageView mRefreshingView;

    private ActionMode mActionMode;

    public static int REQUEST_ASSIGNMENT = 1;
    public static int REQUEST_LABELS = 2;
    public static int REQUEST_EDIT_CONTACT = 3;

    private SearchMenuItemHelper mSearchHelper;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
        mSearchHelper = new SearchMenuItemHelper(this);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mRefreshingView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);
        return onCreateView(inflater, savedInstanceState);
    }

    public abstract View onCreateView(final LayoutInflater inflater, final Bundle savedInstanceState);

    @Override
    public void onViewCreated(View view) {
        if (mActionMode != null) {
            mActionMode = getSupportActivity().startActionMode(this);
        }
    }

    public SearchMenuItemHelper getSearchHelper() {
        return mSearchHelper;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(Menu.NONE, R.id.action_add_contact, Menu.NONE, R.string.action_add_contact).setIcon(R.drawable.ic_action_add_contact)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        mSearchHelper.onCreateOptionsMenu(menu, inflater);

        mRefreshItem = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public abstract ContactListFragment getContactListFragment();

    public void updateRefreshIcon() {
        if (mRefreshItem == null || mRefreshingView == null) return;

        final ContactListFragment fragment = getContactListFragment();

        if (fragment == null || !fragment.isVisible()) return;

        if (fragment.isWorking()) {
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                final ContactListFragment fragment = getContactListFragment();
                if (fragment != null) {
                    fragment.reload();
                    return true;
                }
                break;
            case R.id.action_add_contact:
                EditContactDialogFragment.showForResult(getChildFragmentManager(), REQUEST_EDIT_CONTACT);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        finishActionMode();
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        menu.add(Menu.NONE, R.id.action_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, R.id.action_label, Menu.NONE, R.string.action_label).setIcon(R.drawable.ic_action_label)
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
            ContactAssignmentDialogFragment.showForResult(getChildFragmentManager(), getContactListFragment().getCheckedPeople(), REQUEST_ASSIGNMENT);
        }
        if (item.getItemId() == R.id.action_label) {
            ContactLabelsDialogFragment.showForResult(getChildFragmentManager(), getContactListFragment().getCheckedPeople(), REQUEST_LABELS);
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        if (getContactListFragment().getCheckedItemCount() > 0) {
            getContactListFragment().clearChecked();
        }
        mActionMode = null;
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (requestCode == REQUEST_EDIT_CONTACT && resultCode == RESULT_OK) {
            if (data != null && data instanceof Person) {
                ContactActivity.start(getSupportActivity(), (Person) data);
                getContactListFragment().reload();
                return true;
            }
        } else if (requestCode == REQUEST_ASSIGNMENT && resultCode == RESULT_OK) {
            getContactListFragment().reload();
            finishActionMode();
            return true;
        } else if (requestCode == REQUEST_LABELS && resultCode == RESULT_OK) {
            getContactListFragment().reload();
            finishActionMode();
            return true;
        }
        return super.onFragmentResult(requestCode, resultCode, data);
    }

    protected void finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

}
