package com.missionhub.ui.widget.item;

import com.missionhub.R;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

public class ProfileOrganizationItem extends TextItem {

	/** indentation level */
	public int level = 0;
	
	/** organization id */
	public long organizationId = -1;
	
    /**
     * @hide
     */
    public ProfileOrganizationItem() {
        this(null);
    }

    /**
     * Construct a SeparatorItem made of the given text
     * 
     * @param text The text for this SeparatorItem
     */
    public ProfileOrganizationItem(String text) {
        super(text);
    }
    
    public ProfileOrganizationItem(String text, Long organizationId, int level) {
    	super(text);
    	this.organizationId = organizationId;
    	this.level = level;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_profile_organization_item, parent);
    }

}