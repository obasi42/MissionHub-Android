package com.missionhub.ui;

import java.util.ArrayList;

import com.missionhub.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class RejoicableAdapter extends ArrayAdapter<Rejoicable> {
	private ArrayList<Rejoicable> rejoicables;
	private Activity activity;

	public RejoicableAdapter(Activity a, int textViewResourceId, ArrayList<Rejoicable> rejoicables) {
		super(a, textViewResourceId, rejoicables);
		this.rejoicables = rejoicables;
		activity = a;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {		
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = (CheckedTextView) vi.inflate(R.layout.rejoiceable_item, null);
		}

		final Rejoicable rejoicable = rejoicables.get(position);
		if (rejoicable != null) {
			v.setTag(rejoicable.name);
			((CheckedTextView) v).setCheckMarkDrawable(rejoicable.drawable);
			((CheckedTextView) v).setText(rejoicable.name);
		}
		return v;
	}
}