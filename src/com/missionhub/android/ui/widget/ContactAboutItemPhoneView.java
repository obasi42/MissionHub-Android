package com.missionhub.android.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.android.ui.widget.item.ContactAboutItemPhone;

public class ContactAboutItemPhoneView extends ContactAboutItemView {
    
    ImageView mIcon;
    View mText;
    View mVDivider;
    View mTextButton;
    
    private static Drawable mIconDrawable;
    
    public ContactAboutItemPhoneView(final Context context) {
        super(context);
    }
    
    public ContactAboutItemPhoneView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Class<? extends Item> getItemClass() {
        return ContactAboutItemPhone.class;
    }

    @Override
    public void prepareItemView() {
        super.prepareItemView();
        mIcon = (ImageView) findViewById(android.R.id.icon);
        mText = findViewById(R.id.send_text_button);
        mVDivider = findViewById(R.id.vertical_divider);
        mTextButton = findViewById(R.id.send_text_button);
        
        if (mIconDrawable == null) {
            mIconDrawable = getResources().getDrawable(R.drawable.contact_phone);
        }
    }

    @Override
    public void setObject(final Item item) {
        setObject(item, null, -1);
    }

    @Override
    public void setObject(final Item item, final ViewGroup parent, final int position) {
        super.setObject(item, parent, position);
        final ContactAboutItemPhone cItem = (ContactAboutItemPhone) item;
        
        if (cItem.mFirst) {
            mIcon.setImageDrawable(mIconDrawable);
        } else {
            mIcon.setImageDrawable(null);
        }
        
        mTextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.e("CLICK TEXT", "CLICK TEXT");
                ((ListView) parent).performItemClick(view, position, R.id.send_text_button);
            }
        });
        
        if (cItem.mCanText) {
            mText.setVisibility(View.VISIBLE);
            mVDivider.setVisibility(View.VISIBLE);
        } else {
            mText.setVisibility(View.GONE);
            mVDivider.setVisibility(View.GONE); 
        }
    }
}