package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.ui.widget.ListItemView;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.ui.widget.SelectableListView.SupportActivatable;

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

		// create a new view if the parent size has changed or if the item type
		// if different
		if ((cell != null && parent != null && ((View) cell).getWidth() != parent.getWidth()) || (cell == null || cell.getItemClass() != item.getClass())) {
			cell = (ListItemView) item.newView(mContext, null);
			cell.prepareItemView();
		}

		cell.setObject(item, parent, position);

		final View view = (View) cell;

		if (parent instanceof SelectableListView && view instanceof SupportActivatable) {
			final boolean activated = ((SelectableListView) parent).isItemActivated(position);
			((SupportActivatable) view).setSupportActivated(activated);
		}

		return view;
	}
}