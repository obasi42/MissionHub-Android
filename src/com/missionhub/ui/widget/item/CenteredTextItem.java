package com.missionhub.ui.widget.item;

import com.missionhub.R;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

public class CenteredTextItem extends TextItem {

    /**
     * @hide
     */
    public CenteredTextItem() {
        this(null);
    }

    /**
     * Construct a SeparatorItem made of the given text
     * 
     * @param text The text for this SeparatorItem
     */
    public CenteredTextItem(String text) {
        super(text);
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_centered_text_item, parent);
    }

}