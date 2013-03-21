package com.missionhub.fragment;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.missionhub.R;
import com.missionhub.api.PersonListOptions;
import com.missionhub.contactlist.ApiContactListProvider;
import com.missionhub.contactlist.ContactListFragment;
import com.missionhub.contactlist.ContactListProvider;
import com.missionhub.util.U;
import org.holoeverywhere.LayoutInflater;

public class AllContactsFragment extends ContactListMainFragment {

    ContactListFragment mFragment;

    public AllContactsFragment() {
    }

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

    public static class AllContactsFragmentFragment extends ContactListFragment {
        public AllContactsFragmentFragment() {
        }

        @Override
        public ContactListProvider onCreateContactProvider() {
            final PersonListOptions options = PersonListOptions.builder().build();

            return new ApiContactListProvider(getSupportActivity(), options);
        }
    }

    @Override
    public void onSearchTextChange(final String query) {
        if (query.length() == 0) {
            getContactListFragment().removeAltProvider();
        } else {
            PersonListOptions options = ((ApiContactListProvider) getContactListFragment().getProvider()).getOptions();
            options.addFilter("name_or_email_like", query);
            ApiContactListProvider searchProvider = new ApiContactListProvider(getSupportActivity(), options);
            getContactListFragment().setAltProvider(searchProvider);
            searchProvider.reload();
        }
    }
}