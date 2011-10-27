package com.missionhub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.missionhub.helpers.Flurry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFilterActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private LinearLayout rootView;
    private ListView preferenceView;
    
    public static final String TYPE_MY_CONTACTS = "my_contacts_filter";
    public static final String TYPE_ALL_CONTACTS = "all_contacts_filter";
    private String type = TYPE_MY_CONTACTS;
    
    public static final String NOT_FILTERED = "__not_filtered__";
    public static String notFilteredText = "Not Filtered";
    
    private HashMap<String, Preference> preferences = new HashMap<String, Preference>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        type = getIntent().getStringExtra("TYPE");
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(type);
        prefMgr.setSharedPreferencesMode(MODE_PRIVATE);
        
        sharedPreferences = prefMgr.getSharedPreferences();
        
        rootView = new LinearLayout(this);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        rootView.setOrientation(LinearLayout.VERTICAL);
        
        RelativeLayout titleView = (RelativeLayout) View.inflate(this, R.layout.topbar, null);
        notFilteredText = getString(R.string.contacts_filter_not_filtered);
        
        TextView title = (TextView) titleView.findViewById(R.id.title);
        if (type.equalsIgnoreCase(TYPE_MY_CONTACTS)) {
        	title.setText(R.string.contacts_filter_my);
        } else if (type.equalsIgnoreCase(TYPE_ALL_CONTACTS)) {
        	title.setText(R.string.contacts_filter_all);
        }
        
        preferenceView = new ListView(this);
        preferenceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        preferenceView.setId(android.R.id.list);
        
        PreferenceScreen screen = createPreferenceHierarchy();
        screen.bind(preferenceView);
        preferenceView.setAdapter(screen.getRootAdapter());
        
        rootView.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, 40)); // 40 - height of topbar
        rootView.addView(preferenceView);
        
        this.setContentView(rootView);
        setPreferenceScreen(screen);
        
        Flurry.pageView(this, "ContactsFilter");
    }
    
    private PreferenceScreen createPreferenceHierarchy() {
    	
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        // Category - Predefined
        PreferenceCategory predefined = new PreferenceCategory(this);
        predefined.setTitle(R.string.contacts_filter_predefined);
        root.addPreference(predefined);

    	// Status
    	ListPreference status = new ListPreference(this);
        status.setKey("status");
        status.setTitle("Status");
        status.setDialogTitle("Status");
        CharSequence[] satusEntries = {notFilteredText, "*Not Finished", "Finished (Completed/Do Not Contact)", "Uncontacted", "Attempted Contact", "Contacted", "Do Not Contact", "Completed"};
        status.setEntries(satusEntries);
        CharSequence[] statusEntryValues = {NOT_FILTERED, "not_finished", "finished", "uncontacted", "attempted_contact", "contacted", "do_not_contact", "completed"};
        status.setEntryValues(statusEntryValues);
        status.setDefaultValue("not_finished");
        
        preferences.put("status", status);
        predefined.addPreference(preferences.get("status"));
        
        // Assigned To
        if (!type.equalsIgnoreCase(TYPE_MY_CONTACTS)) {
	    	ListPreference assignedTo = new ListPreference(this);
	    	assignedTo.setKey("assigned_to");
	    	assignedTo.setTitle("Assigned To");
	    	assignedTo.setDialogTitle("Assigned To");
	    	CharSequence[] assignedToEntries = {'*'+notFilteredText, "No One", "Me"};
	        assignedTo.setEntries(assignedToEntries);
	        CharSequence[] assignedToEntryValues = {NOT_FILTERED, "no_one", "me"};
	        assignedTo.setEntryValues(assignedToEntryValues);
	        assignedTo.setDefaultValue(NOT_FILTERED);
	        
	        preferences.put("assigned_to", assignedTo);
	        predefined.addPreference(preferences.get("assigned_to"));
        }
        
        // Gender
    	ListPreference gender = new ListPreference(this);
    	gender.setKey("gender");
    	gender.setTitle("Gender");
    	gender.setDialogTitle("Gender");
        CharSequence[] genderEntries = {'*'+notFilteredText, "Male", "Female"};
        gender.setEntries(genderEntries);
        CharSequence[] genderEntryValues = {NOT_FILTERED, "male", "female"};
        gender.setEntryValues(genderEntryValues);
        gender.setDefaultValue(NOT_FILTERED);
        
        preferences.put("gender", gender);
        predefined.addPreference(preferences.get("gender"));
        
        // Initialize Values
        Map<String, ?> initialPrefs = sharedPreferences.getAll();
        Iterator<String> itr = preferences.keySet().iterator();
        while (itr.hasNext()) {
        	Preference pref = preferences.get(itr.next());
        	setSummary(pref, initialPrefs);
        }
        
        return root;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.contacts_filter, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.clear:
	    	sharedPreferences.edit().clear().commit();
	    	this.setResult(ContactsActivity.RESULT_CHANGED);
	    	Toast.makeText(getApplicationContext(), "Filters Cleared", Toast.LENGTH_SHORT).show();
	    	finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	setSummary(preferences.get(key), sharedPreferences.getAll());
    	this.setResult(ContactsActivity.RESULT_CHANGED);
    }
    
    public void setSummary(Preference pref, Map<String, ?> values) {
    	if (!values.containsKey(pref.getKey())) {
    		if (pref.getKey().equalsIgnoreCase("status")) {
    			pref.setSummary('*'+"Not Finished");
    		} else {
    			pref.setSummary('*'+notFilteredText);
    		}
    		return;
    	}
    	
    	if (pref instanceof CheckBoxPreference) {
    		//TODO: Implement
    	} else if (pref instanceof EditTextPreference) {
    		pref.setSummary(values.get(pref.getKey()).toString());
    	} else if (pref instanceof ListPreference) {
    		pref.setSummary(((ListPreference) pref).getEntry());
    	}
    	//TODO: Add Custom MultiSelect
    }
}