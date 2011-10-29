package com.missionhub;

import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.flurry.android.FlurryAgent;
import com.google.common.collect.HashMultimap;
import com.missionhub.config.Preferences;
import com.missionhub.helpers.Flurry;
import com.missionhub.model.sql.OrganizationalRole;
import com.missionhub.ui.ImageManager;

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

	private LinkedList<String> spinnerOrgIDs = new LinkedList<String>();
	private LinkedList<String> spinnerNames = new LinkedList<String>();
	private int currentSpinnerIndex;
	private String currentSpinnerOrgID;
	private Spinner spinner;
	private ImageView profilePicture;
	private String currentProfilePicture = "";
	private ImageManager imageManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		setTitle(R.string.profile_header);
		setActionBarContentView(R.layout.profile);
		
		addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(R.drawable.action_bar_logout)
                .setContentDescription(R.string.action_bar_logout), R.id.action_bar_logout);

		/* Spinner */
		createSpinnerArrays();
		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		/* Name */
		TextView name = (TextView) findViewById(R.id.name);
		name.setText(getUser().getPerson().getName());
		
		/* Version */
		TextView version = (TextView) findViewById(R.id.version);
		try {
			version.setText(getString(R.string.profile_version) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (Exception e) {
			version.setText("");
		}
		
		/* Profile Picture */
		profilePicture = (ImageView) findViewById(R.id.profile_picture);
		imageManager = new ImageManager(getApplicationContext());
		int defaultImage = R.drawable.facebook_question;
		if (getUser().getPerson().getGender() != null) {
			if (getUser().getPerson().getGender().equalsIgnoreCase("male")) {
				defaultImage = R.drawable.facebook_male;
			} else if (getUser().getPerson().getGender().equalsIgnoreCase("female")) {
				defaultImage = R.drawable.facebook_female;
			}
		}
		if (getUser().getPerson().getPicture() != null && !currentProfilePicture.equals(getUser().getPerson().getPicture())) {
			currentProfilePicture = getUser().getPerson().getPicture();
			profilePicture.setTag(getUser().getPerson().getPicture() + "?type=large");
			imageManager.displayImage(getUser().getPerson().getPicture() + "?type=large", profilePicture, defaultImage);
		}
		
		/* Egg Count */
		clicks = 0;
		
		Flurry.pageView(this, "Profile");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		spinner.setSelection(currentSpinnerIndex);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	currentSpinnerOrgID = spinnerOrgIDs.get(pos);
	    	if (!currentSpinnerOrgID.equalsIgnoreCase(String.valueOf(getUser().getOrganizationID()))) {
	    		try {
	    			HashMap<String, String> params = new HashMap<String, String>();
	    			params.put("orgID", spinnerOrgIDs.get(pos));
	    			FlurryAgent.onEvent("Profile.ChangeOrg", params);
	    		} catch (Exception e) {}
	  	      Toast.makeText(parent.getContext(), "Your current organization is now " +
	  		          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	  	      Preferences.setOrganizationID(ProfileActivity.this, Integer.parseInt(currentSpinnerOrgID));
	  	      getUser().setOrganizationID(Integer.parseInt(currentSpinnerOrgID));
	    	}
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	      // Do nothing.
	    }
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_logout:
            	Intent i = new Intent();
        		i.putExtra("logout", true);
        		this.setResult(RESULT_OK, i);
        		finish();
                break;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
        return true;
    }
	
	private void createSpinnerArrays() {
		
		final HashMultimap<Integer, String> roles = getUser().getRoles();
		final HashMap<Integer, OrganizationalRole> organizations = getUser().getOrganizations();
		
		Iterator<Integer> it = roles.keySet().iterator();

		int count = 0; 
		while (it.hasNext()) {
			int key = it.next();
			Set<String> orgRoles = roles.get(key);
			if (orgRoles.contains("admin") || orgRoles.contains("leader")) {
				spinnerOrgIDs.add(String.valueOf(key));
				spinnerNames.add(organizations.get(key).getName());
				if (key == getUser().getOrganizationID()) {
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