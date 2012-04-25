package com.missionhub.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

import com.missionhub.R;
import com.missionhub.ui.widget.ListItemView;
import com.missionhub.ui.widget.item.SpinnerItem;

public class SpinnerItemAdapter extends ListItemAdapter implements SpinnerAdapter {

	public static final int KEY_TYPE = 100;
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_DROPDOWN = 2;

	final private Context mContext;

	public SpinnerItemAdapter(final Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * Get a View that displays the data at the specified position in the data
	 * set.
	 */
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getView(TYPE_NORMAL, position, convertView, parent);
	}

	/**
	 * Get a View that displays in the drop down popup the data at the specified
	 * position in the data set.
	 */
	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		return getView(TYPE_DROPDOWN, position, convertView, parent);
	}

	private View getView(final int type, final int position, final View convertView, final ViewGroup parent) {
		final SpinnerItem item = (SpinnerItem) getItem(position);
		ListItemView cell = (ListItemView) convertView;

		int currentType = -1;
		if (cell != null) {
			currentType = (Integer) convertView.getTag(R.id.spinner_item_type);
		}

		if (cell == null || cell.getItemClass() != item.getClass() || currentType != type) {
			if (type == TYPE_DROPDOWN) {
				cell = (ListItemView) item.newDropdownView(mContext, null);
			} else {
				cell = (ListItemView) item.newView(mContext, null);
			}
			cell.prepareItemView();
		}
		cell.setObject(item, parent, position);

		final View v = (View) cell;
		v.setTag(R.id.spinner_item_type, type);
		return (View) cell;
	}
}