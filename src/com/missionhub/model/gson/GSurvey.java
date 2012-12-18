package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Survey;
import com.missionhub.model.SurveyDao;
import com.missionhub.util.U;

public class GSurvey {

	public long id;
	public String title;
	public long organization_id;
	public String post_survey_message;
	public String terminology;
	public String login_paragraph;
	public Boolean is_frozen;
	public String created_at;
	public String updated_at;

	public GQuestion[] questions;
	public GSmsKeyword keyword;

	public static final Object lock = new Object();

	/**
	 * Saves the survey to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Survey save(final boolean inTx) throws Exception {
		final Callable<Survey> callable = new Callable<Survey>() {
			@Override
			public Survey call() throws Exception {
				synchronized (lock) {
					final SurveyDao dao = Application.getDb().getSurveyDao();

					Survey survey = dao.load(id);

					boolean insert = false;
					if (survey == null) {
						survey = new Survey();
						insert = true;
					}
					survey.setId(id);
					survey.setTitle(title);
					survey.setOrganization_id(organization_id);
					survey.setPost_survey_message(post_survey_message);
					survey.setTerminology(terminology);
					survey.setLogin_paragraph(login_paragraph);
					survey.setIs_frozen(is_frozen);
					survey.setCreated_at(U.parseISO8601(created_at));
					survey.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(survey);
					} else {
						dao.update(survey);
					}

					if (questions != null) {
						for (final GQuestion question : questions) {
							question.save(true);
						}
					}

					if (keyword != null) {
						keyword.save(true);
					}

					return survey;
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