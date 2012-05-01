package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.ui.widget.item.ContactAboutItemWithImage;
import com.missionhub.util.U;

public class ContactAboutItemWithImageView extends ContactAboutItemView {
    
    ImageView mIcon;
    
    public ContactAboutItemWithImageView(final Context context) {
        super(context);
    }
    
    public ContactAboutItemWithImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Class<? extends Item> getItemClass() {
        return ContactAboutItemWithImage.class;
    }

    @Override
    public void prepareItemView() {
        super.prepareItemView();
        mIcon = (ImageView) findViewById(android.R.id.icon);
    }

    @Override
    public void setObject(final Item item) {
        setObject(item, null, -1);
    }

    @Override
    public void setObject(final Item item, final ViewGroup parent, final int position) {
        super.setObject(item, parent, position);
        final ContactAboutItemWithImage cItem = (ContactAboutItemWithImage) item;
        
        if (!U.isNullEmpty(cItem.mImage)) {
            mIcon.setImageDrawable(cItem.mImage);
        } else {
            mIcon.setImageDrawable(null);
        }
    }
}