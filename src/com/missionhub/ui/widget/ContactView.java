package com.missionhub.ui.widget;

import com.missionhub.api.model.sql.Person;

import android.content.Context;
import android.widget.FrameLayout;

public abstract class ContactView extends FrameLayout {
	
	/** the person the view represents */
	private Person mPerson;
	
	@SuppressWarnings("deprecation")
	public ContactView(final Context context) {
		super(context);
		setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}
	
	/**
	 * @return the person the view represents
	 */
	public Person getPerson() {
		return mPerson;
	}
	
	/**
	 * Sets the person the view represents
	 * @param person
	 */
	public void setPerson(Person person) {
		if (person != mPerson) {
			mPerson = person;
			resetView();	
		}
	}
	
	public abstract void resetView();
	
	public abstract void updateView();
}