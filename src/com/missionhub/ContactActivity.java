package com.missionhub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GAssign;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GEducation;
import com.missionhub.api.GError;
import com.missionhub.api.GFCTop;
import com.missionhub.api.GFollowupComment;
import com.missionhub.api.GIdNameProvider;
import com.missionhub.api.GKeyword;
import com.missionhub.api.GPerson;
import com.missionhub.api.GQA;
import com.missionhub.api.GQuestion;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.CommentItemAdapter;
import com.missionhub.ui.SimpleListItemAdapter;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Guide;
import com.missionhub.ui.ImageManager;
import com.missionhub.ui.Rejoicable;
import com.missionhub.ui.RejoicableAdapter;
import com.missionhub.ui.SeparatedListAdapter;
import com.missionhub.ui.SimpleListItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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
	private MenuItem refreshMenuItem;

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
	private Button contactSave;
	private Spinner contactStatus;
	private ListView rejoicableListView;
	
	ArrayList<Rejoicable> validRejoicables;

	private final int ASSIGNMENT_NONE = 0;
	private final int ASSIGNMENT_ME = 1;
	private final int ASSIGNMENT_OTHER = 2;
	private int assignmentStatus = ASSIGNMENT_NONE;

	private final int DIALOG_REJOICABLES = 0;

	private ArrayList<String> statusList = new ArrayList<String>();
	private ArrayList<String> statusListTag = new ArrayList<String>();
	private ArrayList<Integer> statusListRes = new ArrayList<Integer>();
	
	private CommentItemAdapter commentAdapter;
	private ArrayList<GFollowupComment> comments = new ArrayList<GFollowupComment>();
	
	private SimpleListItemAdapter infoAdapter;
	private ArrayList<SimpleListItem> info = new ArrayList<SimpleListItem>();
	
	private SeparatedListAdapter keywordAdapter;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
						contactMeta = gson.fromJson(savedInstanceState.getString("contactMetaJSON"), GContactAll.class);
					}
					if (savedInstanceState.containsKey("contactJSON")) {
						contact = gson.fromJson(savedInstanceState.getString("contactJSON"), GContact.class);
					}
				} catch (Exception e) {}
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
		contactSave = (Button) contactPost.findViewById(R.id.contact_save);
		
		validRejoicables = new ArrayList<Rejoicable>();
		validRejoicables.add(new Rejoicable(R.string.rejoice_spiritual_conversation, R.drawable.rejoicable_s_convo, "spiritual_conversation"));
		validRejoicables.add(new Rejoicable(R.string.rejoice_prayed_to_receive, R.drawable.rejoicable_r_christ, "prayed_to_receive"));
		validRejoicables.add(new Rejoicable(R.string.rejoice_gospel_presentation, R.drawable.rejoicable_g_present, "gospel_presentation"));

		contactStatus = (Spinner) contactPost.findViewById(R.id.contact_status);
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
		contactStatus.setAdapter(adapter);

		commentAdapter = new CommentItemAdapter(this, R.layout.comment_list_item, comments, statusListTag, statusListRes);
		infoAdapter = new SimpleListItemAdapter(this, R.layout.simple_list_item, info);
		keywordAdapter = new SeparatedListAdapter(this);
		
		header = new LinearLayout(this);
		header.setOrientation(LinearLayout.VERTICAL);
		header.addView(contactHeader);

		contactListView.addHeaderView(header);
		String[] mStrings = {};
		contactListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));

		imageManager = new ImageManager(getApplicationContext());

		setTab(TAB_CONTACT, true);
		Guide.display(this, Guide.CONTACT);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact, menu);
		refreshMenuItem = menu.findItem(R.id.contact_menu_refresh);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.contact_menu_refresh:
			update(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		} catch (Exception e) {};
	}
	
	@Override
	public void onRestoreInstanceState(Bundle b) {
		try {
			Gson gson = new Gson();
			if (b.containsKey("contactMetaJSON")) {
				contactMeta = gson.fromJson(b.getString("contactMetaJSON"), GContactAll.class);
			}
			if (b.containsKey("contactJSON")) {
				contact = gson.fromJson(b.getString("contactJSON"), GContact.class);
			}
		} catch (Exception e) {}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		update(true);
		updateHeader();
	}
	
	private ArrayList<String> processes = new ArrayList<String>();

	private void showProgress(String process) {
		processes.add(process);
		if (refreshMenuItem != null) 
			refreshMenuItem.setEnabled(false);
		this.progress.setVisibility(View.VISIBLE);
	}

	private void hideProgress(String process) {
		processes.remove(process);
		if (processes.size() <= 0) {
			if (refreshMenuItem != null) 
				refreshMenuItem.setEnabled(true);
			this.progress.setVisibility(View.GONE);
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
					if (User.getContact().getPerson().getId() == Integer.parseInt(gid.getId())) {
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
		
		if (person.getStatus() != null) {
			contactStatus.setSelection(statusListTag.indexOf(person.getStatus()));
		}
	}

	public void updateMoreInfo() {
		if (contactMeta == null || contact == null) return;
		final GPerson person = contact.getPerson();
		if (person == null) return;
		
		info.clear();
		
		if (person.getFb_id() != null) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("facebook_id", person.getFb_id());
			info.add(new SimpleListItem(getString(R.string.contact_info_facebook_header), getString(R.string.contact_info_facebook_link), data));
		}
		
		if(person.getAssignment().getAssigned_to_person().length > 0) {
			info.add(new SimpleListItem(getString(R.string.contact_info_assigned_to), person.getAssignment().getAssigned_to_person()[0].getName()));
		}
		
		if(person.getFirst_contact_date() != null) {
			Date date = Helper.getDateFromUTCString(person.getFirst_contact_date());
			SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy hh:mm aaa");
			formatter.setTimeZone(TimeZone.getDefault());
			String formattedDate = formatter.format(date);
			info.add(new SimpleListItem(getString(R.string.contact_info_first_contact_date), formattedDate));
		}
		
		if(person.getPhone_number() != null) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("phone", person.getPhone_number());
			String prettyPhoneNumber = Helper.formatPhoneNumber(person.getPhone_number());
			info.add(new SimpleListItem(getString(R.string.contact_info_phone_number), prettyPhoneNumber, data));
		}
		
		if(person.getEmail_address() !=null) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("email", person.getEmail_address());
			info.add(new SimpleListItem(getString(R.string.contact_info_email_address), person.getEmail_address(), data));
		}
		
		if(person.getBirthday() != null) {
			info.add(new SimpleListItem(getString(R.string.contact_info_birthday), person.getBirthday()));
		}
		
		if(person.getInterests().length > 0) {
			String interests = "";
			for( int i=0; i < person.getInterests().length; i++) {
				GIdNameProvider interest = person.getInterests()[i];
				interests += interest.getName();
				if((i+1) < person.getInterests().length) {
					interests += ", ";
				}
			}
			
			info.add(new SimpleListItem(getString(R.string.contact_info_interests), interests));
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
				
				if(education.getSchool().getName() != null) {
					value += education.getSchool().getName();
				}
				
				if(education.getYear() != null && education.getYear().getName() != null) {
					if (value.length() > 0) {
						value += " " + getString(R.string.contact_info_class_of) + " ";
					}
					value += education.getYear().getName();
				}
			info.add(new SimpleListItem(title, value));	
			}
		}
		
		if(person.getLocation().getName() != null) {
			info.add(new SimpleListItem(getString(R.string.contact_info_location), person.getLocation().getName()));
		}
		
		infoAdapter.notifyDataSetChanged();
	}
	
	public void updateKeywords() {
		if (contactMeta == null || contact == null) return;
		final GPerson person = contact.getPerson();
		if (person == null) return;
		
		keywordAdapter = new SeparatedListAdapter(this);
		
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
					ArrayList<SimpleListItem> keywordData = new ArrayList<SimpleListItem>();
					for (int q : keyword.getQuestions()) {
						final GQuestion quesiton = questions.get(q);
						final GQA qa = answers.get(q);
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("id", String.valueOf(keyword.getKeyword_id()));
						data.put("default", getString(R.string.contact_survey_no_answer));
						keywordData.add(new SimpleListItem(quesiton.getLabel(), qa.getA(), data));
					}
					SimpleListItemAdapter adapter = new SimpleListItemAdapter(this, R.layout.simple_list_item, keywordData);
					keywordAdapter.addSection(getString(R.string.contact_survey_keyword) + ": " + keyword.getName(), adapter);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {}
		if (tab == TAB_SURVEYS) {
			contactListView.setAdapter(keywordAdapter);
		}
	}
	
	public void update(boolean force) {
		updatePerson(force);
		updateComments(force);
	}

	public void updatePerson(boolean force) {
		if (processes.contains("contact") && !force) return;
		Api.getContacts(contact.getPerson().getId(), contactResponseHandler);
	}

	public void updateComments(boolean force) {
		if (processes.contains("comment") && !force) return;
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
					ContactActivity.this.updateMoreInfo();
					ContactActivity.this.updateKeywords();
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
			Api.createContactAssignment(contact.getPerson().getId(), User.getContact().getPerson().getId(), new AssignmentHandler(AssignmentHandler.TYPE_ASSIGN));
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
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			startActivity(intent);
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
		} catch (Exception e) {
			Toast.makeText(this, R.string.contact_cant_email, Toast.LENGTH_LONG).show();
		}
	}
	
	public void openURL(String url) {
		final String new_url = url;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.contact_open_url)
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
		builder.setMessage(R.string.contact_open_profile)
		       .setCancelable(true)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   try {
		        		   Intent intent = new Intent(Intent.ACTION_VIEW);
		        		   intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
		        		   intent.putExtra("extra_user_id", Long.parseLong(new_uid));
		        		   startActivity(intent);
		        	   } catch(Exception e) {
		        		   try {
				       			Intent i = new Intent(Intent.ACTION_VIEW);
				       			i.setData(Uri.parse("http://www.facebook.com/profile.php?id=" + new_uid));
				       			startActivity(i);
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
		
		String comment = "";
		if (contactComment.getText() != null) {
			comment = contactComment.getText().toString();
		}
		if (!comment.equals("")) canSave = true;
		
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
		
		int statusPos = contactStatus.getSelectedItemPosition();
		if (statusPos > -1) {
			if (!contact.getPerson().getStatus().equals(statusListTag.get(statusPos))) {
				canSave = true;
			}
		}
		
		if (canSave) {
			String status = statusListTag.get(statusPos);
			Api.postFollowupComment(contact.getPerson().getId(), User.getContact().getPerson().getId(), statusListTag.get(statusPos), comment, new SaveResponseHandler(status), rejoicables);
		} else {
			Toast.makeText(this, R.string.contact_cant_save, Toast.LENGTH_LONG).show();
		}
	}
	
	private class SaveResponseHandler extends AsyncHttpResponseHandler {

		private String status;
		
		public SaveResponseHandler(String status) {
			this.status = status;
		}
		
		@Override
		public void onStart() {
			showProgress("save");
			contactSave.setEnabled(false);
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out) {
				Log.i(TAG, "SAVE SUCCESS");
				contactComment.setText("");
				if (rejoicableListView != null) {
					rejoicableListView.clearChoices();
				}
				contact.getPerson().setStatus(status);
				updateComments(true);
			}
		}

		@Override
		public void onFailure(Throwable e) {
			Log.e(TAG, "Assignment Failed", e);
			AlertDialog ad = DisplayError.display(ContactActivity.this, e);
			ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					clickSave(false);
				}
			});
			ad.show();
		}

		@Override
		public void onFinish() {
			hideProgress("save");
			contactSave.setEnabled(true);
		}
	};

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
	
	public void deleteComment(int id, boolean force) {
		if (processes.contains("delete_comment") && !force) return;
		Api.deleteComment(id, new DeleteCommentHandler(id));
	}
	
	private class DeleteCommentHandler extends AsyncHttpResponseHandler {

		private int commentid;
		
		public DeleteCommentHandler(int commentid) {
			this.commentid = commentid;
		}
		
		@Override
		public void onStart() {
			showProgress("delete_comment");
		}

		@Override
		public void onSuccess(String response) {
			Gson gson = new Gson();
			try {
				GError error = gson.fromJson(response, GError.class);
				onFailure(new MHError(error));
			} catch (Exception out) {
				Log.i(TAG, "DELETE SUCCESS");
				updateComments(true);
			}
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
		}

		@Override
		public void onFinish() {
			hideProgress("delete_comment");
		}
	};
	
	private OnItemLongClickListener commentLongClickListner = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final GFollowupComment comment = (GFollowupComment) parent.getAdapter().getItem(position);
			if (comment == null) return false;
			
			Log.i(TAG, comment.getComment().getComment());
			
			final CharSequence[] items = { getString(R.string.comment_delete) };
			AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
			builder.setIcon(R.drawable.ic_menu_start_conversation);
			builder.setTitle(R.string.comment_actions);
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	if (item == 0) {
			    		deleteComment(comment.getComment().getId(), false);
			    	}
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
			return false;
		}
	};
	
	
	private OnItemClickListener infoClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final SimpleListItem item = (SimpleListItem) parent.getAdapter().getItem(position);
			//if (comment == null) return false;
			
			Log.i(TAG, item.header + " " + item.info);
			if (item.data.containsKey("phone")) {
				makePhoneCall(item.data.get("phone"));
			}
			
			if (item.data.containsKey("sms")) {
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
				header.addView(contactPost);
				contactListView.setOnItemClickListener(null);
				txtTitle.setText(R.string.contact_contact);
				contactListView.setAdapter(commentAdapter);
				contactListView.setOnItemLongClickListener(commentLongClickListner);
				break;
			case TAB_MORE_INFO:
				header.removeView(contactPost);
				txtTitle.setText(R.string.contact_more);
				contactListView.setAdapter(infoAdapter);
				contactListView.setOnItemLongClickListener(null);
				contactListView.setOnItemClickListener(infoClickListener);
				break;
			case TAB_SURVEYS:
				header.removeView(contactPost);
				txtTitle.setText(R.string.contact_survey);
				contactListView.setAdapter(keywordAdapter);
				contactListView.setOnItemLongClickListener(null);
				contactListView.setOnItemClickListener(null);
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
