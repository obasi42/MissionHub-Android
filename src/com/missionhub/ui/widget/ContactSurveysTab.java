package com.missionhub.ui.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.ProgressItem;

import com.google.common.collect.LinkedListMultimap;
import com.missionhub.Activity;
import com.missionhub.R;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.ApiNotifierHandler;
import com.missionhub.api.Organizations;
import com.missionhub.api.model.sql.Answer;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.Question;
import com.missionhub.ui.ContactHeaderSmallFragment;
import com.missionhub.ui.widget.item.CenteredTextItem;
import com.missionhub.ui.widget.item.ContactSurveyHeaderItem;
import com.missionhub.ui.widget.item.ContactSurveyItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ContactSurveysTab extends LinearLayout {

	private Activity activity;

	private ContactHeaderSmallFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private Person person;

	private final String tag = ContactSurveysTab.this.toString();

	public ContactSurveysTab(Context context) {
		super(context);
		activity = (Activity) context;
		setup();
	}

	public ContactSurveysTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity) context;
		setup();
	}

	public void setup() {
		activity.getApp().getApiNotifier()
				.subscribe(tag, notifierHandler, Type.UPDATE_ORGANIZATION, Type.JSON_ORGANIZATIONS_ON_FAILURE, Type.JSON_ORGANIZATIONS_ON_FINISH, Type.JSON_ORGANIZATIONS_ON_START);

		LayoutInflater.from(activity).inflate(R.layout.tab_contact_surveys, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_surveys)).findViewById(R.id.listview_contact_surveys);

		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_surveys_header, null);
		mListView.addHeaderView(header, null, false);

		mHeader = (ContactHeaderSmallFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_surveys_header);

		mListAdapter = new ItemAdapter(activity);
		mListAdapter.add(new ProgressItem(activity.getString(R.string.loading), true));
		mListView.setAdapter(mListAdapter);
	}

	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
	}

	public void update() {
		if (person == null)
			return;

		activity.showProgress(tag);

		new Thread(new Runnable() {
			public void run() {
				final ListItems li = new ListItems();

				KeywordDao kd = activity.getApp().getDbSession().getKeywordDao();

				LinkedListMultimap<Integer, ContactSurveyItem> tempSurveys = LinkedListMultimap.<Integer, ContactSurveyItem> create();
				List<Answer> answers = person.getAnswer();
				Iterator<Answer> itr = answers.iterator();
				while (itr.hasNext()) {
					Answer answer = itr.next();
					
					if (answer.getOrganization_id() != activity.getUser().getOrganizationID())
						continue;
					
					Question question = answer.getQuestion();

					if (question == null) {
						updateHandler.sendEmptyMessage(UPDATE_ORGS);
						return;
					}

					tempSurveys.put(question.getKeyword_id(), new ContactSurveyItem(question.getLabel(), answer.getAnswer()));
				}
				Iterator<Integer> keys = tempSurveys.keySet().iterator();
				while (keys.hasNext()) {
					int key = keys.next();
					Keyword keyword = kd.load(key);
					
					if (keyword == null) {
						updateHandler.sendEmptyMessage(UPDATE_ORGS);
						return;
					}
					
					li.items.add(new ContactSurveyHeaderItem(activity.getString(R.string.contact_survey_keyword) + ": " + keyword.getKeyword()));
					List<ContactSurveyItem> items = tempSurveys.get(key);
					Iterator<ContactSurveyItem> itemsIterator = items.iterator();
					while (itemsIterator.hasNext()) {
						li.items.add(itemsIterator.next());
					}
				}
				
				if (li.items.isEmpty()) {
					li.items.add(new CenteredTextItem(activity.getString(R.string.contact_no_answers)));
				}

				final Message msg = updateHandler.obtainMessage();
				msg.what = COMPLETE;
				msg.obj = li;
				updateHandler.sendMessage(msg);
			}
		}).start();
	}
	
	private static final int COMPLETE = 0;
	private static final int UPDATE_ORGS = 1;
	

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COMPLETE:
				mListAdapter.setNotifyOnChange(false);
				mListAdapter.clear();
				ListItems items = (ListItems) msg.obj;
				Iterator<Item> itr = items.items.iterator();
				while (itr.hasNext()) {
					mListAdapter.add(itr.next());
				}
				mListAdapter.notifyDataSetChanged();
				break;
			case UPDATE_ORGS:
				activity.showProgress(tag);
				Organizations.get(activity, activity.getUser().getOrganizationID(), ContactSurveysTab.this.toString());
				break;
			}
			activity.hideProgress(tag);
		}
	};

	private boolean orgUpdated = false;

	private ApiNotifierHandler notifierHandler = new ApiNotifierHandler(tag) {

		@Override
		public void handleMessage(Type type, String tag, Bundle bundle, Throwable throwable, long rowId) {
			switch (type) {
			case UPDATE_ORGANIZATION:
				if (rowId == activity.getUser().getOrganizationID() && !orgUpdated) {
					final Intent intent = activity.getIntent();
					Toast.makeText(activity, R.string.contact_refreshing_org, Toast.LENGTH_LONG).show();
					activity.startActivity(intent);
					activity.finish();
				}
				break;
			case JSON_ORGANIZATIONS_ON_FINISH:
				activity.hideProgress(tag);
				break;
			case JSON_ORGANIZATIONS_ON_FAILURE:
				// TODO:
				break;
			}
		}
	};

	private class ListItems {
		public ArrayList<Item> items = new ArrayList<Item>();
	}
}