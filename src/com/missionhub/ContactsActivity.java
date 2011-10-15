package com.missionhub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.missionhub.api.ApiClient;
import com.missionhub.api.ApiResponseHandler;
import com.missionhub.api.client.Contacts;
import com.missionhub.api.client.Roles;
import com.missionhub.api.json.GContact;
import com.missionhub.api.json.GError;
import com.missionhub.api.json.GOrgGeneric;
import com.missionhub.auth.User;
import com.missionhub.error.MHException;
import com.missionhub.helpers.Flurry;
import com.missionhub.helpers.U;
import com.missionhub.ui.ContactItemAdapter;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ContactsActivity extends Activity {

	public static final String TAG = ContactsActivity.class.getName();

	public final static int RESULT_CONTACTS_FILTER_ACTIVITY = 0;
	public final static int RESULT_CHANGED = 1000;
	
	private final int TAB_MY = 0;
	private final int TAB_ALL = 1;
	private int tab = TAB_MY; // Tab state
	
	private ListView contactsList;
	private ContactItemAdapter adapter;
	private ProgressBar progress;
	private TextView txtNoData;
	private TextView txtTitle;
	private EditText search;
	private ToggleButton bottom_button_left;
	private ToggleButton bottom_button_right;
	private Button btnFilter;

	private ArrayList<GContact> data = new ArrayList<GContact>();
	
	Contacts.Options contactsOptions = new Contacts.Options();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		
		Application.restoreApplicationState(savedInstanceState);

		contactsList = (ListView) findViewById(R.id.contacts_list);
		adapter = new ContactItemAdapter(this, R.layout.contact_list_item, data);
		contactsList.setAdapter(adapter);
		contactsList.setOnScrollListener(new ContactsScrollListener());
		contactsList.setOnItemClickListener(new ContactsOnItemClickListener());
		contactsList.setOnItemLongClickListener(new ContactsOnItemLongClickListener());

		progress = (ProgressBar) findViewById(R.id.contacts_progress);
		txtNoData = (TextView) findViewById(R.id.txt_contacts_no_data);
		txtTitle = (TextView) findViewById(R.id.txt_contacts_title);
		search = (EditText) findViewById(R.id.contacts_search);

		bottom_button_left = (ToggleButton) findViewById(R.id.bottom_button_left);
		bottom_button_right = (ToggleButton) findViewById(R.id.bottom_button_right);
		
		btnFilter = (Button) findViewById(R.id.btn_filter);
		
		search.addTextChangedListener(new ContactsSearchWatcher());

		// Large Screens
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (metrics.heightPixels > 480) {
			contactsOptions.setLimit(30);
		}

		setTab(TAB_MY, true);
		
		Flurry.pageView("Contacts");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contacts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.contacts_refresh:
			try {
				FlurryAgent.onEvent("Contacts.Refresh");
			} catch (Exception e) {}
			resetListView(false);
			getMore();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CONTACTS_FILTER_ACTIVITY && resultCode == RESULT_CHANGED) {
			try {
				FlurryAgent.onEvent("Contacts.Refresh");
			} catch (Exception e) {}
			resetListView(false);
			getMore();
		}
	}
	
	private void updateFilterIcon() {
		if (contactsOptions.getFilters().isEmpty())
			return;
		
		boolean filtered = false;
		
		if (!contactsOptions.hasFilter("status", "uncontacted") || !contactsOptions.hasFilter("status", "attempted_contact") || !contactsOptions.hasFilter("status", "contacted")) {
			filtered = true;
		}
		
		if (tab == TAB_ALL) {
			if (contactsOptions.hasFilter("assigned_to")) {
				filtered = true;
			}
		}
		
		if (contactsOptions.hasFilter("gender")) {
			filtered = true;
		}
		
		if (filtered) {
			btnFilter.setBackgroundResource(R.drawable.mh_searchband_more_active);
		} else {
			btnFilter.setBackgroundResource(R.drawable.mh_searchband_more);
		}
	}
	
	private ArrayList<String> processes = new ArrayList<String>();

	private void showProgress(String process) {
		processes.add(process);
		this.progress.setVisibility(View.VISIBLE);
	}

	private void hideProgress(String process) {
		processes.remove(process);
		if (processes.size() <= 0) {
			this.progress.setVisibility(View.GONE);
		}
	}

	public void clickMyContacts(View v) {
		bottom_button_left.setChecked(true);
		bottom_button_right.setChecked(false);
		setTab(TAB_MY, false);
	}

	public void clickAllContacts(View v) {
		bottom_button_left.setChecked(false);
		bottom_button_right.setChecked(true);
		setTab(TAB_ALL, false);
	}
	
	public void clickFilter(View v) {
		Intent i = new Intent(this, ContactsFilterActivity.class);
		if (tab == TAB_MY) {
			i.putExtra("TYPE", ContactsFilterActivity.TYPE_MY_CONTACTS);
		} else if (tab == TAB_ALL) {
			i.putExtra("TYPE", ContactsFilterActivity.TYPE_ALL_CONTACTS);
		}
		startActivityForResult(i, RESULT_CONTACTS_FILTER_ACTIVITY);
	}

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_MY:
				bottom_button_left.setChecked(true); // First State
				txtTitle.setText(R.string.contacts_my);
				txtNoData.setText(R.string.contacts_no_data_my);
				Guide.display(this, Guide.CONTACTS_MY_CONTACTS);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "My");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_ALL:
				txtTitle.setText(R.string.contacts_all);
				txtNoData.setText(R.string.contacts_no_data_all);
				Guide.display(this, Guide.CONTACTS_ALL);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "AllContacts");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			}
			this.tab = tab;
			resetListView(true);
			getMore();
		}
	}

	private boolean atEnd = false;
	
	private ArrayList<Integer> contactIds = new ArrayList<Integer>();

	private void resetListView(boolean notify) {
		txtNoData.setVisibility(View.GONE);
		data.clear();
		contactIds.clear();
		if (notify) {
			adapter.notifyDataSetChanged();
		}
		contactsOptions.setStart(0);
		atEnd = false;
		if (currentRequest != null) {
			currentRequest.cancel(true);
		}
		hideProgress("contacts");
	}
	
	ApiClient currentRequest;

	private void getMore() {
		if (atEnd || processes.contains("contacts"))
			return;

		ApiResponseHandler responseHandler = new ApiResponseHandler() {

			@Override
			public void onStart() {
				showProgress("contacts");
			}

			@Override
			public void onSuccess(String response) {
				Gson gson = new Gson();
				try {
					GError error = gson.fromJson(response, GError.class);
					onFailure(new MHException(error));
				} catch (Exception out) {
					try {
						GContact[] contacts = gson.fromJson(response, GContact[].class);
						if (contacts.length < contactsOptions.getLimit()) {
							atEnd = true;
						} else {
							atEnd = false;
						}
						if (contacts.length > 0) {
							for (GContact contact : contacts) {
								final int id = contact.getPerson().getId();
								if (!contactIds.contains(id)) {
									contactIds.add(id);
									data.add(contact);
								}
							}
							adapter.notifyDataSetChanged();
						}
						if (data.size() <= 0) {
							txtNoData.setVisibility(View.VISIBLE);
						} else {
							txtNoData.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						onFailure(e);
					}
				}
			}

			@Override
			public void onFailure(Throwable e) {
				Log.e(TAG, "Contacts List Get More Failed", e);
				AlertDialog ad = DisplayError.display(ContactsActivity.this, e);
				ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						contactsOptions.incrementStart(-contactsOptions.getLimit());
						getMore();
					}
				});
				ad.show();
				Flurry.error(e, "Contacts.getMore");
			}

			@Override
			public void onFinish() {
				hideProgress("contacts");
			}
		};
		
		setFilters();
		currentRequest = Contacts.list(this, contactsOptions, responseHandler);
		contactsOptions.incrementStart(contactsOptions.getLimit());
	}

	private void setFilters() {
		
		contactsOptions.clearFilters();
		
		SharedPreferences sharedPrefs = null;
		if (tab == TAB_MY) {
			sharedPrefs = getBaseContext().getSharedPreferences(ContactsFilterActivity.TYPE_MY_CONTACTS, 0);
			contactsOptions.setFilter("assigned_to", String.valueOf(User.getContact().getPerson().getId()));
		} else if (tab == TAB_ALL) {
			sharedPrefs = getBaseContext().getSharedPreferences(ContactsFilterActivity.TYPE_ALL_CONTACTS, 0);
			contactsOptions.removeFilter("assigned_to");
		}
		
		// Not Finished Contacts
		contactsOptions.addFilter("status", "uncontacted");
		contactsOptions.addFilter("status", "attempted_contact");
		contactsOptions.addFilter("status", "contacted");
		
		if (!U.nullOrEmpty(searchTerm)){
			contactsOptions.setFilter("name", searchTerm);
		}
		
		Map<String, ?> prefs = sharedPrefs.getAll();
		Iterator<String> itr = prefs.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Object value = prefs.get(key);
			
			if (value instanceof String) {
				// Assigned To Is Not Really A Filter
				if (key.equalsIgnoreCase("assigned_to")) {
					if (((String) value).equalsIgnoreCase("me")) {
						contactsOptions.setFilter("assigned_to", String.valueOf(User.getContact().getPerson().getId()));
					} else if (((String) value).equalsIgnoreCase("no_one")) {
						contactsOptions.setFilter("assigned_to", "none");
					} else {
						contactsOptions.removeFilter("assigned_to");
					}
					continue;
				}
				
				// Ignore Not Filtered
				if (((String) value).equalsIgnoreCase(ContactsFilterActivity.NOT_FILTERED)) {
					contactsOptions.removeFilter(key);
					continue;
				}
				
				if (key.equalsIgnoreCase("status")) {
					if (((String) value).equalsIgnoreCase("not_finished")) {
						contactsOptions.setFilter("status", "uncontacted");
						contactsOptions.addFilter("status", "attempted_contact");
						contactsOptions.addFilter("status", "contacted");
					} else if (((String) value).equalsIgnoreCase("finished")) {
						contactsOptions.setFilter("status", "completed");
						contactsOptions.addFilter("status", "do_no_contact");
					} else {
						contactsOptions.setFilter("status", value.toString());
					}
					continue;
				}
				
				contactsOptions.setFilter(key, value.toString());
			} else if (value instanceof Boolean) {
				//TODO:
			}
		}

		updateFilterIcon();
	}
	
	private String searchTerm = "";
	private Handler searchHandler = new Handler();
	
	private class ContactsSearchWatcher implements TextWatcher {
		public void afterTextChanged(Editable s) {
			searchTerm = s.toString();
			searchHandler.removeCallbacks(doSearch);
			if (searchTerm.length() > 0) {
				searchHandler.postDelayed(doSearch, 350);
			} else if (searchTerm.length() <= 0) {
				contactsOptions.removeFilter("name");
				resetListView(true);
				getMore();
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}

	private Runnable doSearch = new Runnable() {
		public void run() {
			resetListView(true);
			getMore();
		}
	};

	private class ContactsScrollListener implements OnScrollListener {
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (totalItemCount - firstVisibleItem < 2.5 * visibleItemCount) {
				getMore();
			}
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}
	
	private class ContactsOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GContact contact = (GContact) parent.getAdapter().getItem(position);
			Intent i = new Intent(getApplicationContext(), ContactActivity.class);
			Gson gson = new Gson();
			String contactJSON = gson.toJson(contact);
			i.putExtra("contactJSON", contactJSON);
			startActivity(i);
		}
	}
	
	private class ChangeRoleHandler extends ApiResponseHandler {

		String role;
		int contactId;

		public ChangeRoleHandler(String role, int id) {
			this.role = role;
			this.contactId = id;
		}

		@Override
		public void onStart() {
			showProgress("change_role_" + contactId);
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHException(error));
			} catch (Exception out) {
				try {
					FlurryAgent.onEvent("Contacts.ChangeRole");
				} catch (Exception e) {
				}
				resetListView(false);
				getMore();
			}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Change Role Failed", e);
			AlertDialog ad = DisplayError.display(ContactsActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					Roles.change(ContactsActivity.this, id, role, new ChangeRoleHandler(role, contactId));
				}
			});
			ad.show();
			Flurry.error(e, "Contacts.ChangeRoleHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("change_role_" + contactId);
		}
	};
	
	private class ContactsOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final GContact contact = (GContact) parent.getAdapter().getItem(position);
			if (contact != null && contact.getPerson() != null && User.hasRole("admin")) {
				String contactRole = "contact";
	    		for (GOrgGeneric role : contact.getPerson().getOrganizational_roles()) {
					if (role.getOrg_id() == User.getOrganizationID()) {
						contactRole = role.getRole();
						break;
					}
				}
	    		
	    		final ArrayList<CharSequence> items = new ArrayList<CharSequence>();
				if (contactRole.equals("contact")) {
					items.add(getString(R.string.contacts_actions_promote));
				} else if (contactRole.equals("leader")) {
					items.add(getString(R.string.contacts_actions_demote));
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
				builder.setTitle(R.string.contacts_actions);
				
				CharSequence itemsArray[] = new CharSequence[items.size()];
				itemsArray = items.toArray(itemsArray);
				
				builder.setItems(itemsArray, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if (items.get(item).equals(getString(R.string.contacts_actions_promote))) {
				    		Roles.change(ContactsActivity.this, contact.getPerson().getId(), "leader", new ChangeRoleHandler("leader", contact.getPerson().getId()));
				    	} else if (items.get(item).equals(getString(R.string.contacts_actions_demote))) {
				    		Roles.change(ContactsActivity.this, contact.getPerson().getId(), "contact", new ChangeRoleHandler("contact", contact.getPerson().getId()));
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				if (!items.isEmpty()) {
					alert.show();
				}
			}
			return false;
		}
		
	}
}
