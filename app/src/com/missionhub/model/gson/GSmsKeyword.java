package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.SmsKeyword;
import com.missionhub.model.SmsKeywordDao;
import com.missionhub.util.U;

import java.util.concurrent.Callable;

public class GSmsKeyword {

    public long id;
    public String keyword;
    public long organization_id;
    public long user_id;
    public String explanation;
    public String state;
    public String initial_response;
    public Long survey_id;
    public String created_at;
    public String updated_at;

    public static final Object lock = new Object();

    /**
     * Saves the keyword to the sqlite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public SmsKeyword save(final boolean inTx) throws Exception {
        final Callable<SmsKeyword> callable = new Callable<SmsKeyword>() {
            @Override
            public SmsKeyword call() throws Exception {
                synchronized (lock) {
                    final SmsKeywordDao dao = Application.getDb().getSmsKeywordDao();

                    final SmsKeyword key = new SmsKeyword();
                    key.setId(id);
                    key.setKeyword(keyword);
                    key.setOrganization_id(organization_id);
                    key.setUser_id(user_id);
                    key.setExplanation(explanation);
                    key.setState(state);
                    key.setInitial_response(initial_response);
                    key.setSurvey_id(survey_id);
                    key.setCreated_at(U.parseISO8601(created_at));
                    key.setUpdated_at(U.parseISO8601(updated_at));
                    dao.insertOrReplace(key);

                    return key;
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