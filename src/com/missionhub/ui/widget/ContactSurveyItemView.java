package com.missionhub.ui.widget;

import com.missionhub.R;
import com.missionhub.ui.widget.item.ContactSurveyItem;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactSurveyItemView extends LinearLayout implements ItemView {

    private TextView mTextView;
    private TextView mSubtextView;

    public ContactSurveyItemView(Context context) {
        this(context, null);
    }

    public ContactSurveyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
	public void prepareItemView() {
        mTextView = (TextView) findViewById(R.id.text);
        mSubtextView = (TextView) findViewById(R.id.subtext);
    }

    @Override
	public void setObject(Item object) {
        final ContactSurveyItem item = (ContactSurveyItem) object;
        mTextView.setText(item.text);
        
        if (item.subtext == null || item.subtext.equals("")) {
        	mSubtextView.setText(getContext().getString(R.string.contact_tab_surveys_not_answered));
        	mSubtextView.setTextColor(getContext().getResources().getColor(R.color.medium_gray));
        } else {
        	mSubtextView.setText(item.subtext);
        	mSubtextView.setTextColor(getContext().getResources().getColor(R.color.dark_gray));
        }        
        this.setClickable(true);
    }

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactSurveyItem.class;
	}
}
