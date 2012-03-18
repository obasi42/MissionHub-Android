package com.missionhub.api.convert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GQuestion;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Question;
import com.missionhub.api.model.sql.QuestionChoice;
import com.missionhub.api.model.sql.QuestionChoiceDao;
import com.missionhub.api.model.sql.QuestionChoiceDao.Properties;
import com.missionhub.api.model.sql.QuestionDao;

import de.greenrobot.dao.LazyList;

public class QuestionJsonSql {

	/** logging tag */
	public final static String TAG = QuestionJsonSql.class.getSimpleName();

	public static void update(final Context context, final long keywordId, final GQuestion[] questions, final String... categories) {
		update(context, keywordId, questions, true, true, categories);
	}

	public static void update(final Context context, final long keywordId, final GQuestion[] questions, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, keywordId, questions, threaded, notify, categories);
		} catch (final Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}

	private static void privateUpdate(final Context context, final long keywordId, final GQuestion[] questions, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, keywordId, questions, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final QuestionDao qd = session.getQuestionDao();
		final QuestionChoiceDao qcd = session.getQuestionChoiceDao();

		final List<Question> qs = new ArrayList<Question>();
		final ArrayList<QuestionChoice> qcDelete = new ArrayList<QuestionChoice>();
		final List<QuestionChoice> qcInsert = new ArrayList<QuestionChoice>();

		for (final GQuestion question : questions) {
			Question q = qd.load(question.getId());
			if (q == null) {
				q = new Question();
			} else {
				q.refresh();
			}

			q.setId(question.getId());
			q.setKeyword_id(keywordId);

			if (question.getLabel() != null) {
				q.setLabel(question.getLabel());
			}

			if (question.getRequired() != null) {
				q.setRequired(Boolean.parseBoolean(question.getRequired()));
			}

			if (question.getStyle() != null) {
				q.setStyle(question.getStyle());
			}

			if (question.getKind() != null) {
				q.setKind(question.getKind());
			}

			if (question.getChoices() != null) {
				// Delete all questions choices for this question
				final LazyList<QuestionChoice> questionChoices = qcd.queryBuilder().where(Properties.Question_id.eq(question.getId())).listLazyUncached();
				final Iterator<QuestionChoice> itr = questionChoices.listIteratorAutoClose();
				while (itr.hasNext()) {
					qcDelete.add(itr.next());
				}

				for (final String choice : question.getChoices()) {
					final QuestionChoice qc = new QuestionChoice();
					qc.setChoice(choice);
					qc.setQuestion_id(question.getId());
					qcInsert.add(qc);
				}
			}

			qs.add(q);
		}

		app.getDbSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (final QuestionChoice qc : qcDelete) {
					session.delete(qc);
				}

				for (final Question q : qs) {
					session.insertOrReplace(q);
				}

				for (final QuestionChoice qc : qcInsert) {
					session.insert(qc);
				}

			}
		});
	}
}