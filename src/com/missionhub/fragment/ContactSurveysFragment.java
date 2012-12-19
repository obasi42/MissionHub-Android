package com.missionhub.fragment;

import java.util.List;

import org.holoeverywhere.widget.Toast;

import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.Answer;
import com.missionhub.model.AnswerSheet;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.model.Question;
import com.missionhub.model.Survey;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ObjectArrayAdapter.DisabledItem;
import com.missionhub.util.U;

public class ContactSurveysFragment extends BaseFragment {

	/** the person id of the displayed contact */
	private long mPersonId = -1;

	/** the person object of the displayed contact */
	private Person mPerson;

	/** the listview */
	@InjectView(android.R.id.list) private ListView mListView;

	/** the listview adapter */
	private SurveysArrayAdapter mAdapter;

	/** task used to update organization */
	private SafeAsyncTask<Organization> mOrganizationTask;

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
		return inflater.inflate(R.layout.fragment_contact_surveys, null);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mAdapter == null) {
			mAdapter = new SurveysArrayAdapter(getActivity());
		} else {
			mAdapter.setContext(getActivity());
		}

		mListView.setAdapter(mAdapter);

		if (mAdapter.isEmpty()) {
			notifyContactUpdated();
		}
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

				if (object instanceof SurveyItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_survey, null);
					holder.keyword = (TextView) view.findViewById(android.R.id.text1);
				} else if (object instanceof QAItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_qa, null);
					holder.question = (TextView) view.findViewById(android.R.id.text1);
					holder.answer = (TextView) view.findViewById(android.R.id.text2);
				} else if (object instanceof EmptyItem) {
					view = getLayoutInflater().inflate(R.layout.item_contact_surveys_empty, null);
				}
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (object instanceof SurveyItem) {
				final SurveyItem item = (SurveyItem) object;
				if (!U.isNullEmpty(item.survey.getTitle())) {
					holder.keyword.setText(item.survey.getTitle());
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

				if (!U.isNullEmpty(item.answer.getValue())) {
					holder.answer.setText(item.answer.getValue());
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

		mPerson.resetAnswerSheetList();

		boolean fetchQuestionData = false;

		final List<AnswerSheet> sheets = mPerson.getAnswerSheetList();
		for (final AnswerSheet sheet : sheets) {
			if (sheet.getSurvey() == null) {
				fetchQuestionData = true;
				continue;
			}

			// only look at answers from current organization
			if (sheet.getSurvey().getOrganization_id() != Session.getInstance().getOrganizationId()) {
				continue;
			}

			mAdapter.add(new SurveyItem(sheet.getSurvey()));

			final List<Answer> answers = sheet.getAnswerList();
			for (final Answer answer : answers) {
				if (answer.getQuestion() == null) {
					fetchQuestionData = true;
					continue;
				}
				mAdapter.add(new QAItem(answer.getQuestion(), answer));
			}
		}

		if (mAdapter.isEmpty()) {
			mAdapter.add(new EmptyItem());
		}

		mAdapter.notifyDataSetChanged();

		if (fetchQuestionData && !mUpdatedOrganization) {
			updateOrganization();
		}
	}

	public boolean isWorking() {
		return mOrganizationTask != null;
	}

	public static class SurveyItem extends DisabledItem {
		public Survey survey;

		public SurveyItem(final Survey survey) {
			this.survey = survey;
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

		mOrganizationTask = new SafeAsyncTask<Organization>() {

			@Override
			public Organization call() throws Exception {
				return Api.getOrganization(Session.getInstance().getOrganizationId(), ApiOptions.builder() //
						.include(Include.keywords) //
						.include(Include.questions) //
						.include(Include.surveys) //
						.build()).get();
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