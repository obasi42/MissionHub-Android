package com.missionhub.ui.widget.item;

import java.util.List;
import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;

import com.missionhub.R;
import com.missionhub.api.model.sql.FollowupComment;
import com.missionhub.api.model.sql.Rejoicable;

import android.content.Context;
import android.view.ViewGroup;

public class ContactStatusItem extends Item {
	
	public FollowupComment comment;
	public List<Rejoicable> rejoicables;
	
    public ContactStatusItem(FollowupComment comment, List<Rejoicable> rejoicables) {
        this.comment = comment;
        this.rejoicables = rejoicables;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.widget_contact_status_item, parent);
    }
}
