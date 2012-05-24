package com.missionhub.android.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.android.api.old.model.sql.Person;
import com.missionhub.android.ui.DynamicLayoutAdapter.DynamicLayoutItem;

public class ContactListItem extends ContactItem implements DynamicLayoutItem {
	
	public static final int LAYOUT_TABLET = R.layout.widget_contact_list_item_tablet;
	public static final int LAYOUT_NORMAL = R.layout.widget_contact_list_item;
	
	public int mLayout = LAYOUT_NORMAL;
	
    /**
     * Construct a Contact List Item from a person
     * 
     * @param person
     * @param contactListFragment
     */
    public ContactListItem(final Person person) {
        super(person);
    }

    @Override
    public ItemView newView(final Context context, final ViewGroup parent) {
        return createCellFromXml(context, mLayout, parent);
    }

    @Override
    public void setLayoutResource(int resource) {
    	mLayout = resource;
    }
}
