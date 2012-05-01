package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.item.ProgressItem;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter;
import com.missionhub.ui.widget.item.ContactAboutItem;
import com.missionhub.ui.widget.item.ContactAboutItemHeader;
import com.missionhub.ui.widget.item.ContactAboutItemHeading;
import com.missionhub.ui.widget.item.ContactAboutItemPhone;
import com.missionhub.ui.widget.item.ContactAboutItemWithImage;

/**
 * The contact about tab
 */
public class ContactAboutView extends ContactView implements OnItemClickListener {

    /** the list view */
    private final ListView mListView;

    /** the list adapter */
    private final ListItemAdapter mAdapter;

    /** the progress item */
    private final ProgressItem mProgressItem;

    public ContactAboutView(final Context context) {
        this(context, null);
    }

    public ContactAboutView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.widget_contact_about, this);
        
        mProgressItem = new ProgressItem(context.getString(R.string.progress_loading), true);
        
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new ListItemAdapter(context);
        mAdapter.add(mProgressItem);
        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void resetView() {
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();
        
        Item heading = new ContactAboutItemHeading(getPerson());
        mAdapter.add(heading);
        
        Item contactHeader = new ContactAboutItemHeader("CONTACT");
        mAdapter.add(contactHeader);
        
        Item phone = new ContactAboutItemPhone(getPerson().getPhone_number(), "MOBILE", true, true, true, true);
        mAdapter.add(phone);
        
        Item email = new ContactAboutItemWithImage(getResources().getDrawable(R.drawable.contact_email), getPerson().getEmail_address(), null, false);
        mAdapter.add(email);
        
        Item personInfoHeader = new ContactAboutItemHeader("PERSONAL INFO");
        mAdapter.add(personInfoHeader);
        
        Item gender = new ContactAboutItem(getPerson().getGender(), "GENDER", false);
        mAdapter.add(gender);
        
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateView() {
        
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Item item = (Item) adapter.getItemAtPosition(position);
        
        if (item instanceof ContactAboutItemPhone) {
            Log.e("ITEM CLICK", "CLICK PHONE");            
            if (id == R.id.send_text_button) {
                Log.e("ITEM CLICK", "CLICK TEXT");  
            }
        }
        
    }

}
