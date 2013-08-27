package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.User;
import com.missionhub.model.UserDao;

import java.util.concurrent.Callable;

public class GUser {

    public GUser user;

    public long id;
    public Long primary_organization_id;
    public String created_at;
    public String updated_at;

    /**
     * Saves the user to the SQLite database.
     *
     * @param inTx
     * @return
     * @throws Exception
     */
    public User save(final long personId, final boolean inTx) throws Exception {
        final Callable<User> callable = new Callable<User>() {
            @Override
            public User call() throws Exception {

                // wrapped user
                if (user != null) {
                    user.save(personId, true);
                }

                final UserDao dao = Application.getDb().getUserDao();

                User user = dao.load(id);

                boolean insert = false;
                if (user == null) {
                    user = new User();
                    insert = true;
                }
                user.setId(id);
                user.setPerson_id(personId);
                user.setPrimary_organization_id(primary_organization_id);
                user.setCreated_at(created_at);
                user.setUpdated_at(updated_at);

                if (insert) {
                    dao.insert(user);
                } else {
                    dao.update(user);
                }

                return user;
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

}