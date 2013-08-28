package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Answer;
import com.missionhub.model.AnswerDao;

import java.util.concurrent.Callable;

public class GAnswer {

    public GAnswer answer;

    public long id;
    public long question_id;
    public String value;

    /**
     * Saves the current answer to the SQLite database.
     *
     * @param inTx
     * @return saved current address
     * @throws Exception
     */
    public Answer save(final long answerSheetId, final boolean inTx) throws Exception {
        final Callable<Answer> callable = new Callable<Answer>() {
            @Override
            public Answer call() throws Exception {
                // wrapped answer
                if (answer != null) {
                    return answer.save(answerSheetId, true);
                }

                final AnswerDao dao = Application.getDb().getAnswerDao();

                final Answer answer = new Answer();
                answer.setId(id);
                answer.setAnswer_sheet_id(answerSheetId);
                answer.setQuestion_id(question_id);
                answer.setValue(value);
                dao.insertOrReplace(answer);

                return answer;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}