package com.missionhub.ui.widget;

import greendroid.widget.item.ProgressItem;
import greendroid.widget.item.TextItem;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter;

/**
 * The contact about tab
 */
public class ContactAboutView extends ContactView {

    /** the list view */
    private final ListView mListView;

    /** the list adapter */
    private final ListItemAdapter mAdapter;

    /** the progress item */
    private ProgressItem mProgressItem = new ProgressItem("Loading...");

    public ContactAboutView(final Context context) {
        this(context, null);
    }

    public ContactAboutView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.widget_contact_about, this);
        
        mListView = (ListView) findViewById(R.id.listView);
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
