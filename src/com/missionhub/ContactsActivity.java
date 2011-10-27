package com.missionhub;

import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.LoaderActionBarItem;
import greendroid.widget.NormalActionBarItem;

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
import com.missionhub.api.json.GMetaContact;
import com.missionhub.api.json.GOrgGeneric;
import com.missionhub.helpers.Flurry;
import com.missionhub.ui.ContactItemAdapter;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
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
	private TextView txtNoData;
	//private EditText search;
	private ToggleButton bottom_button_left;
	private ToggleButton bottom_button_right;
	
	private NormalActionBarItem searchIcon;
	private LoaderActionBarItem indicator;

	private ArrayList<GContact> data = new ArrayList<GContact>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.contacts);
		
		indicator = (LoaderActionBarItem) addActionBarItem(Type.Refresh, R.id.action_bar_refresh);
		searchIcon = (NormalActionBarItem) addActionBarItem(getActionBar()
                .newActionBarItem(NormalActionBarItem.class)
                .setDrawable(R.drawable.action_bar_search)
                .setContentDescription(R.string.action_bar_search), R.id.action_bar_search);

		contactsList = (ListView) findViewById(R.id.contacts_list);
		adapter = new ContactItemAdapter(this, R.layout.contact_list_item, data);
		contactsList.setAdapter(adapter);
		contactsList.setOnScrollListener(new ContactsScrollListener());
		contactsList.setOnItemClickListener(new ContactsOnItemClickListener());
		contactsList.setOnItemLongClickListener(new ContactsOnItemLongClickListener());

		txtNoData = (TextView) findViewById(R.id.txt_contacts_no_data);

		bottom_button_left = (ToggleButton) findViewById(R.id.bottom_button_left);
		bottom_button_right = (ToggleButton) findViewById(R.id.bottom_button_right);

		setTab(TAB_MY, true);
		
		Flurry.pageView(this, "Contacts");
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
            	try {
    				FlurryAgent.onEvent("Contacts.Refresh");
    			} catch (Exception e) {}
    			resetListView(false);
    			getMore();
                break;
            case R.id.action_bar_search:
            	startSearch();
                break;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
        return true;
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
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			startSearch();
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	private void startSearch() {
		Intent i = new Intent(this, ContactsFilterActivity.class);
		if (tab == TAB_MY) {
			i.putExtra("TYPE", ContactsFilterActivity.TYPE_MY_CONTACTS);
		} else if (tab == TAB_ALL) {
			i.putExtra("TYPE", ContactsFilterActivity.TYPE_ALL_CONTACTS);
		}
		startActivityForResult(i, RESULT_CONTACTS_FILTER_ACTIVITY);
	}
	
	private void updateFilterIcon(Contacts.Options options) {
		if (options.getFilters().isEmpty())
			return;
		
		boolean filtered = false;
		
		if (!options.hasFilter("status", "uncontacted") || !options.hasFilter("status", "attempted_contact") || !options.hasFilter("status", "contacted")) {
			filtered = true;
		}
		
		if (tab == TAB_ALL) {
			if (options.hasFilter("assigned_to")) {
				filtered = true;
			}
		}
		
		if (options.hasFilter("gender")) {
			filtered = true;
		}
		
		if (filtered) {
			searchIcon.setDrawable(R.drawable.action_bar_search_active);
		} else {
			searchIcon.setDrawable(R.drawable.action_bar_search);
		}
	}
	
	private ArrayList<String> processes = new ArrayList<String>();

	private void showProgress(String process) {
		processes.add(process);
		indicator.setLoading(true);
	}

	private void hideProgress(String process) {
		processes.remove(process);
		if (processes.size() <= 0) {
			indicator.setLoading(false);
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

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_MY:
				bottom_button_left.setChecked(true); // First State
				setTitle(R.string.contacts_my);
				txtNoData.setText(R.string.contacts_no_data_my);
				Guide.display(this, Guide.CONTACTS_MY_CONTACTS);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "My");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_ALL:
				setTitle(R.string.contacts_all);
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

	private boolean noMoreContacts = false;
	
	private ArrayList<Integer> contactIds = new ArrayList<Integer>();

	private void resetListView(boolean notify) {
		txtNoData.setVisibility(View.GONE);
		data.clear();
		contactIds.clear();
		if (notify) {
			adapter.notifyDataSetChanged();
		}
		hideProgress(options.toString());
		
		options = new Contacts.Options();
		
		// Large Screens
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (metrics.heightPixels > 480) {
			options.setLimit(40);
		}
		
		addFiltersOrder(options);
		updateFilterIcon(options);
		
		noMoreContacts = false;
		if (client != null) {
			client.cancel(true);
			client = null;
		}
	}	
	
	private ApiClient client = null;	
	private Contacts.Options options = new Contacts.Options();
	
	private void getMore() {
		if (client != null || noMoreContacts)
			return;
		
		client = Contacts.list(this, options, new ContactsApiResponseHandler(options));
		options.incrementStart(options.getLimit());
	}
	
	private class ContactsApiResponseHandler extends ApiResponseHandler {
		
		Contacts.Options ops;
		
		public ContactsApiResponseHandler(Contacts.Options ops) {
			super(GMetaContact.class);
			this.ops = ops;
		}
		
		@Override
		public void onStart() {
			showProgress(ops.toString());
		}
		
		@Override 
		public void onSuccess(Object gsonObject) {
			if (ops != options) // This response was likely canceled
				return;
			
			GMetaContact contactMeta = (GMetaContact) gsonObject;
			GContact[] contacts = contactMeta.getContacts();
			if (contacts.length < options.getLimit()) {
				noMoreContacts = true;
			} else {
				noMoreContacts = false;
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
		}
		
		@Override
		public void onFailure(Throwable e) {
			if (ops != options) // This response was likely canceled
				return;
			
			Log.e(TAG, "Contacts List Get More Failed", e);
			AlertDialog ad = DisplayError.display(ContactsActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					options.incrementStart(-options.getLimit());
					getMore();
				}
			});
			ad.show();
			Flurry.error(ContactsActivity.this, e, "Contacts.getMore");
		}
		
		@Override
		public void onFinish() {
			hideProgress(ops.toString());
			client = null;
		}
	}
	
	private Contacts.Options addFiltersOrder(Contacts.Options options) {
		
		SharedPreferences sharedPrefs = null;
		if (tab == TAB_MY) {
			sharedPrefs = getBaseContext().getSharedPreferences(ContactsFilterActivity.TYPE_MY_CONTACTS, 0);
			options.setFilter("assigned_to", String.valueOf(getUser().getId()));
		} else if (tab == TAB_ALL) {
			sharedPrefs = getBaseContext().getSharedPreferences(ContactsFilterActivity.TYPE_ALL_CONTACTS, 0);
			options.removeFilter("assigned_to");
		}
		
		// Not Finished Contacts
		options.addFilter("status", "uncontacted");
		options.addFilter("status", "attempted_contact");
		options.addFilter("status", "contacted");
		
		Map<String, ?> prefs = sharedPrefs.getAll();
		Iterator<String> itr = prefs.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Object value = prefs.get(key);
			
			if (value instanceof String) {
				// Assigned To Is Not Really A Filter
				if (key.equalsIgnoreCase("assigned_to")) {
					if (((String) value).equalsIgnoreCase("me")) {
						options.setFilter("assigned_to", String.valueOf(getUser().getId()));
					} else if (((String) value).equalsIgnoreCase("no_one")) {
						options.setFilter("assigned_to", "none");
					} else {
						options.removeFilter("assigned_to");
					}
					continue;
				}
				
				// Ignore Not Filtered
				if (((String) value).equalsIgnoreCase(ContactsFilterActivity.NOT_FILTERED)) {
					options.removeFilter(key);
					continue;
				}
				
				if (key.equalsIgnoreCase("status")) {
					if (((String) value).equalsIgnoreCase("not_finished")) {
						options.setFilter("status", "uncontacted");
						options.addFilter("status", "attempted_contact");
						options.addFilter("status", "contacted");
					} else if (((String) value).equalsIgnoreCase("finished")) {
						options.setFilter("status", "completed");
						options.addFilter("status", "do_no_contact");
					} else {
						options.setFilter("status", value.toString());
					}
					continue;
				}
				
				options.setFilter(key, value.toString());
			} else if (value instanceof Boolean) {
				//TODO:
			}
		}
		return options;
	}
	
	private class ContactsScrollListener implements OnScrollListener {
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (totalItemCount - firstVisibleItem < 3 * visibleItemCount) {
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
			super();
			this.role = role;
			this.contactId = id;
		}

		@Override
		public void onStart() {
			showProgress("change_role_" + contactId);
		}

		@Override
		public void onSuccess() {
			Flurry.event(ContactsActivity.this, "Contacts.ChangeRole");
			resetListView(false);
			getMore();
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
			Flurry.error(ContactsActivity.this, e, "Contacts.ChangeRoleHandler");
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
			if (contact != null && contact.getPerson() != null && getUser().hasRole("admin")) {
				String contactRole = "contact";
	    		for (GOrgGeneric role : contact.getPerson().getOrganizational_roles()) {
					if (role.getOrg_id() == getUser().getOrganizationID()) {
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
