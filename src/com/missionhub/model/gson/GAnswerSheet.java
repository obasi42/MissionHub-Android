package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.AnswerSheet;
import com.missionhub.model.AnswerSheetDao;
import com.missionhub.util.U;

public class GAnswerSheet {

	public long id;
	public long survey_id;
	public String created_at;
	public String updated_at;
	public String completed_at;

	public GAnswer[] answers;
	public GSurvey[] surveys;

	public static final Object lock = new Object();

	/**
	 * Saves the current answer sheet to the SQLite database.
	 * 
	 * @param inTx
	 * @return saved current address
	 * @throws Exception
	 */
	public AnswerSheet save(final long personId, final boolean inTx) throws Exception {
		final Callable<AnswerSheet> callable = new Callable<AnswerSheet>() {
			@Override
			public AnswerSheet call() throws Exception {
				synchronized (lock) {
					final AnswerSheetDao dao = Application.getDb().getAnswerSheetDao();

					AnswerSheet sheet = dao.load(id);
					boolean insert = false;
					if (sheet == null) {
						sheet = new AnswerSheet();
						insert = true;
					}

					sheet.setId(id);
					sheet.setPerson_id(personId);
					sheet.setSurvey_id(survey_id);
					sheet.setCreated_at(U.parseISO8601(created_at));
					sheet.setUpdated_at(U.parseISO8601(updated_at));
					sheet.setCompleted_at(U.parseISO8601(completed_at));

					if (insert) {
						dao.insert(sheet);
					} else {
						dao.update(sheet);
					}

					if (answers != null) {
						for (final GAnswer answer : answers) {
							answer.save(id, true);
						}
					}
					if (surveys != null) {
						for (final GSurvey survey : surveys) {
							survey.save(true);
						}
					}

					return sheet;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}