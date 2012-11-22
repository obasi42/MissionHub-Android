package com.missionhub.ui;

import com.missionhub.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NavigationSpinnerAdapter extends ObjectArrayAdapter {

	private int mLayoutResource = R.layout.item_simple_navigation;
	
	public NavigationSpinnerAdapter(Context context) {
		super(context);
	}
	
	public NavigationSpinnerAdapter(Context context, int ... items) {
		super(context);
		for(int item : items) {
			add(new NavigationItem(context.getString(item)));
		}
	}
	
	public NavigationSpinnerAdapter(Context context, String ... items) {
		super(context);
		for(String item : items) {
			add(new NavigationItem(item));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = getDropDownView(position, convertView, parent);
		view.setPadding(0, 0, 0, 0);
		return view;
	}
	
	private static class ViewHolder {
		TextView text;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
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
		public NavigationItem(String text) {
			this.text = text;
		}
	}
}