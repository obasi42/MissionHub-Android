package com.missionhub.ui;

import java.util.ArrayList;

import com.missionhub.R;
import com.missionhub.api.GContact;
import com.missionhub.api.GPerson;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactItemAdapter extends ArrayAdapter<GContact> {
	private ArrayList<GContact> contacts;
	private Activity activity;
	public ImageManager imageManager;

	public ContactItemAdapter(Activity a, int textViewResourceId, ArrayList<GContact> contacts) {
		super(a, textViewResourceId, contacts);
		this.contacts = contacts;
		activity = a;
		
		imageManager = new ImageManager(activity.getApplicationContext());
	}

	public static class ViewHolder{
		public TextView name;
		public TextView status;
		public ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {		
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.contact_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) v.findViewById(R.id.contacts_list_contact_name);
			holder.status = (TextView) v.findViewById(R.id.contacts_list_contact_status);
			holder.image = (ImageView) v.findViewById(R.id.contacts_list_contact_image);
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		final GContact contact = contacts.get(position);
		final GPerson person = contact.getPerson();
		if (contact != null) {
			holder.name.setText(person.getName());
			holder.status.setText(person.getStatus());
			holder.image.setTag(person.getPicture());
			imageManager.displayImage(person.getPicture(), activity, holder.image);
		}
		return v;
	}
}