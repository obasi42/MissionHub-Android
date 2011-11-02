package com.missionhub.ui.widget;

import com.missionhub.ui.widget.item.ContactSurveyHeaderItem;

import greendroid.widget.item.Item;
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

    @Override
	public void prepareItemView() {
    }

    @Override
	public void setObject(Item object) {
        final ContactSurveyHeaderItem item = (ContactSurveyHeaderItem) object;
        setText(item.text);
        
        setClickable(true);
    }
    
    @Override
	public Class<? extends Item> getItemClass() {
		return ContactSurveyHeaderItem.class;
	}
}