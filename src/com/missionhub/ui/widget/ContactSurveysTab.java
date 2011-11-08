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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ContactSurveysTab extends LinearLayout {

	/* The activity the tab is running in */
	private Activity activity;

	/* The tab's header fragment */
	private ContactHeaderSmallFragment mHeader;

	/* The ListView */
	private ListView mListView;

	/* The list adapter */
	private ItemAdapter mListAdapter;

	/* The person this tab is for */
	private Person person;

	/* The api notifier tag */
	private final String tag = ContactSurveysTab.this.toString();

	/* Handler Messages */
	private static final int MSG_COMPLETE = 0;
	private static final int MSG_UPDATE_ORGS = 1;

	/* Number of times an org update has failed */
	private int orgUpdateFailed = 0;

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

	/**
	 * Initializes the tab
	 */
	private void setup() {
		activity.getApp().getApiNotifier().subscribe(this, notifierHandler, Type.UPDATE_PERSON, Type.UPDATE_ORGANIZATION, Type.JSON_ORGANIZATIONS_ON_FAILURE);

		LayoutInflater.from(activity).inflate(R.layout.tab_contact_surveys, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_surveys)).findViewById(R.id.listview);

		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_surveys_header, null);
		mListView.addHeaderView(header, null, false);

		mHeader = (ContactHeaderSmallFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_surveys_header);

		mListAdapter = new ItemAdapter(activity);
		mListAdapter.add(new ProgressItem(activity.getString(R.string.progress_loading), true));
		mListView.setAdapter(mListAdapter);
	}

	/**
	 * Handles actions from the ApiNotifier
	 */
	private ApiNotifierHandler notifierHandler = new ApiNotifierHandler(tag) {

		@Override
		public void handleMessage(Type type, String tag, Bundle bundle, Throwable throwable, long rowId) {
			switch (type) {
			case UPDATE_PERSON:
				if (rowId == person.get_id()) {
					update();
				}
				break;
			case UPDATE_ORGANIZATION:
				activity.hideProgress(tag);
				if (rowId == activity.getUser().getOrganizationID()) {
					update();
				}
				break;
			case JSON_ORGANIZATIONS_ON_FAILURE:
				orgUpdateFailed++;
				activity.hideProgress(tag);
				break;
			}
		}
	};

	/**
	 * Sets the person object
	 * 
	 * @param person
	 */
	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
	}

	/**
	 * Updates the tab
	 */
	public void update() {
		if (person == null)
			return;

		activity.showProgress(tag);

		final Person p = this.person;

		new Thread(new Runnable() {
			public void run() {
				final ListItems li = new ListItems();

				Person person = activity.getApp().getDbSession().getPersonDao().load(p.get_id());
				KeywordDao kd = activity.getApp().getDbSession().getKeywordDao();

				LinkedListMultimap<Integer, ContactSurveyItem> tempSurveys = LinkedListMultimap.<Integer, ContactSurveyItem> create();
				boolean updateOrg = false;

				List<Answer> answers = person.getAnswer();
				Iterator<Answer> itr = answers.iterator();
				while (itr.hasNext()) {
					Answer answer = itr.next();

					if (answer.getOrganization_id() != activity.getUser().getOrganizationID())
						continue;

					Question question = answer.getQuestion();

					if (question == null) {
						updateOrg = true;
						continue;
					}

					tempSurveys.put(question.getKeyword_id(), new ContactSurveyItem(question.getLabel(), answer.getAnswer()));
				}
				Iterator<Integer> keys = tempSurveys.keySet().iterator();
				while (keys.hasNext()) {
					int key = keys.next();
					Keyword keyword = kd.load(key);

					if (keyword == null) {
						updateOrg = true;
						continue;
					}

					li.items.add(new ContactSurveyHeaderItem(activity.getString(R.string.contact_tab_surveys_keyword) + " " + keyword.getKeyword()));
					List<ContactSurveyItem> items = tempSurveys.get(key);
					Iterator<ContactSurveyItem> itemsIterator = items.iterator();
					while (itemsIterator.hasNext()) {
						li.items.add(itemsIterator.next());
					}
				}

				if (updateOrg) {
					li.items.add(new ProgressItem(activity.getString(R.string.progress_loading), true));
				}
				if (orgUpdateFailed > 2) {
					li.items.add(new CenteredTextItem(activity.getString(R.string.contact_tab_surveys_not_available)));
				} else if (li.items.isEmpty()) {
					li.items.add(new CenteredTextItem(activity.getString(R.string.contact_tab_surveys_no_answers)));
				}

				final Message msg = updateHandler.obtainMessage();
				if (updateOrg) {
					msg.what = MSG_UPDATE_ORGS;
				} else {
					msg.what = MSG_COMPLETE;
				}
				msg.obj = li;
				updateHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * Handles actions from update()
	 */
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_UPDATE_ORGS) {
				activity.showProgress(tag);
				Organizations.get(activity, activity.getUser().getOrganizationID(), tag);
			}

			activity.hideProgress(tag);

			mListAdapter.setNotifyOnChange(false);
			mListAdapter.clear();

			ListItems items = (ListItems) msg.obj;
			Iterator<Item> itr = items.items.iterator();
			while (itr.hasNext()) {
				mListAdapter.add(itr.next());
			}

			mListAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * Class to hold handler items
	 */
	private class ListItems {
		public ArrayList<Item> items = new ArrayList<Item>();
	}
}