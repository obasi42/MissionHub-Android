package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.TextItem;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.missionhub.R;

public class PeopleMyCategoryFragment extends MissionHubFragment {
	
	private ListView listView;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_people_my_category, container, false);
		listView = (ListView) view.findViewById(R.id.listView);
		
		ItemAdapter adapter = new ItemAdapter(inflater.getContext());
		adapter.add(new TextItem("All"));
		adapter.add(new TextItem("In Progress"));
		adapter.add(new TextItem("Completed"));
		adapter.add(new TextItem("Detailed"));
		
		listView.setAdapter(adapter);
		
		return view;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}
}