package com.missionhub;

import com.missionhub.api.ApiNotifierHandler;
import com.missionhub.api.Contacts;
import com.missionhub.api.FollowupComments;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.model.sql.Person;
import com.missionhub.helper.TakePhotoHelper;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.widget.ContactAboutTab;
import com.missionhub.ui.widget.ContactStatusTab;
import com.missionhub.ui.widget.ContactSurveysTab;
import com.missionhub.ui.widget.Tab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
	private static final int RESULT_EDIT = 1;

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
				Type.UPDATE_FOLLOWUP_COMMENTS, Type.UPDATE_PERSON);

		person = getApp().getDbSession().getPersonDao().load(personId);

		updateTabPerson();

		updateContact(false);
		updateStatus(false);
		updateStatus(true);
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
		case R.id.action_bar_edit:
			Intent i = new Intent(this, ContactPostActivity.class);
			i.putExtra("personId", personId);
			i.putExtra("status", person.getStatus());
			this.startActivityForResult(i, RESULT_EDIT);
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
			case RESULT_EDIT:
				person = getApp().getDbSession().getPersonDao().load(personId);
				statusTab.setUpdating();
				updateStatus(true);
				mTabHost.setCurrentTabByTag(TAG_STATUS);
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
	    	updateContact(true);
			updateStatus(true);
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
				showProgress(contactTag);
				break;
			case JSON_CONTACTS_ON_FINISH:
				hideProgress(contactTag);
				break;
			case JSON_CONTACTS_ON_FAILURE:
				DisplayError.displayWithRetry(ContactActivity2.this, t, new DisplayError.Retry() {
					@Override
					public void run() {
						updateContact(true);
					}
				}).show();
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_START:
				showProgress(commentTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_FINISH:
				hideProgress(commentTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_ON_FAILURE:
				AlertDialog ad = DisplayError.displayWithRetry(ContactActivity2.this, t, new DisplayError.Retry() {
					@Override
					public void run() {
						updateStatus(true);
					}
				});
				ad.setOnCancelListener(new OnCancelListener(){
					@Override
					public void onCancel(DialogInterface arg0) {
						statusTab.update(false);
					}
				});
				ad.show();
				break;
			case UPDATE_FOLLOWUP_COMMENTS:
				statusTab.update(false);
				break;
			case UPDATE_PERSON:
				if (personId == rowId) {
					person = getApp().getDbSession().getPersonDao().load(personId);
					updateTabPerson();
					updateContact(false);
					surveysTab.update();
				}
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

	public void updateContact(boolean force) {		
		if (force || person == null || person.getRetrieved() == null || (person.getRetrieved().getTime() + 1000 * 60 * 5) < System.currentTimeMillis()) {
			Contacts.get(this, personId, contactTag);
			if (person != null || person.getRetrieved() != null) {
				aboutTab.update(false);
			} else {
				aboutTab.update(true);
			}
		} else {			
			aboutTab.update(false);
		}
		surveysTab.update();
	}
	
	public void updateStatus(boolean reload) {
		if (reload) {
			FollowupComments.get(this, personId, commentTag);
		} else {
			statusTab.update(true);
		}
	}
}