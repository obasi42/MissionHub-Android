package com.missionhub.ui.widget.item;

import com.missionhub.api.model.sql.Person;

import greendroid.widget.item.Item;

public abstract class ContactItem extends Item {
    
    /** the person object for this item */
    public Person mPerson;
    
    /**
     * Construct a Contact Item from a person
     * 
     * @param person
     */
    public ContactItem(final Person person) {
        super();
        mPerson = person;
    }
    
}