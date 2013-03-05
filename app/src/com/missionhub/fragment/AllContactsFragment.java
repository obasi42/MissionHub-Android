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
import com.missionhub.api.ContactListOptions;
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

public class AllContactsFragment extends ContactListMainFragment {

    ContactListFragment mFragment;
    ApiContactListProvider mProvider;

    @Override
    public View onCreateView(final LayoutInflater inflater, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_frame, null);

        if (mFragment == null) {
            mFragment = new AllContactsFragmentFragment();
            mFragment.setContactListFragmentListener(this);
            getChildFragmentManager().beginTransaction().add(R.id.content_frame, mFragment).commit();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSearchHelper().getSearchView().setQueryHint("Search All Contacts...");
    }

    @Override
    public ContactListFragment getContactListFragment() {
        return mFragment;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        U.resetActionBar(getSupportActivity());
        getSupportActivity().getSupportActionBar().setTitle("All Contacts");
    }

    public class AllContactsFragmentFragment extends ContactListFragment {
        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder().build();

            mProvider = new ApiContactListProvider(getSupportActivity(), options);
            return mProvider;
        }
    }

    @Override
    public void onSearchTextChange(final String query) {
        if (query.length() == 0) {
            getContactListFragment().setProvider(mProvider);
        } else {
            PersonListOptions options = mProvider.getOptions();
            options.addFilter("name_or_email_like", query);
            ApiContactListProvider searchProvider = new ApiContactListProvider(getSupportActivity(), options);
            getContactListFragment().setProvider(searchProvider);
            searchProvider.reload();
        }
    }
}