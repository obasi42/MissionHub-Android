package com.missionhub.ui.item;

import com.missionhub.R;
import com.missionhub.model.Person;

public class ContactListItem extends LayoutItem {

	public Person person;

	public ContactListItem(final Person person) {
		super(R.layout.item_contact);
		this.person = person;
	}

	public ContactListItem(final Person person, final int layout) {
		super(layout);
		this.person = person;
	}
}