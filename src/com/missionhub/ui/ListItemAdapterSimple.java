package com.missionhub.ui;

import java.util.ArrayList;

import com.missionhub.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListItemAdapterSimple  extends ArrayAdapter<ListItemSimple>{
	private ArrayList<ListItemSimple> items;
	private Activity activity;

	public ListItemAdapterSimple(Activity a, int textViewResourceId, ArrayList<ListItemSimple> items) {
		super(a, textViewResourceId, items);
		this.items = items;
		activity = a;
	}
	

	public static class ViewHolder{
		public TextView header;
		public TextView info;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {		
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_simple, null);
			holder = new ViewHolder();
			holder.header = (TextView) v.findViewById(R.id.contact_info_header);
			holder.info = (TextView) v.findViewById(R.id.contact_info_info);
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		final ListItemSimple item = items.get(position);
		if (item != null) {
			holder.header.setText(item.header);
			String info = item.info;
			if (info == null || info.equals("")) {
				if (item.data != null && item.data.containsKey("default")) {
					info = item.data.get("default");
					holder.info.setTextColor(R.color.medium_gray);
				}
			}
			holder.info.setText(info);
		}
		return v;
	}
}
