package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;

public class ContactAboutItemHeading extends ContactItem {

    public ContactAboutItemHeading(final Person person) {
        super(person);
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item_heading, parent);
    }
    
    
}