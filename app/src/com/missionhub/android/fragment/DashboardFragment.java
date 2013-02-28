package com.missionhub.android.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

public class DashboardFragment extends MainFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final TextView tv = new TextView(inflater.getContext());
        tv.setText("Dashboard Fragment");
        return tv;
    }
}