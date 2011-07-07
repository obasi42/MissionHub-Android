package com.missionhub;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GAssign;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GError;
import com.missionhub.api.GIdNameProvider;
import com.missionhub.api.GPerson;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;
import com.missionhub.ui.ImageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {

	public static final String TAG = ContactActivity.class.getName();

	public static final int TAB_CONTACT = 0;
	public static final int TAB_MORE_INFO = 1;
	public static final int TAB_SURVEYS = 2;
	private int tab = TAB_CONTACT;
	
	private GContactAll contactMeta;
	private GContact contact;

	private TextView txtTitle;
	private ListView contactListView;
	private LinearLayout header;
	private ImageManager imageManager;
	private ProgressBar progress;

	private LinearLayout contactHeader;
	private ImageView contactPicture;
	private String currentContactPicture = "";
	private TextView contactName;
	private Button contactPhone;
	private Button contactSMS;
	private Button contactEmail;
	private Button contactAssign;

	private LinearLayout contactPost;
	private EditText contactComment;
	private Button contactRejoicable;
	private Button contactSave;
	private Spinner contactStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		if (i != null) {
			try {
				Gson gson = new Gson();
				contact = gson.fromJson(i.getStringExtra("contactJSON"), GContact.class);
			} catch (Exception e) {
			}
		}
		if (contact == null) {
			finish();
		}

		setContentView(R.layout.contact);
		txtTitle = (TextView) findViewById(R.id.contact_title);
		contactListView = (ListView) findViewById(R.id.contact_listview);
		progress = (ProgressBar) findViewById(R.id.contact_progress);

		contactHeader = (LinearLayout) View.inflate(this, R.layout.contact_header, null);
		contactPicture = (ImageView) contactHeader.findViewById(R.id.contact_picture);
		contactName = (TextView) contactHeader.findViewById(R.id.contact_name);
		contactPhone = (Button) contactHeader.findViewById(R.id.contact_phone);
		contactSMS = (Button) contactHeader.findViewById(R.id.contact_sms);
		contactEmail = (Button) contactHeader.findViewById(R.id.contact_email);
		contactAssign = (Button) contactHeader.findViewById(R.id.contact_assign);

		contactPost = (LinearLayout) View.inflate(this, R.layout.contact_post, null);
		contactComment = (EditText) contactPost.findViewById(R.id.contact_comment);
		contactRejoicable = (Button) contactPost.findViewById(R.id.contact_rejoicable);
		contactSave = (Button) contactPost.findViewById(R.id.contact_save);
		contactStatus = (Spinner) contactPost.findViewById(R.id.contact_status);

		header = new LinearLayout(this);
		header.setOrientation(LinearLayout.VERTICAL);
		header.addView(contactHeader);

		contactListView.addHeaderView(header);
		String[] mStrings = { "one", "two", "three", "four", "five", "six", "seven" };
		contactListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
		
		imageManager = new ImageManager(getApplicationContext());
		updateHeader();
		
		setTab(TAB_CONTACT, true);
		update(true);
		Guide.display(this, Guide.CONTACT);
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
	
	
	public void updateHeader() {
		final GPerson person = contact.getPerson();
		if (person == null) return;
		
		int defaultImage = R.drawable.facebook_question;
		if (person.getGender() != null) {
			if (person.getGender().equalsIgnoreCase("male")) {
				defaultImage = R.drawable.facebook_male;
			} else if (person.getGender().equalsIgnoreCase("female")) {
				defaultImage = R.drawable.facebook_female;
			}
		}
		if (person.getPicture() != null && !currentContactPicture.equals(person.getPicture())) {
			currentContactPicture = person.getPicture();
			contactPicture.setTag(person.getPicture() + "?type=large");
			imageManager.displayImage(person.getPicture() + "?type=large", ContactActivity.this, contactPicture, defaultImage);
		}
		if (person.getName() != null) {
			contactName.setText(person.getName());
		}
		if (person.getPhone_number() != null && hasPhoneAbility()) {
			contactPhone.setVisibility(View.VISIBLE);
			contactSMS.setVisibility(View.VISIBLE);
		} else {
			contactPhone.setVisibility(View.GONE);
			contactSMS.setVisibility(View.GONE);
		}
		if (person.getEmail_address() != null) {
			contactEmail.setVisibility(View.VISIBLE);
		} else {
			contactEmail.setVisibility(View.GONE);
		}
		
		String assignedTo = null;
		if (person.getAssignment() != null) {
			GAssign assign = person.getAssignment();
			if (assign.getPerson_assigned_to() != null) {
				GIdNameProvider[] gids = assign.getPerson_assigned_to();
				for (GIdNameProvider gid : gids) {
					if (User.contact.getPerson().getId() == Integer.parseInt(gid.getId())) {
						assignedTo = "_me_";
						break;
					} else {
						assignedTo = gid.getName();
					}
				}
			}
		}
		if (assignedTo == null) {
			contactAssign.setText(R.string.contact_assign_to_me);
			contactAssign.setEnabled(true);
		} else if (assignedTo.equals("_me_")) {
			contactAssign.setText(R.string.contact_unassign);
			contactAssign.setEnabled(true);
		} else {
			contactAssign.setText(R.string.contact_assign_locked);
			contactAssign.setEnabled(false);
		}
	}

	public void update(boolean force) {
		updatePerson(force);
		updateComments(force);
	}

	public void updatePerson(boolean force) {
		Api.getContacts(contact.getPerson().getId(), contactResponseHandler);
	}

	public void updateComments(boolean force) {
		
	}
	
	private AsyncHttpResponseHandler contactResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			showProgress("contact");
		}
		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try{
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out){
				try {
					GContactAll contactAll = gson.fromJson(response, GContactAll.class);
					ContactActivity.this.contactMeta = contactAll;
					ContactActivity.this.contact = contactAll.getPeople()[0];
					ContactActivity.this.updateHeader();
				} catch(Exception e) {
					onFailure(e);
				}
			}
		}
		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Contact Fetch Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					updatePerson(true);
				}
			});
			ad.show();
		}
		@Override
		public void onFinish() {
			hideProgress("contact");
		}
	};

	public void clickAssign(View v) {
		
	}

	public void clickPicture(View v) {
		
	}

	public void clickPhone(View v) {
		try {
		    Intent intent = new Intent(Intent.ACTION_CALL);
		    intent.setData(Uri.parse("tel:"+contact.getPerson().getPhone_number()));
		    startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_call, Toast.LENGTH_LONG).show();
		}
	}

	public void clickSMS(View v) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("address", contact.getPerson().getPhone_number());
			intent.setType("vnd.android-dir/mms-sms"); 
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_sms, Toast.LENGTH_LONG).show();
		}
	}

	public void clickEmail(View v) {
		try {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{contact.getPerson().getEmail_address()});
			startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_send_email)));
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_email, Toast.LENGTH_LONG).show();
		}
	}

	public void clickSave(View v) {
		
	}

	public void clickRejoicables(View v) {
		
	}

	public void clickContact(View v) {
		setTab(TAB_CONTACT, false);
	}

	public void clickMoreInfo(View v) {
		setTab(TAB_MORE_INFO, false);
	}

	public void clickSurveys(View v) {
		setTab(TAB_SURVEYS, false);
	}

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_CONTACT:
				header.addView(contactPost);
				txtTitle.setText(R.string.contact_contact);
				break;
			case TAB_MORE_INFO:
				header.removeView(contactPost);
				txtTitle.setText(R.string.contact_more);
				break;
			case TAB_SURVEYS:
				header.removeView(contactPost);
				txtTitle.setText(R.string.contact_survey);
				break;
			}
			this.tab = tab;
		}
	}
	
	private boolean hasPhoneAbility() {
	   TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	   if(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
	       return false;

	   return true;
	}
}