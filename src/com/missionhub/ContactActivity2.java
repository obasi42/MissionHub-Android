package com.missionhub;

import com.missionhub.api.ApiNotifierHandler;
import com.missionhub.api.Contacts;
import com.missionhub.api.FollowupComments;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.model.sql.Person;
import com.missionhub.helper.TakePhotoHelper;
import com.missionhub.ui.widget.ContactAboutTab;
import com.missionhub.ui.widget.ContactStatusTab;
import com.missionhub.ui.widget.ContactSurveysTab;
import com.missionhub.ui.widget.Tab;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;

public class ContactActivity2 extends Activity {

	public static final String TAG = ContactActivity2.class.getSimpleName();

	private int personId = -1;
	private Person person;

	private TabHost mTabHost;
	ActionBarItem pictureActionBarItem;

	private ContactAboutTab aboutTab;
	private ContactStatusTab statusTab;
	private ContactSurveysTab surveysTab;

	private static final String TAG_ABOUT = "about";
	private static final String TAG_STATUS = "status";
	private static final String TAG_SURVEYS = "surveys";

	private static final String PROGRESS_TAKE_PHOTO = "TAKE_PHOTO";

	private static final int RESULT_TAKE_PHOTO = 0;

	private String contactTag = this.toString() + "person";
	private String commentTag = this.toString() + "comment";

	private String tabTag = TAG_STATUS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		personId = getIntent().getIntExtra("personId", -1);
		if (personId < 0) {
			Toast.makeText(getApplicationContext(), R.string.contact_invalid_person, Toast.LENGTH_LONG).show();
			finish();
		}

		setActionBarContentView(R.layout.activity_contact);
		getActionBar().setType(ActionBar.Type.Dashboard);

		// pictureActionBarItem = addActionBarItem(ActionBarItem.Type.TakePhoto,
		// R.id.action_bar_take_photo);
		// getActionBar().removeItem(pictureActionBarItem);
		addActionBarItem(ActionBarItem.Type.Export, R.id.action_bar_export);
		addActionBarItem(ActionBarItem.Type.Edit, R.id.action_bar_edit);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		aboutTab = (ContactAboutTab) findViewById(R.id.tab_contact_about);
		statusTab = (ContactStatusTab) findViewById(R.id.tab_contact_status);
		surveysTab = (ContactSurveysTab) findViewById(R.id.tab_contact_surveys);

		setupTab(R.id.tab_contact_about, TAG_ABOUT, R.string.contact_tab_about, R.drawable.gd_action_bar_info);
		setupTab(R.id.tab_contact_status, TAG_STATUS, R.string.contact_tab_status, R.drawable.gd_action_bar_list);
		setupTab(R.id.tab_contact_surveys, TAG_SURVEYS, R.string.contact_tab_surveys, R.drawable.gd_action_bar_slideshow);

		restoreState(savedInstanceState);
		
		tabTag = getIntent().getStringExtra("tab");
		if (tabTag == null) tabTag = TAG_STATUS;
		
		mTabHost.setCurrentTabByTag(tabTag);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabTag = tabId;
				ContactActivity2.this.getIntent().putExtra("tab", tabTag);
			}
		});

		getApiNotifier().subscribe(this, personListener, Type.JSON_CONTACTS_ON_START, Type.JSON_CONTACTS_ON_FINISH, Type.JSON_CONTACTS_ON_FAILURE, 
				Type.JSON_FOLLOWUP_COMMENTS_ON_START, Type.JSON_FOLLOWUP_COMMENTS_ON_FINISH, Type.JSON_FOLLOWUP_COMMENTS_ON_FAILURE,
				Type.UPDATE_FOLLOWUP_COMMENTS, Type.UPDATE_PERSON, Type.UPDATE_QUESTION, Type.UPDATE_KEYWORD);

		person = getApp().getDbSession().getPersonDao().load(personId);

		updateTabPerson();

		update(false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("tabTag", tabTag);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		restoreState(savedInstanceState);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_take_photo:
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(TakePhotoHelper.getTempFile(this)));
			startActivityForResult(intent, RESULT_TAKE_PHOTO);
			showProgress(PROGRESS_TAKE_PHOTO);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_TAKE_PHOTO:
				TakePhotoHelper.onActivityResult(this, requestCode, data);
				break;
			}
		}

		if (requestCode == RESULT_TAKE_PHOTO)
			hideProgress(PROGRESS_TAKE_PHOTO);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.contact_activity_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh:
	        update(true);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabTag = savedInstanceState.getString("tabTag");
		}
	}

	private Handler personListener = new ApiNotifierHandler(contactTag, commentTag) {
		@Override
		public void handleMessage(Type type, String tag, Bundle bundle, Throwable t, long rowId) {
			switch (type) {
			case JSON_CONTACTS_ON_START:
				
				break;
			case JSON_CONTACTS_ON_FINISH:
				hideProgress(contactTag);
				break;
			case JSON_CONTACTS_ON_FAILURE:
				Log.e("THROWABLE", "THROWABLE", t);
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_START:
				showProgress(commentTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_FINISH:
				hideProgress(commentTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_FAILURE:
				Log.e("THROWABLE", "THROWABLE", t);
				break;
			case UPDATE_FOLLOWUP_COMMENTS:
				Log.w("UPDATE_FOLLOWUP_COMMENTS", tag);
				break;
			case UPDATE_PERSON:
				if (personId == rowId) {
					person = getApp().getDbSession().getPersonDao().load(personId);
					updateTabPerson();
					update(false);
				}
				break;
			case UPDATE_QUESTION:

				break;
			case UPDATE_KEYWORD:

				break;
			}
		}
	};

	private void setupTab(final int contentId, final String tag, int labelId, int iconId) {
		final Tab tab = Tab.inflate(mTabHost.getContext(), null, labelId, iconId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tab).setContent(contentId);
		mTabHost.addTab(setContent);
	}

	public void updateTabPerson() {
		aboutTab.setPerson(person);
		statusTab.setPerson(person);
		surveysTab.setPerson(person);
	}

	private void update(boolean force) {
		updateContact(force);
		surveysTab.update();
	}

	private void updateContact(boolean force) {
		if (force || person.getRetrieved() == null || (person.getRetrieved().getTime() + 1000 * 60 * 5) < System.currentTimeMillis()) {
			Contacts.get(this, personId, contactTag);
			FollowupComments.get(this, personId, commentTag);
			aboutTab.update(true);
			showProgress(contactTag);
		} else {
			aboutTab.update(false);
		}
	}

}