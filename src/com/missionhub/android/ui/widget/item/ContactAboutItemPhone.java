package com.missionhub.android.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;

public class ContactAboutItemPhone extends ContactAboutItem {

    public boolean mCanText = true;
    
    public ContactAboutItemPhone(final String number) {
        this(number, null);
    }
    
    public ContactAboutItemPhone(final String number, final String type) {
       this(number, type, true);
    }
    
    public ContactAboutItemPhone(final String number, final String type, final boolean dividers) {
        this(number, type, dividers, true, false);
    }
    
    public ContactAboutItemPhone(final String number, final String type, final boolean dividers, final boolean canText) {
        this(number, type, dividers, false, false, canText);
    }
    
    public ContactAboutItemPhone(final String number, final String type, final boolean dividers, final boolean first, final boolean last) {
        this(number, type, dividers, first, last, true);
    }
    
    public ContactAboutItemPhone(final String number, final String type, final boolean dividers, final boolean first, final boolean last, final boolean canText) {
        super(number, type, dividers, first, last);
        enabled = true;
        mCanText = canText;
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item_phone, parent);
    }   
}