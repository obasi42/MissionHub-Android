package com.missionhub;

import greendroid.widget.ActionBarItem;
import greendroid.widget.ItemAdapter;
import greendroid.widget.LoaderActionBarItem;
import greendroid.widget.ActionBarItem.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.missionhub.api.ApiResponseHandler;
import com.missionhub.api.ContactAssignment;
import com.missionhub.api.Contacts;
import com.missionhub.api.FollowupComments;
import com.missionhub.api.Roles;
import com.missionhub.api.model.json.GAssign;
import com.missionhub.api.model.json.GContact;
import com.missionhub.api.model.json.GMetaContact;
import com.missionhub.api.model.json.GEducation;
import com.missionhub.api.model.json.GFCTop;
import com.missionhub.api.model.json.GFollowupComment;
import com.missionhub.api.model.json.GIdNameProvider;
import com.missionhub.api.model.json.GKeyword;
import com.missionhub.api.model.json.GMetaContact;
import com.missionhub.api.model.json.GMetaGFCTop;
import com.missionhub.api.model.json.GOrgGeneric;
import com.missionhub.api.model.json.GPerson;
import com.missionhub.api.model.json.GQA;
import com.missionhub.api.model.json.GQuestion;
import com.missionhub.helper.Helper;
import com.missionhub.helper.U;
import com.missionhub.ui.CommentItemAdapter;
import com.missionhub.ui.ListItemAdapterSimple;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;
import com.missionhub.ui.ImageManager;
import com.missionhub.ui.Rejoicable;
import com.missionhub.ui.RejoicableAdapter;
import com.missionhub.ui.ListItemSimple;
import com.missionhub.ui.widget.item.ContactSurveyHeaderItem;
import com.missionhub.ui.widget.item.ContactSurveyItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ContactActivity extends Activity {

	/* Logging Tag */
	public static final String TAG = ContactActivity.class.getName();

	/* Tab Constants */
	public static final int TAB_CONTACT = 0;
	public static final int TAB_MORE_INFO = 1;
	public static final int TAB_SURVEYS = 2;
	
	/* Current Tab */
	private int tab = TAB_CONTACT;

	/* Dialog Constants */
	private final int DIALOG_REJOICABLES = 0;
	
	/* The currently displayed contact */
	private GMetaContact contactMeta;
	private GContact contact;

	/* contact.xml */
	private ListView listView;
	private LoaderActionBarItem indicator;
	private ToggleButton bottom_button_left;
	private ToggleButton bottom_button_center;
	private ToggleButton bottom_button_right;
	private LinearLayout header;
	
	/* contact_header.xml */
	private LinearLayout contactHeader;
	private ImageView profilePicture;
	private String currentProfilePicture = "";
	private ImageManager imageManager;
	private TextView name;
	private Button phone;
	private Button sms;
	private Button email;
	private Button assign;

	/* contact_post.xml */
	private LinearLayout post;
	private EditText comment;
	private Button save;
	private Spinner status;
	private ListView rejoicableListView;
	
	ArrayList<Rejoicable> validRejoicables;

	/* Assignment Constants */
	private final int ASSIGNMENT_NONE = 0;
	private final int ASSIGNMENT_ME = 1;
	private final int ASSIGNMENT_OTHER = 2;
	private int assignmentStatus = ASSIGNMENT_NONE;

	/* Status Maps */
	private ArrayList<String> statusList = new ArrayList<String>();
	private ArrayList<String> statusListTag = new ArrayList<String>();
	private ArrayList<Integer> statusListRes = new ArrayList<Integer>();
	
	/* Followup Comment Adapters */
	private CommentItemAdapter commentAdapter;
	private ArrayAdapter<String> noCommentAdapter;
	private ArrayList<GFollowupComment> comments = new ArrayList<GFollowupComment>();
	
	/* Info Tab Adapter */
	private ListItemAdapterSimple infoAdapter;
	private ArrayList<ListItemSimple> info = new ArrayList<ListItemSimple>();
	
	/* Survey Adapter */
	//private SeparatedListAdapter keywordAdapter;
	
	private ItemAdapter keywordAdapter;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Restore contact from savedInstanceState if possible */
		Gson gson = new Gson();
		Intent i = getIntent();
		if (i != null && i.hasExtra("contactJSON")) {
			try {
				contact = gson.fromJson(i.getStringExtra("contactJSON"), GContact.class);
			} catch (Exception e) {}
		} else {
			if (savedInstanceState != null) {
				try {
					if (savedInstanceState.containsKey("contactMetaJSON")) {
						contactMeta = gson.fromJson(savedInstanceState.getString("contactMetaJSON"), GMetaContact.class);
					}
					if (savedInstanceState.containsKey("contactJSON")) {
						contact = gson.fromJson(savedInstanceState.getString("contactJSON"), GContact.class);
					}
				} catch (Exception e) { Log.i(TAG, "Expand from instance failed", e); }
			}
		}
		if (contact == null) {
			finish();
		}
		setTitle(R.string.contact_contact);
		setActionBarContentView(R.layout.contact);
		indicator = (LoaderActionBarItem) addActionBarItem(Type.Refresh, R.id.action_bar_refresh);
		
		listView = (ListView) findViewById(R.id.listview);
		bottom_button_left = (ToggleButton) findViewById(R.id.bottom_button_left);
		bottom_button_center = (ToggleButton) findViewById(R.id.bottom_button_center);
		bottom_button_right = (ToggleButton) findViewById(R.id.bottom_button_right);

		/* contact_header.xml */
		contactHeader = (LinearLayout) View.inflate(this, R.layout.contact_header, null);
		profilePicture = (ImageView) contactHeader.findViewById(R.id.contact_picture);
		imageManager = new ImageManager(getApplicationContext());
		name = (TextView) contactHeader.findViewById(R.id.contact_name);
		phone = (Button) contactHeader.findViewById(R.id.contact_phone);
		sms = (Button) contactHeader.findViewById(R.id.contact_sms);
		email = (Button) contactHeader.findViewById(R.id.contact_email);
		assign = (Button) contactHeader.findViewById(R.id.contact_assign);

		/* contact_post.xml */
		post = (LinearLayout) View.inflate(this, R.layout.contact_post, null);
		comment = (EditText) post.findViewById(R.id.contact_comment);
		save = (Button) post.findViewById(R.id.contact_save);
		status = (Spinner) post.findViewById(R.id.contact_status);
		statusList.add(getString(R.string.status_uncontacted));
		statusListRes.add(R.string.status_uncontacted);
		statusListTag.add("uncontacted");
		statusList.add(getString(R.string.status_attempted_contact));
		statusListRes.add(R.string.status_attempted_contact);
		statusListTag.add("attempted_contact");
		statusList.add(getString(R.string.status_contacted));
		statusListRes.add(R.string.status_contacted);
		statusListTag.add("contacted");
		statusList.add(getString(R.string.status_completed));
		statusListRes.add(R.string.status_completed);
		statusListTag.add("completed");
		statusList.add(getString(R.string.status_do_not_contact));
		statusListRes.add(R.string.status_do_not_contact);
		statusListTag.add("do_not_contact");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.contact_spinner_text, statusList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status.setAdapter(adapter);
		validRejoicables = new ArrayList<Rejoicable>();
		validRejoicables.add(new Rejoicable(R.string.rejoice_spiritual_conversation, R.drawable.rejoicable_s_convo, "spiritual_conversation"));
		validRejoicables.add(new Rejoicable(R.string.rejoice_prayed_to_receive, R.drawable.rejoicable_r_christ, "prayed_to_receive"));
		validRejoicables.add(new Rejoicable(R.string.rejoice_gospel_presentation, R.drawable.rejoicable_g_present, "gospel_presentation"));

		/* Set-Up Adapters */
		commentAdapter = new CommentItemAdapter(this, R.layout.comment_list_item, comments, statusListTag, statusListRes);
		ArrayList<String> noComment = new ArrayList<String>();
		noComment.add(getString(R.string.contact_no_comments));
		noCommentAdapter =  new ArrayAdapter<String>(this, R.layout.no_comment_list_item, noComment);
		infoAdapter = new ListItemAdapterSimple(this, R.layout.list_item_simple, info);
		keywordAdapter = new ItemAdapter(this);
		
		/* Create ListView Header */
		header = new LinearLayout(this);
		header.setOrientation(LinearLayout.VERTICAL);
		header.addView(contactHeader);
		
		/* Set-Up ListView */
		listView.addHeaderView(header);
		String[] mStrings = {};
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));

		/* Set The Default Tab */
		setTab(TAB_CONTACT, true);
		
		/* Display The Guide Pop-Up If Not Hidden */
		Guide.display(this, Guide.CONTACT);
		
		getTracker().trackActivityView(this);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_REJOICABLES:
			AlertDialog dialog = new AlertDialog.Builder(this).setIcon(R.drawable.rejoicable_icon).setTitle(R.string.contact_rejoicables)
				.setAdapter(new RejoicableAdapter(this, android.R.layout.simple_spinner_dropdown_item, validRejoicables), null)
				.setNeutralButton(R.string.alert_ok, null)
				.create();
			rejoicableListView = dialog.getListView();
			rejoicableListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			return dialog;
		}
		return null;
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh:
            	//Flurry.event(this, "Contact.Refresh");
    			update(true);
                break;
            default:
                return super.onHandleActionBarItemClick(item, position);
        }
        return true;
    }
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		try {
			Gson gson = new Gson();
			if (contactMeta != null) {
				b.putString("contactMetaJSON", gson.toJson(contactMeta));
			}
			if (contact != null) {
				b.putString("contactJSON", gson.toJson(contact));
			}
		} catch (Exception e) {Log.i(TAG, "save state failed", e); };
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		try {
			Gson gson = new Gson();
			if (b.containsKey("contactMetaJSON")) {
				contactMeta = gson.fromJson(b.getString("contactMetaJSON"), GMetaContact.class);
			}
			if (b.containsKey("contactJSON")) {
				contact = gson.fromJson(b.getString("contactJSON"), GContact.class);
		 	}
		} catch (Exception e) {Log.i(TAG, "restore state failed", e); }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update(true);
		updateHeader();
	}
	
	private ArrayList<String> processes = new ArrayList<String>();

	@Override
	public void showProgress(String process) {
		processes.add(process);
		indicator.setLoading(true);
	}

	@Override
	public void hideProgress(String process) {
		processes.remove(process);
		if (processes.size() <= 0) {
			indicator.setLoading(false);
		}
	}

	public void updateHeader() {
		if (contact == null) return;
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
		if (person.getPicture() != null && !currentProfilePicture.equals(person.getPicture())) {
			currentProfilePicture = person.getPicture();
			profilePicture.setTag(person.getPicture() + "?type=large");
			imageManager.displayImage(person.getPicture() + "?type=large", profilePicture, defaultImage);
		}
		if (person.getName() != null) {
			name.setText(person.getName());
		}
		if (person.getPhone_number() != null && hasPhoneAbility()) {
			phone.setVisibility(View.VISIBLE);
			sms.setVisibility(View.VISIBLE);
		} else {
			phone.setVisibility(View.GONE);
			sms.setVisibility(View.GONE);
		}
		if (person.getEmail_address() != null) {
			email.setVisibility(View.VISIBLE);
		} else {
			email.setVisibility(View.GONE);
		}

		assignmentStatus = ASSIGNMENT_NONE;
		if (person.getAssignment() != null) {
			GAssign assign = person.getAssignment();
			if (assign.getPerson_assigned_to() != null) {
				GIdNameProvider[] gids = assign.getPerson_assigned_to();
				for (GIdNameProvider gid : gids) {
					if (getUser().getId() == Integer.parseInt(gid.getId())) {
						assignmentStatus = ASSIGNMENT_ME;
						break;
					} else {
						assignmentStatus = ASSIGNMENT_OTHER;
					}
				}
			}
		}
		if (assignmentStatus == ASSIGNMENT_NONE) {
			assign.setText(R.string.contact_assign_to_me);
			assign.setEnabled(true);
		} else if (assignmentStatus == ASSIGNMENT_ME) {
			assign.setText(R.string.contact_unassign);
			assign.setEnabled(true);
		} else if (assignmentStatus == ASSIGNMENT_OTHER) {
			assign.setText(R.string.contact_assign_locked);
			assign.setEnabled(false);
		}
		
		if (person.getStatus() != null) {
			status.setSelection(statusListTag.indexOf(person.getStatus()));
		}
	}

	public void updateMoreInfo() {
		if (contactMeta == null || contact == null) return;
		final GPerson person = contact.getPerson();
		if (person == null) return;
		
		info.clear();
		
		if (!U.nullOrEmpty(person.getFb_id())) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("facebook_id", person.getFb_id());
			info.add(new ListItemSimple(getString(R.string.contact_info_facebook_header), getString(R.string.contact_info_facebook_link), data));
		}
		
		if (getUser().hasRole("admin")) {
			HashMap<String, String> data = new HashMap<String, String>();
			String contactRole = "contact";
			for (GOrgGeneric role : person.getOrganizational_roles()) {
				if (role.getOrg_id() == getUser().getOrganizationID()) {
					contactRole = role.getRole();
					break;
				}
			}
			data.put("role", contactRole);
			data.put("org_id", String.valueOf(getUser().getOrganizationID()));
			data.put("contact_id", String.valueOf(person.getId()));
			if (contactRole.equals("contact")) {
				info.add(new ListItemSimple(getString(R.string.contact_role), getString(R.string.contact_role_promote), data));
			} else if (contactRole.equals("leader")) {
				info.add(new ListItemSimple(getString(R.string.contact_role), getString(R.string.contact_role_demote), data));
			} else if (contactRole.equals("admin")){
				info.add(new ListItemSimple(getString(R.string.contact_role), getString(R.string.contact_role_admin)));
			}
		}
		
		if(person.getAssignment() != null && person.getAssignment().getPerson_assigned_to().length > 0) {
			info.add(new ListItemSimple(getString(R.string.contact_info_assigned_to), person.getAssignment().getPerson_assigned_to()[0].getName()));
		}
		
		if(!U.nullOrEmpty(person.getFirst_contact_date())) {
			Date date = Helper.getDateFromUTCString(person.getFirst_contact_date());
			SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy hh:mm aaa");
			formatter.setTimeZone(TimeZone.getDefault());
			String formattedDate = formatter.format(date);
			info.add(new ListItemSimple(getString(R.string.contact_info_first_contact_date), formattedDate));
		}
		
		if(!U.nullOrEmpty(person.getPhone_number())) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("phone", person.getPhone_number());
			String prettyPhoneNumber = Helper.formatPhoneNumber(person.getPhone_number());
			info.add(new ListItemSimple(getString(R.string.contact_info_phone_number), prettyPhoneNumber, data));
		}
		
		if(!U.nullOrEmpty(person.getEmail_address())) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("email", person.getEmail_address());
			info.add(new ListItemSimple(getString(R.string.contact_info_email_address), person.getEmail_address(), data));
		}
		
		if(!U.nullOrEmpty(person.getBirthday())) {
			info.add(new ListItemSimple(getString(R.string.contact_info_birthday), person.getBirthday()));
		}
		
		if(person.getInterests().length > 0) {
			StringBuffer interests = new StringBuffer();
			for( int i=0; i < person.getInterests().length; i++) {
				GIdNameProvider interest = person.getInterests()[i];
				interests.append(interest.getName());
				if((i+1) < person.getInterests().length) {
					interests.append(", ");
				}
			}
			info.add(new ListItemSimple(getString(R.string.contact_info_interests), interests.toString()));
		}
		
		if(person.getEducation().length > 0) {
			for(int k=0; k < person.getEducation().length; k++) {
				GEducation education = person.getEducation()[k];
				String title = null;
				String value = "";
				if(education.getType() == null) {
					title = getString(R.string.contact_info_education);
				} else {
					title = education.getType();
				}
				
				if(education.getSchool() != null && education.getSchool().getName() != null) {
					value += education.getSchool().getName();
				}
				
				if(education.getYear() != null && education.getYear().getName() != null) {
					if (value.length() > 0) {
						value += " " + getString(R.string.contact_info_class_of) + " ";
					}
					value += education.getYear().getName();
				}
			info.add(new ListItemSimple(title, value));	
			}
		}
		
		if(!U.nullOrEmpty(person.getLocation()) && !U.nullOrEmpty(person.getLocation().getName())) {
			info.add(new ListItemSimple(getString(R.string.contact_info_location), person.getLocation().getName()));
		}
		
		infoAdapter.notifyDataSetChanged();
	}
	
	public void updateKeywords() {
		if (contactMeta == null || contact == null) return;
		final GPerson person = contact.getPerson();
		if (person == null) return;
		
		keywordAdapter = new ItemAdapter(this);
		/*
		try {
			final GKeyword[] keywords = contactMeta.getKeywords();
			HashMap<Integer, GQuestion> questions = new HashMap<Integer, GQuestion>();
			for (GQuestion q : contactMeta.getQuestions()) {
				questions.put(q.getId(), q);
			}
			HashMap<Integer, GQA> answers = new HashMap<Integer, GQA>();
			for (GQA qa : contact.getForm()) {
				answers.put(qa.getQ(), qa);
			}
			for (GKeyword keyword : keywords) {
				try {
					keywordAdapter.add(new ContactSurveyHeaderItem(getString(R.string.contact_survey_keyword) + ": " + keyword.getName()));
					for (int q : keyword.getQuestions()) {
						final GQuestion question = questions.get(q);
						final GQA qa = answers.get(q);
						keywordAdapter.add(new ContactSurveyItem(question.getLabel(), qa.getA()));
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {}
		if (tab == TAB_SURVEYS) {
			listView.setAdapter(keywordAdapter);
		}
		*/
	}
	
	public void update(boolean force) {
		updatePerson(force);
		updateComments(force);
	}

	public void updatePerson(boolean force) {
		if (processes.contains("contact") && !force) return;
		Contacts.get(this, contact.getPerson().getId(), contactResponseHandler);
	}

	public void updateComments(boolean force) {
		if (processes.contains("comment") && !force) return;
		FollowupComments.get(this, contact.getPerson().getId(), commentResponseHandler);
	}

	private ApiResponseHandler contactResponseHandler = new ApiResponseHandler(GMetaContact.class) {
		@Override
		public void onStart() {
			showProgress("contact");
		}

		@Override
		public void onSuccess(Object gsonObject) {
			GMetaContact contactAll = (GMetaContact) gsonObject;
			ContactActivity.this.contactMeta = contactAll;
			ContactActivity.this.contact = contactAll.getContacts()[0];
			ContactActivity.this.updateHeader();
			ContactActivity.this.updateMoreInfo();
			ContactActivity.this.updateKeywords();
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Contact Fetch Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					updatePerson(true);
				}
			});
			ad.show();
			//Flurry.error(ContactActivity.this, e, "Contact.contactResponseHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("contact");
		}
	};
	
	private ApiResponseHandler commentResponseHandler = new ApiResponseHandler(GMetaGFCTop.class) {
		@Override
		public void onStart() {
			showProgress("comment");
		}

		@Override
		public void onSuccess(Object gsonObject) {
			GMetaGFCTop fcsm = (GMetaGFCTop) gsonObject;
			GFCTop[] fcs = fcsm.getFollowup_comments();
			ContactActivity.this.processFollowupComments(fcs);
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Comment Fetch Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					updateComments(true);
				}
			});
			ad.show();
			//Flurry.error(ContactActivity.this, e, "Contact.commentResponseHandler");
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
			if (gfc.getFollowup_comment().getComment().getDeleted_at() == null) {
				comments.add(gfc.getFollowup_comment());
			}
		}
		if (tab == TAB_CONTACT) {
			if (comments.isEmpty()) {
				listView.setAdapter(noCommentAdapter);
			} else {
				listView.setAdapter(commentAdapter);
			}
			commentAdapter.notifyDataSetChanged();
		}
	}

	public void clickAssign() {
		if (assignmentStatus == ASSIGNMENT_NONE) {
			ContactAssignment.create(this, contact.getPerson().getId(), getUser().getId(), new AssignmentHandler(AssignmentHandler.TYPE_ASSIGN));
		} else if (assignmentStatus == ASSIGNMENT_ME) {
			ContactAssignment.delete(this, contact.getPerson().getId(), new AssignmentHandler(AssignmentHandler.TYPE_UNASSIGN));
		}
	}

	private class AssignmentHandler extends ApiResponseHandler {

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
			assign.setEnabled(false);
		}

		@Override
		public void onSuccess() {
			if (type == TYPE_ASSIGN) {
				assign.setText(R.string.contact_unassign);
				assignmentStatus = ASSIGNMENT_ME;
			} else if (type == TYPE_UNASSIGN) {
				assign.setText(R.string.contact_assign_to_me);
				assignmentStatus = ASSIGNMENT_NONE;
			}
			updatePerson(false);
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				if (type == ASSIGNMENT_ME) {
					params.put("assignment", "Me");
				} else if (type == ASSIGNMENT_NONE) {
					params.put("assignment", "None");
				}
				//Flurry.event(ContactActivity.this, "Contact.Assign", params);
			} catch (Exception e) {}
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
			//Flurry.error(ContactActivity.this, e, "Contact.AssignmentHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("assign");
			assign.setEnabled(true);
		}
	};

	public void clickPicture(View v) {
		if (contact.getPerson().getFb_id() != null)
			openFacebookProfile(contact.getPerson().getFb_id());
	}

	public void clickPhone(View v) {
		makePhoneCall(contact.getPerson().getPhone_number());
	}

	public void clickSMS(View v) {
		sendSMS(contact.getPerson().getPhone_number());
	}

	public void clickEmail(View v) {
		sendEmail(contact.getPerson().getEmail_address());
	}
	
	public void makePhoneCall(String phoneNumber) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			startActivity(intent);
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("method", "Phone");
				//Flurry.event(ContactActivity.this, "Contact.MakeContact", params);
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_call, Toast.LENGTH_LONG).show();
		}
	}
	
	public void sendSMS(String phoneNumber) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("address", phoneNumber);
			intent.setType("vnd.android-dir/mms-sms");
			startActivity(intent);
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("method", "SMS");
				//Flurry.event(ContactActivity.this, "Contact.MakeContact", params);
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_sms, Toast.LENGTH_LONG).show();
		}
	}
	
	public void sendEmail(String emailAddress) {
		try {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailAddress });
			startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_send_email)));
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("method", "Email");
				//Flurry.event(ContactActivity.this, "Contact.MakeContact", params);
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_email, Toast.LENGTH_LONG).show();
		}
	}
	
	public void openURL(String url) {
		final String new_url = url;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.contact_open_url)
		       .setCancelable(true)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   try {
		       			Intent i = new Intent(Intent.ACTION_VIEW);
		       			i.setData(Uri.parse(new_url));
		       			startActivity(i);
		       		} catch(Exception e) {
		       			Toast.makeText(ContactActivity.this, R.string.contact_cant_open_profile, Toast.LENGTH_LONG).show();
		       		}
		        	   dialog.dismiss();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void openFacebookProfile(String uid) {
		final String new_uid = uid;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.contact_open_profile)
		       .setCancelable(true)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   try {
		        		   Intent intent = new Intent(Intent.ACTION_VIEW);
		        		   intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
		        		   intent.putExtra("extra_user_id", Long.parseLong(new_uid));
		        		   startActivity(intent);
		        		   try {
			       				HashMap<String, String> params = new HashMap<String, String>();
			       				params.put("method", "App");
			       				//Flurry.event(ContactActivity.this, "Contact.OpenFacebook", params);
			       			} catch (Exception e) {
			       				Log.w(TAG, e.getMessage(), e);
			       			}
		        	   } catch(Exception e) {
		        		   try {
				       			Intent i = new Intent(Intent.ACTION_VIEW);
				       			i.setData(Uri.parse("http://www.facebook.com/profile.php?id=" + new_uid));
				       			startActivity(i);
				       			try {
				       				HashMap<String, String> params = new HashMap<String, String>();
				       				params.put("method", "Browser");
				       				//Flurry.event(ContactActivity.this, "Contact.OpenFacebook", params);
				       			} catch (Exception e2) {}
				       		} catch(Exception f) {
				       			Toast.makeText(ContactActivity.this, R.string.contact_cant_open_profile, Toast.LENGTH_LONG).show();
				       		}
		        	   }
		        	   dialog.dismiss();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void clickSave(View v) {
		clickSave(false);
	}

	public void clickSave(boolean force) {
		if (processes.contains("save") && !force) return;
		
		boolean canSave = false;
		
		String commentStr = "";
		if (comment.getText() != null) {
			commentStr = comment.getText().toString();
		}
		if (!commentStr.equals("")) canSave = true;
		
		ArrayList<String> rejoicables = new ArrayList<String>();
		if (rejoicableListView != null) {
			long[] checkedRejoicables = rejoicableListView.getCheckItemIds();
			for (long pos : checkedRejoicables) {
				rejoicables.add(validRejoicables.get((int) pos).tag);
			}
		}
		if (rejoicables.size() > 0) {
			canSave = true;
		}
		
		int statusPos = status.getSelectedItemPosition();
		if (statusPos > -1 && contact.getPerson().getStatus() != null) {
			if (!contact.getPerson().getStatus().equals(statusListTag.get(statusPos))) {
				canSave = true;
			}
		}
		
		if (canSave) {
			String status = statusListTag.get(statusPos);
			FollowupComments.Comment comment = new FollowupComments.Comment(contact.getPerson().getId(), getUser().getId(), getUser().getOrganizationID(), statusListTag.get(statusPos), commentStr, rejoicables);
			FollowupComments.post(this, comment, new SaveResponseHandler(status));
		} else {
			Toast.makeText(this, R.string.contact_cant_save, Toast.LENGTH_LONG).show();
		}
	}
	
	private class SaveResponseHandler extends ApiResponseHandler {

		private String status;
		
		public SaveResponseHandler(String status) {
			super();
			this.status = status;
		}
		
		@Override
		public void onStart() {
			showProgress("save");
			save.setEnabled(false);
		}

		@Override
		public void onSuccess() {
			comment.setText("");
			if (rejoicableListView != null) {
				rejoicableListView.clearChoices();
			}
			contact.getPerson().setStatus(status);
			updateComments(true);
			try {
   				//Flurry.event(ContactActivity.this, "Contact.Comment.Save");
   			} catch (Exception e) {}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Save Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					clickSave(false);
				}
			});
			ad.show();
			//Flurry.error(ContactActivity.this, e, "Contact.SaveResponseHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("save");
			save.setEnabled(true);
		}
	};

	public void clickRejoicables(View v) {
		showDialog(DIALOG_REJOICABLES);
	}

	public void clickContact(View v) {
		bottom_button_center.setChecked(false);
		bottom_button_right.setChecked(false);
		bottom_button_left.setChecked(true);
		setTab(TAB_CONTACT, false);
	}

	public void clickMoreInfo(View v) {
		bottom_button_left.setChecked(false);
		bottom_button_right.setChecked(false);
		bottom_button_center.setChecked(true);
		setTab(TAB_MORE_INFO, false);
	}

	public void clickSurveys(View v) {
		bottom_button_left.setChecked(false);
		bottom_button_center.setChecked(false);
		bottom_button_right.setChecked(true);
		setTab(TAB_SURVEYS, false);
	}
	
	public void deleteComment(int id, boolean force) {
		if (processes.contains("delete_comment") && !force) return;
		FollowupComments.delete(this, id, new DeleteCommentHandler(id));
	}
	
	private class DeleteCommentHandler extends ApiResponseHandler {

		private int commentid;
		
		public DeleteCommentHandler(int commentid) {
			super();
			this.commentid = commentid;
		}
		
		@Override
		public void onStart() {
			showProgress("delete_comment");
		}

		@Override
		public void onSuccess() {
			updateComments(true);
			try {
				//Flurry.event(ContactActivity.this, "Contact.Comment.Delete");
			} catch (Exception e) {}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Delete Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					deleteComment(commentid, false);
				}
			});
			ad.show();
			//Flurry.error(ContactActivity.this, e, "Contact.DeleteCommentHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("delete_comment");
		}
	};
	
	private OnItemLongClickListener commentLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final GFollowupComment comment = (GFollowupComment) parent.getAdapter().getItem(position);
			if (comment == null) return false;
			
			boolean canDelete = false;
			if (getUser().hasRole("admin")) {
				canDelete = true;
			} else if (getUser().hasRole("leader")) {
				if (comment.getComment().getCommenter().getId() == getUser().getId()) {
					canDelete = true;
				}
			}
			
			if (canDelete) {
				final CharSequence[] items = { getString(R.string.comment_delete) };
				AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
				builder.setIcon(R.drawable.ic_menu_start_conversation);
				builder.setTitle(R.string.comment_actions);
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    @Override
					public void onClick(DialogInterface dialog, int item) {
				    	if (item == 0) {
				    		deleteComment(comment.getComment().getId(), false);
				    	}
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(ContactActivity.this, R.string.contact_no_permissions, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};
	
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
		public void onSuccess() {
			updatePerson(true);
			//Flurry.event(ContactActivity.this, "Contact.ChangeRole");
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Change Role Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					Roles.change(ContactActivity.this, id, role, new ChangeRoleHandler(role, contactId));
				}
			});
			ad.show();
		//	Flurry.error(ContactActivity.this, e, "Contact.ChangeRoleHandler");
		}

		@Override
		public void onFinish() {
			hideProgress("change_role_" + contactId);
		}
	};
	
	private OnItemClickListener infoClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final ListItemSimple item = (ListItemSimple) parent.getAdapter().getItem(position);
			if (item == null || item.data == null) return;
			
			if (item.data.containsKey("role")) {
				final String role = item.data.get("role");
				final int contactId = Integer.parseInt(item.data.get("contact_id"));
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this).setCancelable(true);
				if (role.equals("contact")) {
					builder.setTitle(R.string.contact_promote);
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Roles.change(ContactActivity.this, contactId, "leader", new ChangeRoleHandler("leader", contactId));
							dialog.dismiss();
						}
					});
				} else if (role.equals("leader")) {
					builder.setTitle(R.string.contact_demote);
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Roles.change(ContactActivity.this, contactId, "contact", new ChangeRoleHandler("contact", contactId));
							dialog.dismiss();
						}
					});
				}
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			
			if (item.data.containsKey("phone") && hasPhoneAbility()) {
				makePhoneCall(item.data.get("phone"));
			}
			
			if (item.data.containsKey("sms") && hasPhoneAbility()) {
				sendSMS(item.data.get("sms"));
			}
			
			if (item.data.containsKey("email")) {
				sendEmail(item.data.get("email"));
			}
			
			if (item.data.containsKey("facebook_id")) {
				openFacebookProfile(item.data.get("facebook_id"));
			}
			
			if(item.data.containsKey("url")) {
				openURL(item.data.get("url"));
			}
		}
	};

	private void setTab(int tab, boolean force) {
		if (this.tab != tab || force) {
			switch (tab) {
			case TAB_CONTACT:
				bottom_button_left.setChecked(true); // First State
				header.addView(post);
				listView.setOnItemClickListener(null);
				setTitle(R.string.contact_contact);
				if (comments.isEmpty()) {
					listView.setAdapter(noCommentAdapter);
				} else {
					listView.setAdapter(commentAdapter);
				}
				listView.setOnItemLongClickListener(commentLongClickListener);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "Contact");
					//Flurry.event(this, "Contact.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_MORE_INFO:
				header.removeView(post);
				setTitle(R.string.contact_more);
				listView.setAdapter(infoAdapter);
				listView.setOnItemLongClickListener(null);
				listView.setOnItemClickListener(infoClickListener);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "More Info");
					//Flurry.event(this, "Contact.ChangeTab", params);
				} catch (Exception e) {}
				break;
			case TAB_SURVEYS:
				header.removeView(post);
				setTitle(R.string.contact_survey);
				listView.setAdapter(keywordAdapter);
				listView.setOnItemLongClickListener(null);
				listView.setOnItemClickListener(null);
				try {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("tab", "Surveys");
					//Flurry.event(this, "Contact.ChangeTab", params);
				} catch (Exception e) {}
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
