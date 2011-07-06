package com.missionhub;

import com.google.gson.Gson;
import com.missionhub.api.GContact;
import com.missionhub.ui.Guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	
	private TextView txtTitle;
	private ListView contactListView;
	private LinearLayout header;
	
	private LinearLayout contactHeader;
	private ImageView contactPicture;
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
		GContact contact = null;
		if (i != null) {
			try {
				Gson gson = new Gson();
				contact = gson.fromJson(i.getStringExtra("contactJSON"), GContact.class);
			} catch (Exception e) {}
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
		String[] mStrings = {"one", "two", "three", "four", "five", "six", "seven"};
		contactListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
		
		setTab(TAB_CONTACT, true);
		Guide.display(this, Guide.CONTACT);
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