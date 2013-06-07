package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.missionhub.R;
import com.missionhub.activity.HostActivity;
import com.missionhub.ui.ExpandableObjectArrayAdapter;
import com.missionhub.util.U;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ExpandableListView;

public class SideMenuFragment extends Fragment {

    private ExpandableListView mList;
    private SideMenuListAdapter mAdapter;

    public SideMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        // set up the view
        View view = inflater.inflate(R.layout.fragment_side_menu, group, false);
        mList = (ExpandableListView) view.findViewById(android.R.id.list);

        // set up adapter
        if (mAdapter == null) {
            mAdapter = new SideMenuListAdapter(getSupportActivity());
            buildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);

        return mList;
    }

    public static class SideMenuListAdapter extends ExpandableObjectArrayAdapter {
        public SideMenuListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        HostActivity activity = (HostActivity) getSupportActivity();
        if (activity != null && activity.isMenuOpen()) {
            // menu items here
        }
    }

    private void buildAdapter() {

    }
}
