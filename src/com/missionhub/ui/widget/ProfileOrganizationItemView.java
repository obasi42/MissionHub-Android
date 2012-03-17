package com.missionhub.ui.widget;



import com.missionhub.ui.widget.item.ProfileOrganizationItem;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProfileOrganizationItemView extends TextView implements ItemView {

    public ProfileOrganizationItemView(Context context) {
        this(context, null);
    }

    public ProfileOrganizationItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileOrganizationItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
	public void prepareItemView() {
    }

    @Override
	public void setObject(Item object) {
        final ProfileOrganizationItem item = (ProfileOrganizationItem) object;
        setText(item.text);
        
        this.setPadding(item.level * 20, 0, 0, 0);
        
    }
    
    @Override
	public Class<? extends Item> getItemClass() {
		return ProfileOrganizationItem.class;
	}
}