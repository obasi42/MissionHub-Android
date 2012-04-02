package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.ui.widget.ListItemView;

public class ListItemAdapter extends ItemAdapter {

	final private Context mContext;

	public ListItemAdapter(final Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		final Item item = (Item) getItem(position);
		ListItemView cell = (ListItemView) convertView;

		if (cell == null || cell.getItemClass() != item.getClass()) {
			cell = (ListItemView) item.newView(mContext, null);
			cell.prepareItemView();
		}

		cell.setObject(item, parent, position);

		return (View) cell;
	}
}