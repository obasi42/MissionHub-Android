package com.missionhub.android.ui.widget.item;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.android.ui.ListItemAdapter.Enableable;

public class ContactAboutItemHeader extends TextItem implements Enableable {
    
    public ContactAboutItemHeader(final CharSequence text) {
        super(text.toString());
        enabled = false;
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item_header, parent);
    }

    @Override
    public boolean isEnabled() {
    	return enabled;
    }
}