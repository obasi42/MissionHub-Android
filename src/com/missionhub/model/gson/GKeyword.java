package com.missionhub.model.gson;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.Keyword;
import com.missionhub.model.KeywordDao;
import com.missionhub.model.Question;
import com.missionhub.model.QuestionChoice;
import com.missionhub.model.QuestionChoiceDao;
import com.missionhub.model.QuestionDao;
import com.missionhub.util.U;

public class GKeyword {
	public String keyword;
	public long id;
	public GQuestion[] questions;
	public String state;

	public FutureTask<Keyword> save(final long organizationId) {
		return save(organizationId, true);
	}

	public FutureTask<Keyword> save(final long organizationId, final boolean threaded) {
		final FutureTask<Keyword> task = new FutureTask<Keyword>(new Callable<Keyword>() {
			@Override
			public Keyword call() throws Exception {

				final Callable<Keyword> callable = new Callable<Keyword>() {
					@Override
					public Keyword call() throws Exception {

						final KeywordDao kd = Application.getDb().getKeywordDao();
						final QuestionDao qd = Application.getDb().getQuestionDao();
						final QuestionChoiceDao qcd = Application.getDb().getQuestionChoiceDao();

						Keyword k = kd.load(id);
						if (k == null) {
							k = new Keyword();
							k.setId(id);
						}

						k.setOrganization_id(organizationId);
						if (!U.isNullEmpty(keyword)) k.setKeyword(keyword);
						if (!U.isNullEmpty(state)) k.setState(state);

						kd.insertOrReplace(k);

						// delete old questions and choices
						for (final Question q : k.getQuestions()) {
							for (final QuestionChoice c : q.getChoices()) {
								qcd.delete(c);
							}
							qd.delete(q);
						}

						// create new questions and choices
						for (final GQuestion q : questions) {
							if (q == null) continue;

							final Question question = new Question();
							question.setId(q.id);
							question.setKeyword_id(id);

							if (!U.isNullEmpty(q.active)) question.setActive(Boolean.parseBoolean(q.active));
							if (!U.isNullEmpty(q.kind)) question.setKind(q.kind);
							if (!U.isNullEmpty(q.label)) question.setLabel(q.label);
							if (!U.isNullEmpty(q.required)) question.setRequired(Boolean.parseBoolean(q.required));
							if (!U.isNullEmpty(q.style)) question.setStyle(q.style);

							qd.insert(question);

							for (final String c : q.choices) {
								if (U.isNullEmpty(c)) continue;

								final QuestionChoice qc = new QuestionChoice();

								qc.setQuestion_id(q.id);
								qc.setChoice(c);

								qcd.insert(qc);
							}
						}

						Application.postEvent(new KeywordUpdatedEvent(k));

						return k;
					}
				};
				if (threaded) {
					// since we are executing this, it is safe to assume it is the top call,
					// so we should wrap it in a transaction for performance
					return Application.getDb().callInTx(callable);
				} else {
					return callable.call();
				}
			}
		});

		if (threaded) {
			Application.getExecutor().execute(task);
		} else {
			task.run();
		}

		return task;
	}

	/**
	 * Event posted when a Keyword is updated
	 */
	public static class KeywordUpdatedEvent {

		public Keyword keyword;

		public KeywordUpdatedEvent(final Keyword keyword) {
			this.keyword = keyword;
		}

	}
}
