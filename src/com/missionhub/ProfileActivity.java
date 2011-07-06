package com.missionhub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import com.missionhub.api.User;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	private LinkedList<String> spinnerOrgIds = new LinkedList<String>();
	private LinkedList<String> spinnerNames = new LinkedList<String>();
	private int currentSpinnerIndex;
	private String currentSpinnerOrgID;
	private String currentSpinnerName;
	private Spinner spinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		createSpinnerArrays();
		Log.i("ARG", String.valueOf(spinnerOrgIds.size()));
		Log.i("ARG", String.valueOf(spinnerNames.size()));
		Log.i("ARG", String.valueOf(currentSpinnerIndex));
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww


		try {
		    spinner.setAdapter(adapter);
		} catch (Exception e) {
			Log.i("ARG", "ARGGGG", e);
		}

	}
	
	@Override
	public void onStart() {
		super.onStart();
		spinner.setSelection(currentSpinnerIndex);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	if (!spinnerOrgIds.get(pos).equalsIgnoreCase(User.orgID)) {
	  	      Toast.makeText(parent.getContext(), "Your current organization is now " +
	  		          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	    	}
	    	currentSpinnerOrgID = spinnerOrgIds.get(pos);
	    	currentSpinnerName = spinnerNames.get(pos);
	    	User.orgID = currentSpinnerOrgID;
	    	Log.i("ARG", String.valueOf(pos));
	    }

	    public void onNothingSelected(AdapterView parent) {
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
		User.orgID = "56";
		Iterator<Integer> it = User.validRoles.keySet().iterator();
		int count = 0; 
		while (it.hasNext()) {
			Log.i("ARG", "HIIII");
			int key = it.next();
			HashMap<String, String> role = User.validRoles.get(key);
			Log.i("ARG", role.get("name") + " " + String.valueOf(count));
			
			spinnerOrgIds.add(role.get("org_id"));
			spinnerNames.add(role.get("name"));
			if (role.get("org_id").equalsIgnoreCase(User.orgID)) {
				Log.i("ARG", "EQUALITY!!");
				currentSpinnerOrgID = role.get("org_id");
				currentSpinnerName = role.get("name");
				currentSpinnerIndex = count;
			}
			count++;
		}
	}
}	