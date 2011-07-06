package com.missionhub;

import com.google.gson.Gson;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GPerson;
import com.missionhub.ui.Guide;
import com.missionhub.ui.ImageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
	}

	public void update(boolean force) {
		
	}

	public void updatePerson(boolean force) {
		
	}

	public void updateComments(boolean force) {
		
	}

	public void clickAssign(View v) {
		
	}

	public void clickPicture(View v) {
		
	}

	public void clickPhone(View v) {
		
	}

	public void clickSMS(View v) {
		
	}

	public void clickEmail(View v) {
		
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
}