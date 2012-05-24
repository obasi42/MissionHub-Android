package com.missionhub.android.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.missionhub.android.ui.widget.item.ContactAboutItemHeader;
import com.missionhub.android.util.U;

public class ContactAboutItemHeaderView extends LinearLayout implements ListItemView {

    TextView mText;
    
    public ContactAboutItemHeaderView(final Context context) {
        super(context);
    }
    
    public ContactAboutItemHeaderView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Class<? extends Item> getItemClass() {
        return ContactAboutItemHeader.class;
    }

    @Override
    public void prepareItemView() {
        mText = (TextView) findViewById(android.R.id.text1);
    }

    @Override
    public void setObject(final Item item) {
        setObject(item, null, -1);
    }

    @Override
    public void setObject(final Item item, final ViewGroup parent, final int position) {
       final ContactAboutItemHeader cItem = (ContactAboutItemHeader) item;
       if (!U.isNullEmpty(cItem.text)) {
           mText.setText(cItem.text);
           mText.setVisibility(VISIBLE);
       } else {
           mText.setVisibility(GONE);
       }
    }
    
}