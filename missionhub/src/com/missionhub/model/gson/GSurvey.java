package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Survey;
import com.missionhub.model.SurveyDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GSurvey {

    public GSurvey survey;

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
                // wrapped survey
                if (survey != null) {
                    return survey.save(true);
                }

                final SurveyDao dao = Application.getDb().getSurveyDao();

                final Survey survey = new Survey();
                survey.setId(id);
                survey.setTitle(title);
                survey.setOrganization_id(organization_id);
                survey.setPost_survey_message(post_survey_message);
                survey.setTerminology(terminology);
                survey.setLogin_paragraph(login_paragraph);
                survey.setIs_frozen(is_frozen);
                survey.setCreated_at(created_at);
                survey.setUpdated_at(updated_at);
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

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static List<Survey> replaceAll(final GSurvey[] surveys, final long organization_id, final boolean inTx) throws Exception {
        final Callable<List<Survey>> callable = new Callable<List<Survey>>() {
            @Override
            public List<Survey> call() throws Exception {
                final SurveyDao dao = Application.getDb().getSurveyDao();

                List<Long> oldIds = dao.queryBuilder().where(SurveyDao.Properties.Organization_id.eq(organization_id)).listKeys();
                for (Long id : oldIds) {
                    dao.deleteByKey(id);
                }

                final List<Survey> surveys1 = new ArrayList<Survey>();
                for (final GSurvey gsurvey : surveys) {
                    final Survey survey = gsurvey.save(true);
                    if (survey != null) {
                        surveys1.add(survey);
                    }
                }
                return surveys1;
            }

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}