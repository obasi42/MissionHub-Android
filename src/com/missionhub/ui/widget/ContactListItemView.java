package com.missionhub.ui.widget;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.widget.item.ContactListItem;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactListItemView extends LinearLayout implements ItemView {

	private ImageView mPicture;
	private TextView mName;
	private TextView mStatus;

	public ContactListItemView(Context context) {
		this(context, null);
	}

	public ContactListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		mPicture = (ImageView) findViewById(R.id.picture);
		mName = (TextView) findViewById(R.id.name);
		mStatus = (TextView) findViewById(R.id.status);
	}

	@Override
	public void setObject(Item object) {
		final ContactListItem item = (ContactListItem) object;
		
		final Person person = item.mPerson;
		mName.setText(person.getName());
		mStatus.setText(person.getStatus());
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactListItem.class;
	}

}