package com.missionhub.ui.widget.item;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;

import com.missionhub.R;

import android.content.Context;
import android.view.ViewGroup;

public class ContactSurveyItem extends TextItem {

    /**
     * The string that will be displayed above the title of the item (possibly
     * on several lines).
     */
    public String subtext;

    /**
     * @hide
     */
    public ContactSurveyItem() {
        this(null);
    }

    /**
     * @hide
     */
    public ContactSurveyItem(String text) {
        this(text, null);
    }

    /**
     * Constructs a new SubtextItem
     * 
     * @param text The main text for this item
     * @param subtext The subtext
     */
    public ContactSurveyItem(String text, String subtext) {
        super(text);
        this.subtext = subtext;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_survey_item, parent);
    }
}
