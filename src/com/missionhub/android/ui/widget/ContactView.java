package com.missionhub.android.ui.widget;

import com.missionhub.android.api.old.model.sql.Person;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class ContactView extends FrameLayout {
	
	/** the person the view represents */
	private Person mPerson;
	
	public ContactView(final Context context) {
		super(context);
	}
	
	public ContactView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
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