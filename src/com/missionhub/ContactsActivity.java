package com.missionhub;

import java.util.ArrayList;
import java.util.HashMap;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GContact;
import com.missionhub.api.GError;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.ContactItemAdapter;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ContactsActivity extends Activity {

	public static final String TAG = ContactsActivity.class.getName();

	private final int TAB_MY = 0;
	private final int TAB_COMPLETED = 1;
	private final int TAB_UNASSIGNED = 2;

	private int tab = TAB_MY;
	private ListView contactsList;
	private ContactItemAdapter adapter;
	private ProgressBar progress;
	private TextView txtNoData;
	private TextView txtTitle;
	private EditText search;
	private ToggleButton bottom_button_left;
	private ToggleButton bottom_button_center;
	private ToggleButton bottom_button_right;

	private ArrayList<GContact> data = new ArrayList<GContact>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		
		User.setFromBundle(savedInstanceState, this);

		contactsList = (ListView) findViewById(R.id.contacts_list);
		adapter = new ContactItemAdapter(this, R.layout.contact_list_item, data);
		contactsList.setAdapter(adapter);
		contactsList.setOnScrollListener(new ContactsScrollListener());
		contactsList.setOnItemClickListener(new ContactsOnItemClickListener());

		progress = (ProgressBar) findViewById(R.id.contacts_progress);
		txtNoData = (TextView) findViewById(R.id.txt_contacts_no_data);
		txtTitle = (TextView) findViewById(R.id.txt_contacts_title);
		search = (EditText) findViewById(R.id.contacts_search);

		bottom_button_left = (ToggleButton) findViewById(R.id.bottom_button_left);
		bottom_button_center = (ToggleButton) findViewById(R.id.bottom_button_center);
		bottom_button_right = (ToggleButton) findViewById(R.id.bottom_button_right);
		
		search.addTextChangedListener(new ContactsSearchWatcher());

		// Large Screens
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if (metrics.heightPixels > 480) {
			limit = 30;
		}

		setTab(TAB_MY, true);
		User.setFlurryUser();
		try {
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", "Contacts");
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
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
		b.putAll(User.getAsBundle());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		User.setFromBundle(b, this);
	}
	
	@Override
	public void onStart() {
	   super.onStart();
	   User.setFlurryUser();
	   FlurryAgent.onStartSession(this, Config.flurryKey);
	}
	
	@Override
	public void onStop() {
	   super.onStop();
	   User.setFlurryUser();
	   FlurryAgent.onEndSession(this);
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
		bottom_button_center.setChecked(false);
		bottom_button_right.setChecked(false);
		bottom_button_left.setChecked(true);
		setTab(TAB_MY, false);
	}

	public void clickMyCompleted(View v) {
		bottom_button_left.setChecked(false);
		bottom_button_right.setChecked(false);
		bottom_button_center.setChecked(true);
		setTab(TAB_COMPLETED, false);
	}

	public void clickUnassigned(View v) {
		bottom_button_left.setChecked(false);
		bottom_button_center.setChecked(false);
		bottom_button_right.setChecked(true);
		setTab(TAB_UNASSIGNED, false);
	}

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_MY:
				bottom_button_left.setChecked(true); // First State
				txtTitle.setText(R.string.contacts_my_contacts);
				txtNoData.setText(R.string.contacts_no_data_my_contacts);
				options.put("filters", "status");
				options.put("values", "not_finished");
				options.put("assigned_to_id", String.valueOf(User.getContact().getPerson().getId()));
				Guide.display(this, Guide.CONTACTS_MY_CONTACTS);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "My");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_COMPLETED:
				txtTitle.setText(R.string.contacts_my_completed);
				txtNoData.setText(R.string.contacts_no_data_my_completed);
				options.put("filters", "status");
				options.put("values", "finished");
				options.put("assigned_to_id", String.valueOf(User.getContact().getPerson().getId()));
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "My Completed");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_UNASSIGNED:
				txtTitle.setText(R.string.contacts_unassigned);
				txtNoData.setText(R.string.contacts_no_data_unassigned);
				options.remove("filters");
				options.remove("values");
				options.put("assigned_to_id", "none");
				Guide.display(this, Guide.CONTACTS_UNASSIGNED);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "Unassigned");
					FlurryAgent.onEvent("Contacts.ChangeTab", params);
				} catch (Exception e) {}
				break;
			}
			this.tab = tab;
			resetListView(true);
			getMore();
		}
	}

	private int start = 0;
	private int limit = 15;
	private boolean atEnd = false;
	private boolean loading = false;
	private HashMap<String, String> options = new HashMap<String, String>();
	private ArrayList<Integer> contactIds = new ArrayList<Integer>();

	private void resetListView(boolean notify) {
		txtNoData.setVisibility(View.GONE);
		data.clear();
		contactIds.clear();
		if (notify) {
			adapter.notifyDataSetChanged();
		}
		start = 0;
		atEnd = false;
		loading = false;
	}

	private void getMore() {
		if (loading || atEnd || processes.contains("loading_"+tab))
			return;

		options.put("limit", String.valueOf(limit));
		options.put("start", String.valueOf(start));
		start += limit;

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

			final int forTab = tab;

			@Override
			public void onStart() {
				loading = true;
				showProgress("loading_"+forTab);
			}

			@Override
			public void onSuccess(String response) {
				if (forTab != tab)
					return;

				Gson gson = new Gson();
				try {
					GError error = gson.fromJson(response, GError.class);
					onFailure(new MHError(error));
				} catch (Exception out) {
					try {
						GContact[] contacts = gson.fromJson(response, GContact[].class);
						if (contacts.length < limit) {
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
						start -= limit;
						getMore();
					}
				});
				ad.show();
				MHError.onFlurryError(e, "Contacts.getMore");
			}

			@Override
			public void onFinish() {
				loading = false;
				hideProgress("loading_"+forTab);
			}
		};

		Api.getContactsList(options, responseHandler);
	}

	private Handler searchHandler = new Handler();
	private String searchText = "";
	
	private class ContactsSearchWatcher implements TextWatcher {
		public void afterTextChanged(Editable s) {
			searchText = s.toString();
			searchHandler.removeCallbacks(doSearch);
			if (searchText.length() > 0) {
				searchHandler.postDelayed(doSearch, 350);
			} else if (s.length() == 0) {
				options.remove("term");
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
			options.put("term", searchText);
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
}
