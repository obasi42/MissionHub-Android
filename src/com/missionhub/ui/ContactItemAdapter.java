package com.missionhub.ui;

import java.util.ArrayList;

import com.missionhub.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactItemAdapter extends ArrayAdapter<Person> {
	private ArrayList<Person> people;
	private Activity activity;
	public ImageManager imageManager;

	public ContactItemAdapter(Activity a, int textViewResourceId, ArrayList<Person> people) {
		super(a, textViewResourceId, people);
		this.people = people;
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

		final Person person = people.get(position);
		if (person != null) {
			holder.name.setText(person.name);
			holder.status.setText(person.status);
			holder.image.setTag(person.image);
			imageManager.displayImage(person.image, activity, holder.image);
		}
		return v;
	}
}