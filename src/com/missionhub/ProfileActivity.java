package com.missionhub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.flurry.android.FlurryAgent;
import com.google.common.collect.HashMultimap;
import com.missionhub.api.GOrgGeneric;
import com.missionhub.api.GPerson;
import com.missionhub.auth.User;
import com.missionhub.config.Preferences;
import com.missionhub.helpers.Flurry;
import com.missionhub.ui.ImageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	private LinkedList<String> spinnerOrgIds = new LinkedList<String>();
	private LinkedList<String> spinnerNames = new LinkedList<String>();
	private int currentSpinnerIndex;
	private String currentSpinnerOrgID;
	private Spinner spinner;
	private String currentContactPicture = "";
	private ImageView contactPicture;
	private GPerson person;
	private ImageManager imageManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		person = User.getContact().getPerson();
		contactPicture = (ImageView) findViewById(R.id.profile_person_image);

		createSpinnerArrays();
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

		TextView name = (TextView) findViewById(R.id.textView3);
		name.setText(person.getName());
		
		TextView version = (TextView) findViewById(R.id.profile_version);
		try {
			version.setText(getString(R.string.profile_version) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			version.setText("");
		}

		spinner.setAdapter(adapter);
		
		imageManager = new ImageManager(getApplicationContext());
		int defaultImage = R.drawable.facebook_question;
		if (person.getGender() != null) {
			if (person.getGender().equalsIgnoreCase("male")) {
				defaultImage = R.drawable.facebook_male;
			} else if (person.getGender().equalsIgnoreCase("female")) {
				defaultImage = R.drawable.facebook_female;
			}
		}
		if (person.getPicture() != null && !currentContactPicture.equals(person.getPicture())) {
			currentContactPicture = person.getPicture();
			contactPicture.setTag(person.getPicture() + "?type=large");
			imageManager.displayImage(person.getPicture() + "?type=large", ProfileActivity.this, contactPicture, defaultImage);
		}
		clicks = 0;
		
		Flurry.pageView("Profile");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Flurry.startSession(this);
		spinner.setSelection(currentSpinnerIndex);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   Flurry.endSession(this);
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	currentSpinnerOrgID = spinnerOrgIds.get(pos);
	    	if (!currentSpinnerOrgID.equalsIgnoreCase(String.valueOf(User.getOrganizationID()))) {
	    		try {
	    			HashMap<String, String> params = new HashMap<String, String>();
	    			params.put("orgID", spinnerOrgIds.get(pos));
	    			FlurryAgent.onEvent("Profile.ChangeOrg", params);
	    		} catch (Exception e) {}
	  	      Toast.makeText(parent.getContext(), "Your current organization is now " +
	  		          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	  	      Preferences.setOrganizationID(ProfileActivity.this, Integer.parseInt(currentSpinnerOrgID));
	  	      User.setOrganizationID(Integer.parseInt(currentSpinnerOrgID));
	    	}
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	      // Do nothing.
	    }
	}
	
	public void clickLogout(View view) {
		Intent i = new Intent();
	       i.putExtra("logout", true);
	       this.setResult(RESULT_OK, i);
	       finish();
	}
	
	private void createSpinnerArrays() {
		
		final HashMultimap<Integer, String> roles = User.getRoles();
		final HashMap<Integer, GOrgGeneric> organizations = User.getOrganizations();
		
		Iterator<Integer> it = roles.keySet().iterator();

		int count = 0; 
		while (it.hasNext()) {
			int key = it.next();
			Set<String> orgRoles = roles.get(key);
			if (orgRoles.contains("admin") || orgRoles.contains("leader")) {
				spinnerOrgIds.add(String.valueOf(key));
				spinnerNames.add(organizations.get(key).getName());
				if (key == User.getOrganizationID()) {
					currentSpinnerOrgID = String.valueOf(key);
					currentSpinnerIndex = count;
				}
				count++;
			}
		}
	}
	
	private int clicks = 0;
	public void clickVersion(View v) {
		clicks++;
		if (clicks == 20) {
			clicks = 0;
			Intent i = new Intent(this.getApplicationContext(), EggActivity.class);
			startActivity(i);
			finish();
		}
	}
}	