package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Question;
import com.missionhub.model.QuestionDao;

import java.util.concurrent.Callable;

public class GQuestion {

    public GQuestion question;

    public long id;
    public String kind;
    public String style;
    public String label;
    public String content;
    public String object_name;
    public String attribute_name;
    public Boolean web_only;
    public String trigger_words;
    public String notify_via;
    public Boolean hidden;
    public String created_at;
    public String updated_at;

    /**
     * Saves the question to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public Question save(final boolean inTx) throws Exception {
        final Callable<Question> callable = new Callable<Question>() {
            @Override
            public Question call() throws Exception {
                // wrapped question
                if (question != null) {
                    question.save(true);
                }

                final QuestionDao dao = Application.getDb().getQuestionDao();

                final Question question = new Question();
                question.setId(id);
                question.setKind(kind);
                question.setStyle(style);
                question.setLabel(label);
                question.setContent(content);
                question.setObject_name(object_name);
                question.setAttribute_name(attribute_name);
                question.setWeb_only(web_only);
                question.setTrigger_words(trigger_words);
                question.setNotify_via(notify_via);
                question.setHidden(hidden);
                question.setCreated_at(created_at);
                question.setUpdated_at(updated_at);
                dao.insertOrReplace(question);

                return question;
            }

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}