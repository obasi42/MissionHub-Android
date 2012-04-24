package com.missionhub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter;
import com.missionhub.ui.widget.item.NavigationItem;

public class NavigationMenuFragment extends MissionHubFragment implements OnItemClickListener {

	private ListView mListView;

	private ListItemAdapter mAdapter;

	private NavigationItem mCurrentItem;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_navigation_menu, container, false);
		view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));

		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemClickListener(this);

		return view;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View listView, int position, long id) {
		
	}
}