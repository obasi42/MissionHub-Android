package com.missionhub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.flurry.android.FlurryAgent;
import com.missionhub.api.GPerson;
import com.missionhub.api.User;
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
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww

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
		
		User.setFlurryUser();
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Profile");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		User.setFlurryUser();
		FlurryAgent.onStartSession(this, Config.flurryKey);
		spinner.setSelection(currentSpinnerIndex);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   User.setFlurryUser();
	   FlurryAgent.onEndSession(this);
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	if (!spinnerOrgIds.get(pos).equalsIgnoreCase(User.getOrgID())) {
	    		try {
	    			HashMap<String, String> params = new HashMap<String, String>();
	    			params.put("orgID", spinnerOrgIds.get(pos));
	    			FlurryAgent.onEvent("Profile.ChangeOrg", params);
	    		} catch (Exception e) {}
	  	      Toast.makeText(parent.getContext(), "Your current organization is now " +
	  		          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	    	}
	    	currentSpinnerOrgID = spinnerOrgIds.get(pos);
	    	User.setOrgIDPreference(currentSpinnerOrgID, ProfileActivity.this);
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
		Iterator<Integer> it = User.getValidRoles().keySet().iterator();
		int count = 0; 
		while (it.hasNext()) {
			int key = it.next();
			HashMap<String, String> role = User.getValidRoles().get(key);
			spinnerOrgIds.add(role.get("org_id"));
			spinnerNames.add(role.get("name"));
			if (role.get("org_id").equalsIgnoreCase(User.getOrgID())) {
				currentSpinnerOrgID = role.get("org_id");
				currentSpinnerIndex = count;
			}
			count++;
		}
	}
	
	private int clicks = 0;
	public void clickVersion(View v) {
		clicks++;
		if (clicks == 20) {
			clicks = 0;
			Intent i = new Intent(this.getApplicationContext(), MHEgg.class);
			startActivity(i);
			finish();
		}
	}
}	