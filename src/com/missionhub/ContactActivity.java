package com.missionhub;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GAssign;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GError;
import com.missionhub.api.GFCTop;
import com.missionhub.api.GFollowupComment;
import com.missionhub.api.GIdNameProvider;
import com.missionhub.api.GPerson;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.CommentItemAdapter;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;
import com.missionhub.ui.ImageManager;
import com.missionhub.ui.Rejoicable;
import com.missionhub.ui.RejoicableAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

	private final int ASSIGNMENT_NONE = 0;
	private final int ASSIGNMENT_ME = 1;
	private final int ASSIGNMENT_OTHER = 2;
	private int assignmentStatus = ASSIGNMENT_NONE;

	private final int DIALOG_REJOICABLES = 0;

	private ArrayList<String> statusList = new ArrayList<String>();
	
	private CommentItemAdapter commentAdapter;
	private ArrayList<GFollowupComment> comments = new ArrayList<GFollowupComment>();

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
		
		commentAdapter = new CommentItemAdapter(this, R.layout.comment_list_item, comments);

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
		statusList.add(getString(R.string.status_uncontacted));
		statusList.add(getString(R.string.status_attempted_contact));
		statusList.add(getString(R.string.status_contacted));
		statusList.add(getString(R.string.status_completed));
		statusList.add(getString(R.string.status_do_not_contact));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.contact_spinner_text, statusList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		contactStatus.setAdapter(adapter);

		header = new LinearLayout(this);
		header.setOrientation(LinearLayout.VERTICAL);
		header.addView(contactHeader);

		contactListView.addHeaderView(header);
		String[] mStrings = {};
		contactListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));

		imageManager = new ImageManager(getApplicationContext());
		updateHeader();

		setTab(TAB_CONTACT, true);
		update(true);
		Guide.display(this, Guide.CONTACT);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_REJOICABLES:
			ArrayList<Rejoicable> rejoicables = new ArrayList<Rejoicable>();
			rejoicables.add(new Rejoicable(R.string.rejoice_spiritual_conversation, R.drawable.rejoicable_s_convo, "spiritual_conversation"));
			rejoicables.add(new Rejoicable(R.string.rejoice_prayed_to_receive, R.drawable.rejoicable_r_christ, "prayed_to_receive"));
			rejoicables.add(new Rejoicable(R.string.rejoice_gospel_presentation, R.drawable.rejoicable_g_present, "gospel_presentation"));

			AlertDialog dialog = new AlertDialog.Builder(this).setIcon(R.drawable.rejoicable_icon).setTitle(R.string.contact_rejoicables)
				.setAdapter(new RejoicableAdapter(this, android.R.layout.simple_spinner_dropdown_item, rejoicables), null)
				.setNeutralButton(R.string.alert_ok, null)
				.create();
			dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			return dialog;
		}
		return null;
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
		if (person == null)
			return;

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

		assignmentStatus = ASSIGNMENT_NONE;
		if (person.getAssignment() != null) {
			GAssign assign = person.getAssignment();
			if (assign.getPerson_assigned_to() != null) {
				GIdNameProvider[] gids = assign.getPerson_assigned_to();
				for (GIdNameProvider gid : gids) {
					if (User.contact.getPerson().getId() == Integer.parseInt(gid.getId())) {
						assignmentStatus = ASSIGNMENT_ME;
						break;
					} else {
						assignmentStatus = ASSIGNMENT_OTHER;
					}
				}
			}
		}
		if (assignmentStatus == ASSIGNMENT_NONE) {
			contactAssign.setText(R.string.contact_assign_to_me);
			contactAssign.setEnabled(true);
		} else if (assignmentStatus == ASSIGNMENT_ME) {
			contactAssign.setText(R.string.contact_unassign);
			contactAssign.setEnabled(true);
		} else if (assignmentStatus == ASSIGNMENT_OTHER) {
			contactAssign.setText(R.string.contact_assign_locked);
			contactAssign.setEnabled(false);
		}
	}

	public void update(boolean force) {
		updatePerson(force);
		updateComments(force);
	}

	public void updatePerson(boolean force) {
		if (!processes.contains("contact") || force)
		Api.getContacts(contact.getPerson().getId(), contactResponseHandler);
	}

	public void updateComments(boolean force) {
		if (!processes.contains("comment") || force)
		Api.getFollowupComments(contact.getPerson().getId(), commentResponseHandler);	
	}

	private AsyncHttpResponseHandler contactResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			showProgress("contact");
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out) {
				try {
					GContactAll contactAll = gson.fromJson(response, GContactAll.class);
					ContactActivity.this.contactMeta = contactAll;
					ContactActivity.this.contact = contactAll.getPeople()[0];
					ContactActivity.this.updateHeader();
				} catch (Exception e) {
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
	
	private AsyncHttpResponseHandler commentResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onStart() {
			showProgress("comment");
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out) {
				try {
					GFCTop[] fcs = gson.fromJson(response, GFCTop[].class);
					ContactActivity.this.processFollowupComments(fcs);
				} catch (Exception e) {
					onFailure(e);
				}
			}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Comment Fetch Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					updateComments(true);
				}
			});
			ad.show();
		}

		@Override
		public void onFinish() {
			hideProgress("comment");
		}
	};

	public void clickAssign(View v) {
		clickAssign();
	}

	protected void processFollowupComments(GFCTop[] fcs) {
		comments.clear();
		for (GFCTop gfc : fcs) {
			comments.add(gfc.getFollowup_comment());
		}
		if (tab == TAB_CONTACT) {
			contactListView.setAdapter(commentAdapter);
			commentAdapter.notifyDataSetChanged();
		}
	}

	public void clickAssign() {
		if (assignmentStatus == ASSIGNMENT_NONE) {
			Api.createContactAssignment(contact.getPerson().getId(), User.contact.getPerson().getId(), new AssignmentHandler(AssignmentHandler.TYPE_ASSIGN));
		} else if (assignmentStatus == ASSIGNMENT_ME) {
			Api.deleteContactAssignment(contact.getPerson().getId(), new AssignmentHandler(AssignmentHandler.TYPE_UNASSIGN));
		}
	}

	private class AssignmentHandler extends AsyncHttpResponseHandler {

		public static final int TYPE_ASSIGN = 0;
		public static final int TYPE_UNASSIGN = 1;

		private int type = TYPE_ASSIGN;

		AssignmentHandler(int type) {
			super();
			this.type = type;
		}

		@Override
		public void onStart() {
			showProgress("assign");
			contactAssign.setEnabled(false);
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out) {
				Log.i(TAG, "ASSIGNMENT SUCCESS");
				if (type == TYPE_ASSIGN) {
					contactAssign.setText(R.string.contact_unassign);
					assignmentStatus = ASSIGNMENT_ME;
				} else if (type == TYPE_UNASSIGN) {
					contactAssign.setText(R.string.contact_assign_to_me);
					assignmentStatus = ASSIGNMENT_NONE;
				}

				// TODO: on success
			}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Assignment Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					clickAssign();
				}
			});
			ad.show();
		}

		@Override
		public void onFinish() {
			hideProgress("assign");
			contactAssign.setEnabled(true);
		}
	};

	public void clickPicture(View v) {

	}

	public void clickPhone(View v) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + contact.getPerson().getPhone_number()));
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
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { contact.getPerson().getEmail_address() });
			startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_send_email)));
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_email, Toast.LENGTH_LONG).show();
		}
	}

	public void clickSave(View v) {

	}

	public void clickRejoicables(View v) {
		showDialog(DIALOG_REJOICABLES);
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
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
			return false;

		return true;
	}
}
