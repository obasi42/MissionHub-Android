package com.missionhub;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		User.setFromBundle(savedInstanceState);

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
		User.setFromBundle(b);
	}

	public void clickMyContacts(View v) {
		setTab(TAB_MY, false);
	}

	public void clickMyCompleted(View v) {
		setTab(TAB_COMPLETED, false);
	}

	public void clickUnassigned(View v) {
		setTab(TAB_UNASSIGNED, false);
	}

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_MY:
				bottom_button_center.setChecked(false);
				bottom_button_right.setChecked(false);
				bottom_button_left.setChecked(true);
				
				txtTitle.setText(R.string.contacts_my_contacts);
				txtNoData.setText(R.string.contacts_no_data_my_contacts);
				options.put("filters", "status");
				options.put("values", "not_finished");
				options.put("assigned_to_id", String.valueOf(User.getContact().getPerson().getId()));
				Guide.display(this, Guide.CONTACTS_MY_CONTACTS);
				break;
			case TAB_COMPLETED:
				bottom_button_left.setChecked(false);
				bottom_button_right.setChecked(false);
				bottom_button_center.setChecked(true);
				
				txtTitle.setText(R.string.contacts_my_completed);
				txtNoData.setText(R.string.contacts_no_data_my_completed);
				options.put("filters", "status");
				options.put("values", "finished");
				options.put("assigned_to_id", String.valueOf(User.getContact().getPerson().getId()));
				break;
			case TAB_UNASSIGNED:
				bottom_button_left.setChecked(false);
				bottom_button_center.setChecked(false);
				bottom_button_right.setChecked(true);
				
				txtTitle.setText(R.string.contacts_unassigned);
				txtNoData.setText(R.string.contacts_no_data_unassigned);
				options.remove("filters");
				options.remove("values");
				options.put("assigned_to_id", "none");
				Guide.display(this, Guide.CONTACTS_UNASSIGNED);
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
		if (loading || atEnd)
			return;

		options.put("limit", String.valueOf(limit));
		options.put("start", String.valueOf(start));
		start += limit;

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

			final int forTab = tab;

			@Override
			public void onStart() {
				loading = true;
				progress.setVisibility(View.VISIBLE);
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
			}

			@Override
			public void onFinish() {
				loading = false;
				progress.setVisibility(View.GONE);
			}
		};

		Api.getContactsList(options, responseHandler);
	}

	private class ContactsSearchWatcher implements TextWatcher {
		public void afterTextChanged(Editable s) {
			if (s.length() > 2) {
				options.put("term", s.toString());
				resetListView(true);
				getMore();
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
