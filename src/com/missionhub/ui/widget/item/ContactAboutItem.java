package com.missionhub.ui.widget.item;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;

import com.missionhub.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public class ContactAboutItem extends TextItem {

    public String subtext;
	public Drawable icon;
    public Action action;
	
    public ContactAboutItem() {
        this(null);
    }

    public ContactAboutItem(String text) {
        this(text, null, null, null);
    }
    
    public ContactAboutItem(String text, String subtext) {
    	this(text, subtext, null, null);
    }

    public ContactAboutItem(String text, String subtext, Drawable icon) {
    	this(text, subtext, icon, null);
    }
    
    public ContactAboutItem(String text, String subtext, Drawable icon, Action action) {
        super(text);
        this.subtext = subtext;
        this.icon = icon;
        this.action = action;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_about_item, parent);
    }
    
    public static class Action {
    	public void run() {}
    }
}
