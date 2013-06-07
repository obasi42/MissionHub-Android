package com.missionhub.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.people.ApiPeopleListProvider;
import com.missionhub.people.PeopleListView;
import com.missionhub.ui.ObjectArrayAdapter;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

public class HostedPeopleListFragment extends HostedFragment {

    private ImageView mCheckBox;
    private TextView mCheckText;

    private Spinner mSelector;
    private Spinner mSort;
    private ObjectArrayAdapter mSelectorAdapter;
    private ObjectArrayAdapter mSortAdapter;

    private PeopleListView mView;
    private ApiPeopleListProvider mProvider;

    public HostedPeopleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list);

        //mCheckBox = view.findViewById(R.id.checkbox);

        // set up the person list and adapter
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