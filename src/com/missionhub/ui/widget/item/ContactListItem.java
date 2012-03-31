package com.missionhub.ui.widget.item;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.util.U;

public class ContactListItem extends Item {

	/** the person object for this item */
	public Person mPerson;

	/**
	 * Construct a Contact List Item from a person
	 * 
	 * @param person
	 * @param contactListFragment 
	 */
	public ContactListItem(final Person person) {
		super();
		mPerson = person;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		if (U.isOldTablet(context) && U.isW1024dp(context)) {
			return createCellFromXml(context, R.layout.widget_contact_list_item_tablet, parent);
		} else {
			return createCellFromXml(context, R.layout.widget_contact_list_item, parent);
		}
	}
}