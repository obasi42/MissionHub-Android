package com.missionhub.ui.widget.item;

import greendroid.widget.item.SubtextItem;
import greendroid.widget.itemview.ItemView;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;

public class ContactAboutItem extends SubtextItem {
    
    public boolean mDividers = true;
    public boolean mFirst = true;
    public boolean mLast = false;
    
    public ContactAboutItem(String text) {
        this(text, null);
    }
    
    public ContactAboutItem(String text, String subtext) {
        this(text, subtext, true);
    }
    
    public ContactAboutItem(String text, String subtext, boolean dividers) {
        this(text, subtext, dividers, true, false);
    }
    
    public ContactAboutItem(String text, String subtext, boolean dividers, boolean first, boolean last) {
        super(text, subtext);
        mDividers = dividers;
        mFirst = first;
        mLast = last;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item, parent);
    }
}