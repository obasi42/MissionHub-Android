package com.missionhub;

import com.missionhub.helpers.Flurry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ContactsFilterActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private LinearLayout rootView;
    private ListView preferenceView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        Application.restoreApplicationState(savedInstanceState);
        
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName("contact_filters");
        prefMgr.setSharedPreferencesMode(MODE_PRIVATE);

        rootView = new LinearLayout(this);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        rootView.setOrientation(LinearLayout.VERTICAL);
        
        RelativeLayout titleView = (RelativeLayout) View.inflate(this, R.layout.topbar, null);
        
        preferenceView = new ListView(this);
        preferenceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        preferenceView.setId(android.R.id.list);
        
        PreferenceScreen screen = createPreferenceHierarchy();
        screen.bind(preferenceView);
        preferenceView.setAdapter(screen.getRootAdapter());
        
        rootView.addView(titleView, new LayoutParams(LayoutParams.FILL_PARENT, 40));
        rootView.addView(preferenceView);
        
        this.setContentView(rootView);
        setPreferenceScreen(screen);
        
        Flurry.pageView("ContactsFilter");
    }

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
        PreferenceCategory predefined = new PreferenceCategory(this);
        predefined.setTitle(R.string.contacts_filter_predefined);
        root.addPreference(predefined);
        
        // Gender
        ListPreference genderPref = new ListPreference(this);
        genderPref.setKey("gender");
        genderPref.setTitle(R.string.contacts_filter_pref_gender);
        genderPref.setDialogTitle(R.string.contacts_filter_pref_gender);
        genderPref.setEntries(R.array.prefs_gender);
        genderPref.setEntryValues(R.array.prefs_gender_values);
        genderPref.setDefaultValue("both");
        predefined.addPreference(genderPref);
        
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
	public void onSaveInstanceState(Bundle b) {
		b.putAll(Application.saveApplicationState(b));
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		Application.restoreApplicationState(b);
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   Flurry.startSession(this);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   Flurry.endSession(this);
	}

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	this.setResult(ContactsActivity.RESULT_CHANGED);
    }
}