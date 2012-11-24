package com.missionhub.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.missionhub.R;

public class NavigationSpinnerAdapter extends ObjectArrayAdapter {

	private final int mLayoutResource = R.layout.item_simple_navigation;

	public NavigationSpinnerAdapter(final Context context) {
		super(context);
	}

	public NavigationSpinnerAdapter(final Context context, final int... items) {
		super(context);
		for (final int item : items) {
			add(new NavigationItem(context.getString(item)));
		}
	}

	public NavigationSpinnerAdapter(final Context context, final String... items) {
		super(context);
		for (final String item : items) {
			add(new NavigationItem(item));
		}
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view = getDropDownView(position, convertView, parent);
		view.setPadding(0, 0, 0, 0);
		return view;
	}

	private static class ViewHolder {
		TextView text;
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		final NavigationItem item = (NavigationItem) getItem(position);
		View view = convertView;

		ViewHolder holder = null;
		if (view == null) {
			final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			view = inflater.inflate(mLayoutResource, null);
			holder.text = (TextView) view.findViewById(R.id.text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.text.setText(item.text);

		return view;
	}

	public static class NavigationItem {
		public String text;

		public NavigationItem(final String text) {
			this.text = text;
		}
	}
}