package com.missionhub.android.model.gson;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Survey;
import com.missionhub.android.model.SurveyDao;
import com.missionhub.android.util.U;

import java.util.concurrent.Callable;

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
    public GQuestion[] all_questions;
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

                    final Survey survey = new Survey();
                    survey.setId(id);
                    survey.setTitle(title);
                    survey.setOrganization_id(organization_id);
                    survey.setPost_survey_message(post_survey_message);
                    survey.setTerminology(terminology);
                    survey.setLogin_paragraph(login_paragraph);
                    survey.setIs_frozen(is_frozen);
                    survey.setCreated_at(U.parseISO8601(created_at));
                    survey.setUpdated_at(U.parseISO8601(updated_at));
                    dao.insertOrReplace(survey);

                    if (questions != null) {
                        for (final GQuestion question : questions) {
                            question.save(true);
                        }
                    }

                    if (all_questions != null) {
                        for (final GQuestion question : all_questions) {
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