package com.missionhub.ui.widget;

import greendroid.widget.item.ProgressItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter;

/**
 * The contact about tab
 */
public class ContactAboutView extends ContactView {
	
	/** the list view */
	private ListView mListView;
	
	/** the list adapter */
	private ListItemAdapter mAdapter;

	/** the progress item */
	private ProgressItem mProgressItem = new ProgressItem("Loading...");
	
	public ContactAboutView(final Context context) {
		super(context);
		setBackgroundResource(R.color.green);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View view = inflater.inflate(R.layout.widget_contact_about, this, true);
		
		mListView = (ListView) view.findViewById(R.id.listView);
		
		mAdapter = new ListItemAdapter(context);
		mAdapter.add(mProgressItem);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void resetView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
	}

}