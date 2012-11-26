package com.missionhub.fragment;

import java.util.HashMap;

import org.holoeverywhere.widget.Toast;

import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.common.collect.HashMultimap;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.Answer;
import com.missionhub.model.Keyword;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.Question;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ObjectArrayAdapter.DisabledItem;
import com.missionhub.util.U;

public class ContactSurveysFragment extends BaseFragment {

	/** the person id of the displayed contact */
	private long mPersonId = -1;

	/** the person object of the displayed contact */
	private Person mPerson;

	/** the listview */
	private ListView mListView;

	/** the listview adapter */
	private SurveysArrayAdapter mAdapter;

	/** task used to update organization */
	private RoboAsyncTask<Organization> mOrganizationTask;

	/** updated organization */
	private boolean mUpdatedOrganization = false;

	public static ContactSurveysFragment instantiate(final long personId) {
		final Bundle bundle = new Bundle();
		bundle.putLong("personId", personId);

		final ContactSurveysFragment fragment = new ContactSurveysFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(false);

		final Bundle arguments = getArguments();
		mPersonId = arguments.getLong("personId", -1);
		mPerson = Application.getDb().getPersonDao().load(mPersonId);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact_surveys, null);
		mListView = (ListView) view.findViewById(R.id.listview);

		if (mAdapter == null) {
			mAdapter = new SurveysArrayAdapter(getActivity());
		} else {
			mAdapter.setContext(getActivity());
		}

		mListView.setAdapter(mAdapter);

		if (mAdapter.isEmpty()) {
			notifyContactUpdated();
		}

		return view;
	}

	public static class SurveysArrayAdapter extends ObjectArrayAdapter {

		final int answeredColor;
		final int unansweredColor;

		public SurveysArrayAdapter(final Context context) {
			super(context);

			answeredColor = context.getResources().getColor(R.color.abs__primary_text_holo_light);
			unansweredColor = context.getResources().getColor(R.color.gray);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final Object object = getItem(position);
			View view = convertView;

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				if (object instanceof KeyItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_keyword, null);
					holder.keyword = (TextView) view.findViewById(R.id.keyword);
				} else if (object instanceof QAItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_qa, null);
					holder.question = (TextView) view.findViewById(R.id.question);
					holder.answer = (TextView) view.findViewById(R.id.answer);
				} else if (object instanceof EmptyItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_empty, null);
				}
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (object instanceof KeyItem) {
				final KeyItem item = (KeyItem) object;
				if (!U.isNullEmpty(item.keyword.getKeyword())) {
					holder.keyword.setText(item.keyword.getKeyword());
				} else {
					holder.keyword.setText(R.string.contact_surveys_no_key);
				}
			} else if (object instanceof QAItem) {
				final QAItem item = (QAItem) object;
				if (!U.isNullEmpty(item.question.getLabel())) {
					holder.question.setText(item.question.getLabel());
				} else {
					holder.question.setText(R.string.contact_surveys_no_q);
				}

				if (!U.isNullEmpty(item.answer.getAnswer())) {
					holder.answer.setText(item.answer.getAnswer());
					holder.answer.setTextColor(answeredColor);
				} else {
					holder.answer.setText(R.string.contact_surveys_no_a);
					holder.answer.setTextColor(unansweredColor);
				}
			}

			return view;
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			return getView(position, convertView, parent);
		}

		public class ViewHolder {
			TextView keyword;
			TextView question;
			TextView answer;
		}

	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void notifyContactUpdated() {
		if (mAdapter == null || mPerson == null) return;
		mAdapter.setNotifyOnChange(false);
		mAdapter.clear();

		mPerson.resetAnswerList();

		boolean updateOrg = false;

		final HashMultimap<Keyword, Question> multi = HashMultimap.create();
		final HashMap<Question, Answer> qa = new HashMap<Question, Answer>();

		for (final Answer a : mPerson.getAnswerList()) {
			// only grab from current org
			if (a.getOrganization_id() != Session.getInstance().getOrganizationId()) continue;

			final Question q = a.getQuestion();
			if (q == null || q.getKeyword() == null) {
				updateOrg = true;
				Log.e("TEST", a.getAnswer());
				continue;
			}

			multi.put(q.getKeyword(), q);
			qa.put(q, a);
		}

		for (final Keyword key : multi.keySet()) {
			mAdapter.add(new KeyItem(key));
			for (final Question question : multi.get(key)) {
				mAdapter.add(new QAItem(question, qa.get(question)));
			}
		}

		if (mAdapter.isEmpty()) {
			mAdapter.add(new EmptyItem());
		}

		mAdapter.notifyDataSetChanged();

		if (updateOrg && !mUpdatedOrganization) {
			updateOrganization();
		}
	}

	public boolean isWorking() {
		return mOrganizationTask != null;
	}

	public static class KeyItem extends DisabledItem {
		public Keyword keyword;

		public KeyItem(final Keyword keyword) {
			this.keyword = keyword;
		}
	}

	public static class QAItem extends DisabledItem {
		public Question question;
		public Answer answer;

		public QAItem(final Question question, final Answer answer) {
			this.question = question;
			this.answer = answer;
		}
	}

	public static class EmptyItem extends DisabledItem {}

	private void updateOrganization() {
		if (mOrganizationTask != null) {
			mOrganizationTask.cancel(true);
		}

		mOrganizationTask = new RoboAsyncTask<Organization>(Application.getContext()) {

			@Override
			public Organization call() throws Exception {
				return Api.getOrganization(Session.getInstance().getOrganizationId()).get();
			}

			@Override
			public void onSuccess(final Organization organization) {
				notifyContactUpdated();
			}

			@Override
			public void onFinally() {
				mUpdatedOrganization = true;
				mOrganizationTask = null;
				getParent().updateRefreshIcon();
			}

			@Override
			public void onException(final Exception e) {
				// TODO: display error
			}

			@Override
			public void onInterrupted(final Exception e) {
				onException(e);
			}

		};
		Toast.makeText(getActivity(), R.string.contact_surveys_updating_org, Toast.LENGTH_LONG).show();
		getParent().updateRefreshIcon();
		Application.getExecutor().execute(mOrganizationTask.future());
	}

	public ContactFragment getParent() {
		return (ContactFragment) getParentFragment();
	}
}