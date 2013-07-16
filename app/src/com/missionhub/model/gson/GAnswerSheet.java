package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.AnswerSheet;
import com.missionhub.model.AnswerSheetDao;

import java.util.concurrent.Callable;

public class GAnswerSheet {

    public GAnswerSheet answer_sheet;

    public long id;
    public long survey_id;
    public String created_at;
    public String updated_at;
    public String completed_at;

    public GAnswer[] answers;
    public GSurvey[] surveys;


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
                // wrapped sheet
                if (answer_sheet != null) {
                    answer_sheet.save(personId, true);
                }

                final AnswerSheetDao dao = Application.getDb().getAnswerSheetDao();

                final AnswerSheet sheet = new AnswerSheet();
                sheet.setId(id);
                sheet.setPerson_id(personId);
                sheet.setSurvey_id(survey_id);
                sheet.setCreated_at(created_at);
                sheet.setUpdated_at(updated_at);
                sheet.setCompleted_at(completed_at);
                dao.insertOrReplace(sheet);

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
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}