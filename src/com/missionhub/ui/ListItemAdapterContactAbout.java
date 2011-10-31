package com.missionhub.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapterContactAbout  extends ArrayAdapter<ListItemContactAbout>{
	private ArrayList<ListItemContactAbout> items;
	//private Context context;

	public ListItemAdapterContactAbout(Context context, ArrayList<ListItemContactAbout> items) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.items = items;
		//this.context = context;
	}

	public static class ViewHolder{
		public TextView header;
		public TextView info;
		public ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {		
//			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			v = vi.inflate(R.layout.list_item_contact_about, null);
			holder = new ViewHolder();
//			holder.header = (TextView) v.findViewById(R.id.header);
//			holder.info = (TextView) v.findViewById(R.id.info);
//			holder.icon = (ImageView) v.findViewById(R.id.icon);
//			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		final ListItemContactAbout item = items.get(position);
		if (item != null) {
			if (item.header != null) {
				holder.header.setText(item.header);
				holder.header.setVisibility(View.VISIBLE);
			} else{
				holder.header.setVisibility(View.GONE);
			}
			
			if (item.info != null) {
				holder.info.setText(item.info);
				holder.info.setVisibility(View.VISIBLE);
			} else{
				holder.info.setVisibility(View.GONE);
			}
			
			if (item.icon != null) {
				holder.icon.setImageDrawable(item.icon);
				holder.icon.setVisibility(View.VISIBLE);
			} else {
				holder.icon.setVisibility(View.GONE);
			}
			
			if (item.action != null) {
				v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						item.action.perform();
					}
				});
			} else {
				v.setOnClickListener(null);
			}
		}		
		return v;
	}
}
