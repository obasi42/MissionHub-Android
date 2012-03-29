package com.missionhub.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.missionhub.R;

public class ContactListFragment extends MissionHubFragment {
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}

	public void sayHello() {
		Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
	}
	
}