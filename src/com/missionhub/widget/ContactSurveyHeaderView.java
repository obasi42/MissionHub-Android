package com.missionhub.widget;

import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ContactSurveyHeaderView extends TextView implements ItemView {

    public ContactSurveyHeaderView(Context context) {
        this(context, null);
    }

    public ContactSurveyHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactSurveyHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void prepareItemView() {
    }

    public void setObject(Item object) {
        final TextItem item = (TextItem) object;
        setText(item.text);
    }
}