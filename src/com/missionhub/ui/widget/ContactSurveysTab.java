package com.missionhub.ui.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

import com.google.common.collect.LinkedListMultimap;
import com.missionhub.Activity;
import com.missionhub.R;
import com.missionhub.api.model.sql.Answer;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.Question;
import com.missionhub.ui.ContactHeaderSmallFragment;
import com.missionhub.ui.widget.item.ContactSurveyHeaderItem;
import com.missionhub.ui.widget.item.ContactSurveyItem;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ContactSurveysTab extends LinearLayout {

	private Activity activity;

	private ContactHeaderSmallFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private Person person;

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
		LayoutInflater.from(activity).inflate(R.layout.tab_contact_surveys, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_surveys)).findViewById(R.id.listview_contact_surveys);

		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_surveys_header, null);
		mListView.addHeaderView(header, null, false);

		mHeader = (ContactHeaderSmallFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_surveys_header);

		mListAdapter = new ItemAdapter(activity);
		mListView.setAdapter(mListAdapter);
	}

	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
	}

	public void update() {
		if (person == null)
			return;
		
		activity.showProgress(ContactSurveysTab.this.toString());
		
		new Thread(new Runnable() {
			public void run() {
				
				final ListItems li = new ListItems();
				
				KeywordDao kd = activity.getApp().getDbSession().getKeywordDao();
				
				LinkedListMultimap<Integer, ContactSurveyItem> tempSurveys = LinkedListMultimap.<Integer, ContactSurveyItem>create();
				List<Answer> answers = person.getAnswer();
				Iterator<Answer> itr = answers.iterator();
				while(itr.hasNext()) {
					final Answer answer = itr.next();
					final Question question = answer.getQuestion();
					tempSurveys.put(question.getKeyword_id(), new ContactSurveyItem(question.getLabel(), answer.getAnswer()));
				}
				Iterator<Integer> keys = tempSurveys.keySet().iterator();
				while(keys.hasNext()) {
					int key = keys.next();
					final Keyword keyword = kd.load(key);
					li.items.add(new ContactSurveyHeaderItem(activity.getString(R.string.contact_survey_keyword) + ": " + keyword.getKeyword()));
					List<ContactSurveyItem> items = tempSurveys.get(key);
					Iterator<ContactSurveyItem> itemsIterator = items.iterator();
					while(itemsIterator.hasNext()) {
						li.items.add(itemsIterator.next());
					}
				}
				
				final Message msg = updateHandler.obtainMessage();
				msg.obj = li;
				updateHandler.sendMessage(msg);
			}
		}).start();
	}
	
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mListAdapter.setNotifyOnChange(false);
			mListAdapter.clear();
			ListItems items = (ListItems) msg.obj;
			Iterator<Item> itr = items.items.iterator();
			while(itr.hasNext()) {
				mListAdapter.add(itr.next());
			}
			mListAdapter.notifyDataSetChanged();
			activity.hideProgress(ContactSurveysTab.this.toString());
		}
	};
	
	private class ListItems {
		public ArrayList<Item> items = new ArrayList<Item>();
	}
}