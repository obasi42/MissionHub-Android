package com.missionhub.android.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.android.ui.widget.item.ContactAboutItem;
import com.missionhub.android.util.U;

public class ContactAboutItemView extends LinearLayout implements ListItemView {

    ImageView mIcon;
    TextView mText;
    TextView mSubtext;
    View mDivider;
    View mShortDivider;
    View mLongDivider;

    public ContactAboutItemView(final Context context) {
        super(context);
    }

    public ContactAboutItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Class<? extends Item> getItemClass() {
        return ContactAboutItem.class;
    }

    @Override
    public void prepareItemView() {
        mIcon = (ImageView) findViewById(android.R.id.icon);
        mText = (TextView) findViewById(android.R.id.text1);
        mSubtext = (TextView) findViewById(android.R.id.text2);
        mDivider = findViewById(R.id.divider);
        mShortDivider = findViewById(R.id.shortDivider);
        mLongDivider = findViewById(R.id.longDivider);
    }

    @Override
    public void setObject(final Item item) {
        setObject(item, null, -1);
    }

    @Override
    public void setObject(final Item item, final ViewGroup parent, final int position) {
        final ContactAboutItem cItem = (ContactAboutItem) item;
        
        if (mIcon != null) {
            if (cItem.mFirst) {
                mIcon.setVisibility(VISIBLE);
            } else {
                mIcon.setVisibility(INVISIBLE);
            }
        }

        if (!U.isNullEmpty(cItem.text)) {
            mText.setText(cItem.text);
            mText.setVisibility(VISIBLE);
        } else {
            mText.setVisibility(GONE);
        }

        if (!U.isNullEmpty(cItem.subtext)) {
            mSubtext.setText(cItem.subtext);
            mSubtext.setVisibility(VISIBLE);
        } else {
            mSubtext.setVisibility(GONE);
        }

        if (mShortDivider != null && mLongDivider != null) {
            if (cItem.mLast) {
                mShortDivider.setVisibility(GONE);
                mLongDivider.setVisibility(VISIBLE);
            } else {
                mShortDivider.setVisibility(VISIBLE);
                mLongDivider.setVisibility(GONE);
            }
        }

        if (mDivider != null) {
            if (cItem.mDividers) {
                mDivider.setVisibility(VISIBLE);
            } else {
                mDivider.setVisibility(GONE);
            }
        }
    }
}
