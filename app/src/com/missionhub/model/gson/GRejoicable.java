package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Rejoicable;
import com.missionhub.model.RejoicableDao;
import com.missionhub.util.U;

import java.util.concurrent.Callable;

public class GRejoicable {

    public long id;
    public long person_id;
    public long created_by_id;
    public long organization_id;
    public long followup_comment_id;
    public String what;
    public String created_at;
    public String updated_at;
    public String deleted_at;

    public static final Object lock = new Object();

    /**
     * Saves the rejoicable to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public Rejoicable save(final boolean inTx) throws Exception {
        final Callable<Rejoicable> callable = new Callable<Rejoicable>() {
            @Override
            public Rejoicable call() throws Exception {
                synchronized (lock) {
                    final RejoicableDao dao = Application.getDb().getRejoicableDao();

                    if (deleted_at != null) {
                        dao.deleteByKey(id);
                        return null;
                    }

                    final Rejoicable rejoicable = new Rejoicable();
                    rejoicable.setId(id);
                    rejoicable.setPerson_id(person_id);
                    rejoicable.setCreated_by_id(created_by_id);
                    rejoicable.setOrganization_id(organization_id);
                    rejoicable.setFollowup_comment_id(followup_comment_id);
                    rejoicable.setWhat(what);
                    rejoicable.setCreated_at(U.parseISO8601(created_at));
                    rejoicable.setUpdated_at(U.parseISO8601(updated_at));
                    dao.insertOrReplace(rejoicable);

                    return rejoicable;
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