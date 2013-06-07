package com.missionhub.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.people.ApiPeopleListProvider;
import com.missionhub.people.PeopleListProvider;
import com.missionhub.people.PeopleListView;

import org.holoeverywhere.LayoutInflater;

public class HostedPeopleListFragment extends HostedFragment {

    private PeopleListView mView;
    private ApiPeopleListProvider mProvider;

    public HostedPeopleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list);

        mView = (PeopleListView) view.findViewById(android.R.id.list);
        if (mProvider == null) {
            mProvider = new ApiPeopleListProvider(getSupportActivity());
        } else {
            mProvider.setContext(getSupportActivity());
        }
        mView.setProvider(mProvider);

        return view;
    }

}
