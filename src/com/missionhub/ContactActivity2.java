package com.missionhub;

import com.missionhub.auth.User;
import com.missionhub.ui.ContactHeaderFragment;
import com.missionhub.ui.widget.Tab;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem.Type;

public class ContactActivity2 extends GDActivity {

	private TabHost mTabHost;
	private ListView mAboutListView;
	private ListView mStatusListView;
	private ListView mSurveysListView;
	
	private ContactHeaderFragment contactHeader;
	
	private static final String TAG_ABOUT = "about";
    private static final String TAG_STATUS = "status";
    private static final String TAG_SURVEYS = "surveys";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActionBarContentView(R.layout.activity_contact);
		getActionBar().setType(ActionBar.Type.Dashboard);

		setProgressVisible(true);
		
		addActionBarItem(Type.TakePhoto, R.id.action_bar_refresh);
		addActionBarItem(Type.Export, R.id.action_bar_refresh);
		addActionBarItem(Type.Edit, R.id.action_bar_refresh);
		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		mAboutListView = (ListView) findViewById(R.id.tab_contact_about);
		mStatusListView = (ListView) findViewById(R.id.tab_contact_status);
		mSurveysListView = (ListView) findViewById(R.id.tab_contact_surveys);
		
		FragmentManager fm = getSupportFragmentManager();
		contactHeader = (ContactHeaderFragment) fm.findFragmentById(R.id.fragment_contact_header);
		contactHeader.setContact(User.getContact());

		setupTab(R.id.tab_contact_about, TAG_ABOUT, R.string.contact_tab_about, R.drawable.gd_action_bar_info);
		setupTab(R.id.tab_contact_status, TAG_STATUS, R.string.contact_tab_status, R.drawable.gd_action_bar_list);
		setupTab(R.id.tab_contact_surveys, TAG_SURVEYS, R.string.contact_tab_surveys, R.drawable.gd_action_bar_slideshow);
		
		mTabHost.setCurrentTabByTag(TAG_STATUS);
		
		setupAboutList();
		setupStatusList();
		setupSurveysList();
		
		update();
	}

	private void setupTab(final int contentId, final String tag, int labelId, int iconId) {
		final Tab tab = Tab.inflate(mTabHost.getContext(), null, labelId, iconId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tab).setContent(contentId);
		mTabHost.addTab(setContent);
	}
	
	private void update() {
		updateContact();
		updateStatusList();
	}
	
	private void updateContact() {
		
	}
	
	private void setupAboutList() {
		updateAboutList();
		updateSurveysList();
	}
	
	private void setupStatusList() {
		
	}
	
	private void setupSurveysList() {
		
	}
	
	private void updateAboutList() {
		
	}
	
	private void updateStatusList() {
		
	}
	
	private void updateSurveysList() {
		
	}
}