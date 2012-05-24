package com.missionhub.android.ui.widget;

import com.missionhub.R;
import com.missionhub.android.api.old.model.sql.Person;
import com.missionhub.android.ui.widget.item.ContactAboutItemHeading;
import com.missionhub.android.util.U;

import greendroid.widget.item.Item;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactAboutItemHeadingView extends LinearLayout implements ListItemView {

    RoundedAsyncImageView mPicture;
    TextView mName;
    
    public ContactAboutItemHeadingView(Context context) {
        super(context);
    }
    
    public ContactAboutItemHeadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Class<? extends Item> getItemClass() {
        return ContactAboutItemHeading.class;
    }

    @Override
    public void prepareItemView() {
        mPicture = (RoundedAsyncImageView) findViewById(R.id.picture);
        mName = (TextView) findViewById(R.id.name);
    }

    @Override
    public void setObject(Item item) {
        setObject(item, null, -1);
    }

    @Override
    public void setObject(Item item, ViewGroup parent, int position) {
        final Person person = ((ContactAboutItemHeading)item).mPerson;
        
        if (person != null) {
            if (!U.isNullEmpty(person.getPicture())) {
                mPicture.setUrl(person.getPicture() + "?type=large");
            }
            if (!U.isNullEmpty(person.getName())) {
                mName.setText(person.getName());
            }
        }
    }
    
}