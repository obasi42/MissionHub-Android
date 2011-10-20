
package com.missionhub.widget;

import com.missionhub.R;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

public class ContactSurveyHeader extends TextItem {

    /**
     * @hide
     */
    public ContactSurveyHeader() {
        this(null);
    }

    /**
     * Construct a SeparatorItem made of the given text
     * 
     * @param text The text for this SeparatorItem
     */
    public ContactSurveyHeader(String text) {
        super(text);
        enabled = false;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_survey_header, parent);
    }

}