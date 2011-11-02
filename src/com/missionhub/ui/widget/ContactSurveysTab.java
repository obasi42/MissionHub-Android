package com.missionhub.ui.widget;

import java.util.Iterator;
import java.util.List;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.ProgressItem;

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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ContactSurveysTab extends LinearLayout {

	private Activity activity;

	private ContactHeaderSmallFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private ProgressItem progressItem;

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

		progressItem = new ProgressItem(activity.getString(R.string.loading), true);
		progressItem.enabled = false;
	}

	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
		mListAdapter.add(progressItem);
		mListAdapter.notifyDataSetChanged();
	}

	public void update() {
		if (person == null)
			return;

		mListAdapter.setNotifyOnChange(false);
		mListAdapter.clear();
		
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
			mListAdapter.add(new ContactSurveyHeaderItem(activity.getString(R.string.contact_survey_keyword) + ": " + keyword.getKeyword()));
			List<ContactSurveyItem> items = tempSurveys.get(key);
			Iterator<ContactSurveyItem> itemsIterator = items.iterator();
			while(itemsIterator.hasNext()) {
				mListAdapter.add(itemsIterator.next());
			}
		}
		
		mListAdapter.notifyDataSetChanged();
	}
}