package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.missionhub.R;

public class ContactAboutItemWithImage extends ContactAboutItem {

    public Drawable mImage;
    public boolean mCanText = true;
    
    public ContactAboutItemWithImage(final Drawable image, final String text) {
        this(image, text, null);
     }
    
    public ContactAboutItemWithImage(final Drawable image, final String text, final String subtext) {
       this(image, text, subtext, true);
    }
    
    public ContactAboutItemWithImage(final Drawable image, final String text, final String subtext, final boolean dividers) {
        this(image, text, subtext, dividers, true, false);
    }
    
    public ContactAboutItemWithImage(final Drawable image, final String text, final String subtext, final boolean dividers, final boolean canText) {
        this(image, text, subtext, dividers, false, false);
    }
    
    public ContactAboutItemWithImage(final Drawable image, final String text, final String subtext, final boolean dividers, final boolean first, final boolean last) {
        super(text, subtext, dividers, first, last);
        enabled = true;
        mImage = image;
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item_with_image, parent);
    }   
}